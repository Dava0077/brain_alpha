package brain.alpha.controller.mentalAgilityController

import brain.alpha.model.shared.Difficulty
import model.mentalMathModel


object mentalMathController {
    private val model = mentalMathModel
    lateinit var navigateTo: (String) -> Unit

    fun startGame(onQuestionGenerated: (mentalMathModel.Question) -> Unit) {
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
        } else null
        onAnswerChecked(isCorrect, model.score, correctAnswer)
    }

    //Reset game
    fun resetGame() {
        model.resetGame()
    }

    fun setDifficulty(level: Difficulty) {
        model.setDifficulty(level)
    }

    fun getDifficulty(): Difficulty = model.getDifficulty()
    fun getTimeLimit(): Int = model.getTimeLimit()

    fun goHome() {
        navigateTo("agility")
    }

    fun isGameOver(): Boolean = model.isGameOver()
    fun getScore(): Int = model.score
    fun getTotalQuestions(): Int = model.totalQuestions
    fun getCurrentQuestionNumber(): Int = model.currentQuestion + 1
}
