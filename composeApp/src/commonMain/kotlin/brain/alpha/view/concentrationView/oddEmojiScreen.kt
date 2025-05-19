// File: brain.alpha.view.concentrationViews.oddEmojiScreen.kt

package brain.alpha.view.concentrationViews

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import brain.alpha.controller.GameSessionController
import brain.alpha.controller.LoginController
import brain.alpha.controller.concentrationController.oddEmojiController
import brain.alpha.model.UserSession
import brain.alpha.model.shared.Difficulty
import brain.alpha.strings.Strings
import brainalpha.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource


// Composable function to render the Odd Emoji game screen
@Composable
fun OddEmojiScreen(controller: oddEmojiController = oddEmojiController) {

    // Game state variables bound to controller
    // Current emoji grid layout
    val grid by controller.grid
    // Current game level
    val level by controller.level
    // Number of user mistakes
    val errors by controller.errors
    // Game over flag
    val isGameOver by controller.isGameOver
    // Flag to show instruction screen
    val showInstructions by controller.showInstructions
    // Current score
    val score by controller.score
    // Remaining time in seconds
    val timeLeft by controller.timeLeft
    // Progress for timer bar (0.0 to 1.0)
    val timeProgress = controller.getTimeProgress()

    // Dialog and game session state
    // Game over dialog visibility
    val showEndDialog = remember { mutableStateOf(false) }
    // Indicates if game has begun
    val isGameStarted = remember { mutableStateOf(false) }
    // Prevents multiple session saves
    val sessionSaved = remember { mutableStateOf(false) }

    // Difficulty selection and user score total
    val selectedDifficulty = remember { mutableStateOf(Difficulty.EASY) }
    val totalScore = remember { mutableStateOf(0) }

    // Unique game ID for database tracking
    val gameId = "498d59a6-7136-4676-9075-93d115d0395f"

    // Refresh total neuron score for difficulty unlocks
    suspend fun refreshTotalScore() {
        val userId = UserSession.userId.value
        if (!UserSession.isGuest.value && userId != null) {
            val total = GameSessionController.getTotalScore(
                userId = userId,
                gameId = gameId,
                supabase = brain.alpha.services.SupabaseClientProvider.client
            )
            totalScore.value = total
            selectedDifficulty.value = when (total) {
                in 0..499 -> Difficulty.EASY
                in 500..1499 -> Difficulty.MEDIUM
                else -> Difficulty.HARD
            }
        }
    }

    // Load user neuron score when screen launches
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
                in 0..499 -> Difficulty.EASY
                in 500..1499 -> Difficulty.MEDIUM
                else -> Difficulty.HARD
            }
        }
    }


    // Open end dialog if game finishes
    LaunchedEffect(isGameOver) {
        if (isGameOver) showEndDialog.value = true
    }

    // Show instructions screen if toggled
    if (showInstructions) {
        OddInstructionsScreen (onBack = {controller.showInstructions.value = false })
        return
    }

    // Root layout container for the game screen
    Box(modifier = Modifier.fillMaxSize()) {

        // Fullscreen background image for visual theme
        Image(
            painter = painterResource(Res.drawable.fondoConcentracion),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Vertical layout container for game elements
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            // Top header bar containing navigation and logo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .background(Color(0xFFEFEFEF), RoundedCornerShape(4.dp))
                    .border(BorderStroke(2.dp, Color(0xFFB0BEC5)))
            ) {
                // Profile navigation button on left
                IconButton(
                    onClick = { LoginController.navigateTo("profile") },
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 8.dp)
                        .size(48.dp)
                ) {
                    Image(painter = painterResource(Res.drawable.user), contentDescription = "User")
                }

                // Center logo
                Image(
                    painter = painterResource(Res.drawable.logo),
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .align(Alignment.Center)
                        .scale(1.2f)
                )

                // Settings navigation button on right
                IconButton(
                    onClick = { LoginController.navigateTo("settings") },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 8.dp)
                        .size(48.dp)
                ) {
                    Image(painter = painterResource(Res.drawable.ajustes), contentDescription = "Settings")
                }
            }

            // Spacer below top bar
            Spacer(modifier = Modifier.height(5.dp))

            // Additional vertical spacing
            Spacer(modifier = Modifier.height(30.dp))

            // Display total neurons collected by user
            Row(
                verticalAlignment = Alignment.CenterVertically,
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

            // Main content layout for game flow
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Back button: handles exit logic depending on game state
                Button(
                    onClick = {
                        if (isGameStarted.value) {
                            isGameStarted.value = false
                            controller.stopGame()
                            controller.backToDifficultyMenu()
                        } else {
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

                // Show difficulty selection if game has not started yet
                if (!isGameStarted.value) {
                    // Instructional message above difficulty selection
                    Text(Strings.t("insST6"), fontSize = 18.sp, fontWeight = FontWeight.Medium)

                    Spacer(modifier = Modifier.height(8.dp))

                    // Define background colors for each difficulty level
                    val diffColors = mapOf(
                        Difficulty.EASY to Color(0xFF81C784),
                        Difficulty.MEDIUM to Color(0xFF64B5F6),
                        Difficulty.HARD to Color(0xFFE57373)
                    )

                    // Determine the highest unlocked difficulty based on total score
                    val unlockedDifficulty = when {
                        totalScore.value >= 3000 -> Difficulty.HARD
                        totalScore.value >= 1500 -> Difficulty.MEDIUM
                        else -> Difficulty.EASY
                    }

                    // Render difficulty buttons horizontally
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Difficulty.values().forEach { diff ->
                            val isUnlocked = diff.ordinal <= unlockedDifficulty.ordinal
                            val isSelected = selectedDifficulty.value == diff
                            val bgColor = diffColors[diff] ?: Color.LightGray
                            val textColor = if (isUnlocked) Color.White else Color.Gray
                            val icon = if (isUnlocked) "⚡" else "🔒"

                            // Button for each difficulty
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

                    Spacer(modifier = Modifier.height(16.dp))

                    // Show message about how many neurons are needed to unlock next difficulty
                    val (icon, color, msg) = when {
                        totalScore.value < 1500 -> Triple("🔒", Color.Red, "${1500 - totalScore.value}" +  Strings.t("mm8"))
                        totalScore.value < 3000 -> Triple("🔒", Color.Red, "${3000 - totalScore.value}" + Strings.t("mm9"))
                        else -> Triple("🔓", Color.Green, Strings.t("mm10"))
                    }

                    // Message row showing current unlock status
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

                    // Button to start the game
                    Button(onClick = {
                        controller.startGame(selectedDifficulty.value)
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

                    // Button to show instructions
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
                    // Exit early from composable column if game hasn't started
                    return@Column
                }

                // Show title indicating the game is running
                Text(Strings.t("insOS4"), fontSize = 24.sp, fontWeight = FontWeight.Bold)

                // Show time left in seconds with styled text
                Text(Strings.t("insST8") + "$timeLeft" + Strings.t("insST9"), fontSize = 18.sp, color = Color(0xFF1565C0))

                // Progress bar visualizing remaining time
                LinearProgressIndicator(
                    progress = timeProgress,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(12.dp)
                        .padding(top = 4.dp),
                    color = Color(0xFF4CAF50),
                    backgroundColor = Color.LightGray
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Iterate over emoji grid rows
                grid.forEachIndexed { rowIndex, row ->
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Iterate over each emoji in the row
                        row.forEachIndexed { colIndex, emoji ->
                            Text(
                                text = emoji,
                                fontSize = 36.sp,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .clickable {
                                        // Handle emoji selection by passing coordinates to controller
                                        controller.checkSelection(rowIndex, colIndex)
                                    }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Display current level
                Text(Strings.t("insOS1") + "$level", fontSize = 18.sp)
                // Display number of errors with red text
                Text(Strings.t("insOS2") + "$errors", fontSize = 18.sp, color = Color.Red)
                // Display score in green color
                Text(Strings.t("insOS3") + "$score", fontSize = 18.sp, color = Color(0xFF2E7D32))

                Spacer(modifier = Modifier.height(24.dp))

                // Button to reset the game state and return to difficulty selection
                Button(onClick = {
                    controller.resetGame()
                    isGameStarted.value = false
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
                    Text(Strings.t("insST5"))
                }

                // Show end-of-game dialog if the flag is set
                if (showEndDialog.value) {
                    AlertDialog(
                        onDismissRequest = {},
                        // Dialog title centered horizontally
                        title = {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            )
                            {
                                Text(Strings.t("insST1"), fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            }
                        },
                        // Dialog body showing level, errors, average time, and final score
                        text = {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(Strings.t("insOS5") + "$level", fontWeight = FontWeight.Bold)
                                Text(Strings.t("insOS6") + "$errors", fontWeight = FontWeight.Bold)
                                Text(
                                    Strings.t("insOS7") + "${controller.getAverageTime()} ms",
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    Strings.t("insOS8") + "${controller.getFinalScore()}",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        },
                        // Confirm button to play again
                        confirmButton = {
                            TextButton(onClick = {
                                controller.resetGame()
                                isGameStarted.value = false
                                showEndDialog.value = false
                            }) {
                                Text(Strings.t("insST4"))
                            }
                        },
                        // Dismiss button to return to difficulty selection
                        dismissButton = {
                            TextButton(onClick = {
                                controller.backToDifficultyMenu()
                                isGameStarted.value = false
                                showEndDialog.value = false
                            }) {
                                Text(Strings.t("insST5"))
                            }
                        },
                        // Rounded corners for dialog
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }


            // Save session once when the dialog is shown
            LaunchedEffect(showEndDialog.value) {
                if (showEndDialog.value && !sessionSaved.value) {
                    sessionSaved.value = true

                    // Try to get the logged-in user ID
                    val userId = runCatching { UserSession.getUserId() }.getOrNull()

                    if (userId != null) {
                        // Collect session data to register
                        val score = controller.getFinalScore()
                        val avgTime = controller.getAverageTime().toDouble()
                        val difficulty = selectedDifficulty.value.name

                        // Attempt to register the game session
                        GameSessionController.registerGameSession(
                            gameId = "498d59a6-7136-4676-9075-93d115d0395f",
                            difficulty = difficulty,
                            score = score,
                            timeAvg = avgTime
                        ).onSuccess {
                            println("✅ Sesión guardada: score=$score, avgTime=$avgTime, difficulty=$difficulty")
                            // Refresh score to unlock levels
                            refreshTotalScore()
                        }.onFailure {
                            println("❌ Error al guardar sesión: ${it.message}")
                        }
                    } else {
                        println("⚠ Usuario no logueado, sesión no guardada.")
                    }


                }
            }
        }
    }
}

// Composable for displaying Odd Emoji game instructions
@Composable
fun OddInstructionsScreen(onBack: () -> Unit) {
    // Root layout that fills the screen
    Box(modifier = Modifier.fillMaxSize()) {
        // Background image for the instructions screen
        Image(
            painter = painterResource(Res.drawable.fondoConcentracion),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Main column layout centered vertically and horizontally
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Card with instructions content
            Card(
                elevation = 6.dp,
                shape = RoundedCornerShape(16.dp),
                backgroundColor = Color.White.copy(alpha = 0.9f),
                border = BorderStroke(1.dp, Color(0xFFB0BEC5)),
                modifier = Modifier.padding(12.dp)
            ) {
                // Inner content layout inside the card
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Title text for instructions
                    Text(
                        Strings.t("instructions"),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212121)
                    )

                    // Spacer between title and body
                    Spacer(modifier = Modifier.height(24.dp))

                    // Paragraph explaining the rules using styled spans
                    Text(
                        buildAnnotatedString {
                            append(Strings.t("insOE1"))
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(Strings.t("insOE2"))
                            }
                            append(Strings.t("insOE3"))
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF388E3C))) {
                                append(Strings.t("insOE4"))
                            }
                            append(Strings.t("insOE5"))
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F))) {
                                append(Strings.t("insOE6"))
                            }
                            append(Strings.t("insOE7"))
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF1565C0))) {
                                append(Strings.t("insOE8"))
                            }
                        },
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        color = Color(0xFF424242),
                        lineHeight = 24.sp,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )

                    // Spacer before the start button
                    Spacer(modifier = Modifier.height(32.dp))

                    // Button to go back or start the game
                    Button(
                        onClick = onBack,
                        modifier = Modifier.height(46.dp),
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
