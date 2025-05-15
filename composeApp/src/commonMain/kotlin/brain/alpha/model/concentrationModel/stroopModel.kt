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

    // Represents a single challenge in the Stroop test: the text shown and the color it is displayed in
    data class WordColorPair(val text: String, val color: Color)

    // Possible color names that can appear as text
    private val colorNames = listOf("Rojo", "Azul", "Verde", "Amarillo")

    // Mapping of color name strings to actual Color objects
    private val colorMap = mapOf(
        "Rojo" to Color.Red,
        "Azul" to Color.Blue,
        "Verde" to Color.Green,
        "Amarillo" to Color.Yellow
    )

    // Tracks the player's score during a session
    var score by mutableStateOf(0)
        private set

    // Countdown in seconds until the game ends
    var timeLeft by mutableStateOf(30)
        private set

    // True when the game has ended (time has run out)
    var isGameOver by mutableStateOf(false)
        private set

    // The current word and color combination shown to the player
    var currentWord by mutableStateOf(generateRandomWord())
        private set

    // The current shuffled list of colors to choose from
    var options by mutableStateOf(colorMap.values.shuffled())
        private set

    // Stores the last answer's correctness (can be used to show feedback)
    var lastAnswerCorrect by mutableStateOf<Boolean?>(null)

    // Stores current difficulty level
    var currentDifficulty by mutableStateOf(Difficulty.EASY)
        private set

    // Flag that tracks if the game has started
    var hasStartedGame by mutableStateOf(false)






    // Sets the difficulty level
    fun setDifficulty(difficulty: Difficulty) {
        currentDifficulty = difficulty
    }

    // Returns the initial time limit based on difficulty
    private fun getTimeForDifficulty(): Int {
        return when (currentDifficulty) {
            Difficulty.EASY -> 45
            Difficulty.MEDIUM -> 30
            Difficulty.HARD -> 15
        }
    }

    // Public accessor for max time based on difficulty
    fun getMaxTime(): Int {
        return when (currentDifficulty) {
            Difficulty.EASY -> 45
            Difficulty.MEDIUM -> 30
            Difficulty.HARD -> 15
        }
    }

    // Resets all state values to start a new game session
    fun resetGame() {
        score = 0
        timeLeft = getTimeForDifficulty()
        isGameOver = false
        currentWord = generateRandomWord()
        options = colorMap.values.shuffled()
        hasStartedGame = true
    }

    // Submits the user's answer and prepares the next round
    fun submitAnswer(selectedColor: Color) {
        if (isGameOver) return
        if (selectedColor == currentWord.color) {
            score++
        }
        currentWord = generateRandomWord()
        options = colorMap.values.shuffled()
    }

    // Decrements the timer and marks the game as over if it hits zero
    fun decreaseTimer() {
        timeLeft--
        if (timeLeft <= 0) {
            isGameOver = true
        }
    }

    // Converts a color name string into its hex code (useful for debugging or display)
//    fun getColorHex(name: String): Long {
//        return colorMap[name]?.value?.toLong() ?: 0x000000
//    }

    // Returns the color name that corresponds to a given Color object
    fun getColorName(color: Color): String {
        return colorMap.entries.find { it.value == color }?.key ?: "Desconocido"
    }

    // Generates a random Stroop task (word-color pair)
    fun generateRandomWord(): WordColorPair {
        val text = colorNames.random()
        val color = colorMap.values.random()
        return WordColorPair(text, color)
    }
}
