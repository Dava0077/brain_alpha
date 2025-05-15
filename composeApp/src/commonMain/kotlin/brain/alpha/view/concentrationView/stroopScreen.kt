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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import brain.alpha.controller.GameSessionController
import brain.alpha.controller.LoginController
import brain.alpha.controller.concentrationController.stroopController
import brain.alpha.model.UserSession
import brain.alpha.model.shared.Difficulty
import brain.alpha.strings.Strings
import brainalpha.composeapp.generated.resources.*
import brainalpha.composeapp.generated.resources.Res
import brainalpha.composeapp.generated.resources.logo
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
fun StroopScreen(controller: stroopController = stroopController) {

    // State bindings from controller
    val timeLeft by controller.timeLeft
    val colorWord by controller.currentColorWord
    val displayColor by controller.colorToShow
    val score by controller.score
    val isGameOver by controller.showResult
    val answerFeedback by controller.answerFeedback
    val options = controller.options
    val showInstructions by controller.showInstructions
    val isGameStarted = remember { mutableStateOf(false) }

    val showEndDialog = remember { mutableStateOf(false) }
    var sessionSaved by remember { mutableStateOf(false) }


    LaunchedEffect(isGameOver) {
        if (isGameOver) {
            showEndDialog.value = true

            if (!sessionSaved) {
                sessionSaved = true
                val userId = runCatching { UserSession.getUserId() }.getOrNull()
                if (userId != null) {
                    val difficulty = controller.getCurrentDifficulty()
                    GameSessionController.registerGameSession(
                        gameId = "f68da1b6-6e34-422c-be12-579991dbbc54", // UUID del StroopGame
                        score = score,
                        difficulty = difficulty.name,
                        timeLeft = timeLeft.toDouble()
                    ).onSuccess {
                        println("✅ Partida Stroop guardada: Score $score | Dificultad $difficulty | TimeLeft $timeLeft")
                    }.onFailure {
                        println("❌ Error guardando Stroop: ${it.message}")
                    }
                } else {
                    println("⚠ Usuario no logueado, partida no guardada")
                }
            }
        }
    }

    val gameId = "f68da1b6-6e34-422c-be12-579991dbbc54"
    val selectedDifficulty = remember { mutableStateOf(Difficulty.EASY) }
    val totalScore = remember { mutableStateOf(0) }

    LaunchedEffect(UserSession.userId.value) {
        val userId = UserSession.userId.value
        if (!UserSession.isGuest.value && userId != null) {
            val total = GameSessionController.getTotalScore(
                userId = userId,
                gameId = gameId,
                supabase = brain.alpha.services.SupabaseClientProvider.client
            )
            totalScore.value = total

            selectedDifficulty.value = when (total) {
                in 0..49 -> Difficulty.EASY
                in 50..149 -> Difficulty.MEDIUM
                else -> Difficulty.HARD
            }
            controller.setDifficulty(selectedDifficulty.value)
        }
    }

    val showContinuePrompt = remember { mutableStateOf(false) }


    LaunchedEffect(Unit) {
        if (controller.isOngoingGame && !isGameOver) {
            selectedDifficulty.value = controller.getCurrentDifficulty()
            showContinuePrompt.value = true
        }
    }


    // Show instructions
    if (showInstructions) {
        StroopInstructionsScreen(onBack = { controller.showInstructions.value = false })
        return
    }





    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background image
        Image(
            painter = painterResource(Res.drawable.fondoConcentracion),
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
                    .border(BorderStroke(2.dp, Color(0xFFB0BEC5)))
            ) {

                IconButton(
                    onClick = { LoginController.navigateTo("profile") },
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
                    onClick = { LoginController.navigateTo("settings") },
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

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .height(46.dp)
                    .background(Color.White, shape = RoundedCornerShape(12.dp))
                    .border(BorderStroke(1.dp, Color.Black), shape = RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "${totalScore.value} " + Strings.t("neurons"),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            }


            // End-game dialog
            if (showEndDialog.value) {
                AlertDialog(
                    onDismissRequest = {},
                    title = {
                        Text(
                            text = Strings.t("insST1"),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0647D7)
                        )
                    },
                    text = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = Strings.t("insST2"),
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "$score" + Strings.t("insST3"),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF388E3C)
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            controller.stopGame()
                            controller.beginGame()
                            isGameStarted.value = true
                            showEndDialog.value = false
                        }) {
                            Text(Strings.t("insST4"), color = Color(0xFF0647D7))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            controller.goBackToDifficultySelection()
                            isGameStarted.value = false
                            showEndDialog.value = false
                        }) {
                            Text(Strings.t("insST5"), color = Color(0xFF0647D7))
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
                    onClick = {
                        if (isGameStarted.value) {
                            // Si el juego está corriendo, simplemente vuelve a la selección de dificultad
                            isGameStarted.value = false
                            controller.stopGame()
                            controller.goBackToDifficultySelection()
                        } else {
                            // Si no hay juego en curso, vuelve a la pantalla principal de concentración
                            controller.goBackToConcentrationHome()
                        }
                    },
                    modifier = Modifier
                        .padding(8.dp)
                        .height(46.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.White,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.elevation(4.dp),
                    border = BorderStroke(1.dp, Color.Black)
                ) {
                    Text(Strings.t("back"), fontSize = 16.sp, fontWeight = FontWeight.Medium)
                }

                if (!isGameStarted.value) {
                    Text(Strings.t("insST6"), fontSize = 18.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(8.dp))

                    val diffColors = mapOf(
                        Difficulty.EASY to Color(0xFF81C784),   // Verde
                        Difficulty.MEDIUM to Color(0xFF64B5F6), // Azul
                        Difficulty.HARD to Color(0xFFE57373)    // Rojo
                    )

                    val unlockedDifficulty = when {
                        totalScore.value >= 150 -> Difficulty.HARD
                        totalScore.value >= 50 -> Difficulty.MEDIUM
                        else -> Difficulty.EASY
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Difficulty.values().forEach { diff ->
                            val isUnlocked = diff.ordinal <= unlockedDifficulty.ordinal
                            val isSelected = selectedDifficulty.value == diff
                            val bgColor = diffColors[diff] ?: Color.LightGray
                            val textColor = if (isUnlocked) Color.White else Color.Gray
                            val icon = if (isUnlocked) "⚡" else "🔒"

                            Button(
                                onClick = { if (isUnlocked) selectedDifficulty.value = diff },
                                enabled = isUnlocked,
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = if (isSelected) bgColor else bgColor.copy(alpha = 0.6f),
                                    contentColor = textColor
                                ),
                                shape = RoundedCornerShape(10.dp),
                                elevation = ButtonDefaults.elevation(2.dp)
                            ) {
                                Text("$icon ${diff.name}")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

//                    val nextUnlockMsg = when {
//                        totalScore.value < 50 -> "${50 - totalScore.value} neuronas para desbloquear MEDIUM"
//                        totalScore.value < 150 -> "${150 - totalScore.value} neuronas para desbloquear HARD"
//                        else -> "Todas las dificultades desbloqueadas"
//                    }
//
//                    Text(nextUnlockMsg, fontSize = 14.sp, color = Color.DarkGray)

                    val (icon, color, msg) = when {
                        totalScore.value < 50 -> Triple("🔒", Color.Red, "${50 - totalScore.value} neuronas para desbloquear MEDIUM")
                        totalScore.value < 150 -> Triple("🔒", Color.Red, "${150 - totalScore.value} neuronas para desbloquear HARD")
                        else -> Triple("🔓", Color.Green, "Todas las dificultades desbloqueadas")
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = icon,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(end = 6.dp)
                        )
                        Text(
                            text = msg,
                            fontSize = 14.sp,
                            color = color,
                            fontWeight = FontWeight.Medium
                        )
                    }




                    Spacer(modifier = Modifier.height(16.dp))

                    Button(onClick = {
                        controller.setDifficulty(selectedDifficulty.value)
                        controller.beginGame()
                        isGameStarted.value = true
                    },
                        modifier = Modifier
                            .padding(8.dp)
                            .height(46.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.White,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(12.dp),
                        elevation = ButtonDefaults.elevation(4.dp),
                        border = BorderStroke(1.dp, Color.Black)
                    ) {
                        Text(Strings.t("start_game"))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(onClick = {
                        controller.showInstructions.value = true
                    },
                        modifier = Modifier
                            .padding(8.dp)
                            .height(46.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.White,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(12.dp),
                        elevation = ButtonDefaults.elevation(4.dp),
                        border = BorderStroke(1.dp, Color.Black)

                    ) {
                        Text(Strings.t("instructions"))
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    return@Column
                }



                // Game title and timer
                Text(Strings.t("insST7"), fontSize = 24.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Cursive)
                Text(Strings.t("insST8") + "$timeLeft" + Strings.t("insST9"), fontSize = 16.sp, fontWeight = FontWeight.Bold)

                // Progress bar
                val totalTime by controller.totalTime
                val progress = timeLeft / totalTime.toFloat()

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
                                text = if (answerFeedback == true) Strings.t("insST10") else Strings.t("insST11"),
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

//                        val textColor = when (colorOption) {
//                            Color.Yellow, Color.Cyan -> Color.Black
//                            else -> Color.White
//                        }

                        // Genera un color aleatorio para el texto (incluyendo negro)
                        val randomTextColor = listOf(Color.Black, Color.Red, Color.Blue, Color.Green, Color.DarkGray).random()

                        Button(
                            onClick = { controller.checkAnswer(colorOption) },
                            enabled = !controller.isAnswering,
                            modifier = Modifier
                                .width(200.dp)
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color.White,
                                contentColor = randomTextColor
                            ),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color.Gray)
                        ) {
                            Text(
                                text = colorName.uppercase(),
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                textAlign = TextAlign.Center,
                                color = randomTextColor,
                                style = LocalTextStyle.current.copy(
                                    shadow = Shadow(Color.LightGray, offset = Offset(1f, 1f), blurRadius = 2f)
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(25.dp))

                // Score counter
                Text(Strings.t("insST12") + "$score", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            }

        }

    }

//    if (showContinuePrompt.value) {
//        AlertDialog(
//            onDismissRequest = { showContinuePrompt.value = false },
//            title = { Text("¿Continuar partida?") },
//            text = { Text("Tienes una partida en curso. ¿Deseas continuarla o reiniciar desde cero?") },
//            confirmButton = {
//                TextButton(onClick = {
//                    showContinuePrompt.value = false
//                    controller.continueGame()         // ⏱️ Reactiva el timer
//                    isGameStarted.value = true
//                }) {
//                    Text("Continuar")
//                }
//            },
//            dismissButton = {
//                TextButton(onClick = {
//                    showContinuePrompt.value = false
//                    isGameStarted.value = false
//                    controller.beginGame() // Nueva partida
//                }) {
//                    Text("Reiniciar")
//                }
//            }
//        )
//    }

    DisposableEffect(Unit) {
        onDispose {
            controller.stopGame() // ⛔ Detiene el timer en background
            isGameStarted.value = false // 🔄 Resetea el estado del juego local
        }
    }
}

@Composable
fun StroopInstructionsScreen(onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {

        // Background image
        Image(
            painter = painterResource(Res.drawable.fondoConcentracion),
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
                    onClick = { LoginController.navigateTo("profile") },
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
                    onClick = { LoginController.navigateTo("settings") },
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
            Text(
                buildAnnotatedString {
                    append(Strings.t("insST13"))
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(Strings.t("insST14")) }
                    append(Strings.t("insST15"))

                    append(Strings.t("insST16"))
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(Strings.t("insST17")) }
                    append(Strings.t("insST18"))

                    append(Strings.t("insST19"))
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
                onClick = onBack,
                modifier = Modifier
                    .padding(8.dp)
                    .height(46.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.White,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.elevation(4.dp),
                border = BorderStroke(1.dp, Color.Black)
            ) {
                Text(Strings.t("start_game"), fontWeight = FontWeight.SemiBold)
            }
        }
    }
}




