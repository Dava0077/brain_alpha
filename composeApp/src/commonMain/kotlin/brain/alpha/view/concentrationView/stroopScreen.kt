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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
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
 * Main composable for the Stroop Test game.
 * Manages instruction screen, game UI, color buttons, timer, score, and feedback.
 */

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun StroopScreen(controller: stroopController = stroopController,onNavigateToHome: () -> Unit,) {

    // State bindings from controller
    val timeLeft by controller.timeLeft
    val colorWord by controller.currentColorWord
    val displayColor by controller.colorToShow
    val score by controller.score
    val isGameOver by controller.showResult
    val answerFeedback by controller.answerFeedback
    val options = controller.options
    val showInstructions by controller.showInstructions


    // Show instructions first
    if (showInstructions) {
        InstructionsScreen(onStart = { controller.beginGame() }, controller = stroopController)
        return
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background image
        Image(
            painter = painterResource(Res.drawable.stroopTestBG),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Top header bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .background(Color(0xFFEFEFEF), shape = RoundedCornerShape(4.dp))
                    .border(BorderStroke(2.dp, Color(0xFFB0BEC5))))
            {

                IconButton(
                    onClick = { /* future user action */ },
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
                    onClick = { /* future settings action */ },
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
            Spacer(modifier = Modifier.height(5.dp)) // Espacio tras el logo

            Spacer(modifier = Modifier.height(30.dp))


            // End-game dialog
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

                // Back button
                Button(
                    onClick = { controller.goHome() },
                    modifier = Modifier
                        .padding(8.dp)
                        .height(46.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.White,
                        contentColor = Color(0xFF455A64)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.elevation(4.dp),
                    border = BorderStroke(1.dp, Color(0xFFB0BEC5))
                ) {
                    Text("⬅ Volver", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                }

                // Game title and timer
                Text("Prueba de Stroop", fontSize = 24.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Cursive)
                Text("Tiempo restante: $timeLeft s", fontSize = 16.sp, fontWeight = FontWeight.Bold)

                // Progress bar
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

                // Central word shown in color
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
                    // Feedback for last answer
                    if (answerFeedback != null) {
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
                                fontSize = 20.sp,
                                style = LocalTextStyle.current.copy(
                                    shadow = Shadow(Color.Black, offset = Offset(1f, 1f), blurRadius = 2f)
                                )
                            )
                        }
                    }
                    // Color answer options
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

                Spacer(modifier = Modifier.height(25.dp))

                // Score counter
                Text("Puntuación: $score", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            }

        }

    }
}

@Composable
fun InstructionsScreen(onStart: () -> Unit, controller: stroopController) {
    Box(modifier = Modifier.fillMaxSize()) {

        // Background image
        Image(
            painter = painterResource(Res.drawable.stroopTestBG),
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
                    .background(Color(0xFFEFEFEF), shape = RoundedCornerShape(4.dp))
                    .border(BorderStroke(2.dp, Color(0xFFB0BEC5)))
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
                    onClick = { /* Dummy settings action */ },
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

            Spacer(modifier = Modifier.height(50.dp))

            // Back Button (same design as in game)
            Button(
                onClick = { controller.goHome() },
                modifier = Modifier
                    .padding(bottom = 24.dp)
                    .height(46.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.White,
                    contentColor = Color(0xFF455A64)
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.elevation(4.dp),
                border = BorderStroke(1.dp, Color(0xFFB0BEC5))
            ) {
                Text("⬅ Volver", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(100.dp))

            // Title of instruction section
            Text(
                "Instrucciones",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121)
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Game explanation using styled text
            Text(
                buildAnnotatedString {
                    append("Debes seleccionar el ")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("color") }
                    append(" en el que está escrita la palabra, no el nombre que aparece.\n\n")

                    append("Tienes ")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("30 segundos") }
                    append(" para acertar la mayor cantidad posible.\n")

                    append("Concéntrate, ¡y buena suerte!")
                },
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = Color(0xFF424242),
                lineHeight = 24.sp,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Start game button
            Button(
                onClick = onStart,
                modifier = Modifier
                    .height(48.dp)
                    .width(200.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.White,
                    contentColor = Color(0xFF455A64)
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.elevation(4.dp),
                border = BorderStroke(1.dp, Color(0xFFB0BEC5))
            ) {
                Text("Empezar juego", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}




