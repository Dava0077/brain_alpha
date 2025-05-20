package brain.alpha.controller.mentalAgilityController

import brain.alpha.model.shared.Difficulty
import model.mentalMathModel


// Controller object to handle mental math game logic and interactions
object mentalMathController {
    // Reference to the model that contains game state and logic
    private val model = mentalMathModel
    // Navigation callback to allow screen transitions
    lateinit var navigateTo: (String) -> Unit

    // Starts the game and generates the first question
    fun startGame(onQuestionGenerated: (mentalMathModel.Question) -> Unit) {
        if (!model.isGameOver()) {
            val question = model.generateQuestion()
            onQuestionGenerated(question)
        }
    }

    // Submits an answer from the user and triggers feedback callback
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

    // Resets the game state
    fun resetGame() {
        model.resetGame()
    }

    // Sets difficulty in the model
    fun setDifficulty(level: Difficulty) {
        model.setDifficulty(level)
    }

    // Returns current difficulty
    fun getDifficulty(): Difficulty = model.getDifficulty()
    // Returns time limit based on difficulty
    fun getTimeLimit(): Int = model.getTimeLimit()

    // Navigates back to agility screen
    fun goHome() {
        navigateTo("agility")
    }

    // Returns whether the game has ended
    fun isGameOver(): Boolean = model.isGameOver()
    // Returns the current score
    fun getScore(): Int = model.score
    // Returns the total number of questions
    fun getTotalQuestions(): Int = model.totalQuestions
    // Returns the number of the current question (1-based)
    fun getCurrentQuestionNumber(): Int = model.currentQuestion + 1
}

