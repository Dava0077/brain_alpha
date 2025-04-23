
package brain.alpha.controller.concentrationController

import androidx.compose.runtime.*
import brain.alpha.model.concentrationModel.oddEmojiModel
import brain.alpha.model.shared.Difficulty

object oddEmojiController {

    private val model = oddEmojiModel

    lateinit var navigateTo: (String) -> Unit

    var showInstructions by mutableStateOf(false)

    val grid = mutableStateListOf<List<String>>()

    val correctClick = mutableStateOf<Pair<Int, Int>?>(null)
    val wrongClick = mutableStateOf<Pair<Int, Int>?>(null)

    val showParticles = mutableStateOf(false)

    var level by mutableStateOf(model.level)
    val maxLevel: Int get() = model.getMaxLevel()
    var errors by mutableStateOf(model.errors)
    val averageTime: Double get() = model.getAverageTime()
    val gridSize: Int get() = model.gridSize
    val isGameOver = mutableStateOf(false)

    val selectedDifficulty = mutableStateOf(Difficulty.MEDIUM)
    val isGameStarted = mutableStateOf(false)
    val showDifficultySelector = mutableStateOf(true)

    fun beginGame() {
        showInstructions = false
        isGameStarted.value = true
        showDifficultySelector.value = false
        model.setDifficulty(selectedDifficulty.value)
        model.reset()
        model.startLevel()
        grid.clear()
        grid.addAll(model.grid)
        level = model.level
        errors = model.errors
    }

    fun startLevel() {
        model.startLevel()
        grid.clear()
        grid.addAll(model.grid)
        level = model.level
        errors = model.errors
    }

    fun selectEmoji(row: Int, col: Int) {
        val correct = model.select(row, col)
        level = model.level
        errors = model.errors
        if (correct) {
            correctClick.value = row to col
            showParticles.value = true

            if (model.hasFinished()) {
                isGameOver.value = true
            } else {
                startLevel()
            }
        } else if (model.errors >= 3) {
            wrongClick.value = row to col
            isGameOver.value = true
        }
    }



    fun goConcentration() {
        showInstructions = true
        isGameOver.value = false
        model.reset()
        navigateTo("concentration")
    }

    fun goBackToDifficultySelection() {
        showInstructions = false
        isGameStarted.value = false
        showDifficultySelector.value = true
        model.reset()
    }

    fun restartGame() {
        showInstructions = false
        isGameOver.value = false
        correctClick.value = null
        wrongClick.value = null
        showParticles.value = false
        isGameStarted.value = true
        showDifficultySelector.value = false
        model.reset()
        model.setDifficulty(selectedDifficulty.value)
        startLevel()
    }
}
