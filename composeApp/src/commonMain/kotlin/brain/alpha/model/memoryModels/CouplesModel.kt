package brain.alpha.model.memoryModels

object CouplesModel {
    val allCards: List<String> = listOf("P", "Q", "R", "S", "T", "U", "V", "W")
    var shuffledCards: MutableList<String> = mutableListOf()
    var revealed: MutableList<Boolean> = MutableList(16) { false }
    var matched: MutableList<Boolean> = MutableList(16) { false }
    var selectedIndices: MutableList<Int> = mutableListOf()
    var started: Boolean = false
}