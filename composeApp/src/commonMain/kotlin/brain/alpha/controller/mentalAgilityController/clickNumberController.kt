package brain.alpha.controller.mentalAgilityController

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import brain.alpha.model.mentalAgilityModel.clickNumberModel

object clickNumberController {var numbers by mutableStateOf(listOf<Int>())

    lateinit var navigateTo: (String) -> Unit
    var targetNumber by mutableStateOf(0)
        private set
    var message by mutableStateOf("")
        private set

    init {
        startNewGame()
    }

    // Inicia un nuevo juego
    fun startNewGame() {
        clickNumberModel.startGame()
        numbers = clickNumberModel.getNumbers()
        targetNumber = clickNumberModel.targetNumber
        message = "Haz clic en el número: $targetNumber"
    }

    // Maneja el clic en un botón
    fun handleButtonClick(selectedNumber: Int) {
        val isCorrect = clickNumberModel.checkNumber(selectedNumber)
        message = if (isCorrect) "¡Ganaste!" else "Incorrecto, intenta de nuevo."
        if (isCorrect) {
            startNewGame() // Reinicia el juego si ganas
        }
    }
    fun goHome(){
        navigateTo("agility")
    }
}