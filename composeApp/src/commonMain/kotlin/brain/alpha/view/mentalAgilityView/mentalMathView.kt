package view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import brain.alpha.utils.currentTimeMillis
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import brain.alpha.controller.mentalAgilityController.mentalMathController
import brainalpha.composeapp.generated.resources.*
import brainalpha.composeapp.generated.resources.Res
import brainalpha.composeapp.generated.resources.ajustes
import brainalpha.composeapp.generated.resources.logo
import brainalpha.composeapp.generated.resources.user
import model.mentalMathModel
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import brain.alpha.utils.currentTimeMillis

import kotlinx.coroutines.delay

import org.jetbrains.compose.resources.painterResource

@Composable
fun mentalMathView(controller: mentalMathController, onNavigateToHome: () -> Unit,) { // Cambiado a minúscula para seguir las convenciones
    var currentQuestion by remember { mutableStateOf<mentalMathModel.Question?>(null) }
    var userAnswer by remember { mutableStateOf("") }
    var feedback by remember { mutableStateOf<String?>(null) }
    var isCorrect by remember { mutableStateOf<Boolean?>(null) }
    var gameStarted by remember { mutableStateOf(false) }
    var startTime by remember { mutableStateOf(0L) }
    var showOverlay by remember { mutableStateOf(false) }
    var overlayColor by remember { mutableStateOf(Color.Transparent)}

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(Res.drawable.fondoMates),
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
                    .border(BorderStroke(2.dp, Color(0xFF4CE3E8))))
            {

                IconButton(
                    onClick = { /* Dummy action */ },
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
                    onClick = { /* Dummy action */ },
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

            }
            Spacer(modifier = Modifier.height(5.dp)) // Espacio tras el logo

            Button(
                onClick = {controller.goHome()},
                modifier = Modifier.align(Alignment.End),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF0647D7)),
            ){
                Text("ATRAS", color = Color.White)
            }
            val overlayAlpha by animateFloatAsState(
                targetValue = if (showOverlay) 0.5f else 0f,
                animationSpec = tween(durationMillis = 300, easing = LinearEasing),
                finishedListener = {
                    if (it == 0f) {
                        showOverlay = false
                        overlayColor = Color.Transparent
                    }
                }
            )


            // Función para mostrar el overlay y reproducir sonido
            suspend fun showFeedbackOverlay(isCorrect: Boolean) {
                overlayColor = if (isCorrect) Color.Green else Color.Red
                showOverlay = true
                //playSound(isCorrect) // Llamada a la función para reproducir sonido
                delay(300) // Duración del overlay
            }


            // Función para manejar el envío de la respuesta
            LaunchedEffect(isCorrect) {
                if (isCorrect != null) {
                    showFeedbackOverlay(isCorrect!!)
                }
            }


            // Función para manejar el envío de la respuesta
            fun submitAnswer() {
                val answer = userAnswer.toIntOrNull()
                if (answer != null) {
                    val timeTaken = (currentTimeMillis() - startTime) / 1000.0
                    controller.submitAnswer(answer, timeTaken) { correct, score, message ->
                        isCorrect = correct
                        feedback = if (correct) "¡Correcto! Puntuación: $score" else message
                        userAnswer = ""
                        if (!controller.isGameOver()) {
                            controller.startGame { newQuestion ->
                                currentQuestion = newQuestion
                                startTime = currentTimeMillis()
                            }
                        }
                    }
                } else {
                    feedback = "Por favor, ingresa un número válido"
                }
            }


            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (!gameStarted) {
                        Button(onClick = {
                            gameStarted = true
                            controller.resetGame()
                            controller.startGame { question ->
                                currentQuestion = question
                                startTime = currentTimeMillis()
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
                                text = "Pregunta ${controller.getCurrentQuestionNumber()}: ${question.text}",
                                fontSize = 24.sp,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))


                            TextField(
                                value = userAnswer,
                                onValueChange = { userAnswer = it },
                                label = { Text("Tu respuesta") },
                                modifier = Modifier
                                    .width(200.dp)
                                    .onKeyEvent { event ->
                                        if (event.key == Key.Enter && event.type == KeyEventType.KeyUp) {
                                            submitAnswer()
                                            true
                                        } else {
                                            false
                                        }
                                    },
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(16.dp))


                            Button(onClick = { submitAnswer() }) {
                                Text("Enviar Respuesta")
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


                // Overlay para el efecto de color
                if (showOverlay) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(overlayColor.copy(alpha = overlayAlpha))
                    )
                }
            }

        }


        // Función para reproducir sonido (implementación específica por plataforma)




        /*if (!gameStarted) {
            Button(onClick = {
                gameStarted = true
                controller.resetGame()
                controller.startGame { question ->
                    currentQuestion = question
                    startTime = currentTimeMillis()
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
                    modifier = Modifier
                        .width(200.dp)
                        .background(Color.White)

                )
                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    val answer = userAnswer.toIntOrNull()
                    if (answer != null) {
                        val timeTaken = (currentTimeMillis() - startTime) / 1000.0
                        controller.submitAnswer(answer, timeTaken) { correct, score, message ->
                            isCorrect = correct
                            feedback = if (correct) "¡Correcto! Puntuación: $score" else message
                            userAnswer = ""
                            if (!controller.isGameOver()) {
                                controller.startGame { newQuestion ->
                                    currentQuestion = newQuestion
                                    startTime = currentTimeMillis()
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
                        color = if (isCorrect == true) MaterialTheme.colors.primary else MaterialTheme.colors.error,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }*/

        }
    fun playSound(isCorrect: Boolean) {
        // Esta función debe implementarse según la plataforma
        // Por ahora, dejaremos un comentario con las instrucciones
        if (isCorrect) {
            // Reproducir sonido de respuesta correcta
            // Ejemplo: playSound("correct_sound.mp3")
        } else {
            // Reproducir sonido de respuesta incorrecta
            // Ejemplo: playSound("incorrect_sound.mp3")
        }
    }
    }



