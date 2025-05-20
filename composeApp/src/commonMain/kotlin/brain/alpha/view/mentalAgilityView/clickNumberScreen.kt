package brain.alpha.view.mentalAgilityView

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
import androidx.compose.ui.graphics.Color
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
import brain.alpha.controller.LoginController
import brain.alpha.controller.mentalAgilityController.clickNumberController

import brain.alpha.model.shared.Difficulty
import brain.alpha.strings.Strings
import brainalpha.composeapp.generated.resources.*
import brainalpha.composeapp.generated.resources.Res
import brainalpha.composeapp.generated.resources.ajustes
import brainalpha.composeapp.generated.resources.fondoMental
import brainalpha.composeapp.generated.resources.logo
import brainalpha.composeapp.generated.resources.user
import org.jetbrains.compose.resources.painterResource
import brain.alpha.controller.GameSessionController
import brain.alpha.model.UserSession
import kotlinx.coroutines.launch

@Composable
fun clickNumberScreen(
    controller: clickNumberController,
    onNavigateToHome: () -> Unit
) {

    // Observe the instruction visibility state
    val showInstructions by controller.showInstructions
    var sessionSaved by remember { mutableStateOf(false) }
    val totalScore = remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()

    // Load total user score once user ID is available
    LaunchedEffect(UserSession.userId.value) {
        val userId = UserSession.userId.value
        if (!UserSession.isGuest.value && userId != null) {
            val total = GameSessionController.getTotalScore(
                userId = userId,
                gameId = "ed29af2f-6346-4a55-bd4d-1b07c013d726",
                supabase = brain.alpha.services.SupabaseClientProvider.client
            )
            totalScore.value = total
        }
    }

    // Show instructions screen first if applicable
    if (showInstructions) {
        InstructionsClickNumber(
            onStart = { controller.showInstructions.value = false },
            controller = clickNumberController
        )
        return
    }


    // Main game screen layout
    Box(modifier = Modifier.fillMaxSize()) {
        // Background image
        Image(
            painter = painterResource(Res.drawable.fondoMental),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Top bar with navigation icons
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .background(Color(0xFF222937), shape = RoundedCornerShape(4.dp))
                    .border(BorderStroke(2.dp, Color(0xFF4CE3E8)))
            ) {

                IconButton(
                    onClick = { LoginController.navigateTo("profile")},
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 8.dp)
                        .size(55.dp)
                ) {
                    Image(painter = painterResource(Res.drawable.user), contentDescription = "User")
                }
                Image(
                    painter = painterResource(Res.drawable.logo),
                    contentDescription = null,
                    modifier = Modifier
                        .size(120.dp)
                        .align(Alignment.Center)
                        .scale(1.5f)
                )
                IconButton(
                    onClick = { LoginController.navigateTo("settings") },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 8.dp)
                        .size(55.dp)
                ) {
                    Image(painter = painterResource(Res.drawable.ajustes), contentDescription = "Settings")
                }
            }


            Spacer(modifier = Modifier.height(5.dp))
            Spacer(modifier = Modifier.height(30.dp))

            // Back button
            Button(
                onClick = { controller.goHome() },
                modifier = Modifier
                    .align(Alignment.End)
                    .height(46.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF4adef8)),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.elevation(4.dp),
                border = BorderStroke(1.dp, Color.Black)
            ) {
                Text(Strings.t("back"), fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.Black)
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Game title
            Text(
                text = Strings.t("click_number"),
                fontSize = 40.sp,
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Game setup view
            if (!clickNumberController.isGameStarted) {
                Text(Strings.t("mm7"), fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))

                // Show score, difficulty buttons, unlock info, etc.
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

                Spacer(Modifier.height(16.dp))

                val unlocked = when {
                    totalScore.value >= 150 -> Difficulty.HARD
                    totalScore.value >= 50 -> Difficulty.MEDIUM
                    else -> Difficulty.EASY
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Difficulty.values().forEach { diff ->
                        val isUnlocked = diff.ordinal <= unlocked.ordinal
                        val selected = clickNumberController.currentDifficulty == diff
                        val bg = when (diff) {
                            Difficulty.EASY -> Color(0xFF0ffc86)
                            Difficulty.MEDIUM -> Color(0xFF64B5F6)
                            Difficulty.HARD -> Color(0xFFE57373)
                        }

                        Button(
                            onClick = { if (isUnlocked) clickNumberController.setDifficulty(diff) },
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = if (selected) bg else bg.copy(alpha = 0.5f)
                            ),
                            enabled = isUnlocked,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("${if (isUnlocked) "⚡" else "🔒"} ${Strings.translateDifficulty(diff)}")
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                val (icon, color, msg) = when {
                    totalScore.value < 50 -> Triple("🔒", Color.Red, "${50 - totalScore.value} ${Strings.t("mm8")}")
                    totalScore.value < 150 -> Triple("🔒", Color.Red, "${150 - totalScore.value} ${Strings.t("mm9")}")
                    else -> Triple("🔓", Color.Green, Strings.t("mm10"))
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
                    onClick = { clickNumberController.startGame() },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(46.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFF4adef8),
                        contentColor = Color.Black
                    ),
                    elevation = ButtonDefaults.elevation(4.dp),
                    border = BorderStroke(1.dp, Color(0xFF388E3C))
                ) {
                    Text(Strings.t("mm11"))
                }

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = { clickNumberController.showInstructions.value = true },
                    modifier = Modifier.height(46.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFF4adef8),
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.elevation(4.dp),
                    border = BorderStroke(1.dp, Color(0xFF388E3C))
                ) {
                    Text(Strings.t("instructions"))
                }
            } else {
                // Active game: show timer, instruction, result
                val minutes = clickNumberController.timeLeft / 60
                val seconds = clickNumberController.timeLeft % 60
                Text(
                    text = Strings.t("mm15") + " ${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}",
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                Text(
                    text = clickNumberController.instructionMessage,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                Text(
                    text = clickNumberController.resultMessage,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                // Display clickable grid buttons
                if (!clickNumberController.gameOver && clickNumberController.numbers.isNotEmpty()) {
                    for (row in 0 until 4) {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            for (col in 0 until 4) {
                                val index = row * 4 + col
                                val number = clickNumberController.numbers.getOrElse(index) { 0 }
                                Button(
                                    onClick = { clickNumberController.handleButtonClick(number) },
                                    modifier = Modifier.size(60.dp)
                                ) {
                                    Text(text = number.toString())
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        }

        // Show end-of-game dialog and optionally save session
        if (clickNumberController.showGameOverDialog) {
            AlertDialog(
                onDismissRequest = {},
                title = {
                    Text(
                        text = Strings.t("mm1"),
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
                        Text(Strings.t("mm2") + " ${clickNumberController.score}", fontSize = 18.sp)
                        Text(Strings.t("mm4") + " ${clickNumberController.currentDifficulty.name}", fontSize = 18.sp)
                        Text(Strings.t("mm3") + " ${when (clickNumberController.currentDifficulty) {
                            Difficulty.EASY -> 90
                            Difficulty.MEDIUM -> 60
                            Difficulty.HARD -> 30
                        }}s", fontSize = 18.sp)
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        clickNumberController.restartGame()
                    }) {
                        Text(Strings.t("mm5"), color = Color(0xFF6200EE), fontWeight = FontWeight.SemiBold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        clickNumberController.goHome()
                    }) {
                        Text(Strings.t("mm6"), color = Color(0xFF6200EE), fontWeight = FontWeight.SemiBold)
                    }
                },
                shape = RoundedCornerShape(16.dp)
            )

            // Saves session into the database
            if (!sessionSaved) {
                sessionSaved = true
                scope.launch {
                    val userId = runCatching { UserSession.getUserId() }.getOrNull()
                    if (userId != null) {
                        GameSessionController.registerGameSession(
                            gameId = "ed29af2f-6346-4a55-bd4d-1b07c013d726",
                            score = clickNumberController.score,
                            difficulty = clickNumberController.currentDifficulty.name
                        ).onFailure {
                            println("❌ Error al guardar la partida: ${it.message}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InstructionsClickNumber(onStart: () -> Unit, controller: clickNumberController) {
    // Fullscreen background
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(Res.drawable.fondoMental),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
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
                    .background(Color(0xFF222937), shape = RoundedCornerShape(4.dp))
                    .border(BorderStroke(2.dp, Color(0xFF4CE3E8)))
            ) {
                IconButton(
                    onClick = { LoginController.navigateTo("profile")},
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 8.dp)
                        .size(48.dp)
                ) {
                    Image(painter = painterResource(Res.drawable.user), contentDescription = "Usuario")
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
                    onClick = { LoginController.buttonSettings() },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 8.dp)
                        .size(48.dp)
                ) {
                    Image(painter = painterResource(Res.drawable.ajustes), contentDescription = "Ajustes")
                }
            }

            Spacer(modifier = Modifier.height(5.dp))

            // Navigation back button
            Button(
                onClick = { clickNumberController.goHome() },
                modifier = Modifier
                    .align(Alignment.End)
                    .height(46.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF4adef8)),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.elevation(4.dp),
                border = BorderStroke(1.dp, Color.Black)
            ) {
                Text(Strings.t("back"), fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.Black)
            }

            Spacer(modifier = Modifier.height(80.dp))

            // Instructions title
            Text(
                Strings.t("instructions"),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121)
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Main instructions content
            Box(
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .background(Color.White, shape = RoundedCornerShape(16.dp))
                    .padding(20.dp)
            ) {
                Text(
                    buildAnnotatedString {
                        // All instruction content styled and localized
                        append(Strings.t("insCN1") + "\n\n")

                        append(Strings.t("insCN2") + "\n\n")

                        append(Strings.t("insCN3") + "\n\n")

                        append(Strings.t("insCN4"))
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF388E3C))) {
                            append(Strings.t("insCN5"))
                        }
                        append(Strings.t("insCN6") + "\n\n")

                        append(Strings.t("insCN7") + "\n")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF81C784))) {
                            append(Strings.t("easy"))
                        }
                        append(": " + Strings.t("insCN8").split(":")[1] + "\n")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF64B5F6))) {
                            append(Strings.t("medium"))
                        }
                        append(": " + Strings.t("insCN9").split(":")[1] + "\n")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFFE57373))) {
                            append(Strings.t("hard"))
                        }
                        append(": " + Strings.t("insCN10").split(":")[1] + "\n\n")

                        append(Strings.t("insCN11") + "\n")
                        append(Strings.t("insCN12") + "\n")
                        append(Strings.t("insCN13") + "\n\n")

                        append(Strings.t("insCN14"))
                    },
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF424242),
                    lineHeight = 24.sp
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Start button
            Button(
                onClick = onStart,
                modifier = Modifier
                    .height(48.dp)
                    .width(200.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF4adef8)),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.elevation(4.dp),
                border = BorderStroke(1.dp, Color.Black)
            ) {
                Text(Strings.t("start_game"), fontWeight = FontWeight.Medium, color = Color.Black)
            }
        }
    }
}
