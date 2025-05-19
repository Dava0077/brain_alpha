// Define the package for the memory game model
package brain.alpha.model.memoryModel

// Import the Difficulty enum for setting game difficulty levels
import brain.alpha.model.shared.Difficulty

// Singleton object to manage the memory game data and logic
object pairsModel {

    // List of all unique card values (total: 20 unique cards)
    val allCards = listOf(
        "A", "B", "C", "D", "E", "F", "G", "H",
        "I", "J", "K", "L", "M", "N", "O", "P",
        "Q", "R", "S", "T" // total: 20 únicas
    )

    // Mutable list to store shuffled cards for the game
    var shuffledCards: MutableList<String> = mutableListOf()

    // Mutable list to track which cards are currently revealed
    // Initialized with 'false' for all cards (hidden state)
    var revealed: MutableList<Boolean> = MutableList(16) { false }

    // Mutable list to track which cards have been matched
    // Initialized with 'false' for all cards (unmatched state)
    var matched: MutableList<Boolean> = MutableList(16) { false }

    // Mutable list to keep track of selected card indices
    var selectedIndices: MutableList<Int> = mutableListOf()

    // Boolean flag to indicate if the game has started
    var started: Boolean = false

    // Variable to store the current game difficulty level (default is EASY)
    var difficulty: Difficulty = Difficulty.EASY

    // Property to determine the number of pairs based on difficulty
    val pairCount: Int
        get() = when (difficulty) {
            Difficulty.EASY -> 8   // 4x4 grid
            Difficulty.MEDIUM -> 12  // 6x4 grid
            Difficulty.HARD -> 18  // 6x6 grid
        }

    // Function to generate and shuffle the cards for a new game
    fun generateCards() {
        // Select pairs based on the current difficulty
        val selectedPairs = allCards.shuffled().take(pairCount)

        // Create the deck by duplicating the selected pairs and shuffling
        shuffledCards = (selectedPairs + selectedPairs).shuffled().toMutableList()

        // Determine the total number of cards based on pairs
        val totalCards = pairCount * 2

        // Initialize the revealed and matched lists to 'false' for all cards
        revealed = MutableList(totalCards) { false }
        matched = MutableList(totalCards) { false }

        // Clear the selected indices list
        selectedIndices.clear()
    }

    // Function to calculate the score for the EASY difficulty based on elapsed time
    fun calculateEasyScore(seconds: Int): Int {
        return when {
            seconds <= 30 -> 10 // Maximum points for finishing in 30 seconds or less
            seconds <= 60 -> 7  // Reduced points for 31-60 seconds
            seconds <= 90 -> 5  // Further reduced points for 61-90 seconds
            else -> 3           // Minimum points for more than 90 seconds
        }
    }
}