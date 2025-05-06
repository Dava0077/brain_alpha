package brain.alpha.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import brain.alpha.controller.GoogleAuthController
import brain.alpha.controller.LoginController
import brain.alpha.services.SupabaseClientProvider
import brainalpha.composeapp.generated.resources.Res
import brainalpha.composeapp.generated.resources.*
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.compose.auth.ComposeAuth
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import brain.alpha.model.UserSession


/**
 * LoginScreen.kt
 *
 * This composable is the login UI for the app. It provides input fields for
 * email and password, error handling, and navigation options for sign-up
 * and guest access. A snackbar shows confirmation when logging in as guest.
 */



@Composable
fun LoginScreen(
    controller: LoginController,
    onNavigateToSignUp: () -> Unit
) {
    val scaffoldState = rememberScaffoldState()
    val googleLoginSuccess by LoginController.googleLoginSuccess.collectAsState()

    LaunchedEffect(googleLoginSuccess) {
        if (googleLoginSuccess) {
            LoginController.clear()
            LoginController.navigateTo("home")
            LoginController.resetGoogleFlag()
        }
    }

    val coroutineScope = rememberCoroutineScope()
    var showResetDialog by remember { mutableStateOf(false) }
    var resetEmail by remember { mutableStateOf("") }
    var resetFeedback by remember { mutableStateOf<String?>(null) }
    var sendingReset by remember { mutableStateOf(false) }

    val googleSignIn = GoogleAuthController.googleSignInAction(
        scope = coroutineScope,
        onSuccess = {  userInfo ->
            println("✅ Google login success: ${userInfo.email}")

            coroutineScope.launch {
                scaffoldState.snackbarHostState.showSnackbar("Bienvenido, ${userInfo.email}")
            }

            LoginController.googleLoginSuccess.value = true

        },
        onError = { error -> println("Login ERROR: ${error.message}") }
    )


    Scaffold(scaffoldState = scaffoldState) {
        Box {
            // Background image
            Image(
                painter = painterResource(Res.drawable.Login2),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    Spacer(modifier = Modifier.height(10.dp))

                    // App logo
                    Image(painter = painterResource(Res.drawable.logo),
                        contentDescription = "App Logo",
                        modifier = Modifier.size(210.dp, 210.dp))

                    Spacer(modifier = Modifier.height(10.dp))

                    // Avatar icon
                    Image(painter = painterResource(Res.drawable.user),
                        contentDescription = "Avatar",
                        modifier = Modifier.size(87.dp, 88.dp))

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("LOGIN",
                        color = Color.Black,
                        fontSize = 16.sp)

                    Spacer(modifier = Modifier.height(40.dp))

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


