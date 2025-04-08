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
            sequence = emptyList(),
            gameOver = false,
            score = 0
        )
    )
    val uiState: StateFlow<SimonUiState> = _uiState

    private var gameOver = false

    fun onStartPressed() {
        if (gameOver) {
            model.resetGame()
            model.resetScore()
            gameOver = false
        }

        model.startGame()

        _uiState.value = _uiState.value.copy(
            message = "Memoriza el color...",
            isUserTurn = false,
            sequence = model.getMachineSequence(),
            gameOver = false,
            score = model.getScore()
        )

        scope.launch {
            delay(1000) // mostrar el color 1 segundo

            _uiState.value = _uiState.value.copy(
                sequence = emptyList() // ocultar el color (Box transparente)
            )

            delay(300) // pequeña pausa antes del turno

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
                isUserTurn = false,
                sequence = emptyList(),
                gameOver = true
            )
            gameOver = true
            return
        }

        if (model.isPlayerSequenceComplete()) {
            model.incrementScore()

            _uiState.value = _uiState.value.copy(
                score = model.getScore()
            )
            scope.launch {
                delay(500)
                model.setGameState(simonModel.GameState.MACHINE)
                model.resetPlayerSequence()
                model.addRandomColor()

                val newSequence = model.getMachineSequence()

                _uiState.value = _uiState.value.copy(
                    message = "Memoriza la nueva secuencia...",
                    isUserTurn = false,
                    sequence = newSequence
                )

                delay(1000)

                _uiState.value = _uiState.value.copy(
                    sequence = emptyList() // ✅ ocultar color
                )

                delay(300)

                model.beginPlayerTurn()
                _uiState.value = _uiState.value.copy(
                    message = "¡Tu turno!",
                    isUserTurn = true
                )
            }
        }
    }


    fun goHome(){
        navigateTo("memory")
    }
}