package model

import brain.alpha.model.shared.Difficulty

// Object to manage mental math game logic and state
object mentalMathModel {
    // Current total score of the player
    var score = 0
        private set
    // Index of the current question
    var currentQuestion = 0
        private set
    // Total number of questions per game
    val totalQuestions = 10
    // Current active question object
    private var currentQuestionObj: Question? = null
    // Difficulty level of the game
    private var difficulty: Difficulty = Difficulty.MEDIUM

    // Set game difficulty level
    fun setDifficulty(level: Difficulty) {
        difficulty = level
    }

    // Get current difficulty level
    fun getDifficulty(): Difficulty = difficulty

    // Return time limit in seconds based on difficulty level
    fun getTimeLimit(): Int = when (difficulty) {
        Difficulty.EASY -> 60
        Difficulty.MEDIUM -> 30
        Difficulty.HARD -> 15
    }

    // Generate a random arithmetic question
    fun generateQuestion(): Question {
        val num1 = kotlin.random.Random.nextInt(1, 20)
        val num2 = kotlin.random.Random.nextInt(1, 20)
        val operation = kotlin.random.Random.nextInt(0, 3)

        // Randomly choose an operation and create corresponding question
        val question = when (operation) {
            0 -> Question("$num1 + $num2 = ?", num1 + num2)
            1 -> Question("$num1 - $num2 = ?", num1 - num2)
            else -> Question("$num1 * $num2 = ?", num1 * num2)
        }
        currentQuestionObj = question
        return question
    }

    // Check if user's answer is correct, update score and progress
    fun checkAnswer(userAnswer: Int, timeTaken: Double): Boolean {
        val isCorrect = userAnswer == currentQuestionObj?.correctAnswer
        if (isCorrect) score += calculatePoints(timeTaken)
        currentQuestion++
        return isCorrect
    }

    // Calculate points based on time and difficulty
    private fun calculatePoints(timeTaken: Double): Int {
        val basePoints = when (difficulty) {
            Difficulty.EASY -> 10
            Difficulty.MEDIUM -> 15
            Difficulty.HARD -> 20
        }

        return when {
            timeTaken < 3 -> basePoints + 5
            timeTaken < 6 -> basePoints + 2
            else -> basePoints
        }
    }

    // Reset game state to initial values
    fun resetGame() {
        score = 0
        currentQuestion = 0
        currentQuestionObj = null
    }

    // Determine if game has reached the end
    fun isGameOver(): Boolean = currentQuestion >= totalQuestions

    // Retrieve correct answer of the current question (if any)
    fun getCurrentCorrectAnswer(): Int? = currentQuestionObj?.correctAnswer

    // Data class representing a question and its correct answer
    data class Question(val text: String, val correctAnswer: Int)
}
