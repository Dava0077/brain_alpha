package brain.alpha.controller.memoryController


import androidx.compose.runtime.mutableStateOf
import brain.alpha.controller.concentrationController.stroopController
import brain.alpha.model.memoryModel.couplesModel

object couplesController {
    private val model = couplesModel
    lateinit var navigateTo: (String) -> Unit
    val showInstructions = mutableStateOf(true)

    fun startGame() {
        model.shuffledCards = (model.allCards + model.allCards).shuffled().toMutableList()
        model.revealed = MutableList(16) { false }
        model.matched = MutableList(16) { false }
        model.selectedIndices.clear()
        model.started = true
    }
    fun beginGame() {
        showInstructions.value = false
        startGame()
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
        showInstructions.value = true
    }

    fun processTurnEnd() {
        if (model.selectedIndices.size == 2) {
            val (first, second) = model.selectedIndices
            if (!model.matched[first]) model.revealed[first] = false
            if (!model.matched[second]) model.revealed[second] = false
            model.selectedIndices.clear()
        }
    }

    fun isGameFinished(): Boolean = model.matched.all { it }
}

