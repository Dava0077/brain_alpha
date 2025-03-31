package brain.alpha

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import brain.alpha.view.LoginScreen
import brain.alpha.controller.LoginController
import brain.alpha.controller.memoryControllers.couplesController
import brain.alpha.view.HomeScreen
import brain.alpha.view.SignUpScreen
import brain.alpha.view.memoryViews.CouplesScreen
import controller.MentalMathController
import model.MentalMathModel
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
        LoginController.navigateTo = { route -> currentScreen = route }


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


            "home" -> HomeScreen(
                controller = LoginController,
                onNavigateToMemory = { currentScreen = "memory" },
                onNavigateToLogic = { currentScreen = "logic" },
                onNavigateToConcentration = { currentScreen = "concentration" },
                onNavigateToAgility = { currentScreen = "agility" }
            )

            "memory" -> CouplesScreen(
                controller = couplesController

            )


            "agility" -> mentalMathView(
                controller = MentalMathController(MentalMathModel())
            )


            "concentration" -> Text("🧠 Pantalla de Concentración (TODO)")


            "logic" -> Text("🧠 Pantalla de Logica (TODO)")
        }
    }
}


