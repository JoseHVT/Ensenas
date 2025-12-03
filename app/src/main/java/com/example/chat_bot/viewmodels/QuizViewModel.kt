package com.example.chat_bot.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chat_bot.data.api.RetrofitInstance
import com.example.chat_bot.data.auth.TokenManager
import com.example.chat_bot.data.models.QuizResponse
import com.example.chat_bot.data.models.QuizQuestionResponse
import com.example.chat_bot.data.models.QuizAttemptRequest
import com.example.chat_bot.data.models.UserProgressRequest
import com.example.chat_bot.data.repositories.QuizRepository
import com.example.chat_bot.data.repositories.QuizRepositoryImpl
import com.example.chat_bot.data.repository.ModulesRepository
import com.example.chat_bot.data.repository.ModulesRepositoryImpl
import com.example.chat_bot.ui.components.SnackbarController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * ViewModel para QuizScreen - Maneja quizzes y preguntas
 * Refactored to use Repository pattern for testability
 */
class QuizViewModel(
    private val tokenManager: TokenManager,
    // no sw usa  private val authViewModel: AuthViewModel,
    private val quizRepository: QuizRepository = QuizRepositoryImpl(RetrofitInstance.api),
    private val modulesRepository: ModulesRepository = ModulesRepositoryImpl(RetrofitInstance.api)
) : ViewModel() {

    // Estado de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Quiz actual
    private val _quiz = MutableStateFlow<QuizResponse?>(null)
    val quiz: StateFlow<QuizResponse?> = _quiz.asStateFlow()

    // Preguntas del quiz
    private val _questions = MutableStateFlow<List<QuizQuestionResponse>>(emptyList())
    val questions: StateFlow<List<QuizQuestionResponse>> = _questions.asStateFlow()

    // Respuestas del usuario
    private val _userAnswers = MutableStateFlow<MutableMap<Int, Int>>(mutableMapOf())
    val userAnswers: StateFlow<Map<Int, Int>> = _userAnswers.asStateFlow()

    // Índice de pregunta actual
    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

    // Tiempo restante (en segundos)
    private val _timeRemaining = MutableStateFlow(0)
    val timeRemaining: StateFlow<Int> = _timeRemaining.asStateFlow()

    // Score del intento
    private val _score = MutableStateFlow<Float?>(null)
    val score: StateFlow<Float?> = _score.asStateFlow()

    // Estado de error
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    /**
     * Carga un quiz desde el backend
     */
    fun loadQuiz(quizId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val response = quizRepository.getQuizDetails(quizId)

                if (response.isSuccessful && response.body() != null) {
                    val quizData = response.body()!!
                    _quiz.value = quizData
                    _questions.value = quizData.questions
                    _timeRemaining.value = quizData.questions.size * 60 // 1 minuto por pregunta
                    _currentQuestionIndex.value = 0
                    _userAnswers.value = mutableMapOf()
                } else {
                    val errorMsg = "Error al cargar quiz"
                    _errorMessage.value = errorMsg
                    SnackbarController.showError(errorMsg)
                }
            } catch (e: Exception) {
                val errorMsg = "Error: ${e.message}"
                _errorMessage.value = errorMsg
                SnackbarController.showError(errorMsg)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Registra la respuesta del usuario para una pregunta
     */
    fun answerQuestion(questionId: Int, answerId: Int) {
        val answers = _userAnswers.value.toMutableMap()
        answers[questionId] = answerId
        _userAnswers.value = answers
    }

    /**
     * Avanza a la siguiente pregunta
     */
    fun nextQuestion() {
        if (_currentQuestionIndex.value < _questions.value.size - 1) {
            _currentQuestionIndex.value += 1
        }
    }

    /**
     * Retrocede a la pregunta anterior
     */
    fun previousQuestion() {
        if (_currentQuestionIndex.value > 0) {
            _currentQuestionIndex.value -= 1
        }
    }

    /**
     * Envía el intento de quiz al backend
     */
    fun submitQuiz() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val token = tokenManager.getAuthToken().first()
                if (token == null) {
                    _errorMessage.value = "No hay sesión activa"
                    return@launch
                }
                //ref del quizz act
                val currentQuiz = _quiz.value
                val quizId = _quiz.value?.id ?: return@launch

                val totalQuestions = _questions.value.size
                val correctAnswers = calculateCorrectAnswers()
                //Total inicial - Tiempo restante
                val timeSpent = (_timeRemaining.value.let { start ->
                    (questions.value.size * 60).coerceAtLeast(60) - start
                }).coerceAtLeast(0)

                // Convertir Map<Int, Int> a Map<String, String>
                val answersAsStrings = _userAnswers.value.mapKeys { it.key.toString() }
                    .mapValues { it.value.toString() }

                val attemptRequest = QuizAttemptRequest(
                    quizId = quizId,
                    answers = answersAsStrings,
                    score = correctAnswers,
                    total = totalQuestions,
                    durationMs = timeSpent * 1000
                )

                val response = quizRepository.submitQuizAttempt(
                    token = "Bearer $token",
                    attempt = attemptRequest
                )

                if (response.isSuccessful && response.body() != null) {
                    val result = response.body()!!
                    val finalScore= (result.score.toFloat() / result.total.toFloat()) * 100f
                    _score.value = finalScore // Actualizamos el score observable

                    SnackbarController.showSuccess("Quiz completado: ${result.score}/${result.total}")

                    if (finalScore >= 60.0 && currentQuiz != null) {
                        updateModuleProgress(token, currentQuiz.moduleId)
                    }

                } else {
                    val errorMsg = "Error al enviar quiz"
                    _errorMessage.value = errorMsg
                    SnackbarController.showError(errorMsg)
                }
            } catch (e: Exception) {
                val errorMsg = "Error: ${e.message}"
                _errorMessage.value = errorMsg
                SnackbarController.showError(errorMsg)
            } finally {
                _isLoading.value = false
            }
        }
    }
    private suspend fun updateModuleProgress(token: String, moduleId: Int) {
        try {
            val request = UserProgressRequest(moduleId = moduleId, percent = 100)
            val response = modulesRepository.updateProgress("Bearer $token", request)
            if (response.isSuccessful) {
                android.util.Log.d("QuizViewModel", "Progreso actualizado: 100%")
            }
        } catch (e: Exception) {
            android.util.Log.e("QuizViewModel", "Error actualizando progreso: ${e.message}")
        }
    }
    /**
     * Calcula el número de respuestas correctas validando contra las preguntas
     */
    private fun calculateCorrectAnswers(): Int {
        val questions = _questions.value
        val userAnswers = _userAnswers.value
        var correctCount = 0

        userAnswers.forEach { (questionId, userAnswerId) ->
            // Buscar la pregunta correspondiente
            val question = questions.find { it.id == questionId }
            if (question != null) {
                // Validar si la respuesta del usuario coincide con la respuesta correcta
                // La respuesta correcta está en question.answer como String (e.g., "A", "B", "C")
                val correctAnswerKey = question.answer
                val userAnswerKey = userAnswerId.toString() // Convertir ID a key

                if (correctAnswerKey == userAnswerKey) {
                    correctCount++
                }
            }
        }

        return correctCount
    }

    /**
     * Reinicia el quiz
     */
    fun resetQuiz() {
        _currentQuestionIndex.value = 0
        _userAnswers.value = mutableMapOf()
        _score.value = null
        val questionsCount = _quiz.value?.questions?.size ?: 10
        _timeRemaining.value = questionsCount * 60
    }

    /**
     * Actualiza el tiempo restante
     */
    fun updateTime(seconds: Int) {
        _timeRemaining.value = seconds
    }

    private fun showError(msg: String) {
        _errorMessage.value = msg
        SnackbarController.showError(msg)
    }
    /**
     * Limpia el mensaje de error
     */
    fun clearError() {
        _errorMessage.value = null
    }
}
