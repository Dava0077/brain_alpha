package brain.alpha.controller.concentrationController

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import brain.alpha.model.concentrationModel.stroopModel
import kotlinx.coroutines.*

/**
 * StroopController.kt
 *
 * Orchestrates Stroop game logic, UI states, and round updates.
 */

object stroopController {

    private val model = stroopModel
    private var timerJob: Job? = null
    lateinit var navigateTo: (String) -> Unit

    val currentColorWord: State<String> get() = derivedStateOf { model.currentWord.text }
    val colorToShow: State<Color> get() = derivedStateOf { model.currentWord.color }
    val score: State<Int> get() = derivedStateOf { model.score }
    val timeLeft: State<Int> get() = derivedStateOf { model.timeLeft }
    val showResult: State<Boolean> get() = derivedStateOf { model.isGameOver }
    val isGameRunning: State<Boolean> get() = derivedStateOf { !model.isGameOver }
    private val _answerFeedback = mutableStateOf<Boolean?>(null)
    val answerFeedback: State<Boolean?> get() = _answerFeedback

    var isAnswering by mutableStateOf(false)
        private set


    val options: List<Color> get() = model.options

    val showInstructions = mutableStateOf(true)

    fun checkAnswer(color: Color) {
        if (model.isGameOver || isAnswering) return

        isAnswering = true
        model.submitAnswer(color)
        _answerFeedback.value = color == model.currentWord.color

        CoroutineScope(Dispatchers.Main).launch {
            delay(600)
            clearFeedback()
            isAnswering = false
        }
    }

    fun beginGame() {
        showInstructions.value = false
        startGame()
    }

    private fun startGame() {
        model.resetGame()
        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = CoroutineScope(Dispatchers.Default).launch {
            while (model.timeLeft > 0) {
                delay(1000)
                model.decreaseTimer()
            }
        }
    }
    fun goHome(){
        navigateTo("home")
        showInstructions.value = true
    }

    fun stopGame() {
        timerJob?.cancel()
    }

    fun getColorName(color: Color): String {
        return model.getColorName(color)
    }

    fun clearFeedback() {
        _answerFeedback.value = null
    }
}
