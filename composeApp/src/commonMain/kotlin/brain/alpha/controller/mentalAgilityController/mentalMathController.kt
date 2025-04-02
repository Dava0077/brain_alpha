package controller

import model.MentalMathModel
import model.Question

class MentalMathController(private val model: MentalMathModel,
    ) {
    fun startGame(onQuestionGenerated: (Question) -> Unit) {
        if (!model.isGameOver()) {
            val question = model.generateQuestion()
            onQuestionGenerated(question)
        }
    }

    fun submitAnswer(
        userAnswer: Int,
        timeTaken: Double,
        onAnswerChecked: (Boolean, Int, String?) -> Unit
    ) {
        val isCorrect = model.checkAnswer(userAnswer, timeTaken)
        val correctAnswer = if (!isCorrect) {
            "La respuesta correcta era ${model.getCurrentCorrectAnswer()}"
        } else {
            null
        }
        onAnswerChecked(isCorrect, model.score, correctAnswer)
    }

    fun resetGame() {
        model.resetGame()
    }

    fun isGameOver(): Boolean = model.isGameOver()
    fun getScore(): Int = model.score
    fun getTotalQuestions(): Int = model.totalQuestions
    fun getCurrentQuestionNumber(): Int = model.currentQuestion + 1 // Para mostrar el número correcto
}