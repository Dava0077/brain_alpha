package brain.alpha.controller.concentrationController


import androidx.compose.runtime.*
import brain.alpha.model.shared.Difficulty
import brain.alpha.model.concentrationModel.OddEmojiModel

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

    fun startGame(difficulty: Difficulty) {
        model.startGame(difficulty)
        updateState()
        isGameOver.value = false
    }

    fun checkSelection(row: Int, col: Int) {
        if (isGameOver.value) return

        val correct = model.checkSelection(row, col)
        updateState()

        if (model.isGameOver()) {
            isGameOver.value = true
        } else if (correct) {
            model.generateLevel()
            updateState()
        }
    }

    fun getAverageTime(): Long {
        return model.getAverageTime()
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
    }
}
