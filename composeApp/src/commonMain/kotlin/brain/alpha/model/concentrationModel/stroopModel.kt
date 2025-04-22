package brain.alpha.model.concentrationModel

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import brain.alpha.model.shared.Difficulty
import kotlin.random.Random

/**
 * StroopModel.kt
 *
 * Handles the logic for the Stroop Test game.
 * Manages score, timer, color-word combinations, and answer validation.
 */

object stroopModel {

    /**
     * Represents one Stroop task: the word shown and the color it's displayed in.
     */
    data class WordColorPair(val text: String, val color: Color)

    // List of possible color names (used as text)
    private val colorNames = listOf("Rojo", "Azul", "Verde", "Amarillo")

    // Mapping of color names to their Color objects
    private val colorMap = mapOf(
        "Rojo" to Color.Red,
        "Azul" to Color.Blue,
        "Verde" to Color.Green,
        "Amarillo" to Color.Yellow
    )

    // Current score, increases when player selects the correct color
    var score by mutableStateOf(0)
        private set

    // Countdown timer (seconds remaining)
    var timeLeft by mutableStateOf(30)
        private set

    // Game over flag, true when time runs out
    var isGameOver by mutableStateOf(false)
        private set

    // Current word and its color to display in the round
    var currentWord by mutableStateOf(generateRandomWord())
        private set

    // Options shown to the player (shuffled color values)
    var options by mutableStateOf(colorMap.values.shuffled())
        private set

    // Last answer correctness (for optional external use)
    var lastAnswerCorrect by mutableStateOf<Boolean?>(null)

    var currentDifficulty by mutableStateOf(Difficulty.MEDIUM)
        private set

    var hasStartedGame by mutableStateOf(false)
        private set

    fun setDifficulty(difficulty: Difficulty) {
        currentDifficulty = difficulty
    }




    private fun getTimeForDifficulty(): Int {
        return when (currentDifficulty) {
            Difficulty.EASY -> 45
            Difficulty.MEDIUM -> 30
            Difficulty.HARD -> 15
        }
    }


    fun getMaxTime(): Int {
        return when (currentDifficulty) {
            Difficulty.EASY -> 45
            Difficulty.MEDIUM -> 30
            Difficulty.HARD -> 15
        }
    }


    /**
     * Resets all game values to start a new session.
     */
    fun resetGame() {
        score = 0
        timeLeft = getTimeForDifficulty()
        isGameOver = false
        currentWord = generateRandomWord()
        options = colorMap.values.shuffled()
        hasStartedGame = true
    }


    /**
     * Processes user's answer and prepares the next round.
     */
    fun submitAnswer(selectedColor: Color) {
        if (isGameOver) return
        if (selectedColor == currentWord.color) {
            score++
        }
        currentWord = generateRandomWord()
        options = colorMap.values.shuffled()
    }



    /**
     * Decreases the countdown timer.
     * Triggers game over when time reaches 0.
     */
    fun decreaseTimer() {
        timeLeft--
        if (timeLeft <= 0) {
            isGameOver = true
        }
    }

    /**
     * Converts a color name to its hexadecimal value.
     */
    fun getColorHex(name: String): Long {
        return colorMap[name]?.value?.toLong() ?: 0x000000
    }

    /**
     * Returns the name of a given Color value.
     */
    fun getColorName(color: Color): String {
        return colorMap.entries.find { it.value == color }?.key ?: "Desconocido"
    }

    /**
     * Randomly creates a new word-color pair.
     * The text and color may or may not match.
     */
    fun generateRandomWord(): WordColorPair {
        val text = colorNames.random()
        val color = colorMap.values.random()
        return WordColorPair(text, color)
    }
}
