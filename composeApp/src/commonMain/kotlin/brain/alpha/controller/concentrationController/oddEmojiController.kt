
package brain.alpha.controller.concentrationController

import androidx.compose.runtime.*
import brain.alpha.model.concentrationModel.oddEmojiModel

object oddEmojiController {

    private val model = oddEmojiModel

    lateinit var navigateTo: (String) -> Unit

    var showInstructions by mutableStateOf(true)
        private set

    val grid = mutableStateListOf<List<String>>()

    val correctClick = mutableStateOf<Pair<Int, Int>?>(null)
    val wrongClick = mutableStateOf<Pair<Int, Int>?>(null)

    val showParticles = mutableStateOf(false)

    var level by mutableStateOf(model.level)
    val maxLevel: Int get() = model.maxLevel
    var errors by mutableStateOf(model.errors)
    val averageTime: Double get() = model.getAverageTime()
    val gridSize: Int get() = model.gridSize
    val isGameOver = mutableStateOf(false)

    fun beginGame() {
        showInstructions = false
        startLevel()
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

            if (model.level > 10) {
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

    fun restartGame() {
        showInstructions = false
        isGameOver.value = false
        model.reset()
        startLevel()
    }
}
