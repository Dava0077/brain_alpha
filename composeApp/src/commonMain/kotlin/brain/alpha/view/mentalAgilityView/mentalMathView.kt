// File: view/mentalMathView.kt

package view

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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import brain.alpha.controller.GameSessionController
import brain.alpha.controller.LoginController
import brain.alpha.controller.mentalAgilityController.mentalMathController
import brain.alpha.model.UserSession
import brain.alpha.model.shared.Difficulty
import brain.alpha.strings.Strings
import brain.alpha.utils.currentTimeMillis
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import brainalpha.composeapp.generated.resources.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun mentalMathView(
    controller: mentalMathController,
    onNavigateToHome: () -> Unit
) {

    // Define all state variables for UI reactivity
    var currentQuestion by remember { mutableStateOf<model.mentalMathModel.Question?>(null) }
    var userAnswer by remember { mutableStateOf("") }
    var feedback by remember { mutableStateOf<String?>(null) }
    var isCorrect by remember { mutableStateOf<Boolean?>(null) }
    var gameStarted by remember { mutableStateOf(false) }
    var showInstructions by remember { mutableStateOf(false) }
    var startTime by remember { mutableStateOf(0L) }
    var timeLeft by remember { mutableStateOf(0) }
    val showEndDialog = remember { mutableStateOf(false) }
    val sessionSaved = remember { mutableStateOf(false) }

    // Game session identifiers and difficulty selection
    val gameId = "f017777c-f246-4770-bb73-a652a23e761d"
    val totalScore = remember { mutableStateOf(0) }
    val selectedDifficulty = remember { mutableStateOf(Difficulty.EASY) }
    val coroutineScope = rememberCoroutineScope()
    val latestGameOver by rememberUpdatedState(controller.isGameOver())
    val latestSessionSaved by rememberUpdatedState(sessionSaved.value)

    // Function to save game session if not already saved
    fun saveSessionIfNeeded() {
        if (!sessionSaved.value) {
            sessionSaved.value = true
            val userId = runCatching { UserSession.getUserId() }.getOrNull()
            if (userId != null) {
                coroutineScope.launch {
                    kotlin.runCatching {
                        withContext(Dispatchers.Default) {
                            GameSessionController.registerGameSession(
                                gameId = gameId,
                                difficulty = selectedDifficulty.value.name,
                                score = controller.getScore(),
                                timeAvg = (controller.getTimeLimit().toDouble() / controller.getTotalQuestions())
                            )
                        }
                    }.onSuccess {
                        val total = GameSessionController.getTotalScore(
                            userId = userId,
                            gameId = gameId,
                            supabase = brain.alpha.services.SupabaseClientProvider.client
                        )
                        totalScore.value = total
                    }.onFailure {
                        println("❌ Falló guardado: ${it.message}")
                    }
                }
            }
        }
    }


    // Load user data and calculate difficulty
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

    // Trigger end game dialog if game is over
    LaunchedEffect(latestGameOver) {
        if (latestGameOver && !latestSessionSaved) {
            saveSessionIfNeeded()
            delay(500) // da tiempo a que el diálogo se muestre
            showEndDialog.value = true
        }
    }

    // Countdown timer logic during the game
    LaunchedEffect(gameStarted) {
        while (gameStarted && timeLeft > 0 && !controller.isGameOver()) {
            delay(1000)
            timeLeft--
        }

        if (gameStarted && (timeLeft == 0 || controller.isGameOver())) {
            gameStarted = false
            if (timeLeft == 0 && !controller.isGameOver()) {
                feedback = Strings.t("mm1")
            }
            saveSessionIfNeeded()
            showEndDialog.value = true
        }
    }

//    LaunchedEffect(controller.isGameOver()) {
//        if (controller.isGameOver()) {
//            showEndDialog.value = true
//            if (!sessionSaved.value) {
//                sessionSaved.value = true
//                val userId = runCatching { UserSession.getUserId() }.getOrNull()
//                if (userId != null) {
//                    GameSessionController.registerGameSession(
//                        gameId = gameId,
//                        difficulty = selectedDifficulty.value.name,
//                        score = controller.getScore(),
//                        timeAvg = (controller.getTimeLimit().toDouble() / controller.getTotalQuestions())
//                    ).onSuccess {
//                        coroutineScope.launch {
//                            val total = GameSessionController.getTotalScore(
//                                userId = userId,
//                                gameId = gameId,
//                                supabase = brain.alpha.services.SupabaseClientProvider.client
//                            )
//                            totalScore.value = total
//                        }
//                    }
//                }
//            }
//        }
//    }

    // If user chooses to see instructions, show that screen
    if (showInstructions) {
        MentalInstructionsScreen { showInstructions = false }
        return
    }

    // UI: main container
    Box(modifier = Modifier.fillMaxSize()) {
        // Background image
        Image(
            painter = painterResource(Res.drawable.fondoMental),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Content container
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Top bar with profile and settings buttons
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .background(Color(0xFF222937), shape = RoundedCornerShape(4.dp))
                    .border(BorderStroke(2.dp, Color(0xFF4CE3E8)))
            ) {
                IconButton(
                    onClick = {LoginController.navigateTo("profile")},
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 8.dp)
                        .size(48.dp)
                ) {
                    Image(
                        painterResource(Res.drawable.user),
                        contentDescription = "User"
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

            // Add spacing between top elements
            Spacer(Modifier.height(5.dp))

            // Add more spacing before showing the score box
            Spacer(modifier = Modifier.height(30.dp))

            // Box displaying the user's accumulated neurons (score)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .height(46.dp)
                    .background(Color.White, shape = RoundedCornerShape(12.dp))
                    .border(BorderStroke(1.dp, Color.Black), shape = RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp)
            ) {
                // Display total neuron count with localized label
                Text(
                    text = "${totalScore.value} " + Strings.t("neurons"),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            }

            // Add spacing below the score box
            Spacer(modifier = Modifier.height(30.dp))

            // Button to return to the home screen from the game
            Button(
                onClick = { controller.goHome() },
                modifier = Modifier.height(46.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF4adef8)),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.elevation(4.dp),
                border = BorderStroke(1.dp, Color.Black)
            ) {
                // Localized 'Back' label
                Text(Strings.t("back"), fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.Black)
            }

            // Add spacing before potentially showing end game dialog
            Spacer(Modifier.height(24.dp))

            // If game has ended, show a summary dialog with results and options
            if (showEndDialog.value) {
                AlertDialog(
                    onDismissRequest = {},
                    title = {
                        // Title text of the dialog
                        Text(
                            text = Strings.t("mm1"),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    text = {
                        // Content inside the dialog, showing score, difficulty and avg. time
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(Strings.t("mm2") + "${controller.getScore()}", fontSize = 18.sp)
                            Text(Strings.t("mm4") + "${selectedDifficulty.value.name}", fontSize = 18.sp)
                            Text(Strings.t("mm3") +  "${(controller.getTimeLimit().toDouble() / controller.getTotalQuestions()).toInt()}s", fontSize = 18.sp)
                        }
                    },
                    confirmButton = {
                        // Confirm button resets the game and closes the dialog
                        TextButton(onClick = {
                            controller.resetGame()
                            gameStarted = false
                            feedback = null
                            userAnswer = ""
                            showEndDialog.value = false
                        })
                        {
                            Text(Strings.t("mm5"), color = Color(0xFF6200EE), fontWeight = FontWeight.SemiBold)
                        }
                    },
                    // Dismiss button navigates user back to home and resets game state
                    dismissButton = {
                        TextButton(onClick = {
                            controller.resetGame()
                            gameStarted = false
                            showEndDialog.value = false
                            onNavigateToHome()
                        }) {
                            // Localized label for dismiss option
                            Text(Strings.t("mm6"), color = Color(0xFF6200EE), fontWeight = FontWeight.SemiBold) }
                    },
                    // Apply rounded corners to the dialog
                    shape = RoundedCornerShape(16.dp)
                )
            }

            // If game hasn't started yet, show pre-game setup options
            if (!gameStarted) {
                // Show difficulty label
                Text(Strings.t("mm7"), fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))

                // Determine unlocked difficulty based on total score
                val unlocked = when {
                    totalScore.value >= 150 -> Difficulty.HARD
                    totalScore.value >= 50 -> Difficulty.MEDIUM
                    else -> Difficulty.EASY
                }

                // Show difficulty selection buttons
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Difficulty.values().forEach { diff ->
                        val isUnlocked = diff.ordinal <= unlocked.ordinal
                        val selected = selectedDifficulty.value == diff
                        val bg = when (diff) {
                            Difficulty.EASY -> Color(0xFF0ffc86)
                            Difficulty.MEDIUM -> Color(0xFF64B5F6)
                            Difficulty.HARD -> Color(0xFFE57373)
                        }

                        // Button for each difficulty level
                        Button(
                            onClick = { if (isUnlocked) selectedDifficulty.value = diff },
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = if (selected) bg else bg.copy(alpha = 0.5f)
                            ),
                            enabled = isUnlocked,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            // Show unlock status and difficulty name
                            Text("${if (isUnlocked) "⚡" else "🔒"} ${diff.name}")
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Show unlock progress or success message based on score
                val (icon, color, msg) = when {
                    totalScore.value < 50 -> Triple("🔒", Color.Red, "${50 - totalScore.value}" +  Strings.t("mm8"))
                    totalScore.value < 150 -> Triple("🔒", Color.Red, "${150 - totalScore.value}" + Strings.t("mm9"))
                    else -> Triple("🔓", Color.Green, Strings.t("mm10"))
                }

                // Row displaying icon and message about progress
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Text(icon, modifier = Modifier.padding(end = 6.dp))
                    Text(msg, color = color, fontWeight = FontWeight.Medium)
                }

                Spacer(Modifier.height(16.dp))

                // Start game button: sets difficulty, time and initiates game
                Button(
                    onClick = {
                        controller.setDifficulty(selectedDifficulty.value)
                        gameStarted = true
                        timeLeft = controller.getTimeLimit()
                        controller.resetGame()
                        controller.startGame {
                            currentQuestion = it
                            startTime = currentTimeMillis()
                        }
                    },
                    // Set button shape, height, color theme, elevation, and border style
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(46.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFF4adef8),
                        contentColor = Color.Black
                    ),
                    elevation = ButtonDefaults.elevation(4.dp),
                    border = BorderStroke(1.dp, Color(0xFF388E3C))
                ) {
                    // Button text for starting the game
                    Text(Strings.t("mm11"))
                }

                // Add spacing between start and instructions buttons
                Spacer(Modifier.height(8.dp))

                // Button to display the instructions screen
                Button(onClick = { showInstructions = true },
                    modifier = Modifier.height(46.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFF4adef8),
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.elevation(4.dp),
                    border = BorderStroke(1.dp, Color(0xFF388E3C))
                ) {
                    // Button text for instructions
                    Text(Strings.t("instructions"))
                }

            } else {
                // When game has started and question is available
                currentQuestion?.let { question ->
                    // Layout for in-game question and controls
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp)
                    ) {
                        // Display current question text and number
                        Text(
                            Strings.t("mm18") + "${controller.getCurrentQuestionNumber()}: " + "${question.text}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(8.dp))
                        // Show countdown timer
                        Text(Strings.t("mm15") + "$timeLeft s", color = Color.Red, fontSize = 16.sp, fontWeight = FontWeight.Bold)

                        Spacer(Modifier.height(20.dp))
                        // Input field for user to type answer
                        TextField(
                            value = userAnswer,
                            onValueChange = { userAnswer = it },
                            label = { Text(Strings.t("mm12")) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(20.dp))
                        // Button to submit the user's answer
                        Button(
                            onClick = {
                                val ans = userAnswer.toIntOrNull()
                                if (ans != null) {
                                    val time = (currentTimeMillis() - startTime) / 1000.0
                                    controller.submitAnswer(ans, time) { correct, score, message ->
                                        isCorrect = correct
                                        feedback = if (correct) Strings.t("mm17") + "$score" else message
                                        userAnswer = ""
                                        // Start next question if game is not over
                                        if (!controller.isGameOver()) {
                                            controller.startGame {
                                                currentQuestion = it
                                                startTime = currentTimeMillis()
                                            }
                                        }
                                    }
                                } else feedback = Strings.t("mm16")
                            },
                            // Set button width, height, colors, shape, elevation, and border for submit
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color(0xFF4adef8),
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(12.dp),
                            elevation = ButtonDefaults.elevation(4.dp),
                            border = BorderStroke(1.dp, Color(0xFF000405))
                        ) {
                            // Button text to submit answer
                            Text(Strings.t("mm13"))
                        }

                        // Spacer between submit and reset button
                        Spacer(Modifier.height(12.dp))

                        // Button to reset the game manually
                        Button(
                            onClick = {
                                controller.resetGame()
                                gameStarted = false
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color(0xFF4adef8),
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(12.dp),
                            elevation = ButtonDefaults.elevation(2.dp),
                            border = BorderStroke(1.dp, Color.Black)
                        ) {
                            // Button text to reset
                            Text(Strings.t("mm14"))
                        }

                        // Spacer before feedback message
                        Spacer(Modifier.height(16.dp))
                        // Show feedback text if available; color based on correctness
                        feedback?.let {
                            Text(it, color = if (isCorrect == true) Color.Green else Color.Red)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MentalInstructionsScreen(onBack: () -> Unit) {
    // Full-screen container
    Box(modifier = Modifier.fillMaxSize()) {
        // Background image covering full screen
        Image(
            painter = painterResource(Res.drawable.fondoMental),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Centered content for instructions
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Instruction card with text and back button
            Card(
                elevation = 6.dp,
                shape = RoundedCornerShape(16.dp),
                backgroundColor = Color.White.copy(alpha = 0.95f),
                modifier = Modifier.padding(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Instructions title
                    Text(
                        Strings.t("instructions"),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF000405)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Rich text instructions using annotated string
                    Text(
                        buildAnnotatedString {
                            append(Strings.t("mm19") + "\n\n")
                            append(Strings.t("mm20") + " ")
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF388E3C))) {
                                append(Strings.t("mm21"))
                            }
                            append("\n\n" + Strings.t("mm22"))
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(Strings.t("mm23"))
                            }
                            append(Strings.t("mm24") + "\n\n")
                            append(Strings.t("mm25") + "\n")
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF81C784))) {
                                append("EASY")
                            }
                            append(Strings.t("mm26") + ", ")
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF64B5F6))) {
                                append("MEDIUM")
                            }
                            append(Strings.t("mm27") + ", ")
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFFE57373))) {
                                append("HARD")
                            }
                            append(Strings.t("mm28"))
                        },
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        color = Color(0xFF424242),
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Back button to return to the previous screen
                    Button(
                        onClick = onBack,
                        modifier = Modifier.height(46.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFF4adef8),
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(12.dp),
                        elevation = ButtonDefaults.elevation(4.dp),
                        border = BorderStroke(1.dp, Color(0xFF388E3C))
                    ) {
                        Text(Strings.t("back"), fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}


