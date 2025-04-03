package brain.alpha.controller.concentrationController

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import brain.alpha.model.concentrationModel.stroopModel
import kotlinx.coroutines.*

/**
* StroopController.kt
*
* Orchestrates the Stroop Test game logic, manages UI state,
* handles user input, round progression, and timing.
*/

object stroopController {

    // Reference to the Stroop game model
    private val model = stroopModel

    // Timer coroutine for countdown logic
    private var timerJob: Job? = null

    // Callback for navigating between screens (e.g. to home)
    lateinit var navigateTo: (String) -> Unit

    // Derived state: the current text shown to the player (e.g., "BLUE")
    val currentColorWord: State<String> get() = derivedStateOf { model.currentWord.text }
    // Derived state: the actual color the word is painted in (e.g., Color.Red)
    val colorToShow: State<Color> get() = derivedStateOf { model.currentWord.color }
    // Current score of the player
    val score: State<Int> get() = derivedStateOf { model.score }
    // Remaining time in seconds
    val timeLeft: State<Int> get() = derivedStateOf { model.timeLeft }
    // Game over status
    val showResult: State<Boolean> get() = derivedStateOf { model.isGameOver }
    // Whether the game is still running
    val isGameRunning: State<Boolean> get() = derivedStateOf { !model.isGameOver }

    // Feedback state: true = correct, false = incorrect, null = no feedback
    private val _answerFeedback = mutableStateOf<Boolean?>(null)
    val answerFeedback: State<Boolean?> get() = _answerFeedback

    // Flag to prevent multiple inputs during feedback delay
    var isAnswering by mutableStateOf(false)
        private set


    // The list of current answer options (shuffled colors)
    val options: List<Color> get() = model.options

    // Controls visibility of the instructions screen
    val showInstructions = mutableStateOf(true)


    /**
     * Checks whether the selected answer is correct and updates state.
     * Disables input during feedback delay and schedules the next round.
     */
    fun checkAnswer(color: Color) {
        if (model.isGameOver || isAnswering) return

        isAnswering = true

        // Lock current word state before it updates
        val current = model.currentWord
        val wasCorrect = color == current.color
        _answerFeedback.value = wasCorrect

        // Delay for feedback, then update word and allow next input
        CoroutineScope(Dispatchers.Main).launch {
            delay(600)
            model.submitAnswer(color)
            clearFeedback()
            isAnswering = false
        }
    }


    /**
     * Starts the game and hides instructions.
     */
    fun beginGame() {
        showInstructions.value = false
        startGame()
    }

    /**
     * Resets the game and starts the countdown timer.
     */
    private fun startGame() {
        model.resetGame()
        startTimer()
    }


    /**
     * Coroutine to decrease timer every second.
     */
    private fun startTimer() {
        timerJob?.cancel()
        timerJob = CoroutineScope(Dispatchers.Default).launch {
            while (model.timeLeft > 0) {
                delay(1000)
                model.decreaseTimer()
            }
        }
    }


    /**
     * Navigates to the home screen and resets instruction state.
     */
    fun goHome(){
        navigateTo("home")
        showInstructions.value = true
    }


    /**
     * Stops the timer coroutine.
     */
    fun stopGame() {
        timerJob?.cancel()
    }



    /**
     * Returns the color name from a Color object (e.g., Color.Red -> "Rojo").
     */
    fun getColorName(color: Color): String {
        return model.getColorName(color)
    }

    /**
     * Clears current feedback state (hides "Correct"/"Incorrect").
     */
    fun clearFeedback() {
        _answerFeedback.value = null
    }
}
