package brain.alpha.model.mentalAgilityModel

import kotlin.random.Random

// Model that handles number generation and answer checking for the Click Number game
object clickNumberModel {

    // Grid size is 4x4
    private val gridSize = 4
    // Internal list of numbers shown to the player
    private val numbers = mutableListOf<Int>()
    // The current target number the player needs to find
    var targetNumber: Int = 0
        private set

    // Initializes the game by generating new random numbers and selecting a target
    fun startGame() {
        numbers.clear()
        for (i in 0 until gridSize * gridSize) {
            // Fill grid with random numbers from 1 to 99
            numbers.add(Random.nextInt(1, 100))
        }
        // Randomly select one as the target
        targetNumber = numbers[Random.nextInt(numbers.size)]
    }

    // Checks whether the number selected by the user matches the target
    fun checkNumber(selectedNumber: Int): Boolean {
        return selectedNumber == targetNumber
    }

    // Returns the current list of numbers for display
    fun getNumbers(): List<Int> {
        return numbers
    }
}