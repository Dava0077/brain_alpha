package brain.alpha.model.concentrationModel

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

/**
 * StroopModel.kt
 *
 * Handles the logic for the Stroop Test game.
 * Tracks score, timing, color logic, and round progression.
 */

object stroopModel {

    data class WordColorPair(val text: String, val color: Color)

    private val colorNames = listOf("Rojo", "Azul", "Verde", "Amarillo")
    private val colorMap = mapOf(
        "Rojo" to Color.Red,
        "Azul" to Color.Blue,
        "Verde" to Color.Green,
        "Amarillo" to Color.Yellow
    )

    var score by mutableStateOf(0)
        private set

    var timeLeft by mutableStateOf(30)
        private set

    var isGameOver by mutableStateOf(false)
        private set

    var currentWord by mutableStateOf(generateRandomWord())
        private set

    var options by mutableStateOf(colorMap.values.shuffled())
        private set

    var lastAnswerCorrect by mutableStateOf<Boolean?>(null)


    fun resetGame() {
        score = 0
        timeLeft = 30
        isGameOver = false
        currentWord = generateRandomWord()
        options = colorMap.values.shuffled()
    }

    fun submitAnswer(selectedColor: Color) {
        lastAnswerCorrect = selectedColor == currentWord.color
        if (lastAnswerCorrect == true) score++
        currentWord = generateRandomWord()
        options = colorMap.values.shuffled()
    }

    fun decreaseTimer() {
        timeLeft--
        if (timeLeft <= 0) {
            isGameOver = true
        }
    }

    fun getColorHex(name: String): Long {
        return colorMap[name]?.value?.toLong() ?: 0x000000
    }

    fun getColorName(color: Color): String {
        return colorMap.entries.find { it.value == color }?.key ?: "Desconocido"
    }
    fun generateRandomWord(): WordColorPair {
        val text = colorNames.random()
        val color = colorMap.values.random()
        return WordColorPair(text, color)
    }
}
