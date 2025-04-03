package brain.alpha.model.mentalAgilityModel

import kotlin.random.Random

object clickNumberModel {private val gridSize = 4 // Tamaño de la cuadrícula (4x4)
    private val numbers = mutableListOf<Int>()
    var targetNumber: Int = 0
        private set

    // Inicializa el juego generando números aleatorios y seleccionando el objetivo
    fun startGame() {
        numbers.clear()
        for (i in 0 until gridSize * gridSize) {
            numbers.add(Random.nextInt(1, 100)) // Números aleatorios entre 1 y 99
        }
        targetNumber = numbers[Random.nextInt(numbers.size)] // Selecciona un número objetivo
    }

    // Verifica si el número seleccionado es el objetivo
    fun checkNumber(selectedNumber: Int): Boolean {
        return selectedNumber == targetNumber
    }

    // Devuelve la lista de números para la cuadrícula
    fun getNumbers(): List<Int> {
        return numbers
    }
}