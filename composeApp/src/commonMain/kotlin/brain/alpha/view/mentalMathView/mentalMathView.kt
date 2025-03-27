package view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import controller.MentalMathController
import model.Question
import kotlin.system.measureTimeMillis

@Composable
fun mentalMathView(controller: MentalMathController) { // Cambiado a minúscula para seguir las convenciones
    var currentQuestion by remember { mutableStateOf<Question?>(null) }
    var userAnswer by remember { mutableStateOf("") }
    var feedback by remember { mutableStateOf<String?>(null) }
    var isCorrect by remember { mutableStateOf<Boolean?>(null) }
    var gameStarted by remember { mutableStateOf(false) }
    var startTime by remember { mutableStateOf(0L) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (!gameStarted) {
            Button(onClick = {
                gameStarted = true
                controller.resetGame()
                controller.startGame { question ->
                    currentQuestion = question
                    startTime = System.currentTimeMillis()
                }
            }) {
                Text("Iniciar Juego")
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
                    text = "Pregunta ${controller.getTotalQuestions() - controller.getTotalQuestions() + 1}: ${question.text}",
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = userAnswer,
                    onValueChange = { userAnswer = it },
                    label = { Text("Tu respuesta") },
                    modifier = Modifier.width(200.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    val answer = userAnswer.toIntOrNull()
                    if (answer != null) {
                        val timeTaken = (System.currentTimeMillis() - startTime) / 1000.0
                        controller.submitAnswer(answer, timeTaken) { correct, score, message ->
                            isCorrect = correct
                            feedback = if (correct) "¡Correcto! Puntuación: $score" else message
                            userAnswer = ""
                            if (!controller.isGameOver()) {
                                controller.startGame { newQuestion ->
                                    currentQuestion = newQuestion
                                    startTime = System.currentTimeMillis()
                                }
                            }
                        }
                    } else {
                        feedback = "Por favor, ingresa un número válido"
                    }
                }) {
                    Text("Enviar Respuesta")
                }

                Spacer(modifier = Modifier.height(16.dp))
                feedback?.let {
                    Text(
                        text = it,
                        color = if (isCorrect == true) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}