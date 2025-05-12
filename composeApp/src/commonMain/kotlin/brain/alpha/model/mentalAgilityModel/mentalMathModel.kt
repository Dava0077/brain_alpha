package model

import brain.alpha.model.shared.Difficulty

object mentalMathModel {
    var score = 0
        private set
    var currentQuestion = 0
        private set
    val totalQuestions = 10
    private var currentQuestionObj: Question? = null
    private var difficulty: Difficulty = Difficulty.MEDIUM

    fun setDifficulty(level: Difficulty) {
        difficulty = level
    }

    fun getDifficulty(): Difficulty = difficulty

    fun getTimeLimit(): Int = when (difficulty) {
        Difficulty.EASY -> 90
        Difficulty.MEDIUM -> 60
        Difficulty.HARD -> 30
    }

    fun generateQuestion(): Question {
        val num1 = kotlin.random.Random.nextInt(1, 20)
        val num2 = kotlin.random.Random.nextInt(1, 20)
        val operation = kotlin.random.Random.nextInt(0, 3)

        val question = when (operation) {
            0 -> Question("$num1 + $num2 = ?", num1 + num2)
            1 -> Question("$num1 - $num2 = ?", num1 - num2)
            else -> Question("$num1 * $num2 = ?", num1 * num2)
        }
        currentQuestionObj = question
        return question
    }

    fun checkAnswer(userAnswer: Int, timeTaken: Double): Boolean {
        val isCorrect = userAnswer == currentQuestionObj?.correctAnswer
        if (isCorrect) score += calculatePoints(timeTaken)
        currentQuestion++
        return isCorrect
    }

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

    fun resetGame() {
        score = 0
        currentQuestion = 0
        currentQuestionObj = null
    }

    fun isGameOver(): Boolean = currentQuestion >= totalQuestions
    fun getCurrentCorrectAnswer(): Int? = currentQuestionObj?.correctAnswer

    data class Question(val text: String, val correctAnswer: Int)
}