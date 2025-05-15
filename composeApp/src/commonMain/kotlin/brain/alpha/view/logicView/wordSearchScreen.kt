package brain.alpha.view.logicView

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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import brain.alpha.controller.AuthController
import brain.alpha.controller.GameSessionController
import brain.alpha.controller.LoginController
import brain.alpha.controller.logicController.wordSearchController
import brain.alpha.model.UserSession
import brain.alpha.model.shared.Difficulty
import brain.alpha.services.GameSessionService
import brain.alpha.services.SupabaseClientProvider
import brain.alpha.strings.Strings
import brainalpha.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.abs

@Composable
fun wordSearchScreen(controller: wordSearchController, onNavigateToHome: () -> Unit) {
    val grid = controller.getGrid()
    val words = controller.getWordsToFind()
    val selectedCells = remember { mutableStateListOf<Pair<Int, Int>>() }
    var selectionDirection by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var gameStarted by remember { mutableStateOf(false) }
    var gameFinished by remember { mutableStateOf(false) }
    val validatedCells = remember { mutableStateListOf<Pair<Int, Int>>() }
    var timeLeft by remember { mutableStateOf(60) }
    var showDialog by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val showInstructions by controller.showInstructions
    var elapsedTime by remember { mutableStateOf(0) }
    var timerTrigger by remember { mutableStateOf(0) }
    val min = (elapsedTime / 60).toString().padStart(2, '0')
    val sec = (elapsedTime % 60).toString().padStart(2, '0')
    var selectedDifficulty by remember { mutableStateOf(Difficulty.EASY) }
    val displayName = UserSession.getDisplayName() // Show guest or username
    var sessionSaved by remember { mutableStateOf(false) }
    val userId = runCatching { UserSession.getUserId() }.getOrNull()
    val totalScore = remember { mutableStateOf(0) }
    val scrollState = rememberScrollState()

    LaunchedEffect(userId) {
        if (userId != null) {
            val total = GameSessionController.getTotalScore(
                userId = userId,
                gameId = "d15d1344-df81-41d5-87f0-c468668be2d8",
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
                in 75..149 -> Difficulty.MEDIUM
                else -> Difficulty.HARD
            }
            controller.configureGame(selectedDifficulty)
            println("🎯 Dificultad autoasignada: $selectedDifficulty")
        }
    }



    if (showInstructions) {
        InstructionsWordSearch(
            onStart = { controller.beginGame() },
            controller = wordSearchController
        )
        return
    }

    LaunchedEffect(timerTrigger) {
        if (gameStarted) {
            elapsedTime = 0
            while (!controller.isGameFinished()) {
                delay(1000)
                elapsedTime++
            }
        }
    }
    LaunchedEffect(validatedCells.size) {
        if (gameStarted && controller.isGameFinished()) {
            gameFinished = true
            showDialog = "win"
        }
    }

   Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(Res.drawable.fondoLogic),
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
                    .border(BorderStroke(2.dp, Color(0xFF4CE3E8)))
            ) {
                IconButton(
                    onClick = { LoginController.buttonUsername()},
                    modifier = Modifier.align(Alignment.CenterStart).padding(start = 8.dp).size(55.dp)
                ) {
                    Image(painter = painterResource(Res.drawable.user), contentDescription = "User")
                }
                Image(
                    painter = painterResource(Res.drawable.logo),
                    contentDescription = null,
                    modifier = Modifier.size(120.dp).align(Alignment.Center).scale(1.5f)
                )
                IconButton(
                    onClick = { LoginController.buttonSettings() },
                    modifier = Modifier.align(Alignment.CenterEnd).padding(end = 8.dp).size(55.dp)
                ) {
                    Image(painter = painterResource(Res.drawable.ajustes), contentDescription = "Settings")
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { controller.toggleInstructions(true) },
                    modifier = Modifier.height(46.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.elevation(4.dp),
                    border = BorderStroke(1.dp, Color(0xFFB0BEC5))
                ) {
                    Text(Strings.t("instructions"), fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.Black)
                }

                Text(
                    text = "${totalScore.value} " + Strings.t("neurons"),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier
                        .background(Color.White, shape = RoundedCornerShape(8.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )

                Button(
                    onClick = { controller.goHomeLogic() },
                    modifier = Modifier.height(46.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.elevation(4.dp),
                    border = BorderStroke(1.dp, Color(0xFFB0BEC5))
                ) {
                    Text(Strings.t("back"), fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.Black)
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
                    totalScore.value >= 75 -> Difficulty.MEDIUM
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
                                    controller.configureGame(diff)
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
                    totalScore.value < 75 -> Triple("🔒", Color.Red, "${75 - totalScore.value} neuronas para desbloquear MEDIUM")
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


            Spacer(modifier = Modifier.height(30.dp))
            Text(Strings.t("title_wordSearch"), fontSize = 40.sp, color = Color.Black, fontFamily = FontFamily.SansSerif, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(15.dp))
            Text("${Strings.t("time")} $min:$sec", fontSize = 20.sp, color = Color.Black, fontFamily = FontFamily.SansSerif, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(10.dp))

            if (!gameStarted) {
                Button(
                    onClick = {
                        scope.launch {
                            isLoading = true
                            withContext(Dispatchers.Default) {
                                controller.configureGame(selectedDifficulty)
                                controller.startGame()
                            }
                            selectedCells.clear()
                            validatedCells.clear()
                            timeLeft = 60
                            gameStarted = true
                            gameFinished = false
                            sessionSaved = false
                            isLoading = false
                            timerTrigger++
                        }
                    },
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)
                ) {
                    Text(if (isLoading) Strings.t("charging") else Strings.t("play"), color = Color.Black)
                }
            }

            if (gameStarted) {

                Spacer(modifier = Modifier.height(10.dp))

                Column {
                    for (i in grid.indices) {
                        Row {
                            for (j in grid[i].indices) {
                                val isSelected = selectedCells.contains(Pair(i, j))
                                val isValidated = validatedCells.contains(Pair(i, j))
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(
                                            when {
                                                isValidated -> Color.Yellow
                                                isSelected -> Color.Cyan
                                                else -> Color.White
                                            }, shape = RoundedCornerShape(4.dp)
                                        )
                                        .border(1.dp, Color.Black)
                                        .clickable(enabled = !gameFinished) {
                                            val pos = Pair(i, j)
                                            if (selectedCells.isEmpty()) {
                                                selectedCells.add(pos)
                                            } else {
                                                val (prevRow, prevCol) = selectedCells.last()
                                                val dx = i - prevRow
                                                val dy = j - prevCol
                                                if (selectionDirection == null && abs(dx) <= 1 && abs(dy) <= 1 && (dx != 0 || dy != 0)) {
                                                    selectionDirection = Pair(dx, dy)
                                                    selectedCells.add(pos)
                                                } else if (selectionDirection != null) {
                                                    val expected = Pair(
                                                        selectedCells.first().first + selectionDirection!!.first * selectedCells.size,
                                                        selectedCells.first().second + selectionDirection!!.second * selectedCells.size
                                                    )
                                                    if (pos == expected) selectedCells.add(pos)
                                                }
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(grid[i][j].toString(), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                Text(Strings.t("words_find"), fontSize = 20.sp, color = Color.Black)

                val wordsPerLine = 5
                val wordGroups = controller.getWordsToFind().chunked(wordsPerLine)

                wordGroups.forEach { group ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        group.forEachIndexed { index, word ->
                            val isFound = controller.isWordFound(word)
                            Text(
                                text = word,
                                fontSize = 14.sp,
                                color = if (isFound) Color.Gray else Color.Black,
                                textDecoration = if (isFound) TextDecoration.LineThrough else TextDecoration.None
                            )
                            if (index != group.lastIndex) {
                                Text(" - ", fontSize = 14.sp, color = Color.Black)
                            }
                        }
                    }
                }
                if (gameFinished) {
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
                                    gameId = "d15d1344-df81-41d5-87f0-c468668be2d8",
                                    score = score,
                                    difficulty = selectedDifficulty.name,
                                ).onSuccess {
                                    println("✅ Partida guardada correctamente: Score $score | Dificultad $selectedDifficulty")
                                    totalScore.value += score

                                }.onFailure {
                                    println("❌ Error guardando partida: ${it.message}")
                                }
                            } else {
                                println("⚠ Usuario no logueado, no se puede guardar la partida")
                            }
                        }
                    }
                }


                Spacer(modifier = Modifier.height(20.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(
                        onClick = {
                            controller.resetSelection()
                            controller.configureGame(selectedDifficulty) // reinicia palabras si es necesario
                            controller.startGame()

                            selectedCells.clear()
                            validatedCells.clear()
                            selectionDirection = null
                            elapsedTime = 0
                            timeLeft = 60
                            timerTrigger++


                            gameFinished = false
                            gameStarted = true
                            sessionSaved = false
                            showDialog = null
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)
                    ) {
                        Text(Strings.t("restart"), color = Color.Black)
                    }
                    Button(
                        onClick = {
                            val word = selectedCells.map { (r, c) -> grid[r][c] }.joinToString("")
                            if (controller.getWordsToFind().contains(word) && !controller.isWordFound(word)) {
                                println("✅ Palabra correcta: $word")
                                controller.markWordAsFound(word)
                                validatedCells.addAll(selectedCells)
                            } else {
                                println("❌ Palabra incorrecta: $word")
                            }
                            selectedCells.clear()
                            selectionDirection = null
                        },
                        enabled = selectedCells.isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)
                    ) {
                        Text(Strings.t("validate"), color = Color.Black)
                    }
                }
            }
        }
    }
}

@Composable
fun InstructionsWordSearch(onStart: () -> Unit, controller: wordSearchController) {
    val displayName = UserSession.getDisplayName() // Show guest or username

    Box(modifier = Modifier.fillMaxSize()) {

        // Background image
        Image(
            painter = painterResource(Res.drawable.fondoLogic),
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
            )
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
                    .background(Color.White, shape = RoundedCornerShape(16.dp))
                    .padding(16.dp) // Padding interno
            ) {
                Text(
                    buildAnnotatedString {
                        append(Strings.t("insW1"))
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(Strings.t("insW2"))
                        }
                        append(Strings.t("insW3") + "\n\n")

                        append(Strings.t("insW4"))
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(Strings.t("insW5"))
                        }
                        append(" .\n\n")
                        append(Strings.t("insW7")+"\n\n")
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

