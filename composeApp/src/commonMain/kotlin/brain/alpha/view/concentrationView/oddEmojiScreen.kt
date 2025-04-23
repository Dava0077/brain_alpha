package brain.alpha.view.concentrationViews

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import brain.alpha.controller.concentrationController.oddEmojiController
import brain.alpha.model.shared.Difficulty
import brain.alpha.strings.Strings
import brainalpha.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource

@Composable
fun oddEmojiScreen(controller: oddEmojiController = oddEmojiController) {

    val grid = remember { controller.grid }
    val correctClick by controller.correctClick
    val wrongClick by controller.wrongClick

    val level = controller.level
    val maxLevel = controller.maxLevel
    val errors = controller.errors
    val averageTime = controller.averageTime
    val gridSize = controller.gridSize

    val selectedDifficulty = controller.selectedDifficulty
    val isGameStarted = controller.isGameStarted
    val showInstructions = controller.showInstructions
    val isGameOver by controller.isGameOver
    val showSelector = controller.showDifficultySelector

    if (showInstructions) {
        oddEmojiInstructions(onBack = { controller.showInstructions = false })
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(Res.drawable.stroopTestBG),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
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
                    .background(Color(0xFFEFEFEF), shape = RoundedCornerShape(4.dp))
                    .border(BorderStroke(2.dp, Color(0xFFB0BEC5)))
            ) {
                IconButton(
                    onClick = { },
                    modifier = Modifier.align(Alignment.CenterStart).padding(start = 8.dp).size(48.dp)
                ) {
                    Image(painter = painterResource(Res.drawable.user), contentDescription = "User")
                }
                Image(
                    painter = painterResource(Res.drawable.logo),
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.Center).size(100.dp).scale(1.2f)
                )
                IconButton(
                    onClick = { brain.alpha.controller.LoginController.buttonSettings() },
                    modifier = Modifier.align(Alignment.CenterEnd).padding(end = 8.dp).size(48.dp)
                ) {
                    Image(painter = painterResource(Res.drawable.ajustes), contentDescription = "Settings")
                }
            }

            Spacer(modifier = Modifier.height(5.dp))

            Button(
                onClick = {
                    if (isGameStarted.value) {
                        isGameStarted.value = false
                        controller.goBackToDifficultySelection()
                    } else {
                        controller.goConcentration()
                    }
                },
                modifier = Modifier.padding(8.dp).height(46.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.elevation(4.dp),
                border = BorderStroke(1.dp, Color(0xFFB0BEC5))
            ) {
                Text("← Volver", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }

            if (showSelector.value) {
                Text("Selecciona dificultad:", fontSize = 18.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Difficulty.values().forEach { diff ->
                        Button(
                            onClick = { selectedDifficulty.value = diff },
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = if (selectedDifficulty.value == diff) Color.Gray else Color.LightGray
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(diff.name)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    controller.beginGame()
                }) {
                    Text("Iniciar juego")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(onClick = {
                    controller.showInstructions = true
                }) {
                    Text("Instrucciones")
                }

                Spacer(modifier = Modifier.height(16.dp))
                return@Column
            }

            Text(Strings.t("title_oddEmoji"), fontSize = 28.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Nivel: $level", fontSize = 18.sp)
                Text("Errores: $errors", fontSize = 18.sp)
                val avg = if (level <= 1) "—"
                else if (averageTime < 1000) "${averageTime.toInt()} ms"
                else "${(averageTime / 1000.0 * 10).toInt() / 10.0} s"
                Text("⏱ Avg: $avg", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            BoxWithConstraints(
                modifier = Modifier.padding(horizontal = 16.dp).weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                val clampedGridSize = gridSize.coerceAtMost(6)
                val cellSize = maxWidth / (gridSize + 1)

                LazyVerticalGrid(
                    columns = GridCells.Fixed(clampedGridSize),
                    modifier = Modifier
                        .width(cellSize * clampedGridSize)
                        .height(cellSize * clampedGridSize),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    userScrollEnabled = false
                ) {
                    grid.forEachIndexed { rowIndex, row ->
                        itemsIndexed(row) { colIndex, emoji ->
                            val isCorrect = correctClick == rowIndex to colIndex
                            val isWrong = wrongClick == rowIndex to colIndex

                            val scale by animateFloatAsState(
                                targetValue = if (isCorrect) 1.5f else 1f,
                                animationSpec = tween(durationMillis = 300),
                                label = "scale"
                            )

                            Box(
                                modifier = Modifier
                                    .size(cellSize)
                                    .graphicsLayer(scaleX = scale, scaleY = scale)
                                    .clickable { controller.selectEmoji(rowIndex, colIndex) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(emoji, fontSize = 32.sp)
                                if (isCorrect) Text("✔", color = Color.Green, fontSize = 20.sp)
                                if (isWrong) Text("❌", color = Color.Red, fontSize = 20.sp)
                            }
                        }
                    }
                }
            }

            if (isGameOver) {
                AlertDialog(
                    onDismissRequest = {},
                    title = { Text(Strings.t("game_over"), fontSize = 22.sp, fontWeight = FontWeight.Bold) },
                    text = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${Strings.t("level_reached")}: $maxLevel")
                            Text("${Strings.t("errors")}: $errors")
                            val avg = if (level <= 1) "—"
                            else if (averageTime < 1000) "${averageTime.toInt()} ms"
                            else "${(averageTime / 1000.0 * 10).toInt() / 10.0} s"
                            Text("⏱ Avg: $avg", fontSize = 18.sp)
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { controller.restartGame() }) {
                            Text(Strings.t("play_again"))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { controller.goBackToDifficultySelection() }) {
                            Text(Strings.t("back"))
                        }
                    },
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
    }
}

@Composable
fun oddEmojiInstructions(onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(Res.drawable.stroopTestBG),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Box(
                modifier = Modifier.fillMaxWidth().height(70.dp)
                    .background(Color(0xFFEFEFEF), shape = RoundedCornerShape(4.dp))
                    .border(BorderStroke(2.dp, Color(0xFFB0BEC5)))
            ) {
                IconButton(
                    onClick = { },
                    modifier = Modifier.align(Alignment.CenterStart).padding(start = 8.dp).size(48.dp)
                ) {
                    Image(painter = painterResource(Res.drawable.user), contentDescription = "User")
                }
                Image(
                    painter = painterResource(Res.drawable.logo),
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.Center).size(100.dp).scale(1.2f)
                )
                IconButton(
                    onClick = { },
                    modifier = Modifier.align(Alignment.CenterEnd).padding(end = 8.dp).size(48.dp)
                ) {
                    Image(painter = painterResource(Res.drawable.ajustes), contentDescription = "Settings")
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
            Text(Strings.t("instructions"), fontSize = 28.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(30.dp))
            Text(
                Strings.t("insOdd"),
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = Color.White,
                lineHeight = 24.sp,
                modifier = Modifier.padding(horizontal = 32.dp)
                    .background(Color(0xFF0647D7), shape = RoundedCornerShape(16.dp))
                    .padding(16.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = onBack,
                modifier = Modifier.height(48.dp).width(200.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.elevation(4.dp),
                border = BorderStroke(1.dp, Color(0xFFB0BEC5))
            ) {
                Text(Strings.t("start_game"), fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
