// Define the package for the memory game controller
package brain.alpha.controller.memoryController

// Import necessary libraries for state management and coroutine handling
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

// Singleton object to manage the Simon game logic and UI state
object simonController {

    // Function reference to navigate to different screens
    lateinit var navigateTo: (String) -> Unit

    // StateFlow to highlight the current color in the sequence
    val highlightedColor = MutableStateFlow<String?>(null)

    // State to show or hide instructions
    val showInstructions = mutableStateOf(false)

    // Reference to the game model
    private val model = simonModel

    // Coroutine scope for running background tasks
    private val scope = CoroutineScope(Dispatchers.Default)

    // State to indicate if a game is currently in progress
    val gameInProgress = mutableStateOf(false)

    // Mutable state for managing the UI state of the game
    private val _uiState = MutableStateFlow(
        SimonUiState(
            message = Strings.t("press_play"),
            isUserTurn = false, // Indicates if it is the player's turn
            sequence = emptyList(), // Empty sequence at the start
            gameOver = false, // Game is not over at the start
            score = 0
        )
    )

    // Function to toggle the visibility of instructions
    fun toggleInstructions(show: Boolean) {
        showInstructions.value = show
    }

    // Function to set the game difficulty
    fun setDifficulty(level: Difficulty) {
        model.difficulty = level
    }

    // Expose the UI state as a StateFlow
    val uiState: StateFlow<SimonUiState> = _uiState

    // Variable to track if the game is over
    private var gameOver = false

    // Function to start the game
    fun beginGame() {
        // Hide instructions when the game starts
        showInstructions.value = false
    }

    // Function to handle the "Play" or "Restart" button press
    fun onStartPressed() {
        // If the game is over, reset the game and score
        if (gameOver) {
            model.resetGame()
            model.resetScore()
            gameOver = false
        }

        // Set game in progress to true
        gameInProgress.value = true

        // Start the game in the model
        model.startGame()

        // Update the UI state to reflect game start
        _uiState.value = _uiState.value.copy(
            message = Strings.t("mem_color"),
            isUserTurn = false,
            sequence = model.getMachineSequence(),
            gameOver = false,
            score = model.getScore()
        )

        // Launch a coroutine to play the machine sequence
        scope.launch {
            playMachineSequence()
        }
    }

    // Coroutine to play the machine's sequence
    private suspend fun playMachineSequence() {

        // Get the last color in the machine's sequence
        val lastColor = model.getMachineSequence().lastOrNull() ?: return

        // Update UI to show the last color
        _uiState.value = _uiState.value.copy(sequence = listOf(lastColor))

        // Delay based on difficulty
        delay(model.getDelayBetweenColors())

        // Clear the sequence display
        _uiState.value = _uiState.value.copy(sequence = emptyList())

        // Delay for the transition
        delay(model.getAnimationSpeed().toLong())

        // Switch to player's turn
        model.beginPlayerTurn()

        // Update the UI to indicate the player's turn
        _uiState.value = _uiState.value.copy(
            message = Strings.t("your_turn"),
            isUserTurn = true
        )
    }

    // Function to retrieve the current game difficulty
    fun getDifficulty(): Difficulty = model.difficulty

    // Function to handle player's color selection
    fun onColorPressed(color: String) {
        // Ignore input if it's not the player's turn or the game is over
        if (!_uiState.value.isUserTurn || gameOver) return

        // Add the player's selected color to the sequence
        model.addPlayerColor(color)

        // Validate the player's input
        val isValid = model.validatePlayerInput()

        // If the input is incorrect
        if (!isValid) {
            val finalScore = model.getFinalScore()

            // Update the UI to show the error message and game over state
            _uiState.value = _uiState.value.copy(
                message = Strings.t("error_press_rei"),
                isUserTurn = false,
                sequence = emptyList(),
                gameOver = true,
                score = finalScore
            )

            // Set game over state to true
            gameOver = true
            gameInProgress.value = false
            return
        }

        // If the player's sequence is complete
        if (model.isPlayerSequenceComplete()) {
            model.incrementScore()

            // Update the UI with the new score
            _uiState.value = _uiState.value.copy(
                score = model.getScore()
            )

            // Launch a coroutine to add a new color and play the new sequence
            scope.launch {
                delay(500) // Short delay before starting the next sequence

                // Switch back to the machine's turn
                model.setGameState(simonModel.GameState.MACHINE)

                // Reset the player's sequence
                model.resetPlayerSequence()

                // Add a new random color to the machine's sequence
                model.addRandomColor()

                // Update the UI to show the new sequence
                _uiState.value = _uiState.value.copy(
                    message = Strings.t("mem_seq"),
                    isUserTurn = false,
                    sequence = model.getMachineSequence()
                )

                // Play the new machine sequence
                playMachineSequence() // 👈 llamamos la misma lógica con delays personalizados
            }
        }
    }

    // Function to handle "Go Home" action
    fun goHome() {
        // Reset the game and score
        model.resetGame()
        model.resetScore()

        // Hide instructions
        showInstructions.value = false

        // Reset the UI state to the initial state
        _uiState.value = SimonUiState(
            message = Strings.t("press_play"),
            isUserTurn = false,
            sequence = emptyList(),
            gameOver = false,
            score = 0
        )

        // Reset game over state
        gameOver = false

        // Navigate back to the memory menu
        navigateTo("memory")

        // Set game in progress to false
        gameInProgress.value = false // ✅ Salimos del juego

    }
}