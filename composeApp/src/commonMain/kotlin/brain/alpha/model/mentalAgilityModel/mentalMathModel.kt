package model

import kotlin.random.Random

data class Question(val text: String, val correctAnswer: Int)

class MentalMathModel {
    var score = 0
        private set
    var currentQuestion = 0
        private set
    val totalQuestions = 10
    private var currentQuestionObj: Question? = null

    fun generateQuestion(): Question {
        val num1 = Random.nextInt(1, 20)
        val num2 = Random.nextInt(1, 20)
        val operation = Random.nextInt(0, 3)

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
        if (isCorrect) {
            score += calculatePoints(timeTaken)
        }
        currentQuestion++
        return isCorrect
    }

    private fun calculatePoints(timeTaken: Double): Int {
        return when {
            timeTaken < 3 -> 10
            timeTaken < 6 -> 5
            else -> 3
        }
    }

    fun resetGame() {
        score = 0
        currentQuestion = 0
        currentQuestionObj = null
    }

    fun isGameOver(): Boolean = currentQuestion >= totalQuestions
}