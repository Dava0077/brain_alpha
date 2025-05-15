package brain.alpha.controller.concentrationController

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import brain.alpha.model.concentrationModel.stroopModel
import brain.alpha.model.shared.Difficulty
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

    // Coroutine job used for the countdown timer
    private var timerJob: Job? = null

    // Navigation callback (set externally)
    lateinit var navigateTo: (String) -> Unit

    // State: word text shown to the player (e.g. "BLUE")
    val currentColorWord: State<String> get() = derivedStateOf { model.currentWord.text }
    // State: actual color of the displayed word (e.g. Color.Red)
    val colorToShow: State<Color> get() = derivedStateOf { model.currentWord.color }
    // State: current score value
    val score: State<Int> get() = derivedStateOf { model.score }
    // State: remaining time in seconds
    val timeLeft: State<Int> get() = derivedStateOf { model.timeLeft }
    // State: whether the game is finished
    val showResult: State<Boolean> get() = derivedStateOf { model.isGameOver }
    // State: whether the game is currently running
    val isGameRunning: State<Boolean> get() = derivedStateOf { !model.isGameOver }

    // Mutable state: true = correct, false = incorrect, null = no feedback
    private val _answerFeedback = mutableStateOf<Boolean?>(null)
    val answerFeedback: State<Boolean?> get() = _answerFeedback

    // Prevents interaction while feedback is being shown
    var isAnswering by mutableStateOf(false)
        private set


    // List of color options to display as answer buttons
    val options: List<Color> get() = model.options

    // Controls visibility of the instruction screen
    val showInstructions = mutableStateOf(false)

    // Total time based on selected difficulty
    val totalTime: State<Int> get() = derivedStateOf { model.getMaxTime() }

    // Flag: true if a game is active and still within time
    val isOngoingGame: Boolean
        get() = model.hasStartedGame && model.timeLeft > 0 && !model.isGameOver

    // Getter for current difficulty
    fun getCurrentDifficulty(): Difficulty = model.currentDifficulty

    // Sets the difficulty in the model
    fun setDifficulty(difficulty: Difficulty) {
        model.setDifficulty(difficulty)
    }

    // Shows instruction screen
    fun showInstructionsScreen() {
        showInstructions.value = true
    }

    /**
     * Handles user answer input.
     * Triggers feedback and moves to the next question.
     */
    fun checkAnswer(color: Color) {
        if (model.isGameOver || isAnswering) return

        isAnswering = true

        // Save current word for feedback evaluation
        val current = model.currentWord
        val wasCorrect = color == current.color
        _answerFeedback.value = wasCorrect

        // After short delay, proceed to next word
        CoroutineScope(Dispatchers.Main).launch {
            delay(600)
            model.submitAnswer(color)
            clearFeedback()
            isAnswering = false
        }
    }

    /**
     * Starts the game and hides the instruction screen.
     */
    fun beginGame() {
        showInstructions.value = false
        startGame()
    }

    // Continues the timer for a paused game
    fun continueGame() {
        startTimer()
    }

    // Initializes game and starts the countdown
    private fun startGame() {
        model.resetGame()
        startTimer()
    }

    /**
     * Coroutine-based countdown: decrements time every second.
     */
    private fun startTimer() {
        timerJob?.cancel() // Stop any previous timer
        timerJob = CoroutineScope(Dispatchers.Default).launch {
            while (model.timeLeft > 0) {
                delay(1000)
                model.decreaseTimer()
            }
        }
    }

    /**
     * Navigates to home screen and resets instruction state.
     */
    fun goHome(){
        stopGame()
        navigateTo("home")
        showInstructions.value = false
    }

    // Returns to concentration game selection
    fun goBackToConcentrationHome(){
        stopGame()
        navigateTo("concentration")
        showInstructions.value = false
    }

    // Returns to difficulty selection and resets state
    fun goBackToDifficultySelection(){
        stopGame()
        model.hasStartedGame = false
        showInstructions.value = false
    }

    // Navigates to any given route
    fun goBackTo(route: String) {
        navigateTo(route)
        showInstructions.value = false
    }

    /**
     * Stops the timer and any game logic loop.
     */
    fun stopGame() {
        timerJob?.cancel()
    }

    /**
     * Converts a Color object to its name for display.
     */
    fun getColorName(color: Color): String {
        return model.getColorName(color)
    }

    /**
     * Resets the answer feedback state.
     */
    fun clearFeedback() {
        _answerFeedback.value = null
    }


}
