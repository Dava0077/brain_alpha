package brain.alpha.model.memoryModel

import brain.alpha.model.shared.Difficulty

object couplesModel {
    val allCards = listOf(
        "A", "B", "C", "D", "E", "F", "G", "H",
        "I", "J", "K", "L", "M", "N", "O", "P",
        "Q", "R", "S", "T" // total: 20 únicas
    )
    var shuffledCards: MutableList<String> = mutableListOf()
    var revealed: MutableList<Boolean> = MutableList(16) { false }
    var matched: MutableList<Boolean> = MutableList(16) { false }
    var selectedIndices: MutableList<Int> = mutableListOf()
    var started: Boolean = false
    var difficulty: Difficulty = Difficulty.EASY

    val pairCount: Int
        get() = when (difficulty) {
            Difficulty.EASY -> 8   // 4x4 grid
            Difficulty.MEDIUM -> 12  // 6x4 grid
            Difficulty.HARD -> 18  // 6x6 grid
        }

    fun generateCards() {
        val selectedPairs = allCards.shuffled().take(pairCount)
        shuffledCards = (selectedPairs + selectedPairs).shuffled().toMutableList()

        val totalCards = pairCount * 2
        revealed = MutableList(totalCards) { false }
        matched = MutableList(totalCards) { false }
        selectedIndices.clear()
    }
}