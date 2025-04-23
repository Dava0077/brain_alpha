package brain.alpha.controller.mentalAgilityController

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import brain.alpha.controller.logicController.wordSearchController
import brain.alpha.controller.logicController.wordSearchController.showInstructions
import brain.alpha.controller.logicController.wordSearchController.startGame
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import brain.alpha.model.mentalAgilityModel.clickNumberModel
import kotlinx.coroutines.Job

object clickNumberController {

    var numbers by mutableStateOf(listOf<Int>())

    lateinit var navigateTo: (String) -> Unit
    var targetNumber by mutableStateOf(0)
        private set
    var instructionMessage by mutableStateOf("")
        private set
    var resultMessage by mutableStateOf("")
        private set
    var timeLeft by mutableStateOf(30) // Contador de 30 segundos
        private set
    var score by mutableStateOf(0) // Contador de aciertos
        private set
    var gameOver by mutableStateOf(false) // Estado para fin del juego
        private set
    val showInstructions = mutableStateOf(true)
    var isGameStarted by mutableStateOf(false)
        private set

    private var timerJob: Job? = null

    private val scope = CoroutineScope(Dispatchers.Main)

    init {
        startGame()
        startTimer()
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
        timeLeft = 30
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
        timerJob?.cancel() // Cancela cualquier temporizador anterior
        timerJob = scope.launch {
            while (timeLeft > 0) {
                delay(1000L)
                timeLeft--
            }
            gameOver = true
            instructionMessage = "¡Tiempo terminado!"
            resultMessage = "Aciertos: $score"
        }
    }

    // Reinicia el juego (botón Atrás)
    fun resetGame() {
        timerJob?.cancel()
        isGameStarted = false
        gameOver = false
        score = 0
        timeLeft = 30
        numbers = emptyList()
        targetNumber = 0
        instructionMessage = ""
        resultMessage = ""
    }

    fun goHomeLogic() {
        navigateTo("logic") // Solo navegamos, sin reiniciar el modelo
        wordSearchController.showInstructions.value = true
    }


    fun goHome(){
        navigateTo("agility")
    }
}