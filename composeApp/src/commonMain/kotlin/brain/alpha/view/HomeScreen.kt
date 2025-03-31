package brain.alpha.view

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import brain.alpha.controller.LoginController
import brain.alpha.model.UserSession
import brainalpha.composeapp.generated.resources.*
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Serializable
object HomeRoute

@Composable
fun HomeScreen(
    controller: LoginController,
    onNavigateToMemory: () -> Unit,
    onNavigateToLogic: () -> Unit,
    onNavigateToConcentration: () -> Unit,
    onNavigateToAgility: () -> Unit
) {
    val username by UserSession.username
    val showConfirmDialog = remember { mutableStateOf(false) }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(Res.drawable.fondoMain),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Header superior
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .background(Color(0xFF222937))
                    .border(BorderStroke(2.dp, Color(0xFF4CE3E8)))
            ) {
                // Avatar Izquierda
                IconButton(
                    onClick = { controller.buttonUsername() },
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 8.dp)
                        .size(55.dp)
                        .background(Color(0xFF222937))
                ) {
                    Image(
                        painter = painterResource(Res.drawable.user),
                        contentDescription = "Usuario"
                    )
                }

                // Botones a la derecha: Logout + Ajustes
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { showConfirmDialog.value = true },
                        modifier = Modifier
                            .size(55.dp)
                            .background(Color(0xFF222937))
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.logout),
                            contentDescription = "Cerrar sesión"
                        )
                    }

                    IconButton(
                        onClick = { controller.buttonSettings() },
                        modifier = Modifier
                            .size(55.dp)
                            .background(Color(0xFF222937))
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.ajustes),
                            contentDescription = "Settings"
                        )
                    }
                }

                Text(
                    text = "Hola, ${username ?: "Guest"}!",
                    color = Color.White,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 72.dp)
                )
            }

            Spacer(modifier = Modifier.height(70.dp))

            Text(
                text = "CATEGORIAS",
                fontSize = 40.sp,
                color = Color.Black,
                fontFamily = FontFamily.SansSerif,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    CategoryItem("MEMORIA", Res.drawable.memoria, Color(0xFF0647D7), onNavigateToMemory)
                    CategoryItem("LOGICA", Res.drawable.logica, Color(0xFF0647D7), onNavigateToLogic)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    CategoryItem("CONCENTRACIÓN", Res.drawable.concentracion, Color(0xFF0647D7), onNavigateToConcentration)
                    CategoryItem("AGILIDAD MENTAL", Res.drawable.mentalA, Color(0xFF0647D7), onNavigateToAgility)
                }
            }
        }

        // Logo
        Image(
            painter = painterResource(Res.drawable.logo),
            contentDescription = null,
            modifier = Modifier
                .size(150.dp)
                .align(Alignment.TopCenter)
                .absoluteOffset(y = 30.dp)
                .scale(1.5f)
        )

        // Barra inferior
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 18.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(134.dp)
                    .height(5.dp)
                    .background(Color.Black, shape = RoundedCornerShape(100.dp))
            )
        }

        // 🔒 Confirmación de logout
        if (showConfirmDialog.value) {
            AlertDialog(
                onDismissRequest = { showConfirmDialog.value = false },
                title = { Text("¿Cerrar sesión?") },
                text = { Text("¿Estás seguro de que deseas cerrar sesión?") },
                confirmButton = {
                    TextButton(onClick = {
                        showConfirmDialog.value = false
                        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Default).launch {
                            brain.alpha.controller.AuthController.signOut()
                            LoginController.clear()
                            controller.navigateTo("login")
                        }
                    }) {
                        Text("Sí")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showConfirmDialog.value = false
                    }) {
                        Text("Cancelar")
                    }
                },
                properties = DialogProperties()
            )
        }
    }
}

@Composable
fun CategoryItem(
    title: String,
    image: DrawableResource,
    borderColor: Color,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(160.dp)
            .height(180.dp)
            .clickable { onClick() }
            .background(borderColor, shape = RoundedCornerShape(8.dp))
            .padding(4.dp)
            .background(Color.White, shape = RoundedCornerShape(8.dp)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Image(
                painter = painterResource(image),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = title,
            fontSize = 14.sp,
            color = Color.Black,
            fontFamily = FontFamily.SansSerif,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )
    }
}

