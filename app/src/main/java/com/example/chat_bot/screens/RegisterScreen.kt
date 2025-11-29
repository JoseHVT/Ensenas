package com.example.chat_bot.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.chat_bot.R
import com.example.chat_bot.ui.components.*
import com.example.chat_bot.ui.theme.*
import kotlinx.coroutines.delay

import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chat_bot.data.auth.AuthState // para toast y el contexto
import com.example.chat_bot.viewmodels.AuthViewModel
import com.example.chat_bot.viewmodels.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    //conex con viewmodel r
    val context = LocalContext.current
    val authState by viewModel.authState.collectAsState()
    //forms var
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }
    //toast
    LaunchedEffect(authState) {
        // uardamos el estado en una var local "stableState"
        // Esto permite que Kotlin confie  (Smart Cast)
        val stableState = authState

        when (stableState) {
            is AuthState.Authenticated -> {
                Toast.makeText(context, "¡Cuenta creada exitosamente!", Toast.LENGTH_LONG).show()
                onRegisterSuccess()
            }
            is AuthState.Error -> {

                Toast.makeText(context, stableState.message, Toast.LENGTH_LONG).show()
            }
            else -> { /* Nada */ }
        }
    }
    //ver si carga
    val isLoading = authState is AuthState.Loading

    Box(modifier = Modifier.fillMaxSize()) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear Cuenta") },
                navigationIcon = {
                    IconButton(onClick = onNavigateToLogin) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AzulTec,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn() + scaleIn(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            ) {
                Image(
                    painter = painterResource(id = R.drawable.borrego_celebration),
                    contentDescription = "Borrego celebrando",
                    modifier = Modifier.size(100.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "¡Únete a EnSeñas!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = AzulTec
            )
            
            Text(
                text = "Crea tu cuenta y comienza a aprender LSM",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 })
            ) {
                Column {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it; errorMessage = null },
                            label = { Text("Nombre completo") },
                            leadingIcon = { 
                                Icon(
                                    Icons.Default.Person, 
                                    null,
                                    tint = AzulTec
                                ) 
                            },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AzulTec,
                                focusedLabelColor = AzulTec,
                                unfocusedBorderColor = Color.Transparent
                            )
                        )
                    }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; errorMessage = null },
                    label = { Text("Correo electrónico") },
                    leadingIcon = { 
                        Icon(
                            Icons.Default.Email, 
                            null,
                            tint = AzulTec
                        ) 
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AzulTec,
                        focusedLabelColor = AzulTec,
                        unfocusedBorderColor = Color.Transparent
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; errorMessage = null },
                    label = { Text("Contraseña") },
                    leadingIcon = { 
                        Icon(
                            Icons.Default.Lock, 
                            null,
                            tint = AzulTec
                        ) 
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                null,
                                tint = AzulTec
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AzulTec,
                        focusedLabelColor = AzulTec,
                        unfocusedBorderColor = Color.Transparent
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it; errorMessage = null },
                    label = { Text("Confirmar contraseña") },
                    leadingIcon = { 
                        Icon(
                            Icons.Default.Lock, 
                            null,
                            tint = AzulTec
                        ) 
                    },
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                null,
                                tint = AzulTec
                            )
                        }
                    },
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AzulTec,
                        focusedLabelColor = AzulTec,
                        unfocusedBorderColor = Color.Transparent
                    )
                )
            }
                }
            }
            
            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage!!,
                    color = RojoError,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = {
                    when {
                        name.isBlank() -> errorMessage = "Ingresa tu nombre"
                        email.isBlank() -> errorMessage = "Ingresa tu correo"
                        !email.contains("@") -> errorMessage = "Correo inválido"
                        password.length < 6 -> errorMessage = "La contraseña debe tener al menos 6 caracteres"
                        password != confirmPassword -> errorMessage = "Las contraseñas no coinciden"
                        else -> {

                            //para ver que show con el fallo
                            android.util.Log.d("DEBUG_REGISTRO", "Email limpio: '${email.trim()}'")
                            // LLAMADA REAL A FIREBASE

                            //el orden era al revez

                            viewModel.signUp(email.trim(), password, name.trim())
                        }//trim para limpiar espacios en blanco
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AzulTec),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text("Crear Cuenta", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
    
    // Loading overlay durante registro
    LoadingOverlay(
        isLoading = isLoading,
        message = "Creando cuenta..."
    )
    }
}
