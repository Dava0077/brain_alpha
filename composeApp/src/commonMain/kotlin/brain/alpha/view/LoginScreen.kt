package brain.alpha.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import brain.alpha.controller.AuthController
import brain.alpha.controller.GoogleAuthController
import brain.alpha.controller.LoginController
import brain.alpha.services.SupabaseClientProvider
import brainalpha.composeapp.generated.resources.Res
import brainalpha.composeapp.generated.resources.*
import io.github.jan.supabase.auth.auth
import org.jetbrains.compose.resources.painterResource
import brain.alpha.model.UserSession
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.*


/**
 * LoginScreen.kt
 *
 * Provides login UI with email/password, Google OAuth, and guest login support.
 * Includes password reset dialog, and uses Supabase for authentication.
 */

@Composable
fun LoginScreen(
    controller: LoginController,
    onNavigateToSignUp: () -> Unit
) {
    // UI + coroutine state
    val scaffoldState = rememberScaffoldState()
    val googleLoginSuccess by LoginController.googleLoginSuccess

    var showResetDialog by remember { mutableStateOf(false) }
    var resetEmail by remember { mutableStateOf("") }
    var resetFeedback by remember { mutableStateOf<String?>(null) }
    var sendingReset by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    // Google login launcher
    val googleSignIn = GoogleAuthController.googleSignInAction(
        scope = coroutineScope,
        onSuccess = {}, // no usamos el callback directamente, sino via LaunchedEffect
        onError = { error -> println("❌ Google login failed: ${error.message}") }
    )

    val alreadyHandled = remember { mutableStateOf(false) }

    // Polls user session every 250ms and proceeds if logged in
    LaunchedEffect(Unit) {
        while (true) {
            delay(250)
            val user = SupabaseClientProvider.client.auth.currentUserOrNull()

            if (user != null && !alreadyHandled.value) {
                alreadyHandled.value = true

                SupabaseClientProvider.client.auth.sessionManager.loadSession()

                // Load profile, fallback to email name
                val userProfile = withContext(Dispatchers.Default) {
                    AuthController.loadUserProfile()
                }

                val fallbackName = user.email?.substringBefore('@')?.replaceFirstChar { it.uppercase() } ?: "Usuario"
                val username = userProfile?.name?.takeIf { it.isNotBlank() } ?: fallbackName

                // Save session info
                UserSession.setSession(
                    id = user.id,
                    emailValue = user.email ?: "desconocido@correo.com",
                    usernameValue = username
                )

                scaffoldState.snackbarHostState.showSnackbar("Bienvenido, ${user.email}")

                LoginController.setUser(
                    UserInfo(id = user.id, email = user.email ?: "unknown", aud = user.aud ?: "")
                )

                LoginController.clear()
                LoginController.navigateTo("home")
            }
        }
    }
    /*val googleSignIn = GoogleAuthController.googleSignInAction(
        scope = coroutineScope,
        onSuccess = { userInfo ->
            coroutineScope.launch {
                val user = withTimeoutOrNull(5000) {
                    while (SupabaseClientProvider.client.auth.currentUserOrNull() == null) {
                        delay(100)
                    }
                    SupabaseClientProvider.client.auth.currentUserOrNull()
                }

                if (user == null) {
                    println("❌ Sesión no disponible tras Google Login")
                    scaffoldState.snackbarHostState.showSnackbar("Error: Sesión no disponible.")
                    return@launch
                }

                // ✅ Restaurar sesión explícitamente
                SupabaseClientProvider.client.auth.sessionManager.loadSession()

                UserSession.setSession(
                    id = user.id,
                    emailValue = user.email ?: "desconocido@correo.com",
                    usernameValue = user.userMetadata?.get("username")?.toString()
                        ?: user.email?.substringBefore('@')?.replaceFirstChar { it.uppercase() }
                        ?: "Usuario"
                )

                println("✅ Google login success: ${user.email}")
                scaffoldState.snackbarHostState.showSnackbar("Bienvenido, ${user.email}")

                LoginController.setUser(
                    UserInfo(id = user.id, email = user.email ?: "unknown", aud = user.aud ?: "")
                )
                LoginController.clear()
                LoginController.navigateTo("home")
            }
        },
        onError = { error ->
            println("Login ERROR: ${error.message}")
        }
    )*/



    /*LaunchedEffect(googleLoginSuccess) {
        println("💡 googleLoginSuccess: $googleLoginSuccess")
        if (googleLoginSuccess) {
            println("✅ Navegando a home...")
            LoginController.clear()
            LoginController.navigateTo("home")
            LoginController.resetGoogleFlag()
        }
    }*/

    // UI scaffold
    Scaffold(scaffoldState = scaffoldState) {
        Box {
            // Background image
            Image(
                painter = painterResource(Res.drawable.Login2),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Main login content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp)
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    // App logo
                    Image(painter = painterResource(Res.drawable.logo),
                        contentDescription = "App Logo",
                        modifier = Modifier.size(210.dp, 210.dp))

                    Spacer(modifier = Modifier.height(8.dp))

                    // Avatar icon
                    Image(painter = painterResource(Res.drawable.user),
                        contentDescription = "Avatar",
                        modifier = Modifier.size(87.dp, 88.dp))

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("LOGIN",
                        color = Color.Black,
                        fontSize = 16.sp)

                    Spacer(modifier = Modifier.height(20.dp))

                    // Email field
                    OutlinedTextField(
                        value = controller.email,
                        onValueChange = {
                            controller.email = it
                            controller.errorMessage = null
                        },
                        label = { Text("Email") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            backgroundColor = Color.White,
                            focusedBorderColor = Color.Gray,
                            unfocusedBorderColor = Color.LightGray
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password field
                    OutlinedTextField(
                        value = controller.password,
                        onValueChange = {
                            controller.password = it
                            controller.errorMessage = null
                        },
                        label = { Text("Contraseña") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            backgroundColor = Color.White,
                            focusedBorderColor = Color.Gray,
                            unfocusedBorderColor = Color.LightGray
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Forgot password (non-functional placeholder)
                    TextButton(
                        onClick = {
                            resetEmail = controller.email
                            resetFeedback = null
                            showResetDialog = true
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("¿Olvidaste tu contraseña?", color = Color.Black, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    // Error message if login fails
                    controller.errorMessage?.let {

                        Text(it, color = Color.Red,
                            fontSize = 14.sp,
                            modifier = Modifier.fillMaxWidth())

                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Login button
                    Button(
                        onClick = { controller.onLoginClicked() },
                        modifier = Modifier.fillMaxWidth().height(40.dp),
                        enabled = !controller.isLoading,
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF0647D7)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(if (controller.isLoading) "Cargando..." else "CONTINUAR", color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Divider with "o"
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Divider(modifier = Modifier.weight(1f), color = Color.LightGray)
                        Text(" Si no tienes cuenta, registrate ", color = Color.Black, fontSize = 14.sp)
                        Divider(modifier = Modifier.weight(1f), color = Color.LightGray)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Navigate to sign-up
                    Button(
                        onClick = onNavigateToSignUp,
                        modifier = Modifier.fillMaxWidth().height(40.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF0647D7)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("REGISTRARSE", color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Login with Google
                    Button(
                        onClick = { googleSignIn.startFlow() },
                        modifier = Modifier.fillMaxWidth().height(40.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Iniciar sesión con Google", color = Color.Black)
                    }


                    Spacer(modifier = Modifier.height(12.dp))

                    // Guest login
                    TextButton(
                        onClick = {
                            controller.loginAsGuest()
                            coroutineScope.launch {
                                scaffoldState.snackbarHostState.showSnackbar("Sesión iniciada como invitado.")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Continuar como invitado", color = Color.Black)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Terms and conditions
                    Text(
                        "Al continuar, estás aceptando nuestros Términos de Servicio y Política de Privacidad",
                        fontSize = 12.sp,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                }

                // Bottom bar decoration
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(34.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .width(134.dp)
                            .height(5.dp)
                            .background(Color.Black, shape = RoundedCornerShape(100.dp))
                    )

                    //Password reset
                    if (showResetDialog) {
                        AlertDialog(
                            onDismissRequest = { showResetDialog = false },
                            title = { Text("Recuperar contraseña") },
                            text = {
                                Column {
                                    OutlinedTextField(
                                        value = resetEmail,
                                        onValueChange = { resetEmail = it },
                                        label = { Text("Email") },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    if (resetFeedback != null) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(resetFeedback!!, color = Color.Red)
                                    }
                                }
                            },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        coroutineScope.launch {
                                            sendingReset = true
                                            val result = brain.alpha.controller.AuthController.sendPasswordReset(resetEmail.trim())
                                            sendingReset = false
                                            result.onSuccess {
                                                resetFeedback = "Revisa tu correo para restablecer tu contraseña."
                                            }.onFailure { e ->
                                                resetFeedback = e.message ?: "Error al enviar el correo."
                                            }
                                        }
                                    },
                                    enabled = !sendingReset
                                ) {
                                    Text("Enviar")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showResetDialog = false }) {
                                    Text("Cancelar")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}


