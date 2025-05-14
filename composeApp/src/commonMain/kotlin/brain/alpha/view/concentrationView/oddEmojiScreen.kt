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


@Composable
fun OddEmojiScreen(controller: oddEmojiController = oddEmojiController) {

    val grid by controller.grid
    val level by controller.level
    val errors by controller.errors
    val isGameOver by controller.isGameOver
    val showInstructions by controller.showInstructions
    val score by controller.score
    val timeLeft by controller.timeLeft
    val timeProgress = controller.getTimeProgress()
    val showEndDialog = remember { mutableStateOf(false) }
    val isGameStarted = remember { mutableStateOf(false) }
    val sessionSaved = remember { mutableStateOf(false) }
    val selectedDifficulty = remember { mutableStateOf(Difficulty.EASY) }
    val totalScore = remember { mutableStateOf(0) }
    val gameId = "498d59a6-7136-4676-9075-93d115d0395f"

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


    LaunchedEffect(isGameOver) {
        if (isGameOver) showEndDialog.value = true
    }

    if (showInstructions) {
        OddInstructionsScreen (onBack = {controller.showInstructions.value = false })
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(Res.drawable.fondoConcentracion),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            // Top bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .background(Color(0xFFEFEFEF), RoundedCornerShape(4.dp))
                    .border(BorderStroke(2.dp, Color(0xFFB0BEC5)))
            ) {
                IconButton(
                    onClick = { LoginController.navigateTo("profile") },
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 8.dp)
                        .size(48.dp)
                ) {
                    Image(painter = painterResource(Res.drawable.user), contentDescription = "User")
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
                    Image(painter = painterResource(Res.drawable.ajustes), contentDescription = "Settings")
                }
            }

            Spacer(modifier = Modifier.height(5.dp))

            Spacer(modifier = Modifier.height(30.dp))

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
                            controller.backToDifficultyMenu()
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
                        Difficulty.EASY to Color(0xFF81C784),
                        Difficulty.MEDIUM to Color(0xFF64B5F6),
                        Difficulty.HARD to Color(0xFFE57373)
                    )

                    val unlockedDifficulty = when {
                        totalScore.value >= 3000 -> Difficulty.HARD
                        totalScore.value >= 1500 -> Difficulty.MEDIUM
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

                    Spacer(modifier = Modifier.height(16.dp))

                    val (icon, color, msg) = when {
                        totalScore.value < 1500 -> Triple("🔒", Color.Red, "${1500 - totalScore.value} neuronas para desbloquear MEDIUM")
                        totalScore.value < 3000 -> Triple("🔒", Color.Red, "${3000 - totalScore.value} neuronas para desbloquear HARD")
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

                // Game running
                Text(Strings.t("insOS4"), fontSize = 24.sp, fontWeight = FontWeight.Bold)

                Text(Strings.t("insST8") + "$timeLeft" + Strings.t("insST9"), fontSize = 18.sp, color = Color(0xFF1565C0))

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

                grid.forEachIndexed { rowIndex, row ->
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        row.forEachIndexed { colIndex, emoji ->
                            Text(
                                text = emoji,
                                fontSize = 36.sp,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .clickable {
                                        controller.checkSelection(rowIndex, colIndex)
                                    }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(Strings.t("insOS1") + "$level", fontSize = 18.sp)
                Text(Strings.t("insOS2") + "$errors", fontSize = 18.sp, color = Color.Red)
                Text(Strings.t("insOS3") + "$score", fontSize = 18.sp, color = Color(0xFF2E7D32))

                Spacer(modifier = Modifier.height(24.dp))

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

                if (showEndDialog.value) {
                    AlertDialog(
                        onDismissRequest = {},
                        title = {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            )
                            {
                                Text(Strings.t("insST1"), fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            }
                        },
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
                        confirmButton = {
                            TextButton(onClick = {
                                controller.resetGame()
                                isGameStarted.value = false
                                showEndDialog.value = false
                            }) {
                                Text(Strings.t("insST4"))
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = {
                                controller.backToDifficultyMenu()
                                isGameStarted.value = false
                                showEndDialog.value = false
                            }) {
                                Text(Strings.t("insST5"))
                            }
                        },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }


            LaunchedEffect(showEndDialog.value) {
                if (showEndDialog.value && !sessionSaved.value) {
                    sessionSaved.value = true

                    val userId = runCatching { UserSession.getUserId() }.getOrNull()

                    if (userId != null) {
                        val score = controller.getFinalScore()
                        val avgTime = controller.getAverageTime().toDouble()
                        val difficulty = selectedDifficulty.value.name

                        GameSessionController.registerGameSession(
                            gameId = "498d59a6-7136-4676-9075-93d115d0395f",
                            difficulty = difficulty,
                            score = score,
                            timeAvg = avgTime
                        ).onSuccess {
                            println("✅ Sesión guardada: score=$score, avgTime=$avgTime, difficulty=$difficulty")
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

@Composable
fun OddInstructionsScreen(onBack: () -> Unit) {
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
                    append(Strings.t("insS09"))
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(Strings.t("insS10")) }

                    append(Strings.t("insS11"))
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(Strings.t("insS12")) }
                    append(Strings.t("insS13"))

                    append(Strings.t("insS14"))
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
                Text(Strings.t("insST6"), fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

