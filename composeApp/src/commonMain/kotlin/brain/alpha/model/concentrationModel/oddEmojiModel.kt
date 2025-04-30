package brain.alpha.model.concentrationModel

import io.ktor.util.date.*
import brain.alpha.model.shared.Difficulty
import kotlin.random.Random

class OddEmojiModel {

    var difficulty: Difficulty = Difficulty.EASY
    var currentLevel: Int = 1
    var errors: Int = 0
    private val levelTimes: MutableList<Long> = mutableListOf()
    private var levelStartTime: Long = 0

    var score: Int = 0
    var gridSize: Int = 3
    var correctEmoji: String = "😀"
    var oddEmoji: String = "😃"
    var grid: List<List<String>> = listOf()
    var oddPosition: Pair<Int, Int> = 0 to 0

    private val similarEmojis = listOf("😀", "😃", "😄", "😁", "😆", "😅")

    fun startGame(difficulty: Difficulty) {
        this.difficulty = difficulty
        currentLevel = 1
        errors = 0
        levelTimes.clear()
        score = 0
        generateLevel()
    }

    fun generateLevel() {
        val baseEmoji = similarEmojis.random()
        val oddOutEmoji = similarEmojis.filter { it != baseEmoji }.random()

        gridSize = 3 + (currentLevel / 2)
        val oddRow = Random.nextInt(gridSize)
        val oddCol = Random.nextInt(gridSize)
        oddPosition = oddRow to oddCol

        grid = List(gridSize) { row ->
            List(gridSize) { col ->
                if (row == oddRow && col == oddCol) oddOutEmoji else baseEmoji
            }
        }

        correctEmoji = baseEmoji
        oddEmoji = oddOutEmoji
        levelStartTime = getTimeMillis()
    }

    fun checkSelection(x: Int, y: Int): Boolean {
        val levelEndTime = getTimeMillis()
        levelTimes.add(levelEndTime - levelStartTime)

        val isCorrect = x == oddPosition.first && y == oddPosition.second
        if (isCorrect) {
            score +=100
            currentLevel++
        } else {
            score -=20
            errors++
        }
        return isCorrect
    }

    fun getAverageTime(): Long {
        return if (levelTimes.isEmpty()) 0 else levelTimes.sum() / levelTimes.size
    }

    fun isGameOver(): Boolean {
        val maxLevels = when (difficulty) {
            Difficulty.EASY -> 5
            Difficulty.MEDIUM -> 8
            Difficulty.HARD -> 12
        }
        return currentLevel > maxLevels || errors >= 3
    }

    fun getFinalScore(): Int {
        val avgTime = getAverageTime()
        val bonus = when {
            avgTime < 2000 -> 100
            avgTime < 4000 -> 50
            else -> 0
        }
        return score + bonus
    }
}
