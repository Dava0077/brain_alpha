package brain.alpha.controller.memoryController


import androidx.compose.runtime.mutableStateOf
import brain.alpha.model.memoryModel.pairsModel
import brain.alpha.model.shared.Difficulty

object pairsController {
    private val model = pairsModel
    lateinit var navigateTo: (String) -> Unit
    val showInstructions = mutableStateOf(false)

    fun startGame() {
        val pairCount = model.pairCount
        val cardsToUse = model.allCards.shuffled().take(pairCount)
        model.shuffledCards = (cardsToUse + cardsToUse).shuffled().toMutableList()
        model.revealed = MutableList(pairCount * 2) { false }
        model.matched = MutableList(pairCount * 2) { false }
        model.selectedIndices.clear()
        model.started = true
    }

    fun beginGame() {
        showInstructions.value = false
        startGame()
    }
    fun toggleInstructions(show: Boolean) {
        showInstructions.value = show
    }

    fun hasStarted(): Boolean = model.started

    fun getCardValue(index: Int): String = model.shuffledCards.getOrElse(index) { "" }
    fun getCardCount(): Int = model.shuffledCards.size

    fun isVisible(index: Int): Boolean =
        model.revealed.getOrElse(index) { false } || model.matched.getOrElse(index) { false }

    fun getRevealedStates(): List<Boolean> = model.revealed.toList()
    fun getMatchedStates(): List<Boolean> = model.matched.toList()
    fun getSelectedIndices(): List<Int> = model.selectedIndices.toList()

    fun selectCard(index: Int): Boolean {
        if (!model.started || model.selectedIndices.contains(index) || isVisible(index) || model.selectedIndices.size == 2)
            return false

        model.revealed[index] = true
        model.selectedIndices.add(index)

        if (model.selectedIndices.size == 2) {
            val (first, second) = model.selectedIndices
            if (model.shuffledCards[first] == model.shuffledCards[second]) {
                model.matched[first] = true
                model.matched[second] = true
            }
        }
        return true
    }
    fun goHome(){
        navigateTo("memory")
        model.started = false;
        showInstructions.value = false
    }

    fun processTurnEnd() {
        if (model.selectedIndices.size == 2) {
            val (first, second) = model.selectedIndices
            if (!model.matched[first]) model.revealed[first] = false
            if (!model.matched[second]) model.revealed[second] = false
            model.selectedIndices.clear()
        }
    }
    fun getShuffledCardCount(): Int = model.shuffledCards.size

    fun setDifficulty(difficulty: Difficulty) {
        pairsModel.difficulty = difficulty
    }
    fun getDifficulty(): Difficulty = model.difficulty

    fun getPairCount(): Int = pairsModel.pairCount

    fun isGameFinished(): Boolean = model.matched.all { it }
    fun calculateScore(seconds: Int): Int {
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

        val multiplier = when (model.difficulty) {
            Difficulty.EASY -> 1
            Difficulty.MEDIUM -> 2
            Difficulty.HARD -> 3
        }

        return baseScore * multiplier
    }
}

