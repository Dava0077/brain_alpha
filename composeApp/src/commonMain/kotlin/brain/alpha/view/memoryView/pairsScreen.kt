// Import necessary libraries for UI components and layout
package brain.alpha.view.memoryView


import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

// Import controllers and model classes
import brain.alpha.controller.AuthController
import brain.alpha.controller.GameSessionController
import brain.alpha.controller.LoginController
import brain.alpha.controller.memoryController.pairsController
import brain.alpha.model.UserSession
import brain.alpha.model.shared.Difficulty
import brain.alpha.services.GameSessionService
import brain.alpha.services.SupabaseClientProvider
import brain.alpha.strings.Strings
import brainalpha.composeapp.generated.resources.*
import kotlinx.coroutines.launch

// Main composable function for the couples game screen
@Composable
fun couplesScreen(
    controller: pairsController,
    ) {
    // State variables to manage game state and UI updates
    val revealed = remember { mutableStateListOf<Boolean>().apply { addAll(controller.getRevealedStates()) } }
    val matched = remember { mutableStateListOf<Boolean>().apply { addAll(controller.getMatchedStates()) } }
    var selectedIndices by remember { mutableStateOf(controller.getSelectedIndices()) }
    var gameStarted by remember { mutableStateOf(controller.hasStarted()) }
    var gameFinished by remember { mutableStateOf(false) }
    var elapsedTime by remember { mutableStateOf(0) }

    // Convert elapsed time to minutes and seconds for display
    val min = (elapsedTime / 60).toString().padStart(2, '0')
    val sec = (elapsedTime % 60).toString().padStart(2, '0')

    var gameLost by remember { mutableStateOf(false) }
    var selectedDifficulty by remember { mutableStateOf(Difficulty.EASY) }
    val displayName = UserSession.getDisplayName() // Show guest or username
    var sessionSaved by remember { mutableStateOf(false) }
    var timerTrigger by remember { mutableStateOf(0) }
    val showInstructions by controller.showInstructions

    // Retrieve game configuration data
    val pairCount = controller.getPairCount()
    val (rows, cols) = getGridDimensions(pairCount)
    val userId = runCatching { UserSession.getUserId() }.getOrNull()
    val totalScore = remember { mutableStateOf(0) }

    // Shuffled card values for the grid
    val shuffledCards = List(controller.getShuffledCardCount()) { index -> controller.getCardValue(index) }

    // State for vertical scroll
    val scrollState = rememberScrollState()

    // Fetch total score from server for the logged-in user
    LaunchedEffect(userId) {
        if (userId != null) {
            val total = GameSessionController.getTotalScore(
                userId = userId,
                gameId = "24bc262b-ab64-4c71-9c45-8d4fa04a549e",
                supabase = SupabaseClientProvider.client
            )
            println("🧠 Score obtenido del servidor: $total")
            totalScore.value = total
        }
    }

    // Automatically adjust difficulty based on score
    LaunchedEffect(totalScore.value) {
        if (!UserSession.isGuest.value) {
            selectedDifficulty = when (totalScore.value) {
                in 0..74 -> Difficulty.EASY
                in 50..149 -> Difficulty.MEDIUM
                else -> Difficulty.HARD
            }
            controller.setDifficulty(selectedDifficulty)
            println("🎯 Dificultad autoasignada: $selectedDifficulty")
        }
    }


    // Show instructions if enabled
    if (showInstructions) {
        InstructionsCouples(
            onStart = { controller.beginGame() },
            controller = pairsController
        )
        return
    }

    // Handle turn end logic after two cards are selected
    LaunchedEffect(selectedIndices) {
        if (selectedIndices.size == 2) {
            delay(1000)
            controller.processTurnEnd()
            revealed.clear(); revealed.addAll(controller.getRevealedStates())
            matched.clear(); matched.addAll(controller.getMatchedStates())
            selectedIndices = controller.getSelectedIndices()

            // Check if game has finished
            if (controller.isGameFinished()) {
                gameFinished = true
                gameStarted = false // ✅ REACTIVA los RadioButtons
            }
        }
    }

    // Timer logic to manage game duration and loss condition
    LaunchedEffect(timerTrigger) {
        if (gameStarted) {
            elapsedTime = 0
            while (!gameFinished && !gameLost) {
                delay(1000)
                elapsedTime++
            }
            if (!gameFinished && elapsedTime >= 90) {
                gameLost = true
                gameStarted = false
            }
        }
    }

    // Main game layout
    Box(modifier = Modifier.fillMaxSize()) {
        // Background image
        Image(
            painter = painterResource(Res.drawable.fondoMemory),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Game UI components
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            // Header with logo, user icon, and settings
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .background(Color(0xFF222937), shape = RoundedCornerShape(4.dp))
                    .border(BorderStroke(2.dp, Color(0xFF4CE3E8))))
            {
                // User icon button
                IconButton(
                    onClick = { LoginController.buttonUsername() },
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

                // Center logo
                Image(
                    painter = org.jetbrains.compose.resources.painterResource(Res.drawable.logo),
                    contentDescription = null,
                    modifier = Modifier
                        .size(120.dp)
                        .align(Alignment.Center)
                        .scale(1.5f)
                )

                // Settings button
                IconButton(
                    onClick = { brain.alpha.controller.LoginController.buttonSettings() },
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

                // Display username
                Text(
                    text = "$displayName",
                    color = Color.White,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 72.dp)
                )

            }
            Spacer(modifier = Modifier.height(5.dp)) // Espacio tras el logo

            // Row containing buttons for instructions, score, and back
            Row(
                modifier = Modifier
                    .fillMaxWidth(), // Make the row occupy the full width
                horizontalArrangement = Arrangement.SpaceBetween // Distribute elements evenly with space between

            ){
                // Button to show instructions
                Button(
                    onClick = {controller.toggleInstructions(true)}, // Trigger instruction visibility
                    modifier = Modifier
                        .height(46.dp), // Button height
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF0647D7)), // Background color
                    shape = RoundedCornerShape(12.dp), // Rounded corners
                    elevation = ButtonDefaults.elevation(4.dp), // Elevation effect
                    border = BorderStroke(1.dp, Color(0xFFB0BEC5)) // Border properties
                ){
                    // Text inside the button
                    Text(
                        Strings.t("instructions"), // Button label
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White) // Text color
                }
                // Box to display the current score
                Box(
                    contentAlignment = Alignment.Center, // Center content inside the box
                    modifier = Modifier
                        .height(46.dp) // Box height
                        .background(Color(0xFF0647D7), shape = RoundedCornerShape(12.dp)) // Background color and shape
                        .border(BorderStroke(1.dp, Color(0xFFB0BEC5)), shape = RoundedCornerShape(12.dp)) // Border properties
                        .padding(horizontal = 16.dp) // Horizontal padding
                ) {
                    // Display the score as text
                    Text(
                        text = "${totalScore.value} " + Strings.t("neurons"), // Score text with label
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White // Text color
                    )
                }

                // Button to return to the home screen
                Button(
                    onClick = {controller.goHome()}, // Navigate to home
                    modifier = Modifier
                        .height(46.dp), // Button height
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF0647D7)), // Background color
                    shape = RoundedCornerShape(12.dp), // Rounded corners
                    elevation = ButtonDefaults.elevation(4.dp), // Elevation effect
                    border = BorderStroke(1.dp, Color(0xFFB0BEC5)) // Border properties
                ){
                    // Text inside the button
                    Text(Strings.t("back"), fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.White)
                }
            }

            // Check if the game has not started to display difficulty selection and score status
            if (!gameStarted) {
                // Map of difficulty levels and their corresponding colors
                val diffColors = mapOf(
                    Difficulty.EASY to Color(0xFF81C784), // Green for EASY
                    Difficulty.MEDIUM to Color(0xFF64B5F6), // Blue for MEDIUM
                    Difficulty.HARD to Color(0xFFE57373) // Red for HARD
                )

                // Determine unlocked difficulty based on the score
                val unlockedDifficulty = when {
                    totalScore.value >= 150 -> Difficulty.HARD
                    totalScore.value >= 50 -> Difficulty.MEDIUM
                    else -> Difficulty.EASY
                }

                // Row to display difficulty selection buttons
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Iterate over all difficulty levels
                    Difficulty.values().forEach { diff ->
                        val isUnlocked = diff.ordinal <= unlockedDifficulty.ordinal // Check if difficulty is unlocked
                        val isSelected = selectedDifficulty == diff // Check if this difficulty is selected
                        val bgColor = diffColors[diff] ?: Color.LightGray // Determine background color
                        val textColor = if (isUnlocked) Color.White else Color.Gray // Determine text color
                        val icon = if (isUnlocked) "⚡" else "🔒" // Display icon based on lock status

                        // Button for each difficulty level
                        Button(
                            onClick = {
                                if (isUnlocked) {
                                    selectedDifficulty = diff // Set selected difficulty
                                    controller.setDifficulty(diff) // Update the controller
                                }
                            },
                            enabled = isUnlocked, // Enable only if unlocked
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = if (isSelected) bgColor else bgColor.copy(alpha = 0.6f), // Dim if not selected
                                contentColor = textColor
                            ),
                            shape = RoundedCornerShape(10.dp), // Rounded corners
                            elevation = ButtonDefaults.elevation(2.dp) // Elevation effect
                        ) {
                            // Display the icon and difficulty name
                            Text("$icon ${diff.name}")
                        }
                    }
                }

                // Spacer to provide vertical space
                Spacer(modifier = Modifier.height(8.dp))

                // Determine the icon, color, and message based on score to unlock difficulties
                val (icon, color, msg) = when {
                    totalScore.value < 50 -> Triple("🔒", Color.Red, "${50 - totalScore.value} neuronas para desbloquear MEDIUM")
                    totalScore.value < 150 -> Triple("🔒", Color.Red, "${150 - totalScore.value} neuronas para desbloquear HARD")
                    else -> Triple("🔓", Color.Green, "Todas las dificultades desbloqueadas")
                }

                // Row to display the unlock message
                Row(
                    verticalAlignment = Alignment.CenterVertically, // Align items vertically in the center
                    modifier = Modifier
                        .padding(vertical = 4.dp) // Vertical padding
                        .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp)) // Background color and shape
                        .padding(horizontal = 12.dp, vertical = 6.dp) // Internal padding
                ) {
                    // Display the icon (lock/unlock)
                    Text(
                        text = icon,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(end = 6.dp) // Space between icon and message
                    )

                    // Display the unlock message
                    Text(
                        text = msg,
                        fontSize = 14.sp,
                        color = color,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            // Spacer to separate sections
            Spacer(modifier = Modifier.height(15.dp)) // Espacio tras el logo

            // Display the game title
            Text(
                text = Strings.t("title_couples"),
                fontSize = 40.sp,
                color = Color.Black,
                fontFamily = FontFamily.SansSerif,
                textAlign = TextAlign.Center
            )

            // Spacer to separate sections
            Spacer(modifier = Modifier.height(30.dp))

            // Display the game timer
            Text(
                text = "${Strings.t("time")} $min:$sec", // Timer text
                fontSize = 20.sp,
                color = Color.Black,
                fontFamily = FontFamily.SansSerif,
                textAlign = TextAlign.Center

            )

            // Spacer to separate sections
            Spacer(modifier = Modifier.height(10.dp))

            // Determine the number of rows and columns based on the number of pairs
            val (rows, cols) = getGridDimensions(pairCount)

            // Loop through each row
            for (row in 0 until rows) {

                // Create a horizontal row container
                Row {

                    // Loop through each column
                    for (col in 0 until cols) {

                        // Calculate the index of the current card
                        val index = row * cols + col

                        // Skip rendering if the index exceeds the number of shuffled cards
                        if (index >= shuffledCards.size) continue  // Evita crash si sobran casillas

                        // Check if the card is revealed or matched
                        val isVisible = revealed.getOrNull(index) == true || matched.getOrNull(index) == true

                        // Create a box to represent the card
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .size(64.dp)
                                .background(
                                    // Set the background color based on visibility
                                    if (isVisible) Color.White else Color(0xFF80C6E8),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                // Make the card clickable if the game is started, the card is not visible, and less than 2 cards are selected
                                .clickable(enabled = gameStarted && !isVisible && selectedIndices.size < 2) {

                                    // Handle card selection
                                    if (controller.selectCard(index)) {
                                        // Update revealed and matched states
                                        revealed.clear(); revealed.addAll(controller.getRevealedStates())
                                        matched.clear(); matched.addAll(controller.getMatchedStates())
                                        selectedIndices = controller.getSelectedIndices()
                                    }
                                },
                            contentAlignment = Alignment.Center // Center the content within the box
                        ) {
                            // Display the card value if it is visible
                            if (isVisible && index < shuffledCards.size) {
                                Text(
                                    text = shuffledCards[index],
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
            }

            // Spacer to separate sections vertically
            Spacer(modifier = Modifier.height(30.dp))

            // Display the "Play" button if the game is not started, finished, or lost
            if (!gameStarted || gameFinished || gameLost) {

                // Create the "Play" button
                Button(onClick = {
                    controller.startGame()
                    // Reset game state
                    revealed.clear(); revealed.addAll(controller.getRevealedStates())
                    matched.clear(); matched.addAll(controller.getMatchedStates())
                    selectedIndices = controller.getSelectedIndices()
                    gameStarted = true
                    gameFinished = false
                    sessionSaved = false
                    gameLost = false
                    elapsedTime = 0
                    timerTrigger++ // fuerza reinicio del temporizador
                },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF0647D7)),) {
                    Text(Strings.t("play"), color = Color.White) // Display "Play" text
                }
            }

            // Check if the game is finished
            if (gameFinished) {

                // Spacer for vertical separation
                Spacer(modifier = Modifier.height(16.dp))

                // Calculate the final score based on elapsed time
                val score = controller.calculateScore(elapsedTime)

                // Display the score
                Text(
                    text = Strings.t("your_score") + " $score " + Strings.t("neurons"), // Display the score with label
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF2E7D32)
                )

                // Create a coroutine scope for async tasks
                val scope = rememberCoroutineScope()

                // Save the session only if not already saved
                if (gameFinished && !sessionSaved) {
                    sessionSaved = true

                    // Launch a coroutine to handle game session saving
                    scope.launch {
                        val userId = runCatching { UserSession.getUserId() }.getOrNull()

                        // If the user is logged in, proceed with saving
                        if (userId != null) {
                            // Ensure the user exists in the database
                            AuthController.ensureUserExists(
                                userId = userId,
                                email = UserSession.email.value,
                                username = UserSession.username.value
                            )

                            // Save the game session
                            GameSessionController.registerGameSession(
                                gameId = "24bc262b-ab64-4c71-9c45-8d4fa04a549e", // Game ID
                                score = score, // Achieved score
                                difficulty = selectedDifficulty.name, // Selected difficulty
                            ).onSuccess {
                                println("✅ Partida guardada correctamente: Score $score | Dificultad $selectedDifficulty")

                            }.onFailure {
                                println("❌ Error guardando partida: ${it.message}")
                            }
                        } else {
                            println("⚠ Usuario no logueado, no se puede guardar la partida")
                        }
                    }
                }
            }
        }
    }
}

// Composable function to display game instructions
@Composable
fun InstructionsCouples(onStart: () -> Unit, controller: pairsController) {

    // Get the display name of the user
    val displayName = UserSession.getDisplayName() // Show guest or username

    // Create a container for the instructions screen
    Box(modifier = Modifier.fillMaxSize()) {

        // Background image
        Image(
            painter = painterResource(Res.drawable.fondoMemory),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop // Scale image to cover the entire screen
        )

        // Main layout container
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
                    .border(BorderStroke(2.dp, Color(0xFF4CE3E8))))
            {
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
                    onClick = { brain.alpha.controller.LoginController.buttonSettings() },
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
                Text(
                    text = "$displayName",
                    color = Color.White,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 72.dp)
                )
            }

            Spacer(modifier = Modifier.height(5.dp))
            // Back Button (same design as in game)


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
                    .background(Color(0xFF0647D7), shape = RoundedCornerShape(16.dp))
                    .padding(16.dp) // Padding interno
            ) {
                Text(
                    buildAnnotatedString {
                        append(Strings.t("insC1"))
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(Strings.t("insC2"))
                        }
                        append(Strings.t("insC3") + "\n\n")

                        append(Strings.t("insC7")+"\n")

                        append(Strings.t("insC4"))
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(Strings.t("insC5"))
                        }
                        append(" .\n")

                        append(Strings.t("insC6"))
                    },
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    lineHeight = 24.sp
                )
            }

            // Spacer to add vertical space of 40.dp
            Spacer(modifier = Modifier.height(40.dp))

            // Start game button
            Button(
                onClick = onStart,
                modifier = Modifier
                    .height(48.dp)
                    .width(200.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF0647D7)),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.elevation(4.dp),
                border = BorderStroke(1.dp, Color(0xFFB0BEC5))
            ) {
                // Text displayed inside the button
                Text(Strings.t("start_game"), fontWeight = FontWeight.Medium, color = Color.White)
            }
        }
    }
}

// Function to determine grid dimensions based on the number of pairs
fun getGridDimensions(pairCount: Int): Pair<Int, Int> {

    // Calculate the total number of cards (each pair has 2 cards)
    val totalCards = pairCount * 2

    // Determine the grid size based on the total number of cards
    return when (totalCards) {
        16 -> 4 to 4
        24 -> 6 to 4
        36 -> 6 to 6
        else -> 4 to 4 // Default grid size if not specified
    }
}





