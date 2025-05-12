package view

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import brain.alpha.controller.mentalAgilityController.mentalMathController
import brain.alpha.model.shared.Difficulty
import brain.alpha.utils.currentTimeMillis

import model.mentalMathModel
import org.jetbrains.compose.resources.painterResource
import brainalpha.composeapp.generated.resources.*

import kotlinx.coroutines.delay
import kotlinx.datetime.Clock

@Composable
fun mentalMathView(
    controller: mentalMathController,
    onNavigateToHome: () -> Unit
) {
    var currentQuestion by remember { mutableStateOf<mentalMathModel.Question?>(null) }
    var userAnswer by remember { mutableStateOf("") }
    var feedback by remember { mutableStateOf<String?>(null) }
    var isCorrect by remember { mutableStateOf<Boolean?>(null) }
    var gameStarted by remember { mutableStateOf(false) }
    var startTime by remember { mutableStateOf(0L) }
    var timeLeft by remember { mutableStateOf(0) }
    var showOverlay by remember { mutableStateOf(false) }
    var overlayColor by remember { mutableStateOf(Color.Transparent) }
    var selectedDifficulty by remember { mutableStateOf(Difficulty.MEDIUM) }
    var flashRed by remember { mutableStateOf(false) }


    // Timer effect
    LaunchedEffect(gameStarted, timeLeft) {
        if (gameStarted && timeLeft > 0) {
            delay(1000)
            timeLeft--
        } else if (gameStarted && timeLeft == 0) {
            gameStarted = false
            feedback = "¡Tiempo agotado!"
        }
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
            // Encabezado superior
            TopAppBar(
                backgroundColor = Color(0xFF222937),
                contentColor = Color.White,
                elevation = 4.dp
            ) {
                IconButton(onClick = {}) {
                    Image(painterResource(Res.drawable.user), contentDescription = "User")
                }
                Spacer(Modifier.weight(1f))
                Image(
                    painterResource(Res.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier.size(100.dp)
                )
                Spacer(Modifier.weight(1f))
                IconButton(onClick = {}) {
                    Image(painterResource(Res.drawable.ajustes), contentDescription = "Settings")
                }
            }

            Spacer(Modifier.height(10.dp))

            // Botón volver
            Button(
                onClick = {
                    gameStarted = false
                    controller.resetGame()
                    onNavigateToHome()
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Yellow),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.align(Alignment.End).padding(8.dp)
            ) {
                Text("⬅ Volver", color = Color.Black)
            }

            // Selector dificultad antes de iniciar juego
            if (!gameStarted) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Dificultad:", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(8.dp))
                    DropdownMenuBox(selected = selectedDifficulty) {
                        selectedDifficulty = it
                        controller.setDifficulty(it)
                    }
                }

            }

            Spacer(Modifier.height(16.dp))

            // Cuerpo principal
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (!gameStarted) {
                        Button(
                            onClick = {
                                controller.setDifficulty(selectedDifficulty)
                                gameStarted = true
                                timeLeft = controller.getTimeLimit()
                                controller.resetGame()
                                controller.startGame {
                                    currentQuestion = it
                                    startTime = currentTimeMillis()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Yellow)
                        ) {
                            Text("Iniciar Juego", color = Color.Black)
                        }
                    } else if (controller.isGameOver()) {
                        Text(
                            text = "¡Juego Terminado!\nPuntuación: ${controller.getScore()} de ${controller.getTotalQuestions() * 10}",
                            fontSize = 24.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = {
                            gameStarted = false
                            feedback = null
                            isCorrect = null
                            userAnswer = ""
                        }) {
                            Text("Jugar de Nuevo")
                        }
                    } else {
                        currentQuestion?.let { question ->
                            Text(
                                text = "Pregunta ${controller.getCurrentQuestionNumber()}: ${question.text}",
                                fontSize = 24.sp,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Text("Tiempo restante: $timeLeft s", fontWeight = FontWeight.Bold, color = Color.Red)

                            Spacer(modifier = Modifier.height(16.dp))

                            TextField(
                                value = userAnswer,
                                onValueChange = { userAnswer = it },
                                label = { Text("Tu respuesta") },
                                modifier = Modifier.width(200.dp),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    val answer = userAnswer.toIntOrNull()
                                    if (answer != null) {
                                        val timeTaken = (currentTimeMillis() - startTime) / 1000.0
                                        controller.submitAnswer(answer, timeTaken) { correct, score, message ->
                                            isCorrect = correct
                                            feedback = if (correct) "¡Correcto! Puntuación: $score" else message
                                            userAnswer = ""
                                            if (!controller.isGameOver()) {
                                                controller.startGame {
                                                    currentQuestion = it
                                                    startTime = currentTimeMillis()
                                                }
                                            }
                                        }
                                    } else {
                                        feedback = "Por favor, ingresa un número válido"
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Yellow)
                            ) {
                                Text("Enviar Respuesta", color = Color.Black)
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            feedback?.let {
                                Text(
                                    text = it,
                                    color = if (isCorrect == true) Color.Green else Color.Red,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DropdownMenuBox(selected: Difficulty, onSelect: (Difficulty) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        Button(onClick = { expanded = true }) {
            Text(text = selected.name.lowercase().replaceFirstChar { it.uppercase() })
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            Difficulty.values().forEach { diff ->
                DropdownMenuItem(onClick = {
                    onSelect(diff)
                    expanded = false
                }) {
                    Text(diff.name.lowercase().replaceFirstChar { it.uppercase() })
                }
            }
        }
    }
}
