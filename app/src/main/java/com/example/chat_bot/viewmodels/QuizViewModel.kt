package com.example.chat_bot.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chat_bot.data.api.RetrofitInstance
import com.example.chat_bot.data.auth.TokenManager
import com.example.chat_bot.data.models.QuizResponse
import com.example.chat_bot.data.models.QuizQuestionResponse
import com.example.chat_bot.data.models.QuizAttemptRequest
import com.example.chat_bot.ui.components.SnackbarController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * ViewModel para QuizScreen - Maneja quizzes y preguntas
 */
class QuizViewModel(
    private val tokenManager: TokenManager
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
                val response = RetrofitInstance.api.getQuizDetails(quizId)
                
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
                
                val quizId = _quiz.value?.id ?: return@launch
                val totalQuestions = _questions.value.size
                val correctAnswers = calculateCorrectAnswers()
                val timeSpent = (_quiz.value?.questions?.size ?: 10) * 60 - _timeRemaining.value
                
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
                
                val response = RetrofitInstance.api.submitQuizAttempt(
                    token = "Bearer $token",
                    attempt = attemptRequest
                )
                
                if (response.isSuccessful && response.body() != null) {
                    val result = response.body()!!
                    _score.value = (result.score.toFloat() / result.total.toFloat()) * 100f
                    SnackbarController.showSuccess("Quiz completado: ${result.score}/${result.total}")
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
    
    /**
     * Calcula el número de respuestas correctas (mock por ahora)
     */
    private fun calculateCorrectAnswers(): Int {
        // TODO: Comparar con respuestas correctas del backend
        return _userAnswers.value.size
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
    
    /**
     * Limpia el mensaje de error
     */
    fun clearError() {
        _errorMessage.value = null
    }
}
