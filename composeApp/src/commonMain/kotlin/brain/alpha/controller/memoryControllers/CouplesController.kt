package brain.alpha.controller.memoryControllers


import kotlin.random.Random

class CouplesController {

    private val allCards = listOf("P", "Q", "R", "S", "T", "U", "V", "W")
    private var shuffledCards = mutableListOf<String>()
    private var revealed = MutableList(16) { false }
    private var matched = MutableList(16) { false }
    private var selectedIndices = mutableListOf<Int>()

    init {
        resetGame()
    }

    fun getCardValue(index: Int): String = shuffledCards[index]

    fun isRevealed(index: Int): Boolean = revealed[index] || matched[index]

    fun selectCard(index: Int): Boolean {
        if (index in selectedIndices || isRevealed(index) || selectedIndices.size == 2) return false

        revealed[index] = true
        selectedIndices.add(index)

        if (selectedIndices.size == 2) {
            val (first, second) = selectedIndices
            if (shuffledCards[first] == shuffledCards[second]) {
                matched[first] = true
                matched[second] = true
            }
        }
        return true
    }

    fun processTurnEnd() {
        if (selectedIndices.size == 2) {
            val (first, second) = selectedIndices
            if (!matched[first]) revealed[first] = false
            if (!matched[second]) revealed[second] = false
            selectedIndices.clear()
        }
    }

    fun isGameFinished(): Boolean = matched.all { it }

    fun resetGame() {
        shuffledCards = (allCards + allCards).shuffled(Random.Default).toMutableList()
        revealed = MutableList(16) { false }
        matched = MutableList(16) { false }
        selectedIndices.clear()
    }

    fun getRevealedStates(): List<Boolean> = revealed.toList()
    fun getMatchedStates(): List<Boolean> = matched.toList()
    fun getSelectedIndices(): List<Int> = selectedIndices.toList()
}