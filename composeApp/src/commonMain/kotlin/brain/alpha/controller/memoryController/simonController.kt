package brain.alpha.controller.memoryController

import brain.alpha.model.memoryModel.simonModel
import brain.alpha.view.memoryView.SimonUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


object simonController {
    lateinit var navigateTo: (String) -> Unit
    val highlightedColor = MutableStateFlow<String?>(null)

    private val model = simonModel
    private val scope = CoroutineScope(Dispatchers.Default)

    private val _uiState = MutableStateFlow(
        SimonUiState(
            message = "Presiona Empezar",
            isUserTurn = false,
            sequence = emptyList()
        )
    )
    val uiState: StateFlow<SimonUiState> = _uiState

    private var gameOver = false

    fun onStartPressed() {
        if (gameOver) {
            model.resetGame()
            gameOver = false
        }

        model.startGame()
        _uiState.value = _uiState.value.copy(
            message = "Memoriza la secuencia...",
            isUserTurn = false,
            sequence = model.getMachineSequence()
        )

        scope.launch {
            delay(1000)
            model.beginPlayerTurn()
            _uiState.value = _uiState.value.copy(
                message = "¡Tu turno!",
                isUserTurn = true
            )
        }
    }

    fun onColorPressed(color: String) {
        if (!_uiState.value.isUserTurn || gameOver) return

        model.addPlayerColor(color)

        val isValid = model.validatePlayerInput()

        if (!isValid) {
            _uiState.value = _uiState.value.copy(
                message = "¡Fallaste! Pulsa Reiniciar.",
                isUserTurn = false
            )
            gameOver = true
            return
        }

        if (model.isPlayerSequenceComplete()) {
            scope.launch {
                delay(500)
                model.setGameState(simonModel.GameState.MACHINE)
                model.resetPlayerSequence()
                model.addRandomColor()

                _uiState.value = _uiState.value.copy(
                    message = "Memoriza la nueva secuencia...",
                    isUserTurn = false,
                    sequence = model.getMachineSequence()
                )

                delay(500)
                scope.launch {
                    simulateSequence(model.getMachineSequence()) // ✅ llama simulación
                    model.beginPlayerTurn()
                    _uiState.value = _uiState.value.copy(
                        message = "¡Tu turno!",
                        isUserTurn = true
                    )
                }
            }
        }
    }
    private suspend fun simulateSequence(sequence: List<String>) {
        for (color in sequence) {
            highlightedColor.value = color
            delay(500)
            highlightedColor.value = null
            delay(200)
        }
    }

    fun goHome(){
        navigateTo("memory")
    }
}