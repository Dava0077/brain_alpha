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
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import brain.alpha.controller.AuthController
import brain.alpha.model.UserSession
import brainalpha.composeapp.generated.resources.*
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

/**
 * SignUpScreen.kt
 *
 * Handles user registration. Displays a form to enter email, username, password, and confirmation.
 * On successful registration:
 *   - Inserts the user into the DB (if needed),
 *   - Stores the session locally,
 *   - Shows a confirmation dialog.
 * On failure, shows a snackbar with the error.
 */


@Composable
fun SignUpScreen(
    onSuccess: () -> Unit,
    onFailure: (String) -> Unit,
    onGoToLogin: () -> Unit
) {
    // Form field state
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    // Scaffold and coroutine scope
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()


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
                    Spacer(modifier = Modifier.height(44.dp))

                    // App logo
                    Image(
                        painter = painterResource(Res.drawable.logo),
                        contentDescription = "App Logo",
                        modifier = Modifier.size(210.dp, 78.dp)
                    )
                    Spacer(modifier = Modifier.height(40.dp))

                    // Title
                    Text("SIGN UP", color = Color.Black, fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(24.dp))

                    // Email input
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            backgroundColor = Color.White,
                            focusedBorderColor = Color.Gray,
                            unfocusedBorderColor = Color.LightGray
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Username input
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Usuario") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            backgroundColor = Color.White,
                            focusedBorderColor = Color.Gray,
                            unfocusedBorderColor = Color.LightGray
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Password input
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
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

                    Spacer(modifier = Modifier.height(12.dp))

                    // Confirm password input
                    OutlinedTextField(
                        value = repeatPassword,
                        onValueChange = { repeatPassword = it },
                        label = { Text("Repite contraseña") },
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

                    Spacer(modifier = Modifier.height(12.dp))

                    // Submit button
                    Button(
                        onClick = {
                            loading = true
                            coroutineScope.launch {
                                val result = AuthController.signUp(
                                    email = email.trim(),
                                    username = username.trim(),
                                    password = password,
                                    repeatPassword = repeatPassword
                                )
                                result.onSuccess { user ->
                                    // Insert into users table if needed
                                    AuthController.ensureUserExists(
                                        userId = user.id,
                                        email = user.email,
                                        username = username
                                    )

                                    // Save session
                                    UserSession.setSession(
                                        id = user.id,
                                        emailValue = user.email ?: "desconocido@correo.com",
                                        usernameValue = username
                                    )

                                    // Clear form and show confirmation
                                    showSuccessDialog = true
                                    email = ""
                                    username = ""
                                    password = ""
                                    repeatPassword = ""
                                }.onFailure { exception ->
                                    val message = exception.message ?: "Error inesperado"
                                    scaffoldState.snackbarHostState.showSnackbar(message)
                                    onFailure(message)
                                }
                                loading = false
//                                result.onSuccess {
//                                    // Clear form on success
//                                    email = ""
//                                    username = ""
//                                    password = ""
//                                    repeatPassword = ""
//                                    showSuccessDialog = true
//                                }.onFailure { exception ->
//                                    val message = exception.message ?: "Error inesperado"
//                                    onFailure(message)
//                                    scaffoldState.snackbarHostState.showSnackbar(message)
//                                }
                            }
                        },
                        enabled = !loading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF0647D7)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(if (loading) "Creando..." else "SIGN UP", color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Navigate to login screen
                    TextButton(onClick = {
                        email = ""
                        username = ""
                        password = ""
                        repeatPassword = ""
                        showSuccessDialog = false
                        onGoToLogin()
                    }) {
                        Text("¿Ya tienes cuenta? Inicia sesión!", color = Color.Blue)
                    }
                }

            }


            // Success alert dialog "TO DO"
            /*if (showSuccessDialog) {
                AlertDialog(
                    onDismissRequest = {},
                    title = { Text("Verifica tu correo electrónico") },
                    text = {
                        Text("Hemos enviado un enlace de confirmación a tu email. Debes verificarlo antes de iniciar sesión.")
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            showSuccessDialog = false
                            onGoToLogin()
                        }) {
                            Text("OK")
                        }
                    }
                )
            }*/



//            //Dialog email validated "TO DO"
//            if (UserSession.) {
//                AlertDialog(
//                    onDismissRequest = {},
//                    title = { Text("Email validado") },
//                    text = {
//                        Text("Hemos enviado un enlace de confirmación a tu email. Debes verificarlo antes de iniciar sesión.")
//                    },
//                    confirmButton = {
//                        TextButton(onClick = {
//                            showSuccessDialog = false
//                            onGoToLogin()
//                        }) {
//                            Text("OK")
//                        }
//                    }
//                )
//            }
        }
    }
}

