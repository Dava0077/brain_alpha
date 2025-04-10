package brain.alpha.view.memoryView

import brain.alpha.strings.Strings

data class SimonUiState(
    val message: String = Strings.t("press_play"),
    val isUserTurn: Boolean = false,
    val sequence: List<String> = emptyList(),
    val gameOver: Boolean = false,
    val score: Int = 0


)