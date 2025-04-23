package brain.alpha.model.memoryModel

import brain.alpha.model.shared.Difficulty

object simonModel {
    var difficulty: Difficulty = Difficulty.EASY
    private val availableColors: List<String> = listOf("Rojo", "Verde", "Azul", "Amarillo")
    enum class GameState { START, MACHINE, PLAYER }

    private val machineSequence = mutableListOf<String>()
    private val playerSequence = mutableListOf<String>()
    private var state = GameState.START
    private var score = 0

    fun incrementScore() {
        score++
    }
    fun getDelayBetweenColors(): Long = when (difficulty) {
        Difficulty.EASY -> 1000L
        Difficulty.MEDIUM -> 700L
        Difficulty.HARD -> 400L
    }

    fun getAnimationSpeed(): Int = when (difficulty) {
        Difficulty.EASY -> 300
        Difficulty.MEDIUM -> 150
        Difficulty.HARD -> 80
    }
    fun getScore(): Int = score

    fun resetScore() {
        score = 0
    }

    fun startGame() {
        machineSequence.clear()
        playerSequence.clear()
        state = GameState.MACHINE
        addRandomColor()
    }

    fun addRandomColor() {
        val color = availableColors.random()
        machineSequence.add(color)
    }

    fun addPlayerColor(color: String) {
        playerSequence.add(color)
    }

    fun validatePlayerInput(): Boolean {
        val index = playerSequence.lastIndex
        if (index >= machineSequence.size) return false
        return playerSequence[index] == machineSequence[index]
    }

    fun isPlayerSequenceComplete(): Boolean {
        return playerSequence.size == machineSequence.size
    }

    fun resetPlayerSequence() {
        playerSequence.clear()
    }

    fun resetGame() {
        machineSequence.clear()
        playerSequence.clear()
        state = GameState.START
    }
    fun beginPlayerTurn() {
        state = GameState.PLAYER
        playerSequence.clear()
    }

    fun getMachineSequence(): List<String> = machineSequence.toList()
    fun getPlayerSequence(): List<String> = playerSequence.toList()
    fun getGameState(): GameState = state
    fun setGameState(newState: GameState) {
        state = newState
    }
}