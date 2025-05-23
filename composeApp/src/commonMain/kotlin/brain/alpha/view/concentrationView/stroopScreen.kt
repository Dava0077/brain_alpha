package brain.alpha.view.concentrationViews

// Required Compose and UI imports
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
 * Main game screen for the Stroop Test
 * Handles UI, game flow, difficulty logic, score saving, and instructions
 */

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun StroopScreen(controller: stroopController = stroopController) {

    // UI state bindings from the controller
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


    // Trigger score saving when game ends
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

    // Difficulty unlocking and progress tracking
    val gameId = "f68da1b6-6e34-422c-be12-579991dbbc54"
    val selectedDifficulty = remember { mutableStateOf(Difficulty.EASY) }
    val totalScore = remember { mutableStateOf(0) }

    // Load user's total neurons for unlocking difficulties
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

    // Resume prompt if a game is already running
    val showContinuePrompt = remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (controller.isOngoingGame && !isGameOver) {
            selectedDifficulty.value = controller.getCurrentDifficulty()
            showContinuePrompt.value = true
        }
    }


    // Show instruction screen if requested
    if (showInstructions) {
        StroopInstructionsScreen(onBack = { controller.showInstructions.value = false })
        return
    }

    // Main screen container
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
            // Top bar with profile and settings
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

            Spacer(modifier = Modifier.height(5.dp))
            // Display user's total neuron score
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


            // Show game-over dialog
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

            // Main gameplay column layout
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Adds space at the top
                Spacer(modifier = Modifier.height(16.dp))

                // Back button logic depending on current game state
                Button(
                    onClick = {
                        if (isGameStarted.value) {
                            // If game is running, stop and return to difficulty selection
                            isGameStarted.value = false
                            controller.stopGame()
                            controller.goBackToDifficultySelection()
                        } else {
                            // If not playing, go back to concentration home screen
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
                    // Label for back button
                    Text(Strings.t("back"), fontSize = 16.sp, fontWeight = FontWeight.Medium)
                }

                // Displayed if game is not currently running
                if (!isGameStarted.value) {
                    // Instruction prompt
                    Text(Strings.t("insST6"), fontSize = 18.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(8.dp))

                    // Define colors for each difficulty level
                    val diffColors = mapOf(
                        Difficulty.EASY to Color(0xFF81C784),   // Green
                        Difficulty.MEDIUM to Color(0xFF64B5F6), // Blue
                        Difficulty.HARD to Color(0xFFE57373)    // Red
                    )

                    // Determine currently unlocked difficulty level
                    val unlockedDifficulty = when {
                        totalScore.value >= 150 -> Difficulty.HARD
                        totalScore.value >= 50 -> Difficulty.MEDIUM
                        else -> Difficulty.EASY
                    }

                    // Row of difficulty buttons with visual status
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
                                // Difficulty level label with status icon
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

                    // Unlock message info block (with emoji and color-coded status)
                    val (icon, color, msg) = when {
                        totalScore.value < 50 -> Triple("🔒", Color.Red, "${50 - totalScore.value}"  + Strings.t("mm8"))
                        totalScore.value < 150 -> Triple("🔒", Color.Red, "${150 - totalScore.value}" + Strings.t("mm9"))
                        else -> Triple("🔓", Color.Green, Strings.t("mm10"))
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

                    // Start button
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

                    // Show instruction screen
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

                // Display the game title text
                Text(Strings.t("insST7"), fontSize = 24.sp, fontWeight = FontWeight.Bold)
                // Show remaining time in seconds
                Text(Strings.t("insST8") + "$timeLeft" + Strings.t("insST9"), fontSize = 16.sp, fontWeight = FontWeight.Bold)

                // Progress bar setup
                // Get the total time from the controller
                val totalTime by controller.totalTime
                // Calculate progress as a float value from 0 to 1
                val progress = timeLeft / totalTime.toFloat()

                // Render a linear progress bar to show remaining time visually
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .padding(horizontal = 32.dp),
                    backgroundColor = Color.LightGray,
                    color = Color(0xFF4CAF50) // Green progress color
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Word displayed in the color (Stroop challenge)
                // Use AnimatedContent for a transition effect when color or word changes
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
                    // Show feedback (Correct / Incorrect) if a previous answer was given
                    if (answerFeedback != null) {
                        LaunchedEffect(answerFeedback) {
                            delay(600) // Short delay before clearing feedback
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
                    // Render answer buttons for each color option
                    options.forEach { colorOption ->
                        val colorName = controller.getColorName(colorOption)

//                        val textColor = when (colorOption) {
//                            Color.Yellow, Color.Cyan -> Color.Black
//                            else -> Color.White
//                        }

                        // Generate a random color for the text label of the button
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

                // Display final score
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

    // On leaving screen, stop any active game session
    DisposableEffect(Unit) {
        onDispose {
            controller.stopGame()
            isGameStarted.value = false
        }
    }
}

// Instructions screen for the Stroop test game
@Composable
fun StroopInstructionsScreen(onBack: () -> Unit) {
    // Root container filling the screen
    Box(modifier = Modifier.fillMaxSize()) {
        // Background image for the screen
        Image(
            painter = painterResource(Res.drawable.fondoConcentracion),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Main layout column centered on screen
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Card that contains the instructions
            Card(
                elevation = 6.dp,
                shape = RoundedCornerShape(16.dp),
                backgroundColor = Color.White.copy(alpha = 0.9f),
                border = BorderStroke(1.dp, Color(0xFFB0BEC5)),
                modifier = Modifier.padding(12.dp)
            ) {
                // Inner column for card content
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Title text "Instructions"
                    Text(
                        Strings.t("instructions"),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212121)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Rich text instructions block
                    Text(
                        buildAnnotatedString {
                            append(Strings.t("insST20"))
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(Strings.t("insST21")) }
                            append(Strings.t("insST22"))
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF388E3C))) { append(Strings.t("insST23")) }
                            append(Strings.t("insST24"))
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F))) { append(Strings.t("insST25")) }
                            append(Strings.t("insST26"))
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF1565C0))) { append(Strings.t("insST27")) }
                        },
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        color = Color(0xFF424242),
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Button to start the game, triggering onBack callback
                    Button(
                        onClick = onBack,
                        modifier = Modifier
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
    }
}
