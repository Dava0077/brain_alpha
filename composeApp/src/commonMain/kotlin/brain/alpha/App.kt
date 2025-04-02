package brain.alpha

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import brain.alpha.view.LoginScreen
import brain.alpha.controller.LoginController
import brain.alpha.controller.concentrationController.stroopController
import brain.alpha.controller.logicController.logicHomeController
import brain.alpha.controller.logicController.sudokuController
import brain.alpha.controller.logicController.wordSearchController
import brain.alpha.controller.memoryController.couplesController
import brain.alpha.controller.mentalAgilityController.mentalMathController
import brain.alpha.view.HomeScreen
import brain.alpha.view.SignUpScreen
import brain.alpha.view.concentrationViews.StroopScreen
import brain.alpha.view.logicView.logicHomeScreen
import brain.alpha.view.logicView.sudokuScreen
import brain.alpha.view.logicView.wordSearchScreen
import brain.alpha.view.memoryView.CouplesScreen

import view.mentalMathView

/**
 * App.kt
 *
 * This is the main Composable entry point of the application.
 * It controls navigation between screens using a simple state-based approach.
 * The screen displayed is determined by the value of `currentScreen`.
 */





@Composable
fun App() {
    MaterialTheme {
        // Stores the current screen route
        var currentScreen by remember { mutableStateOf("login") }

        // Assigns a navigation callback for use in controllers
        //LOGIN
        LoginController.navigateTo = { route -> currentScreen = route }

        //MEMORY GAMES
        couplesController.navigateTo = { route -> currentScreen = route }

        //MENTAL AGILTY
        mentalMathController.navigateTo = { route -> currentScreen = route }

        //CONCENTRATION
        stroopController.navigateTo = { route -> currentScreen = route }

        //LOGIC GAMES
        sudokuController.navigateTo = { route -> currentScreen = route }
        logicHomeController.navigateTo = { route -> currentScreen = route }
        wordSearchController.navigateTo = { route -> currentScreen = route }


        // Controls which screen to display based on currentScreen
        when (currentScreen) {
            "login" -> {
                LoginController.clear() // Clear fields on login screen load
                LoginScreen(
                    controller = LoginController,
                    onNavigateToSignUp = { currentScreen = "signup" }
                )
            }


            "signup" -> SignUpScreen(
                onSuccess = {
                    LoginController.clear() // Clear fields on successful signup
                    currentScreen = "login"
                },
                onFailure = { errorMessage ->
                    println("Error de registro: $errorMessage")
                },
                onGoToLogin = {
                    LoginController.clear() // Clear fields if manually returning
                    currentScreen = "login"
                }
            )

            //HOME
            "home" -> HomeScreen(
                controller = LoginController,
                onNavigateToMemory = { currentScreen = "memory" },
                onNavigateToLogic = { currentScreen = "logic" },
                onNavigateToConcentration = { currentScreen = "concentration" },
                onNavigateToAgility = { currentScreen = "agility" }
            )

            "memory" -> CouplesScreen(
                controller = couplesController,
                onNavigateToHome = {currentScreen = "home"}


            )


            "agility" -> mentalMathView(
                controller = mentalMathController,
                onNavigateToHome = {currentScreen = "home"}

            )


            "concentration" -> StroopScreen(
                controller = stroopController,
                onNavigateToHome = {currentScreen="home"}
            )

            "logic" -> logicHomeScreen(
                controller = logicHomeController,
                onNavigateToHome = {currentScreen = "home"},
                onNavigateToSudoku = {currentScreen = "sudoku"},
                onNavigateToWordS = {currentScreen = "wordS"}
            )
            "sudoku" -> sudokuScreen(
                controller = sudokuController,
                onNavigateToHome = {currentScreen = "logic"}
            )
            "wordS" -> wordSearchScreen(
                controller = wordSearchController,
                onNavigateToHome = {currentScreen = "logic"}
            )
        }
    }
}


