// Define the package for the memory game view
package brain.alpha.view.memoryView

// Import libraries for layout and UI components
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight

// Import game controller and resources
import brain.alpha.controller.memoryController.simonController
import brainalpha.composeapp.generated.resources.*

// Import libraries for animations and effects
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.shadow
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle

// Import controllers and services
import brain.alpha.controller.AuthController
import brain.alpha.controller.GameSessionController
import brain.alpha.controller.LoginController
import brain.alpha.controller.concentrationController.stroopController
import brain.alpha.model.UserSession
import brain.alpha.model.shared.Difficulty
import brain.alpha.services.SupabaseClientProvider
import brain.alpha.strings.Strings

// Import for coroutine support
import kotlinx.coroutines.launch

// Main composable function for the Simon game screen
@Composable
fun simonScreen(
    controller: simonController,
    ) {
    // State to track if the session has been saved
    var sessionSaved by remember { mutableStateOf(false) }
    // Collect UI state from the controller
    val uiState by controller.uiState.collectAsState()
    // Create a coroutine scope
    val coroutineScope = rememberCoroutineScope()
    // State to show or hide instructions
    val showInstructions by controller.showInstructions
    // Get the display name of the current user (or "Guest")
    val displayName = UserSession.getDisplayName() // Show guest or username
    // Retrieve the user ID if logged in, null if not
    val userId = runCatching { UserSession.getUserId() }.getOrNull()
    // Mutable state to hold the total score
    val totalScore = remember { mutableStateOf(0) }
    // Mutable state for the selected difficulty level
    var selectedDifficulty by remember { mutableStateOf(Difficulty.EASY) }
    // Scroll state for vertical scrolling
    val scrollState = rememberScrollState()

    // Fetch the total score from the server when userId changes
    LaunchedEffect(userId) {
        if (userId != null) {
            val total = GameSessionController.getTotalScore(
                userId = userId,
                gameId = "2927d18f-b1ab-4376-990e-1275e5ba181e", // Game ID for Simon game
                supabase = SupabaseClientProvider.client
            )
            println("🧠 Score obtenido del servidor: $total")
            totalScore.value = total
        }
    }

    // Adjust difficulty based on score
    LaunchedEffect(totalScore.value) {
        if (!UserSession.isGuest.value) {
            selectedDifficulty = when (totalScore.value) {
                in 0..74 -> Difficulty.EASY
                in 50..149 -> Difficulty.MEDIUM
                else -> Difficulty.HARD
            }
            controller.setDifficulty(selectedDifficulty)
            println("🎯 Dificultad autoasignada para usuario: $selectedDifficulty")
        }
    }



    // Show the instructions screen if enabled
    if (showInstructions) {
        InstructionsSimon(
            onStart = { controller.beginGame() },
            controller = simonController
        )
        return // Exit to prevent rendering other content
    }

    // Main container for the game screen
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(Res.drawable.fondoMemory),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )


        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            // Header box for user info and settings
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .background(Color(0xFF222937), shape = RoundedCornerShape(4.dp))
                    .border(BorderStroke(2.dp, Color(0xFF4CE3E8)))
            )
            {

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
                Image(
                    painter = org.jetbrains.compose.resources.painterResource(Res.drawable.logo),
                    contentDescription = null,
                    modifier = Modifier
                        .size(120.dp)
                        .align(Alignment.Center)
                        .scale(1.5f)
                )
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

            // Spacer to separate sections
            Spacer(modifier = Modifier.height(5.dp)) // Espacio tras el logo

            // Row for buttons: Instructions, Score, and Back
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween

            ){
                Button(
                    onClick = {controller.toggleInstructions(true)},
                    modifier = Modifier
                        .height(46.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF0647D7)),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.elevation(4.dp),
                    border = BorderStroke(1.dp, Color(0xFFB0BEC5))
                ){
                    Text(Strings.t("instructions"), fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.White)
                }

                // Score display box
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .height(46.dp)
                        .background(Color(0xFF0647D7), shape = RoundedCornerShape(12.dp))
                        .border(BorderStroke(1.dp, Color(0xFFB0BEC5)), shape = RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "${totalScore.value} " + Strings.t("neurons"),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }

                // Button to navigate back to the home screen
                Button(
                    onClick = {controller.goHome()},
                    modifier = Modifier
                        .height(46.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF0647D7)),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.elevation(4.dp),
                    border = BorderStroke(1.dp, Color(0xFFB0BEC5))
                ){
                    Text(Strings.t("back"), fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.White)
                }
            }

            // Check if the game is not in progress to display difficulty selection
            if (!controller.gameInProgress.value) {

                // Map of colors for each difficulty level
                val diffColors = mapOf(
                    Difficulty.EASY to Color(0xFF81C784),
                    Difficulty.MEDIUM to Color(0xFF64B5F6),
                    Difficulty.HARD to Color(0xFFE57373)
                )

                // Determine the unlocked difficulty based on the user's score
                val unlockedDifficulty = when {
                    totalScore.value >= 150 -> Difficulty.HARD
                    totalScore.value >= 50 -> Difficulty.MEDIUM
                    else -> Difficulty.EASY
                }

                // Row to display difficulty buttons
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                    // Iterate over each difficulty level
                    Difficulty.values().forEach { diff ->

                        // Check if the difficulty is unlocked
                        val isUnlocked = diff.ordinal <= unlockedDifficulty.ordinal

                        // Check if the difficulty is currently selected
                        val isSelected = selectedDifficulty == diff

                        // Determine background and text colors
                        val bgColor = diffColors[diff] ?: Color.LightGray
                        val textColor = if (isUnlocked) Color.White else Color.Gray

                        // Determine icon based on lock status
                        val icon = if (isUnlocked) "⚡" else "🔒"

                        // Button for each difficulty level
                        Button(
                            onClick = {
                                if (isUnlocked) {
                                    // Set the selected difficulty
                                    selectedDifficulty = diff
                                    controller.setDifficulty(diff)
                                }
                            },
                            enabled = isUnlocked, // Enable only if unlocked
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = if (isSelected) bgColor else bgColor.copy(alpha = 0.6f),
                                contentColor = textColor
                            ),
                            shape = RoundedCornerShape(10.dp),
                            elevation = ButtonDefaults.elevation(2.dp)
                        ) {
                            // Display the icon and difficulty name
                            Text("$icon ${diff.name}")
                        }
                    }
                }

                // Spacer to separate difficulty buttons from the lock message
                Spacer(modifier = Modifier.height(8.dp))

                // Determine the lock/unlock icon, message color, and message text
                val (icon, color, msg) = when {
                    totalScore.value < 50 -> Triple("🔒", Color.Red, "${50 - totalScore.value} neuronas para desbloquear MEDIUM")
                    totalScore.value < 150 -> Triple("🔒", Color.Red, "${150 - totalScore.value} neuronas para desbloquear HARD")
                    else -> Triple("🔓", Color.Green, "Todas las dificultades desbloqueadas")
                }

                // Row to display the lock/unlock message
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    // Display the lock/unlock icon
                    Text(
                        text = icon,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(end = 6.dp)
                    )
                    // Display the lock/unlock message
                    Text(
                        text = msg,
                        fontSize = 14.sp,
                        color = color,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Spacer to add space after the difficulty section
            Spacer(modifier = Modifier.height(30.dp)) // Espacio tras el logo

            // Display the game title
            Text(
                text = Strings.t("title_simon"),
                fontSize = 40.sp,
                color = Color.Black,
                fontFamily = FontFamily.SansSerif,
                textAlign = TextAlign.Center
            )

            // Spacer to separate the title from the game content
            Spacer(modifier = Modifier.height(10.dp))

            // Column to hold the game content
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Display the message from the UI state
                Text(
                    text = uiState.message,
                    style = MaterialTheme.typography.h5
                )

                // Spacer to separate the message from the animated box
                Spacer(modifier = Modifier.height(16.dp))

                // Animated color based on the last item in the sequence
                val colorAnimado by animateColorAsState(
                    targetValue = colorFromName(uiState.sequence.lastOrNull() ?: ""),
                    label = "colorAnimado"
                )

                // Box to display the animated color
                Box(
                    modifier = Modifier
                        .width(200.dp)
                        .height(120.dp)
                        .background(colorAnimado)
                )

                // Spacer to separate the animated box from the buttons
                Spacer(modifier = Modifier.height(15.dp))

                // Column for the color buttons
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

                    // Row containing the first set of color buttons
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        ColorButton( Color.Red, uiState.isUserTurn, ) {
                            controller.onColorPressed("Rojo")
                        }
                        ColorButton( Color.Green, uiState.isUserTurn, ) {
                            controller.onColorPressed("Verde")
                        }
                    }

                    // Row containing the second set of color buttons
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        ColorButton( Color.Blue, uiState.isUserTurn, ) {
                            controller.onColorPressed("Azul")
                        }
                        ColorButton( Color.Yellow, uiState.isUserTurn, ) {
                            controller.onColorPressed("Amarillo")
                        }
                    }
                }

                // Spacer to separate the buttons from the rest of the content
                Spacer(modifier = Modifier.height(32.dp))

                // Button to start or restart the game
                Button(onClick = {
                    // Notify the controller to start the game
                    controller.onStartPressed()
                    // Reset session saved status
                    sessionSaved = false
                },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF0647D7)),) {
                    // Display "Play" or "Restart" depending on the game state
                    Text(if (uiState.gameOver) Strings.t("restart") else Strings.t("play"), color = Color.White)
                }

                // Check if the game is over to display the score
                if (uiState.gameOver) {

                    // Display the winning message and score
                    Text(
                        text = Strings.t("you_won")+ "${uiState.score} "+Strings.t("neurons"),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF2E7D32)
                    )

                    // Create a coroutine scope to handle asynchronous operations
                    val scope = rememberCoroutineScope()

                    // Check if the game is over and session is not saved
                    if(uiState.gameOver && !sessionSaved){
                        scope.launch {
                            // Attempt to get the user ID
                            val userId = runCatching { UserSession.getUserId() }.getOrNull()

                            // If user is logged in, proceed to save the session
                            if (userId != null) {

                                // Ensure the user exists in the database
                                AuthController.ensureUserExists(
                                    userId = userId,
                                    email = UserSession.email.value,
                                    username = UserSession.username.value
                                )

                                // Register the game session
                                GameSessionController.registerGameSession(
                                    gameId = "2927d18f-b1ab-4376-990e-1275e5ba181e", // Game ID for Simon
                                    score = uiState.score, // User's score
                                    difficulty = selectedDifficulty.name, // Difficulty level
                                ).onSuccess {
                                    sessionSaved = false
                                    println("✅ Partida guardada correctamente: Score ${uiState.score} | Dificultad $selectedDifficulty")

                                }.onFailure {
                                    println("❌ Error guardando partida: ${it.message}")
                                }
                            } else {
                                // If user is not logged in, display a warning
                                println("⚠ Usuario no logueado, no se puede guardar la partida")
                            }
                        }
                    }
                }
            }
        }

    }

}

// Composable function for a color button
@Composable
fun ColorButton(
    color: Color,
    enabled: Boolean,
    onClick: () -> Unit
) {
    // Animation for button scale effect
    val scale by animateFloatAsState(
        targetValue = if (enabled) 1f else 0.95f, // Scale based on enabled state
        label = "buttonScale"
    )

    // Box to represent the button with graphical properties
    Box(
        modifier = Modifier
            .size(100.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(16.dp))
            .background(color)
            .border(2.dp, Color.White, RoundedCornerShape(16.dp))
            .shadow(8.dp, RoundedCornerShape(16.dp), clip = false)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) { }
}

// Function to convert color names to Color objects
fun colorFromName(name: String): Color = when (name) {
    "Rojo" -> Color.Red
    "Verde" -> Color.Green
    "Azul" -> Color.Blue
    "Amarillo" -> Color.Yellow
    else -> Color.Transparent // Default to Transparent
}

// Composable function to display game instructions for the Simon game
@Composable
fun InstructionsSimon(onStart: () -> Unit, controller: simonController) {

    // Get the current display name (guest or user)
    val displayName = UserSession.getDisplayName() // Show guest or username

    // Main container for the instructions screen
    Box(modifier = Modifier.fillMaxSize()) {

        // Background image covering the entire screen
        Image(
            painter = painterResource(Res.drawable.fondoMemory),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Main content column for instructions
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

                // Center logo
                Image(
                    painter = painterResource(Res.drawable.logo),
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .align(Alignment.Center)
                        .scale(1.2f)
                )

                // Settings icon button
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

                // Display the username
                Text(
                    text = "$displayName",
                    color = Color.White,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 72.dp)
                )
            }

            // Spacer to add space below the header
            Spacer(modifier = Modifier.height(5.dp))
            // Back Button (same design as in game)

            // Spacer for vertical alignment
            Spacer(modifier = Modifier.height(100.dp))

            // Title of instruction section
            Text(
                Strings.t("instructions"),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121)
            )

            // Spacer to separate the title from the content
            Spacer(modifier = Modifier.height(30.dp))

            // Instruction content box with background and padding
            Box(
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .background(Color(0xFF0647D7), shape = RoundedCornerShape(16.dp))
                    .padding(16.dp) // Padding interno
            ) {
                // Display formatted instruction text
                Text(
                    buildAnnotatedString {
                        append(Strings.t("insS1")+ "\n\n")

                        append(Strings.t("insS2"))
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(Strings.t("insS3")) }
                        append(Strings.t("insS4")+ "\n\n")
                        append(Strings.t("insS5"))

                    },
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
            }

            // Spacer to separate the instruction content from the start button
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
                Text(Strings.t("start_game"), fontWeight = FontWeight.Medium, color = Color.White)
            }
        }
    }
}





