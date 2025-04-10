package brain.alpha.view.concentrationViews

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import brain.alpha.controller.concentrationController.oddEmojiController
import brain.alpha.strings.Strings
import brainalpha.composeapp.generated.resources.*
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import kotlin.random.Random

@Composable
fun oddEmojiScreen(controller: oddEmojiController) {

    val grid = remember { controller.grid }

    val correctClick by controller.correctClick
    val wrongClick by controller.wrongClick

    val showParticles by controller.showParticles

    val level = controller.level
    val maxLevel = controller.maxLevel
    val errors = controller.errors
    val averageTime = controller.averageTime
    val gridSize = controller.gridSize
    val showInstructions = controller.showInstructions
    val isGameOver by controller.isGameOver

    if (showInstructions) {
        oddEmojiInstructions(
            onStart = { controller.beginGame() },
            controller = controller
        )
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(Res.drawable.fondoMemory),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Top Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .background(Color(0xFF222937), shape = RoundedCornerShape(4.dp))
                    .border(BorderStroke(2.dp, Color(0xFF4CE3E8)))
            ) {
                IconButton(
                    onClick = { /* Usuario */ },
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
                    onClick = { brain.alpha.controller.LoginController.buttonSettings() },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 8.dp)
                        .size(48.dp)
                ) {
                    Image(painter = painterResource(Res.drawable.ajustes), contentDescription = "Settings")
                }
            }

            Spacer(modifier = Modifier.height(5.dp))

            Button(
                onClick = { controller.goConcentration() },
                modifier = Modifier
                    .align(Alignment.End)
                    .height(46.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF0647D7)),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.elevation(4.dp),
                border = BorderStroke(1.dp, Color(0xFFB0BEC5))
            ) {
                Text(Strings.t("back"), fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = Strings.t("title_oddEmoji"),
                fontSize = 36.sp,
                color = Color.Black,
                fontFamily = FontFamily.SansSerif,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Nivel: $level", fontSize = 18.sp)
                Text("Errores: $errors", fontSize = 18.sp)
                val avgTimeDisplay = if (averageTime < 1000)
                    "${averageTime.toInt()} ms"
                else
                    "${(averageTime / 1000.0).let { (it * 10).toInt() / 10.0 }} s"

                Text("⏱ Avg: $avgTimeDisplay", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            BoxWithConstraints(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .weight(1f) // Usa espacio restante vertical
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                // Este gridSize local fuerza un máximo
                val clampedGridSize = (gridSize).coerceAtMost(6)
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
                                label = "scaleAnimation"
                            )

                            Box(
                                modifier = Modifier
                                    .size(cellSize)
                                    .graphicsLayer(scaleX = scale, scaleY = scale)
                                    .clickable { controller.selectEmoji(rowIndex, colIndex) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = emoji, fontSize = 32.sp)
                                if (isCorrect) {
                                    Text("✔", fontSize = 24.sp, color = Color.Green, modifier = Modifier.offset(y = (-24).dp))
                                    if (showParticles) ConfettiParticles()
                                }
                                if (isWrong) {
                                    Text("❌", fontSize = 24.sp, color = Color.Red, modifier = Modifier.offset(y = (-24).dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        if (isGameOver) {
            AlertDialog(
                onDismissRequest = {},
                title = {
                    Text(Strings.t("game_over"), fontSize = 22.sp, fontWeight = FontWeight.Bold)
                },
                text = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${Strings.t("level_reached")}: $maxLevel")
                        Text("${Strings.t("errors")}: $errors")
                        val avgTimeDisplay = if (averageTime < 1000)
                            "${averageTime.toInt()} ms"
                        else
                            "${(averageTime / 1000.0).let { (it * 10).toInt() / 10.0 }} s"

                        Text("⏱ Avg: $avgTimeDisplay", fontSize = 18.sp)
                    }
                },
                confirmButton = {
                    TextButton(onClick = { controller.restartGame() }) {
                        Text(Strings.t("play_again"))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { controller.goConcentration() }) {
                        Text(Strings.t("back"))
                    }
                },
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}

@Composable
fun oddEmojiInstructions(onStart: () -> Unit, controller: oddEmojiController) {
    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(Res.drawable.fondoMemory),
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
                    .background(Color(0xFF222937), shape = RoundedCornerShape(4.dp))
                    .border(BorderStroke(2.dp, Color(0xFF4CE3E8)))
            ) {
                IconButton(
                    onClick = { /* Dummy user */ },
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
            }

            Spacer(modifier = Modifier.height(100.dp))

            Text(
                Strings.t("instructions"),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121)
            )

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = Strings.t("insOdd"),
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = Color.White,
                lineHeight = 24.sp,
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .background(Color(0xFF0647D7), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

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

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = { controller.goConcentration() }
            ) {
                Text(Strings.t("back"))
            }
        }
    }

}

@Composable
fun ConfettiParticles() {
    val particles = remember { List(8) { Offset.Zero } }
    val animation = rememberInfiniteTransition()
    val yOffsets by animation.animateFloat(
        initialValue = 0f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "confetti"
    )

    Canvas(modifier = Modifier.size(60.dp)) {
        particles.forEachIndexed { i, _ ->
            drawCircle(
                color = Color(
                    red = Random.nextFloat(),
                    green = Random.nextFloat(),
                    blue = Random.nextFloat(),
                    alpha = 0.8f
                ),
                radius = 4f,
                center = Offset(
                    x = (i * 8 + 10).toFloat(),
                    y = size.height / 2 + yOffsets + Random.nextInt(-5, 5)
                )
            )
        }
    }
}
