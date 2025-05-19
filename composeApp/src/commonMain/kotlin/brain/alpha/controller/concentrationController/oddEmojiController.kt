// Import necessary packages and libraries
package brain.alpha.controller.concentrationController

// Import for state management in Compose
import androidx.compose.runtime.*
// Import for game difficulty management
import brain.alpha.model.shared.Difficulty
// Import for the OddEmojiModel class
import brain.alpha.model.concentrationModel.OddEmojiModel
// Import for coroutine handling
import kotlinx.coroutines.*

// Define the object to manage the Odd Emoji game logic and UI interaction
object oddEmojiController {

    // Callback for navigating between screens (e.g. to home)
    lateinit var navigateTo: (String) -> Unit

    // Instance of the OddEmojiModel to handle game logic
    private val model = OddEmojiModel()

    // Mutable state representing the game grid
    var grid = mutableStateOf(listOf<List<String>>())
        private set

    // Mutable state representing the current level
    var level = mutableStateOf(1)
        private set

    // Mutable state representing the number of errors made
    var errors = mutableStateOf(0)
        private set

    // Mutable state to track if the game is over
    var isGameOver = mutableStateOf(false)
        private set

    // Mutable state to control the visibility of instructions
    var showInstructions = mutableStateOf(false)
    // Flag to indicate if the player is currently answering
    var isAnswering = false

    // Mutable state representing the current score
    var score = mutableStateOf(0)
        private set

    // Mutable state for tracking the remaining time in seconds
    var timeLeft = mutableStateOf(30)
    // Coroutine job for the timer
    private var timerJob: Job? = null
    // Default time limit in seconds
    private var timeLimit = 30
    // Current game difficulty level
    private var currentDifficulty: Difficulty = Difficulty.EASY

    // Function to start a new game with a specified difficulty
    fun startGame(difficulty: Difficulty) {
        // Initialize the game model
        model.startGame(difficulty)
        // Update the current difficulty
        currentDifficulty = difficulty

        // Set the time limit based on the difficulty level
        timeLimit = when (difficulty) {
            Difficulty.EASY -> 30
            Difficulty.MEDIUM -> 15
            Difficulty.HARD -> 10
        }

        // Update the UI state
        updateState()
        // Reset the game over state
        isGameOver.value = false
        // Start the timer
        startTimer()
    }

    // Function to check the player's selection
    fun checkSelection(row: Int, col: Int) {
        // Exit if the game is already over
        if (isGameOver.value) return

        // Cancel the timer
        timerJob?.cancel()

        // Check if the selected position is correct
        val correct = model.checkSelection(row, col)
        // Update the UI state
        updateState()

        // If the game is over, update the game over state
        if (model.isGameOver()) {
            isGameOver.value = true
        }
        // Otherwise, if the selection was correct, generate a new level
        else if (correct) {
            model.generateLevel()
            updateState()
            startTimer()
        }
    }

    // Function to get the average time taken per level
    fun getAverageTime(): Long {
        return model.getAverageTime()
    }

    // Function to calculate the time progress as a percentage
    fun getTimeProgress(): Float {
        return timeLeft.value.toFloat() / timeLimit
    }

    // Function to reset the game state
    fun resetGame() {
        // Reset the model state
        model.currentLevel = 1
        model.errors = 0

        // Update UI states
        isGameOver.value = false
        showInstructions.value = false
        grid.value = listOf()
    }

    // Function to stop the game and reset all states
    fun stopGame() {
            model.currentLevel = 1
            model.errors = 0
            isGameOver.value = false
            showInstructions.value = false
            isAnswering = false
            grid.value = listOf()
            level.value = 1
            errors.value = 0
    }

    // Function to navigate back to the difficulty menu
    fun backToDifficultyMenu() {
        showInstructions.value = false
        resetGame()
        stopGame()
    }

    // Function to navigate back to the home screen
    fun goBackToConcentrationHome(){
        // Stop the game and navigate to the concentration home screen
        stroopController.stopGame()
        navigateTo("concentration")
        stroopController.showInstructions.value = false
    }

    // Private function to update the UI states based on the model state
    private fun updateState() {
        grid.value = model.grid
        level.value = model.currentLevel
        errors.value = model.errors
        score.value = model.score
    }

    // Function to get the final score after the game is over
    fun getFinalScore(): Int {
        return model.getFinalScore()
    }

    // Private function to start the timer
    private fun startTimer() {
        // Cancel any existing timer job
        timerJob?.cancel()
        // Initialize the timer state
        timeLeft.value = timeLimit

        // Start a coroutine for the timer countdown
        timerJob = CoroutineScope(Dispatchers.Default).launch {
            while (timeLeft.value > 0) {
                // Wait for 1 second
                delay(1000)
                // Decrease the time left
                timeLeft.value -= 1
            }

            // Switch back to the main thread to handle timeout
            withContext(Dispatchers.Main) {
                handleTimeout()
            }
        }
    }

    // Private function to handle timeout when time runs out
    private fun handleTimeout() {
        // Increase the error count
        model.errors++

        // If the game is over, set the game over state
        if (model.isGameOver()) {
            isGameOver.value = true
        }
        // Otherwise, generate a new level and restart the timer
        else {
            model.generateLevel()
            updateState()
            startTimer()
        }
    }
}
