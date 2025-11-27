package com.example.chat_bot.viewmodels

import com.example.chat_bot.data.auth.TokenManager
import com.example.chat_bot.data.models.ModuleResponse
import com.example.chat_bot.data.models.UserProgressRequest
import com.example.chat_bot.data.models.UserProgressResponse
import com.example.chat_bot.data.repository.ModulesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Ignore
import org.junit.Assert.*
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.mockito.kotlin.never
import retrofit2.Response

/**
 * Unit tests para ModulesViewModel
 * 
 * Cobertura funcional:
 * - ModuleWithProgress data class structure
 * - clearError functionality
 * - updateModuleProgress with repository injection
 * - loadModules with mock data
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ModulesViewModelTest {
    
    @Mock
    private lateinit var tokenManager: TokenManager
    
    @Mock
    private lateinit var authViewModel: AuthViewModel
    
    @Mock
    private lateinit var modulesRepository: ModulesRepository
    
    private lateinit var viewModel: ModulesViewModel
    
    private val testDispatcher = StandardTestDispatcher()
    
    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        
        // Setup default mocks
        `when`(tokenManager.getAuthToken()).thenReturn(flowOf(null))
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    // ============================================
    // DATA STRUCTURE TESTS
    // ============================================
    
    @Test
    fun `ModuleWithProgress should store all properties correctly`() {
        // Given
        val module = ModuleWithProgress(
            id = 1,
            code = "test",
            title = "Test Module",
            description = "Test Description",
            sortOrder = 1,
            completedCount = 10,
            totalCount = 20,
            isLocked = false
        )
        
        // Then
        assertEquals(1, module.id)
        assertEquals("test", module.code)
        assertEquals("Test Module", module.title)
        assertEquals("Test Description", module.description)
        assertEquals(1, module.sortOrder)
        assertEquals(10, module.completedCount)
        assertEquals(20, module.totalCount)
        assertFalse(module.isLocked)
    }
    
    @Test
    fun `ModuleWithProgress should have default values`() {
        // Given
        val module = ModuleWithProgress(
            id = 1,
            code = "test",
            title = "Test",
            description = "Desc",
            sortOrder = 1
        )
        
        // Then
        assertEquals(0, module.completedCount)
        assertEquals(20, module.totalCount)
        assertFalse(module.isLocked)
    }
    
    @Test
    fun `ModuleWithProgress should handle locked state`() {
        // Given
        val locked = ModuleWithProgress(
            id = 1,
            code = "test",
            title = "Test",
            description = "Desc",
            sortOrder = 1,
            isLocked = true
        )
        
        // Then
        assertTrue(locked.isLocked)
    }
    
    // ============================================
    // ERROR HANDLING TESTS
    // ============================================
    
    @Test
    fun `clearError should set errorMessage to null`() = runTest {
        // Given
        viewModel = ModulesViewModel(tokenManager, authViewModel, modulesRepository)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Simulate error state
        whenever(modulesRepository.getModules()).thenReturn(
            Response.error(500, "".toResponseBody("application/json".toMediaTypeOrNull()))
        )
        viewModel.loadModules()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // When
        viewModel.clearError()
        
        // Then
        assertNull(viewModel.errorMessage.value)
    }
    
    @Test
    fun `updateModuleProgress should not crash without token`() = runTest {
        // Given
        `when`(tokenManager.getAuthToken()).thenReturn(flowOf(null))
        viewModel = ModulesViewModel(tokenManager, authViewModel, modulesRepository)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // When - no exception should be thrown
        viewModel.updateModuleProgress(1, 50)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then - silent fail, no crash
        verify(modulesRepository, never()).updateProgress(any(), any())
    }
    
    // ============================================
    // LOAD MODULES TESTS
    // ============================================
    
    @Test
    fun `loadModules should populate modules list with backend data`() = runTest {
        // Given - repository mock
        val mockModules = listOf(
            ModuleResponse(id = 1, code = "abecedario", title = "Abecedario", description = "26 letras", sortOrder = 1, createdAt = "2024-01-01"),
            ModuleResponse(id = 2, code = "numeros", title = "Números", description = "0-100", sortOrder = 2, createdAt = "2024-01-01"),
            ModuleResponse(id = 3, code = "colores", title = "Colores", description = "Básicos", sortOrder = 3, createdAt = "2024-01-01"),
            ModuleResponse(id = 4, code = "animales", title = "Animales", description = "Domésticos", sortOrder = 4, createdAt = "2024-01-01"),
            ModuleResponse(id = 5, code = "comida", title = "Comida", description = "Alimentos", sortOrder = 5, createdAt = "2024-01-01")
        )
        whenever(modulesRepository.getModules()).thenReturn(Response.success(mockModules))
        viewModel = ModulesViewModel(tokenManager, authViewModel, modulesRepository)
        
        // When
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        assertEquals(5, viewModel.modules.value.size)
        assertFalse(viewModel.isLoading.value)
        assertNull(viewModel.errorMessage.value)
    }
    
    @Test
    fun `modules should be sorted by sortOrder`() = runTest {
        // Given
        val mockModules = listOf(
            ModuleResponse(id = 1, code = "abecedario", title = "Abecedario", description = "26 letras", sortOrder = 1, createdAt = "2024-01-01"),
            ModuleResponse(id = 2, code = "numeros", title = "Números", description = "0-100", sortOrder = 2, createdAt = "2024-01-01"),
            ModuleResponse(id = 3, code = "colores", title = "Colores", description = "Básicos", sortOrder = 3, createdAt = "2024-01-01"),
            ModuleResponse(id = 4, code = "animales", title = "Animales", description = "Domésticos", sortOrder = 4, createdAt = "2024-01-01"),
            ModuleResponse(id = 5, code = "comida", title = "Comida", description = "Alimentos", sortOrder = 5, createdAt = "2024-01-01")
        )
        whenever(modulesRepository.getModules()).thenReturn(Response.success(mockModules))
        viewModel = ModulesViewModel(tokenManager, authViewModel, modulesRepository)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // When
        val modules = viewModel.modules.value
        
        // Then
        for (i in 0 until modules.size - 1) {
            assertTrue(modules[i].sortOrder <= modules[i + 1].sortOrder)
        }
    }
    
    @Test
    fun `first module should be Abecedario`() = runTest {
        // Given
        val mockModules = listOf(
            ModuleResponse(id = 1, code = "abecedario", title = "Abecedario", description = "26 letras", sortOrder = 1, createdAt = "2024-01-01"),
            ModuleResponse(id = 2, code = "numeros", title = "Números", description = "0-100", sortOrder = 2, createdAt = "2024-01-01"),
            ModuleResponse(id = 3, code = "colores", title = "Colores", description = "Básicos", sortOrder = 3, createdAt = "2024-01-01"),
            ModuleResponse(id = 4, code = "animales", title = "Animales", description = "Domésticos", sortOrder = 4, createdAt = "2024-01-01"),
            ModuleResponse(id = 5, code = "comida", title = "Comida", description = "Alimentos", sortOrder = 5, createdAt = "2024-01-01")
        )
        whenever(modulesRepository.getModules()).thenReturn(Response.success(mockModules))
        viewModel = ModulesViewModel(tokenManager, authViewModel, modulesRepository)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // When
        val firstModule = viewModel.modules.value.first()
        
        // Then
        assertEquals("Abecedario", firstModule.title)
        assertEquals("abecedario", firstModule.code)
    }
    
    @Test
    fun `Abecedario module should have 26 total count`() = runTest {
        // Given
        val mockModules = listOf(
            ModuleResponse(id = 1, code = "abecedario", title = "Abecedario", description = "26 letras", sortOrder = 1, createdAt = "2024-01-01"),
            ModuleResponse(id = 2, code = "numeros", title = "Números", description = "0-100", sortOrder = 2, createdAt = "2024-01-01"),
            ModuleResponse(id = 3, code = "colores", title = "Colores", description = "Básicos", sortOrder = 3, createdAt = "2024-01-01"),
            ModuleResponse(id = 4, code = "animales", title = "Animales", description = "Domésticos", sortOrder = 4, createdAt = "2024-01-01"),
            ModuleResponse(id = 5, code = "comida", title = "Comida", description = "Alimentos", sortOrder = 5, createdAt = "2024-01-01")
        )
        whenever(modulesRepository.getModules()).thenReturn(Response.success(mockModules))
        viewModel = ModulesViewModel(tokenManager, authViewModel, modulesRepository)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // When
        val abecedarioModule = viewModel.modules.value.find { it.code == "abecedario" }
        
        // Then
        assertNotNull(abecedarioModule)
        assertEquals(26, abecedarioModule!!.totalCount)
    }
    
    @Test
    fun `first two modules should have progress`() = runTest {
        // Given
        val mockModules = listOf(
            ModuleResponse(id = 1, code = "abecedario", title = "Abecedario", description = "26 letras", sortOrder = 1, createdAt = "2024-01-01"),
            ModuleResponse(id = 2, code = "numeros", title = "Números", description = "0-100", sortOrder = 2, createdAt = "2024-01-01"),
            ModuleResponse(id = 3, code = "colores", title = "Colores", description = "Básicos", sortOrder = 3, createdAt = "2024-01-01"),
            ModuleResponse(id = 4, code = "animales", title = "Animales", description = "Domésticos", sortOrder = 4, createdAt = "2024-01-01"),
            ModuleResponse(id = 5, code = "comida", title = "Comida", description = "Alimentos", sortOrder = 5, createdAt = "2024-01-01")
        )
        whenever(modulesRepository.getModules()).thenReturn(Response.success(mockModules))
        viewModel = ModulesViewModel(tokenManager, authViewModel, modulesRepository)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // When
        val modules = viewModel.modules.value
        
        // Then - Mock implementation adds progress to first 2 modules
        assertTrue(modules[0].completedCount > 0)
        assertTrue(modules[1].completedCount > 0)
    }
    
    @Test
    fun `first four modules should be unlocked`() = runTest {
        // Given
        val mockModules = listOf(
            ModuleResponse(id = 1, code = "abecedario", title = "Abecedario", description = "26 letras", sortOrder = 1, createdAt = "2024-01-01"),
            ModuleResponse(id = 2, code = "numeros", title = "Números", description = "0-100", sortOrder = 2, createdAt = "2024-01-01"),
            ModuleResponse(id = 3, code = "colores", title = "Colores", description = "Básicos", sortOrder = 3, createdAt = "2024-01-01"),
            ModuleResponse(id = 4, code = "animales", title = "Animales", description = "Domésticos", sortOrder = 4, createdAt = "2024-01-01"),
            ModuleResponse(id = 5, code = "comida", title = "Comida", description = "Alimentos", sortOrder = 5, createdAt = "2024-01-01")
        )
        whenever(modulesRepository.getModules()).thenReturn(Response.success(mockModules))
        viewModel = ModulesViewModel(tokenManager, authViewModel, modulesRepository)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // When
        val modules = viewModel.modules.value
        
        // Then - Mock implementation unlocks first 4 modules
        assertFalse(modules[0].isLocked)
        assertFalse(modules[1].isLocked)
        assertFalse(modules[2].isLocked)
        assertFalse(modules[3].isLocked)
    }
    
    @Test
    fun `loadModules should handle API error gracefully`() = runTest {
        // Given - error response
        whenever(modulesRepository.getModules()).thenReturn(
            Response.error(500, "Server error".toResponseBody("application/json".toMediaTypeOrNull()))
        )
        viewModel = ModulesViewModel(tokenManager, authViewModel, modulesRepository)
        
        // When
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then - should fall back to mock modules
        assertTrue(viewModel.modules.value.isNotEmpty())
        assertFalse(viewModel.isLoading.value)
    }
    
    @Ignore("android.util.Log is not mocked in unit tests - would require instrumented test")
    @Test
    fun `updateModuleProgress with token should not crash`() = runTest {
        // Given
        val token = "test_token_123"
        `when`(tokenManager.getAuthToken()).thenReturn(flowOf(token))
        val mockResponse = UserProgressResponse(
            userId = "user123",
            moduleId = 1,
            percent = 50,
            lastActivity = "2024-01-01T12:00:00Z"
        )
        whenever(modulesRepository.updateProgress(any(), any())).thenReturn(Response.success(mockResponse))
        
        viewModel = ModulesViewModel(tokenManager, authViewModel, modulesRepository)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // When - no exception should be thrown (despite android.util.Log being unmocked)
        viewModel.updateModuleProgress(1, 50)
        
        // Then - test passes if no exception thrown
        // Note: Actual verification skipped due to android.util.Log.d() being unmocked in unit tests
    }
}
