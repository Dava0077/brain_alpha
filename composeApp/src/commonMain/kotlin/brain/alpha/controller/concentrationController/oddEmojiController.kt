package brain.alpha.controller.concentrationController


import androidx.compose.runtime.*
import brain.alpha.model.shared.Difficulty
import brain.alpha.model.concentrationModel.OddEmojiModel
import kotlinx.coroutines.*

object oddEmojiController {

    // Callback for navigating between screens (e.g. to home)
    lateinit var navigateTo: (String) -> Unit

    private val model = OddEmojiModel()

    var grid = mutableStateOf(listOf<List<String>>())
        private set

    var level = mutableStateOf(1)
        private set

    var errors = mutableStateOf(0)
        private set

    var isGameOver = mutableStateOf(false)
        private set

    var showInstructions = mutableStateOf(false)
    var isAnswering = false

    var score = mutableStateOf(0)
        private set

    var timeLeft = mutableStateOf(30)
    private var timerJob: Job? = null
    private var timeLimit = 30
    private var currentDifficulty: Difficulty = Difficulty.EASY

    fun startGame(difficulty: Difficulty) {
        model.startGame(difficulty)
        currentDifficulty = difficulty
        timeLimit = when (difficulty) {
            Difficulty.EASY -> 30
            Difficulty.MEDIUM -> 15
            Difficulty.HARD -> 10
        }
        updateState()
        isGameOver.value = false
        startTimer()
    }

    fun checkSelection(row: Int, col: Int) {
        if (isGameOver.value) return
        timerJob?.cancel()

        val correct = model.checkSelection(row, col)
        updateState()

        if (model.isGameOver()) {
            isGameOver.value = true
        } else if (correct) {
            model.generateLevel()
            updateState()
            startTimer()
        }
    }

    fun getAverageTime(): Long {
        return model.getAverageTime()
    }

    fun getTimeProgress(): Float {
        return timeLeft.value.toFloat() / timeLimit
    }

    fun resetGame() {
        model.currentLevel = 1
        model.errors = 0
        isGameOver.value = false
        showInstructions.value = false
        grid.value = listOf()
    }

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

    fun backToDifficultyMenu() {
        showInstructions.value = false
        resetGame()
        stopGame()
    }

    fun goBackToConcentrationHome(){
        stroopController.stopGame()
        navigateTo("concentration")
        stroopController.showInstructions.value = false
    }

    private fun updateState() {
        grid.value = model.grid
        level.value = model.currentLevel
        errors.value = model.errors
        score.value = model.score
    }

    fun getFinalScore(): Int {
        return model.getFinalScore()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timeLeft.value = timeLimit
        timerJob = CoroutineScope(Dispatchers.Default).launch {
            while (timeLeft.value > 0) {
                delay(1000)
                timeLeft.value -= 1
            }
            withContext(Dispatchers.Main) {
                handleTimeout()
            }
        }
    }

    private fun handleTimeout() {
        model.errors++
        if (model.isGameOver()) {
            isGameOver.value = true
        } else {
            model.generateLevel()
            updateState()
            startTimer()
        }
    }
}
