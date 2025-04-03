package brain.alpha.view.memoryView

data class SimonUiState(
    val message: String = "Presiona Empezar",
    val isUserTurn: Boolean = false,
    val sequence: List<String> = emptyList()
)