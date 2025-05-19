// Define the package for the memory game controller
package brain.alpha.controller.memoryController

// Import necessary libraries for state management
import androidx.compose.runtime.mutableStateOf
// Import the game model to manage game data and logic
import brain.alpha.model.memoryModel.pairsModel
// Import the Difficulty enum to manage game difficulty levels
import brain.alpha.model.shared.Difficulty

// Singleton object to control the game logic and handle interactions
object pairsController {

    // Reference to the game model
    private val model = pairsModel

    // Navigation function to handle screen transitions
    lateinit var navigateTo: (String) -> Unit

    // Mutable state to control the visibility of the instructions screen
    val showInstructions = mutableStateOf(false)

    // Function to start a new game
    fun startGame() {
        // Get the number of pairs based on the selected difficulty
        val pairCount = model.pairCount

        // Select a subset of cards and shuffle them to create the game deck
        val cardsToUse = model.allCards.shuffled().take(pairCount)
        model.shuffledCards = (cardsToUse + cardsToUse).shuffled().toMutableList()

        // Initialize the revealed and matched states to 'false'
        model.revealed = MutableList(pairCount * 2) { false }
        model.matched = MutableList(pairCount * 2) { false }

        // Clear the list of selected indices
        model.selectedIndices.clear()

        // Mark the game as started
        model.started = true
    }

    // Function to begin the game after instructions are hidden
    fun beginGame() {
        showInstructions.value = false
        startGame()
    }

    // Function to toggle the visibility of instructions
    fun toggleInstructions(show: Boolean) {
        showInstructions.value = show
    }

    // Function to check if the game has started
    fun hasStarted(): Boolean = model.started

    // Function to get the card value at a specific index
    fun getCardValue(index: Int): String = model.shuffledCards.getOrElse(index) { "" }

    // Function to get the number of cards in the shuffled deck
    fun getCardCount(): Int = model.shuffledCards.size

    // Function to check if a card is visible (either revealed or matched)
    fun isVisible(index: Int): Boolean =
        model.revealed.getOrElse(index) { false } || model.matched.getOrElse(index) { false }

    // Function to get the current states of all revealed cards
    fun getRevealedStates(): List<Boolean> = model.revealed.toList()

    // Function to get the current states of all matched cards
    fun getMatchedStates(): List<Boolean> = model.matched.toList()

    // Function to get the currently selected card indices
    fun getSelectedIndices(): List<Int> = model.selectedIndices.toList()

    // Function to handle card selection logic
    fun selectCard(index: Int): Boolean {
        // Check if the game is started, index is not already selected, and less than 2 cards are selected
        if (!model.started || model.selectedIndices.contains(index) || isVisible(index) || model.selectedIndices.size == 2)
            return false

        // Reveal the selected card
        model.revealed[index] = true

        // Add the selected index to the list of selected indices
        model.selectedIndices.add(index)

        // If two cards are selected, check for a match
        if (model.selectedIndices.size == 2) {
            val (first, second) = model.selectedIndices

            // If both selected cards match, mark them as matched
            if (model.shuffledCards[first] == model.shuffledCards[second]) {
                model.matched[first] = true
                model.matched[second] = true
            }
        }
        return true // Indicate successful card selection
    }

    // Function to handle navigation to the home screen
    fun goHome(){
        navigateTo("memory") // Navigate to the "memory" screen
        model.started = false; // Mark the game as not started
        showInstructions.value = false // Hide instructions
    }
    // Function to handle the end of a turn (when two cards have been selected)
    fun processTurnEnd() {
        // Check if two cards have been selected
        if (model.selectedIndices.size == 2) {
            val (first, second) = model.selectedIndices

            // If the cards are not matched, hide them again
            if (!model.matched[first]) model.revealed[first] = false
            if (!model.matched[second]) model.revealed[second] = false

            // Clear the list of selected indices for the next turn
            model.selectedIndices.clear()
        }
    }

    // Function to get the count of shuffled cards in the deck
    fun getShuffledCardCount(): Int = model.shuffledCards.size

    // Function to set the difficulty level
    fun setDifficulty(difficulty: Difficulty) {
        pairsModel.difficulty = difficulty
    }

    // Function to get the current difficulty level
    fun getDifficulty(): Difficulty = model.difficulty

    // Function to get the number of pairs based on the current difficulty
    fun getPairCount(): Int = pairsModel.pairCount

    // Function to check if all cards are matched, indicating that the game is finished
    fun isGameFinished(): Boolean = model.matched.all { it }

    // Function to calculate the score based on time and difficulty
    fun calculateScore(seconds: Int): Int {
        // Calculate base score based on difficulty and time taken
        val baseScore = when (model.difficulty) {
            Difficulty.EASY -> {
                when {
                    seconds <= 30 -> 10
                    seconds <= 60 -> 7
                    seconds <= 90 -> 5
                    else -> 3
                }
            }
            Difficulty.MEDIUM -> {
                when {
                    seconds <= 45 -> 10
                    seconds <= 75 -> 7
                    seconds <= 105 -> 5
                    else -> 3
                }
            }
            Difficulty.HARD -> {
                when {
                    seconds <= 60 -> 10
                    seconds <= 90 -> 7
                    seconds <= 120 -> 5
                    else -> 3
                }
            }
        }

        // Determine the score multiplier based on difficulty
        val multiplier = when (model.difficulty) {
            Difficulty.EASY -> 1
            Difficulty.MEDIUM -> 2
            Difficulty.HARD -> 3
        }

        // Calculate and return the final score
        return baseScore * multiplier
    }
}

