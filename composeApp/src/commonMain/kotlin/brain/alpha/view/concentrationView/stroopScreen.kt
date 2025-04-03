package brain.alpha.view.concentrationViews

import androidx.compose.animation.*
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import brain.alpha.controller.concentrationController.stroopController
import brainalpha.composeapp.generated.resources.*
import brainalpha.composeapp.generated.resources.Res
import brainalpha.composeapp.generated.resources.logo
import brainalpha.composeapp.generated.resources.memoryFondo
import brainalpha.composeapp.generated.resources.user
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource

/**
 * StroopScreen.kt
 *
 * Main UI for the Stroop Test. Displays game or instructions.
 */

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun StroopScreen(controller: stroopController = stroopController,onNavigateToHome: () -> Unit,) {

    val timeLeft by controller.timeLeft
    val colorWord by controller.currentColorWord
    val displayColor by controller.colorToShow
    val score by controller.score
    val isGameOver by controller.showResult
    val answerFeedback by controller.answerFeedback
    val options = controller.options
    val showInstructions by controller.showInstructions

    if (showInstructions) {
        InstructionsScreen(onStart = { controller.beginGame() }, controller = stroopController)
        return
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(Res.drawable.memoryFondo),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .background(Color(0xFF222937), shape = RoundedCornerShape(4.dp))
                    .border(BorderStroke(2.dp, Color(0xFF4CE3E8))))
            {

                IconButton(
                    onClick = { /* Dummy action */ },
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 8.dp)
                        .size(55.dp)
                ) {
                    Image(
                        painter = painterResource(Res.drawable.user),
                        contentDescription = "User"
                    )
                }
                Image(
                    painter = org.jetbrains.compose.resources.painterResource(Res.drawable.logo),
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
                    Image(
                        painter = painterResource(Res.drawable.ajustes),
                        contentDescription = "Settings"
                    )
                }

            }
            Spacer(modifier = Modifier.height(5.dp)) // Espacio tras el logo

            Spacer(modifier = Modifier.height(30.dp))


            if (isGameOver) {
                AlertDialog(
                    onDismissRequest = {},
                    title = {
                        Text(
                            text = "¡Juego terminado!",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0647D7)
                        )
                    },
                    text = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Puntuación final:",
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "$score puntos",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF388E3C)
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { controller.beginGame() }) {
                            Text("Volver a jugar", color = Color(0xFF0647D7))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { controller.goHome() }) {
                            Text("Volver al inicio", color = Color(0xFF0647D7))
                        }
                    },
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { controller.goHome() },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF0647D7)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("⬅ Volver", color = Color.White)
                }

                Text("Prueba de Stroop", fontSize = 24.sp, fontWeight = FontWeight.Bold)

                Text("Tiempo restante: $timeLeft s", fontSize = 16.sp)

                // Calcula progreso (0.0 - 1.0)
                val totalTime = 30f
                val progress = timeLeft / totalTime

                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .padding(horizontal = 32.dp),
                    backgroundColor = Color.LightGray,
                    color = Color(0xFF4CAF50) // verde
                )

                Spacer(modifier = Modifier.height(32.dp))

                AnimatedContent(
                    targetState = Pair(colorWord, displayColor),
                    transitionSpec = {
                        fadeIn() + scaleIn() with fadeOut() + scaleOut()
                    },
                    label = "colorChangeAnimation"
                ) { (word, color) ->
                    Text(
                        text = word.uppercase(),
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = color,
                        style = TextStyle(
                            shadow = Shadow(color = Color.Black, offset = Offset(2f, 2f), blurRadius = 4f)
                        )
                    )
                }
//----------------------------------------------------------------
//  Classic representation word
//                Text(
//                    text = colorWord.uppercase(),
//                    fontSize = 48.sp,
//                    fontWeight = FontWeight.Bold,
//                    color = displayColor,
//                    style = TextStyle(shadow = Shadow(color = Color.Black, offset = Offset(2f, 2f), blurRadius = 4f))
//                )
//-----------------------------------------------------------------

                Spacer(modifier = Modifier.height(32.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AnimatedVisibility(
                        visible = answerFeedback != null,
                        enter = fadeIn() + slideInVertically(),
                        exit = fadeOut() + slideOutVertically()
                    ) {
                        LaunchedEffect(answerFeedback) {
                            delay(600)
                            controller.clearFeedback()
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (answerFeedback == true) "¡Correcto!" else "Incorrecto",
                                color = if (answerFeedback == true) Color.Green else Color.Red,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        }
                    }
                    options.forEach { colorOption ->
                        val colorName = controller.getColorName(colorOption)

                        val textColor = when (colorOption) {
                            Color.Yellow, Color.Cyan -> Color.Black
                            else -> Color.White
                        }

                        Button(
                            onClick = { controller.checkAnswer(colorOption) },
                            enabled = !controller.isAnswering,
                            modifier = Modifier
                                .width(200.dp)
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = colorOption,
                                contentColor = textColor
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = colorName.uppercase(),
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                textAlign = TextAlign.Center,
                                color = textColor,
                                style = LocalTextStyle.current.copy(
                                    shadow = Shadow(Color.Black, offset = Offset(1f, 1f), blurRadius = 2f)
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text("Puntuación: $score", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            }

        }

    }
}

@Composable
fun InstructionsScreen(onStart: () -> Unit, controller: stroopController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Button(
            onClick = {controller.goHome()},
            modifier = Modifier.align(Alignment.End),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF0647D7)),
        ){
            Text("INICIO", color = Color.White)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text("Instrucciones", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Debes seleccionar el color en que está escrita la palabra, no el nombre que dice. Tienes 30 segundos para acertar el mayor número posible.",
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onStart) {
            Text("Empezar juego")
        }
    }
}
