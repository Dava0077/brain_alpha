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
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import brain.alpha.controller.LoginController
import brainalpha.composeapp.generated.resources.Res
import brainalpha.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource

@Composable
fun LoginScreen(
    controller: LoginController,
    onNavigateToSignUp: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(44.dp))

            Image(
                painter = painterResource(Res.drawable.logo),
                contentDescription = "App Logo",
                modifier = Modifier.size(210.dp, 78.dp)
            )

            Spacer(modifier = Modifier.height(50.dp))

            Image(
                painter = painterResource(Res.drawable.avatar),
                contentDescription = "Avatar",
                modifier = Modifier.size(87.dp, 88.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text("LOGIN", color = Color.Black, fontSize = 16.sp)

            Spacer(modifier = Modifier.height(40.dp))

            OutlinedTextField(
                value = controller.email,
                onValueChange = { controller.email = it },
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

            OutlinedTextField(
                value = controller.password,
                onValueChange = { controller.password = it },
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
            Text(
                "Olvidaste contraseña?",
                color = Color.Gray,
                fontSize = 14.sp,
                modifier = Modifier.align(Alignment.End)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Mostrar errores
            controller.errorMessage?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = { controller.onLoginClicked() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                enabled = !controller.isLoading,
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF0647D7)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(if (controller.isLoading) "Cargando..." else "CONTINUAR", color = Color.White)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Divider(modifier = Modifier.weight(1f), color = Color.LightGray)
                Text(" o ", color = Color.Gray)
                Divider(modifier = Modifier.weight(1f), color = Color.LightGray)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onNavigateToSignUp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF0647D7)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("SIGN UP", color = Color.White)
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = { controller.loginAsGuest() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Continuar como invitado", color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Al continuar, estás aceptando nuestros Términos de Servicio y Política de Privacidad",
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }

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
