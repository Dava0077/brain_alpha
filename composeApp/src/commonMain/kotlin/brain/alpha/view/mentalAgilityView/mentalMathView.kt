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

    val gameId = "f017777c-f246-4770-bb73-a652a23e761d"
    val totalScore = remember { mutableStateOf(0) }
    val selectedDifficulty = remember { mutableStateOf(Difficulty.EASY) }
    val coroutineScope = rememberCoroutineScope()
    val latestGameOver by rememberUpdatedState(controller.isGameOver())
    val latestSessionSaved by rememberUpdatedState(sessionSaved.value)

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

    LaunchedEffect(latestGameOver) {
        if (latestGameOver && !latestSessionSaved) {
            saveSessionIfNeeded()
            delay(500) // da tiempo a que el diálogo se muestre
            showEndDialog.value = true
        }
    }

    LaunchedEffect(gameStarted) {
        while (gameStarted && timeLeft > 0 && !controller.isGameOver()) {
            delay(1000)
            timeLeft--
        }

        if (gameStarted && (timeLeft == 0 || controller.isGameOver())) {
            gameStarted = false
            if (timeLeft == 0 && !controller.isGameOver()) {
                feedback = "¡Tiempo agotado!"
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

    if (showInstructions) {
        MentalInstructionsScreen { showInstructions = false }
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(Res.drawable.fondoMental),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
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

            Spacer(Modifier.height(5.dp))

            Spacer(modifier = Modifier.height(30.dp))

            // 🔢 Mostrar neuronas acumuladas
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

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = { controller.goHome() },
                modifier = Modifier.height(46.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF4adef8)),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.elevation(4.dp),
                border = BorderStroke(1.dp, Color.Black)
            ) {
                Text(Strings.t("back"), fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.Black)
            }

            Spacer(Modifier.height(24.dp))

            if (showEndDialog.value) {
                AlertDialog(
                    onDismissRequest = {},
                    title = {
                        Text(
                            text = "¡Juego Terminado!",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    text = {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("Puntuación: ${controller.getScore()}", fontSize = 18.sp)
                            Text("Dificultad: ${selectedDifficulty.value.name}", fontSize = 18.sp)
                            Text("Tiempo promedio: ${(controller.getTimeLimit().toDouble() / controller.getTotalQuestions()).toInt()}s", fontSize = 18.sp)
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            controller.resetGame()
                            gameStarted = false
                            feedback = null
                            userAnswer = ""
                            showEndDialog.value = false
                        })
                        {
                            Text("Jugar de nuevo", color = Color(0xFF6200EE), fontWeight = FontWeight.SemiBold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            controller.resetGame()
                            gameStarted = false
                            showEndDialog.value = false
                            onNavigateToHome()
                        }) { Text("Salir", color = Color(0xFF6200EE), fontWeight = FontWeight.SemiBold) }
                    },
                    shape = RoundedCornerShape(16.dp)
                )
            }

            if (!gameStarted) {
                Text("Selecciona Dificultad:", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))

                val unlocked = when {
                    totalScore.value >= 150 -> Difficulty.HARD
                    totalScore.value >= 50 -> Difficulty.MEDIUM
                    else -> Difficulty.EASY
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Difficulty.values().forEach { diff ->
                        val isUnlocked = diff.ordinal <= unlocked.ordinal
                        val selected = selectedDifficulty.value == diff
                        val bg = when (diff) {
                            Difficulty.EASY -> Color(0xFF0ffc86)
                            Difficulty.MEDIUM -> Color(0xFF64B5F6)
                            Difficulty.HARD -> Color(0xFFE57373)
                        }

                        Button(
                            onClick = { if (isUnlocked) selectedDifficulty.value = diff },
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = if (selected) bg else bg.copy(alpha = 0.5f)
                            ),
                            enabled = isUnlocked,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("${if (isUnlocked) "⚡" else "🔒"} ${diff.name}")
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                val (icon, color, msg) = when {
                    totalScore.value < 50 -> Triple("🔒", Color.Red, "${50 - totalScore.value} neuronas para desbloquear MEDIUM")
                    totalScore.value < 150 -> Triple("🔒", Color.Red, "${150 - totalScore.value} neuronas para desbloquear HARD")
                    else -> Triple("🔓", Color.Green, "Todas las dificultades desbloqueadas")
                }

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
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(46.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFF4adef8),
                        contentColor = Color.Black
                    ),
                    elevation = ButtonDefaults.elevation(4.dp),
                    border = BorderStroke(1.dp, Color(0xFF388E3C))
                ) {
                    Text("Iniciar Juego")
                }

                Spacer(Modifier.height(8.dp))

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
                    Text("Instrucciones")
                }

            } else {
                currentQuestion?.let { question ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp)
                    ) {
                        Text(
                            "Pregunta ${controller.getCurrentQuestionNumber()}: ${question.text}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(8.dp))
                        Text("⏱️ Tiempo restante: $timeLeft s", color = Color.Red, fontSize = 16.sp, fontWeight = FontWeight.Bold)

                        Spacer(Modifier.height(20.dp))
                        TextField(
                            value = userAnswer,
                            onValueChange = { userAnswer = it },
                            label = { Text("Tu respuesta") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(20.dp))
                        Button(
                            onClick = {
                                val ans = userAnswer.toIntOrNull()
                                if (ans != null) {
                                    val time = (currentTimeMillis() - startTime) / 1000.0
                                    controller.submitAnswer(ans, time) { correct, score, message ->
                                        isCorrect = correct
                                        feedback = if (correct) "✅ Correcto. Puntuación: $score" else message
                                        userAnswer = ""
                                        if (!controller.isGameOver()) {
                                            controller.startGame {
                                                currentQuestion = it
                                                startTime = currentTimeMillis()
                                            }
                                        }
                                    }
                                } else feedback = "Introduce un número válido"
                            },
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
                            Text("Enviar Respuesta")
                        }

                        Spacer(Modifier.height(12.dp))
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
                            Text("Volver")
                        }

                        Spacer(Modifier.height(16.dp))
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
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(Res.drawable.fondoMental),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
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
                    Text(
                        "Instrucciones",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF000405)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        buildAnnotatedString {
                            append("Resuelve operaciones matemáticas lo más rápido posible.\n\n")
                            append("Cada respuesta correcta te da puntos. ")
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF388E3C))) {
                                append("¡Cuanto más rápido respondas, más puntos obtendrás!")
                            }
                            append("\n\nTienes ")
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("10 preguntas")
                            }
                            append(" por partida. Elige una dificultad para comenzar.\n\n")
                            append("Dificultades disponibles:\n")
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF81C784))) {
                                append("EASY")
                            }
                            append(" (10 puntos), ")
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF64B5F6))) {
                                append("MEDIUM")
                            }
                            append(" (15 puntos), ")
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFFE57373))) {
                                append("HARD")
                            }
                            append(" (20 puntos)\n")
                        },
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        color = Color(0xFF424242),
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(32.dp))

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
                        Text("Volver", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}


