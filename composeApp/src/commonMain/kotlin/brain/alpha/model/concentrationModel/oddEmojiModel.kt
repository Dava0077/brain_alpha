// Import necessary packages and libraries
package brain.alpha.model.concentrationModel

// Import for date and time utilities
import io.ktor.util.date.*
// Import for difficulty level management
import brain.alpha.model.shared.Difficulty
// Import for random number generation
import kotlin.random.Random

// Define the OddEmojiModel class
class OddEmojiModel {


    // Declare difficulty level, default to EASY
    var difficulty: Difficulty = Difficulty.EASY
    // Track the current game level, starting at 1
    var currentLevel: Int = 1
    // Track the number of mistakes made
    var errors: Int = 0
    // Store times taken for each level
    private val levelTimes: MutableList<Long> = mutableListOf()
    // Store the start time of the current level
    private var levelStartTime: Long = 0

    // Track the player's score
    var score: Int = 0
    // Set the grid size for the game, starting at 3x3
    var gridSize: Int = 3
    // Define the emoji that appears most frequently
    var correctEmoji: String = "😀"
    // Define the odd (different) emoji in the grid
    var oddEmoji: String = "😃"
    // Represent the grid as a list of lists (rows and columns)
    var grid: List<List<String>> = listOf()
    // Store the position of the odd emoji as a pair (row, column)
    var oddPosition: Pair<Int, Int> = 0 to 0

    // Define a list of similar emojis to use in the game
    private val similarEmojis = listOf("😀", "😃", "😄", "😁", "😆", "😅")


    // Start a new game with the specified difficulty level
    fun startGame(difficulty: Difficulty) {
        // Set the game difficulty
        this.difficulty = difficulty
        // Reset the game level to 1
        currentLevel = 1
        // Reset the error count
        errors = 0
        // Clear recorded times from previous games
        levelTimes.clear()
        // Reset the player score
        score = 0
        // Generate the first game level
        generateLevel()
    }

    // Generate a new level with randomized settings
    fun generateLevel() {
        // Select a random base emoji from the list
        val baseEmoji = similarEmojis.random()
        // Select a different emoji to be the odd one out
        val oddOutEmoji = similarEmojis.filter { it != baseEmoji }.random()

        // Adjust grid size based on the current level
        gridSize = 3 + (currentLevel / 2)
        // Randomly select the row and column for the odd emoji
        val oddRow = Random.nextInt(gridSize)
        val oddCol = Random.nextInt(gridSize)
        // Store the position of the odd emoji
        oddPosition = oddRow to oddCol

        // Generate the grid with correct and odd emojis
        grid = List(gridSize) { row ->
            List(gridSize) { col ->
                if (row == oddRow && col == oddCol) oddOutEmoji else baseEmoji
            }
        }

        // Set the correct and odd emojis for this level
        correctEmoji = baseEmoji
        oddEmoji = oddOutEmoji
        // Record the start time of the level
        levelStartTime = getTimeMillis()
    }

    // Check whether the selected position is correct
    fun checkSelection(x: Int, y: Int): Boolean {
        // Record the end time of the level
        val levelEndTime = getTimeMillis()
        // Calculate and store the time taken for the level
        levelTimes.add(levelEndTime - levelStartTime)

        // Check if the selected position matches the odd emoji's position
        val isCorrect = x == oddPosition.first && y == oddPosition.second
        if (isCorrect) {
            // Increment score and move to the next level if correct
            score +=100
            currentLevel++
        } else {
            // Decrement score and increase error count if incorrect
            score -=20
            errors++
        }
        // Return whether the selection was correct
        return isCorrect
    }

    // Calculate the average time taken per level
    fun getAverageTime(): Long {
        // Return the average time, or 0 if no levels were played
        return if (levelTimes.isEmpty()) 0 else levelTimes.sum() / levelTimes.size
    }

    // Check whether the game is over
    fun isGameOver(): Boolean {
        // Determine the maximum number of levels based on difficulty
        val maxLevels = when (difficulty) {
            Difficulty.EASY -> 5
            Difficulty.MEDIUM -> 8
            Difficulty.HARD -> 12
        }
        // Return true if the game is over (too many errors or levels exceeded)
        return currentLevel > maxLevels || errors >= 3
    }

    // Calculate the final score with a time-based bonus
    fun getFinalScore(): Int {
        // Calculate the average time taken
        val avgTime = getAverageTime()
        val bonus = when {
            avgTime < 2000 -> 100
            avgTime < 4000 -> 50
            else -> 0
        }
        // Return the final score including the bonus
        return score + bonus
    }
}
