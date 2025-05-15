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


@Composable
fun couplesScreen(
    controller: pairsController,
    ) {

    val revealed = remember { mutableStateListOf<Boolean>().apply { addAll(controller.getRevealedStates()) } }
    val matched = remember { mutableStateListOf<Boolean>().apply { addAll(controller.getMatchedStates()) } }
    var selectedIndices by remember { mutableStateOf(controller.getSelectedIndices()) }
    var gameStarted by remember { mutableStateOf(controller.hasStarted()) }
    var gameFinished by remember { mutableStateOf(false) }
    var elapsedTime by remember { mutableStateOf(0) }
    val min = (elapsedTime / 60).toString().padStart(2, '0')
    val sec = (elapsedTime % 60).toString().padStart(2, '0')
    var gameLost by remember { mutableStateOf(false) }
    var selectedDifficulty by remember { mutableStateOf(Difficulty.EASY) }
    val displayName = UserSession.getDisplayName() // Show guest or username
    var sessionSaved by remember { mutableStateOf(false) }
    var timerTrigger by remember { mutableStateOf(0) }
    val showInstructions by controller.showInstructions
    val pairCount = controller.getPairCount()
    val (rows, cols) = getGridDimensions(pairCount)
    val userId = runCatching { UserSession.getUserId() }.getOrNull()
    val totalScore = remember { mutableStateOf(0) }
    val shuffledCards = List(controller.getShuffledCardCount()) { index -> controller.getCardValue(index) }
    val scrollState = rememberScrollState()

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


    // Show instructions first
    if (showInstructions) {
        InstructionsCouples(
            onStart = { controller.beginGame() },
            controller = pairsController
        )
        return
    }


    LaunchedEffect(selectedIndices) {
        if (selectedIndices.size == 2) {
            delay(1000)
            controller.processTurnEnd()
            revealed.clear(); revealed.addAll(controller.getRevealedStates())
            matched.clear(); matched.addAll(controller.getMatchedStates())
            selectedIndices = controller.getSelectedIndices()
            if (controller.isGameFinished()) {
                gameFinished = true
                gameStarted = false // ✅ REACTIVA los RadioButtons
            }
        }
    }
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
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(Res.drawable.fondoMemory),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(scrollState),
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
            if (!gameStarted) {
                val diffColors = mapOf(
                    Difficulty.EASY to Color(0xFF81C784),
                    Difficulty.MEDIUM to Color(0xFF64B5F6),
                    Difficulty.HARD to Color(0xFFE57373)
                )

                val unlockedDifficulty = when {
                    totalScore.value >= 150 -> Difficulty.HARD
                    totalScore.value >= 50 -> Difficulty.MEDIUM
                    else -> Difficulty.EASY
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Difficulty.values().forEach { diff ->
                        val isUnlocked = diff.ordinal <= unlockedDifficulty.ordinal
                        val isSelected = selectedDifficulty == diff
                        val bgColor = diffColors[diff] ?: Color.LightGray
                        val textColor = if (isUnlocked) Color.White else Color.Gray
                        val icon = if (isUnlocked) "⚡" else "🔒"

                        Button(
                            onClick = {
                                if (isUnlocked) {
                                    selectedDifficulty = diff
                                    controller.setDifficulty(diff)
                                }
                            },
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
            }

            Spacer(modifier = Modifier.height(15.dp)) // Espacio tras el logo

            Text(
                text = Strings.t("title_couples"),
                fontSize = 40.sp,
                color = Color.Black,
                fontFamily = FontFamily.SansSerif,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(30.dp))


            Text(
                text = "${Strings.t("time")} $min:$sec",
                fontSize = 20.sp,
                color = Color.Black,
                fontFamily = FontFamily.SansSerif,
                textAlign = TextAlign.Center

            )
            Spacer(modifier = Modifier.height(10.dp))

            val (rows, cols) = getGridDimensions(pairCount)

            for (row in 0 until rows) {
                Row {
                    for (col in 0 until cols) {
                        val index = row * cols + col
                        if (index >= shuffledCards.size) continue  // Evita crash si sobran casillas

                        val isVisible = revealed.getOrNull(index) == true || matched.getOrNull(index) == true

                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .size(64.dp)
                                .background(
                                    if (isVisible) Color.White else Color(0xFF80C6E8),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable(enabled = gameStarted && !isVisible && selectedIndices.size < 2) {
                                    if (controller.selectCard(index)) {
                                        revealed.clear(); revealed.addAll(controller.getRevealedStates())
                                        matched.clear(); matched.addAll(controller.getMatchedStates())
                                        selectedIndices = controller.getSelectedIndices()
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
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



            Spacer(modifier = Modifier.height(30.dp))

            if (!gameStarted || gameFinished || gameLost) {
                Button(onClick = {
                    controller.startGame()
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
                    Text(Strings.t("play"), color = Color.White)
                }
            }

            if (gameFinished) {
                Spacer(modifier = Modifier.height(16.dp))

                val score = controller.calculateScore(elapsedTime)

                Text(
                    text = Strings.t("your_score") + " $score " + Strings.t("neurons"),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF2E7D32)
                )
                val scope = rememberCoroutineScope()

                if (gameFinished && !sessionSaved) {
                    sessionSaved = true
                    scope.launch {
                        val userId = runCatching { UserSession.getUserId() }.getOrNull()
                        if (userId != null) {
                            AuthController.ensureUserExists(
                                userId = userId,
                                email = UserSession.email.value,
                                username = UserSession.username.value
                            )
                            GameSessionController.registerGameSession(
                                gameId = "24bc262b-ab64-4c71-9c45-8d4fa04a549e",
                                score = score,
                                difficulty = selectedDifficulty.name,
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
@Composable
fun InstructionsCouples(onStart: () -> Unit, controller: pairsController) {
    val displayName = UserSession.getDisplayName() // Show guest or username

    Box(modifier = Modifier.fillMaxSize()) {

        // Background image
        Image(
            painter = painterResource(Res.drawable.fondoMemory),
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
fun getGridDimensions(pairCount: Int): Pair<Int, Int> {
    val totalCards = pairCount * 2
    return when (totalCards) {
        16 -> 4 to 4
        24 -> 6 to 4
        36 -> 6 to 6
        else -> 4 to 4
    }
}





