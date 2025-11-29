package com.example.chat_bot.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chat_bot.R
import com.example.chat_bot.ui.components.LoadingOverlay
import com.example.chat_bot.ui.theme.*
import com.example.chat_bot.viewmodels.AuthViewModel
import com.example.chat_bot.viewmodels.ViewModelFactory
import com.example.chat_bot.data.auth.AuthState

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current

    // Estados observables del ViewModel
    val authState by viewModel.authState.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    
    // Estados locales de UI
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var startAnimations by remember { mutableStateOf(false) }
    
    val focusManager = LocalFocusManager.current
    
    // Observar cambios en el estado de autenticación
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Authenticated -> {
                onLoginSuccess()
            }
            else -> { /* No hacer nada */ }
        }
    }
    
    // Animaciones de entrada
    val headerAlpha by animateFloatAsState(
        targetValue = if (startAnimations) 1f else 0f,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "headerAlpha"
    )
    
    val contentAlpha by animateFloatAsState(
        targetValue = if (startAnimations) 1f else 0f,
        animationSpec = tween(600, delayMillis = 200, easing = FastOutSlowInEasing),
        label = "contentAlpha"
    )
    
    val logoScale by animateFloatAsState(
        targetValue = if (startAnimations) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "logoScale"
    )
    
    LaunchedEffect(Unit) {
        startAnimations = true
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            
            Image(
                painter = painterResource(id = R.drawable.logo_tec),
                contentDescription = "Logo Tec",
                modifier = Modifier.size(100.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Image(
                painter = painterResource(id = R.drawable.borrego_happy),
                contentDescription = "Borrego",
                modifier = Modifier.size(120.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "¡Bienvenido a EnSeñas!",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = AzulTec
            )
            
            Text(
                text = "Inicia sesión para continuar aprendiendo",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { 
                        email = it
                        viewModel.clearError()
                    },
                    placeholder = { Text("Correo electrónico") },
                    leadingIcon = {
                        Icon(Icons.Default.Email, contentDescription = null, tint = AzulTec)
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                OutlinedTextField(
                    value = password,
                    onValueChange = { 
                        password = it
                        viewModel.clearError()
                    },
                    placeholder = { Text("Contraseña") },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = AzulTec)
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                if (passwordVisible) Icons.Default.Visibility 
                                else Icons.Default.VisibilityOff,
                                contentDescription = if (passwordVisible) "Ocultar" else "Mostrar",
                                tint = AzulTec
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None 
                                         else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { 
                            focusManager.clearFocus()
                        }
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
            }
            
            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = RojoError.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = RojoError,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = errorMessage ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = RojoError
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        return@Button
                    }
                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        return@Button
                    }
                    
                    // Llamar al ViewModel para hacer login real
                    viewModel.signIn(email, password)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AzulTec,
                    disabledContainerColor = AzulTec.copy(alpha = 0.6f)
                ),
                shape = RoundedCornerShape(16.dp),
                enabled = authState !is AuthState.Loading
            ) {
                if (authState is AuthState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 3.dp
                    )
                } else {
                    Text(
                        text = "Iniciar Sesión",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "¿No tienes cuenta? ",
                    style = MaterialTheme.typography.bodyMedium
                )
                TextButton(onClick = onNavigateToRegister) {
                    Text(
                        text = "Regístrate",
                        color = AzulTec,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        
        // Loading overlay durante autenticación
        LoadingOverlay(
            isLoading = authState is AuthState.Loading,
            message = "Iniciando sesión..."
        )
    }
}
