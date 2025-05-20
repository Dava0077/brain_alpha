package brain.alpha.controller.mentalAgilityController



import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import brain.alpha.controller.logicController.wordSearchController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import brain.alpha.model.mentalAgilityModel.clickNumberModel
import brain.alpha.model.shared.Difficulty
import brain.alpha.strings.Strings

// Controller for managing logic and state of the Click Number game
object clickNumberController {

    // List of numbers currently displayed
    var numbers by mutableStateOf(listOf<Int>())
        private set
    // Navigation lambda to switch screens
    lateinit var navigateTo: (String) -> Unit
    // Current number to be found by the player
    var targetNumber by mutableStateOf(0)
        private set
    // Message to instruct the user
    var instructionMessage by mutableStateOf("")
        private set
    // Message to show result of last action
    var resultMessage by mutableStateOf("")
        private set
    // Remaining game time in seconds
    var timeLeft by mutableStateOf(30)
        private set
    // Player's current score
    var score by mutableStateOf(0)
        private set
    // Game over flag
    var gameOver by mutableStateOf(false)
        private set
    // Controls visibility of instructions screen
    val showInstructions = mutableStateOf(true)
    // Flag to indicate if the game is in progress
    var isGameStarted by mutableStateOf(false)
        private set
    // Controls visibility of game over dialog
    var showGameOverDialog by mutableStateOf(false)
        private set
    // Selected difficulty level
    var currentDifficulty by mutableStateOf(Difficulty.EASY) // Dificultad por defecto: Fácil
        private set

    // Timer job for countdown
    private var timerJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    // Set the difficulty level of the game
    fun setDifficulty(difficulty: Difficulty) {
        currentDifficulty = difficulty
    }

    // Start the game after showing instructions
    fun beginClickGame() {
        showInstructions.value = false
        startGame()
    }

    // Initializes the game state and timer
    fun startGame() {
        isGameStarted = true
        gameOver = false
        score = 0
        // Establecer el tiempo según la dificultad
        timeLeft = when (currentDifficulty) {
            Difficulty.EASY -> 60 // 1:30 minutos
            Difficulty.MEDIUM -> 30 // 1:00 minuto
            Difficulty.HARD -> 15 // 30 segundos
        }
        showGameOverDialog = false
        clickNumberModel.startGame()
        numbers = clickNumberModel.getNumbers()
        targetNumber = clickNumberModel.targetNumber
        instructionMessage = Strings.t("insCN15") +  "$targetNumber"
        resultMessage = ""
        startTimer()
    }

    // Handles logic when a number button is clicked
    fun handleButtonClick(selectedNumber: Int) {
        if (!gameOver && isGameStarted) {
            val isCorrect = clickNumberModel.checkNumber(selectedNumber)
            if (isCorrect) {
                score++
                resultMessage = Strings.t("insCN16")
                clickNumberModel.startGame()
                numbers = clickNumberModel.getNumbers()
                targetNumber = clickNumberModel.targetNumber
                instructionMessage = Strings.t("insCN15") + "$targetNumber"
            } else {
                resultMessage = Strings.t("insCN17")
            }
        }
    }

    // Starts the countdown timer for the game
    private fun startTimer() {
        timerJob?.cancel()
        timerJob = scope.launch {
            while (timeLeft > 0 && isGameStarted) {
                delay(1000L)
                timeLeft--
            }
            if (isGameStarted) {
                gameOver = true
                instructionMessage = Strings.t("insCN18")
                resultMessage = Strings.t("insCN19") + " $score"
                isGameStarted = false
                showGameOverDialog = true
            }
        }
    }

    // Reset all game state to initial values
    fun resetGame() {
        timerJob?.cancel()
        timerJob = null
        isGameStarted = false
        gameOver = false
        score = 0
        timeLeft = when (currentDifficulty) {
            Difficulty.EASY -> 60
            Difficulty.MEDIUM -> 30
            Difficulty.HARD -> 15
        }
        numbers = emptyList()
        targetNumber = 0
        instructionMessage = ""
        resultMessage = ""
        showGameOverDialog = false
    }

    // Reset and immediately restart the game
    fun restartGame() {
        resetGame()
        startGame()
    }

    // Navigate back to the logic games home screen and show instructions
    fun goHomeLogic() {
        resetGame()
        navigateTo("logic")
        wordSearchController.showInstructions.value = true
    }

    // Navigate back to general agility menu
    fun goHome() {
        resetGame()
        navigateTo("agility")
    }
}