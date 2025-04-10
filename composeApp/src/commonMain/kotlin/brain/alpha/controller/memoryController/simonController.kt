package brain.alpha.controller.memoryController

import androidx.compose.runtime.mutableStateOf
import brain.alpha.model.memoryModel.simonModel
import brain.alpha.strings.Strings
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
    val showInstructions = mutableStateOf(true)

    private val model = simonModel
    private val scope = CoroutineScope(Dispatchers.Default)

    private val _uiState = MutableStateFlow(
        SimonUiState(
            message = Strings.t("press_play"),
            isUserTurn = false,
            sequence = emptyList(),
            gameOver = false,
            score = 0
        )
    )
    val uiState: StateFlow<SimonUiState> = _uiState

    private var gameOver = false

    fun beginGame() {
        showInstructions.value = false
    }
    fun onStartPressed() {
        if (gameOver) {
            model.resetGame()
            model.resetScore()
            gameOver = false
        }

        model.startGame()

        _uiState.value = _uiState.value.copy(
            message = Strings.t("mem_color"),
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
                message = Strings.t("your_turn"),
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
                message = Strings.t("error_press_rei"),
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
                    message = Strings.t("mem_seq"),
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
                    message = Strings.t("your_turn"),
                    isUserTurn = true
                )
            }
        }
    }


    fun goHome() {
        model.resetGame()
        model.resetScore()
        showInstructions.value = true
        _uiState.value = SimonUiState(
            message = Strings.t("press_play"),
            isUserTurn = false,
            sequence = emptyList(),
            gameOver = false,
            score = 0
        )
        gameOver = false
        navigateTo("memory")
    }
}