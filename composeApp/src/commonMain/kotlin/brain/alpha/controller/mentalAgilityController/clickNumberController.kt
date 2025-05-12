package brain.alpha.controller.mentalAgilityController



import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import brain.alpha.controller.logicController.wordSearchController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import brain.alpha.model.mentalAgilityModel.clickNumberModel
import brain.alpha.model.shared.Difficulty


object clickNumberController {
    var numbers by mutableStateOf(listOf<Int>())
        private set
    lateinit var navigateTo: (String) -> Unit
    var targetNumber by mutableStateOf(0)
        private set
    var instructionMessage by mutableStateOf("")
        private set
    var resultMessage by mutableStateOf("")
        private set
    var timeLeft by mutableStateOf(30)
        private set
    var score by mutableStateOf(0)
        private set
    var gameOver by mutableStateOf(false)
        private set
    val showInstructions = mutableStateOf(true)
    var isGameStarted by mutableStateOf(false)
        private set
    var showGameOverDialog by mutableStateOf(false)
        private set
    var currentDifficulty by mutableStateOf(Difficulty.EASY) // Dificultad por defecto: Fácil
        private set

    private var timerJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    // Método para establecer la dificultad
    fun setDifficulty(difficulty: Difficulty) {
        currentDifficulty = difficulty
    }

    fun beginClickGame() {
        showInstructions.value = false
        startGame()
    }

    // Inicia el juego
    fun startGame() {
        isGameStarted = true
        gameOver = false
        score = 0
        // Establecer el tiempo según la dificultad
        timeLeft = when (currentDifficulty) {
            Difficulty.EASY -> 90 // 1:30 minutos
            Difficulty.MEDIUM -> 60 // 1:00 minuto
            Difficulty.HARD -> 30 // 30 segundos
        }
        showGameOverDialog = false
        clickNumberModel.startGame()
        numbers = clickNumberModel.getNumbers()
        targetNumber = clickNumberModel.targetNumber
        instructionMessage = "Haz clic en el número: $targetNumber"
        resultMessage = ""
        startTimer()
    }

    // Maneja el clic en un botón
    fun handleButtonClick(selectedNumber: Int) {
        if (!gameOver && isGameStarted) {
            val isCorrect = clickNumberModel.checkNumber(selectedNumber)
            if (isCorrect) {
                score++
                resultMessage = "¡Correcto!"
                clickNumberModel.startGame()
                numbers = clickNumberModel.getNumbers()
                targetNumber = clickNumberModel.targetNumber
                instructionMessage = "Haz clic en el número: $targetNumber"
            } else {
                resultMessage = "Incorrecto, intenta de nuevo."
            }
        }
    }

    // Inicia el temporizador
    private fun startTimer() {
        timerJob?.cancel()
        timerJob = scope.launch {
            while (timeLeft > 0 && isGameStarted) {
                delay(1000L)
                timeLeft--
            }
            if (isGameStarted) {
                gameOver = true
                instructionMessage = "¡Tiempo terminado!"
                resultMessage = "Aciertos: $score"
                isGameStarted = false
                showGameOverDialog = true
            }
        }
    }

    // Reinicia el juego (botón Atrás o "Jugar de Nuevo")
    fun resetGame() {
        timerJob?.cancel()
        timerJob = null
        isGameStarted = false
        gameOver = false
        score = 0
        timeLeft = when (currentDifficulty) {
            Difficulty.EASY -> 90
            Difficulty.MEDIUM -> 60
            Difficulty.HARD -> 30
        }
        numbers = emptyList()
        targetNumber = 0
        instructionMessage = ""
        resultMessage = ""
        showGameOverDialog = false
    }

    // Metodo para reiniciar el juego desde la ventana emergente
    fun restartGame() {
        resetGame()
        startGame()
    }

    fun goHomeLogic() {
        resetGame()
        navigateTo("logic")
        wordSearchController.showInstructions.value = true
    }

    fun goHome() {
        resetGame()
        navigateTo("agility")
    }
}