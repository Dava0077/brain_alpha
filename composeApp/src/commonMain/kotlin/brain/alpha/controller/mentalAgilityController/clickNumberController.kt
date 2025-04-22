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

    private val scope = CoroutineScope(Dispatchers.Main)

    init {
        startNewGame()
        startTimer()
    }

    fun beginClickGame() {
        showInstructions.value = false
        startGame()
    }

    // Inicia un nuevo juego
    fun startNewGame() {
        if (!gameOver) {
            clickNumberModel.startGame()
            numbers = clickNumberModel.getNumbers()
            targetNumber = clickNumberModel.targetNumber
            instructionMessage = "Haz clic en el número: $targetNumber"
            resultMessage = ""
        }
    }

    // Maneja el clic en un botón
    fun handleButtonClick(selectedNumber: Int) {
        if (!gameOver) {
            val isCorrect = clickNumberModel.checkNumber(selectedNumber)
            if (isCorrect) {
                score++
                resultMessage = "¡Correcto!"
                startNewGame() // Genera una nueva cuadrícula
            } else {
                resultMessage = "Incorrecto, intenta de nuevo."
            }
        }
    }

    // Inicia el temporizador de 30 segundos
    private fun startTimer() {
        scope.launch {
            while (timeLeft > 0) {
                delay(1000L) // Espera 1 segundo
                timeLeft--
            }
            gameOver = true
            instructionMessage = "¡Tiempo terminado!"
            resultMessage = "Aciertos: $score"
        }
    }

    fun goHomeLogic() {
        navigateTo("logic") // Solo navegamos, sin reiniciar el modelo
        wordSearchController.showInstructions.value = true
    }


    fun goHome(){
        navigateTo("agility")
    }
}
