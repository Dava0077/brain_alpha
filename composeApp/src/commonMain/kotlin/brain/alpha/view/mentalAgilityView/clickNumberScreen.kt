package brain.alpha.view.mentalAgilityView

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import brain.alpha.controller.LoginController
import brain.alpha.controller.mentalAgilityController.clickNumberController

import brain.alpha.model.shared.Difficulty
import brain.alpha.strings.Strings
import brainalpha.composeapp.generated.resources.*
import brainalpha.composeapp.generated.resources.Res
import brainalpha.composeapp.generated.resources.ajustes
import brainalpha.composeapp.generated.resources.fondoMental
import brainalpha.composeapp.generated.resources.logo
import brainalpha.composeapp.generated.resources.user
import org.jetbrains.compose.resources.painterResource
import brain.alpha.controller.GameSessionController
import brain.alpha.model.UserSession
import kotlinx.coroutines.launch

@Composable
fun clickNumberScreen(
    controller: clickNumberController,
    onNavigateToHome: () -> Unit
) {

    val showInstructions by controller.showInstructions
    var sessionSaved by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Show instructions first
    if (showInstructions) {
        InstructionsClickNumber(
            onStart = { controller.beginClickGame() },
            controller = clickNumberController
        )
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(Res.drawable.fondoMental),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .background(Color(0xFF222937), shape = RoundedCornerShape(4.dp))
                    .border(BorderStroke(2.dp, Color(0xFF4CE3E8)))
            ) {
                IconButton(
                    onClick = { /* Dummy action */ },
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 8.dp)
                        .size(55.dp)
                ) {
                    Image(painter = painterResource(Res.drawable.user), contentDescription = "User")
                }
                Image(
                    painter = painterResource(Res.drawable.logo),
                    contentDescription = null,
                    modifier = Modifier
                        .size(120.dp)
                        .align(Alignment.Center)
                        .scale(1.5f)
                )
                IconButton(
                    onClick = { /* Dummy action */ },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 8.dp)
                        .size(55.dp)
                ) {
                    Image(painter = painterResource(Res.drawable.ajustes), contentDescription = "Settings")
                }
            }

            Spacer(modifier = Modifier.height(5.dp))

            Button(
                onClick = { controller.goHome() },
                modifier = Modifier
                    .align(Alignment.End)
                    .height(46.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Yellow),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.elevation(4.dp),
                border = BorderStroke(1.dp, Color(0xFFB0BEC5))
            ) {
                Text("⬅ Volver", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.Black)
            }

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = "CLICK NUMBER",
                fontSize = 40.sp,
                color = Color.Black,
                fontFamily = FontFamily.SansSerif,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Contenido del juego o pantalla inicial
            if (!clickNumberController.isGameStarted) {
                // Pantalla inicial
                Text(
                    text = "",
                    fontSize = 24.sp,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                // Selector de dificultad
                Text(
                    text = "Selecciona la dificultad:",
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(bottom = 20.dp)
                ) {
                    Button(
                        onClick = { controller.setDifficulty(Difficulty.EASY) },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = if (clickNumberController.currentDifficulty == Difficulty.EASY) Color.Green else Color.Gray
                        ),
                        shape = RoundedCornerShape(12.dp),
                        elevation = ButtonDefaults.elevation(4.dp),
                        border = BorderStroke(1.dp, Color(0xFFB0BEC5))
                    ) {
                        Text("Fácil", color = Color.White)
                    }
                    Button(
                        onClick = { controller.setDifficulty(Difficulty.MEDIUM) },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = if (clickNumberController.currentDifficulty == Difficulty.MEDIUM) Color.Yellow else Color.Gray
                        ),
                        shape = RoundedCornerShape(12.dp),
                        elevation = ButtonDefaults.elevation(4.dp),
                        border = BorderStroke(1.dp, Color(0xFFB0BEC5))
                    ) {
                        Text("Medio", color = Color.Black)
                    }
                    Button(
                        onClick = { controller.setDifficulty(Difficulty.HARD) },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = if (clickNumberController.currentDifficulty == Difficulty.HARD) Color.Red else Color.Gray
                        ),
                        shape = RoundedCornerShape(12.dp),
                        elevation = ButtonDefaults.elevation(4.dp),
                        border = BorderStroke(1.dp, Color(0xFFB0BEC5))
                    ) {
                        Text("Difícil", color = Color.White)
                    }
                }

                Button(
                    onClick = { clickNumberController.startGame() },
                    modifier = Modifier.padding(bottom = 20.dp)
                ) {
                    Text("Iniciar Juego")
                }
            } else {
                // UI del juego
                // Temporizador (formato MM:SS)
                val minutes = clickNumberController.timeLeft / 60
                val seconds = clickNumberController.timeLeft % 60
                Text(
                    text = "Tiempo restante: ${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}",
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                // Instrucción del juego
                Text(
                    text = clickNumberController.instructionMessage,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                // Mensaje de resultado
                Text(
                    text = clickNumberController.resultMessage,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                // Cuadrícula de botones
                if (!clickNumberController.gameOver && clickNumberController.numbers.isNotEmpty()) {
                    for (row in 0 until 4) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            for (col in 0 until 4) {
                                val index = row * 4 + col
                                val number = clickNumberController.numbers.getOrElse(index) { 0 }
                                Button(
                                    onClick = { clickNumberController.handleButtonClick(number) },
                                    modifier = Modifier.size(60.dp)
                                ) {
                                    Text(text = number.toString())
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        }

        // Ventana emergente cuando el juego termina
        if (clickNumberController.showGameOverDialog) {
            AlertDialog(
                onDismissRequest = { /* No permitir cerrar haciendo clic fuera */ },
                title = {
                    Text(
                        text = "¡Juego Terminado!",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                text = {
                    Text(
                        text = "Puntuación obtenida: ${clickNumberController.score}",
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                buttons = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = { clickNumberController.goHome() },
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Yellow),
                            shape = RoundedCornerShape(12.dp),
                            elevation = ButtonDefaults.elevation(4.dp),
                            border = BorderStroke(1.dp, Color(0xFFB0BEC5))
                        ) {
                            Text("Volver al Menú", color = Color.Black)
                        }
                        Button(
                            onClick = { clickNumberController.restartGame() },
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            elevation = ButtonDefaults.elevation(4.dp),
                            border = BorderStroke(1.dp, Color(0xFFB0BEC5))
                        ) {
                            Text("Jugar de Nuevo", color = Color.Black)
                        }
                    }
                },
                modifier = Modifier
                    .background(Color.White, shape = RoundedCornerShape(16.dp))
                    .padding(16.dp)
            )
            if (!sessionSaved) {
                sessionSaved = true
                scope.launch {
                    val userId = runCatching { UserSession.getUserId() }.getOrNull()
                    if (userId != null) {
                        GameSessionController.registerGameSession(
                            gameId = "ed29af2f-6346-4a55-bd4d-1b07c013d726", // ID único del juego ClickNumber
                            score = clickNumberController.score,
                            difficulty = clickNumberController.currentDifficulty.name
                        ).onSuccess {
                            println("✅ Partida guardada: ${clickNumberController.score} puntos.")
                        }.onFailure {
                            println("❌ Error al guardar la partida: ${it.message}")
                        }
                    } else {
                        println("⚠ Usuario no logueado, no se puede guardar la partida.")
                    }
                }
            }
        }
    }
}
@Composable
fun InstructionsClickNumber(onStart: () -> Unit, controller: clickNumberController) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Background image
        Image(
            painter = painterResource(Res.drawable.fondoMental),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Same HEADER as game screen (user icon, logo, settings)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .background(Color(0xFF222937), shape = RoundedCornerShape(4.dp))
                    .border(BorderStroke(2.dp, Color(0xFF4CE3E8)))
            ) {
                IconButton(
                    onClick = { /* Dummy user action */ },
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 8.dp)
                        .size(48.dp)
                ) {
                    Image(
                        painter = painterResource(Res.drawable.user),
                        contentDescription = "Usuario"
                    )
                }

                Image(
                    painter = painterResource(Res.drawable.logo),
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .align(Alignment.Center)
                        .scale(1.2f)
                )

                IconButton(
                    onClick = { LoginController.buttonSettings() },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 8.dp)
                        .size(48.dp)
                ) {
                    Image(
                        painter = painterResource(Res.drawable.ajustes),
                        contentDescription = "Ajustes"
                    )
                }
            }

            Spacer(modifier = Modifier.height(5.dp))
            // Back Button (same design as in game)
            Button(
                onClick = { controller.goHomeLogic() },
                modifier = Modifier
                    .align(Alignment.End)
                    .height(46.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.elevation(4.dp),
                border = BorderStroke(1.dp, Color(0xFFB0BEC5))
            ) {
                Text(Strings.t("back"), fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.Black)
            }

            Spacer(modifier = Modifier.height(100.dp))

            // Title of instruction section
            Text(
                Strings.t("instructions"),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121)
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Game explanation using styled text
            Box(
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .background(Color.White, shape = RoundedCornerShape(16.dp))
                    .padding(16.dp) // Padding interno
            ) {
                Text(
                    buildAnnotatedString {
                        append(Strings.t("insW1"))
                        withStyle(androidx.compose.ui.text.SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(Strings.t("insW2"))
                        }
                        append(Strings.t("insW3") + "\n\n")

                        append(Strings.t("insW4"))
                        withStyle(androidx.compose.ui.text.SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(Strings.t("insW5"))
                        }
                        append(" .\n")

                        append(Strings.t("insW6"))
                    },
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    color = Color.Black,
                    lineHeight = 24.sp
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Start game button
            Button(
                onClick = onStart,
                modifier = Modifier
                    .height(48.dp)
                    .width(200.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.elevation(4.dp),
                border = BorderStroke(1.dp, Color(0xFFB0BEC5))
            ) {
                Text(Strings.t("start_game"), fontWeight = FontWeight.Medium, color = Color.Black)
            }
        }
    }
}