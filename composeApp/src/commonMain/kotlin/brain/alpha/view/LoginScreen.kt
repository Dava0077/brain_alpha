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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import brain.alpha.controller.LoginController
import brainalpha.composeapp.generated.resources.Res
import brainalpha.composeapp.generated.resources.*
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource


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
                    Image(painter = painterResource(Res.drawable.logo),
                        contentDescription = "App Logo",
                        modifier = Modifier.size(210.dp, 78.dp))

                    Spacer(modifier = Modifier.height(50.dp))

                    // Avatar icon
                    Image(painter = painterResource(Res.drawable.avatar),
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
                    Text("Olvidaste contraseña?",
                        color = Color.Black, fontSize = 14.sp,
                        modifier = Modifier.align(Alignment.End))

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
                        Text(if (controller.isLoading) "Cargando..." else "CONTINUAR", color = Color.Black)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Divider with "o"
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Divider(modifier = Modifier.weight(1f), color = Color.LightGray)
                        Text(" o ", color = Color.Gray)
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
                        Text("SIGN UP", color = Color.White)
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
                }
            }
        }
    }
}
