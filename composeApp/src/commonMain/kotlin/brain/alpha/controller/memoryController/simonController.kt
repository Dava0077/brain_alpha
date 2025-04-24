package brain.alpha.controller.memoryController

import androidx.compose.runtime.mutableStateOf
import brain.alpha.model.memoryModel.simonModel
import brain.alpha.model.shared.Difficulty
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
    val showInstructions = mutableStateOf(false)

    private val model = simonModel
    private val scope = CoroutineScope(Dispatchers.Default)
    val gameInProgress = mutableStateOf(false)


    private val _uiState = MutableStateFlow(
        SimonUiState(
            message = Strings.t("press_play"),
            isUserTurn = false,
            sequence = emptyList(),
            gameOver = false,
            score = 0
        )
    )
    fun toggleInstructions(show: Boolean) {
        showInstructions.value = show
    }
    fun setDifficulty(level: Difficulty) {
        model.difficulty = level
    }
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
        gameInProgress.value = true

        model.startGame()

        _uiState.value = _uiState.value.copy(
            message = Strings.t("mem_color"),
            isUserTurn = false,
            sequence = model.getMachineSequence(),
            gameOver = false,
            score = model.getScore()
        )

        scope.launch {
            playMachineSequence()
        }
    }
    private suspend fun playMachineSequence() {
        val lastColor = model.getMachineSequence().lastOrNull() ?: return

        _uiState.value = _uiState.value.copy(sequence = listOf(lastColor))
        delay(model.getDelayBetweenColors())
        _uiState.value = _uiState.value.copy(sequence = emptyList())
        delay(model.getAnimationSpeed().toLong())

        model.beginPlayerTurn()
        _uiState.value = _uiState.value.copy(
            message = Strings.t("your_turn"),
            isUserTurn = true
        )
    }
    fun getDifficulty(): Difficulty = model.difficulty

    fun onColorPressed(color: String) {
        if (!_uiState.value.isUserTurn || gameOver) return

        model.addPlayerColor(color)

        val isValid = model.validatePlayerInput()

        if (!isValid) {
            val finalScore = model.getFinalScore()

            _uiState.value = _uiState.value.copy(
                message = Strings.t("error_press_rei"),
                isUserTurn = false,
                sequence = emptyList(),
                gameOver = true,
                score = finalScore
            )
            gameOver = true
            gameInProgress.value = false
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

                _uiState.value = _uiState.value.copy(
                    message = Strings.t("mem_seq"),
                    isUserTurn = false,
                    sequence = model.getMachineSequence()
                )

                playMachineSequence() // 👈 llamamos la misma lógica con delays personalizados
            }
        }
    }


    fun goHome() {
        model.resetGame()
        model.resetScore()
        showInstructions.value = false
        _uiState.value = SimonUiState(
            message = Strings.t("press_play"),
            isUserTurn = false,
            sequence = emptyList(),
            gameOver = false,
            score = 0
        )
        gameOver = false
        navigateTo("memory")
        gameInProgress.value = false // ✅ Salimos del juego

    }
}