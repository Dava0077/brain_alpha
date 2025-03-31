package brain.alpha

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*


import brain.alpha.view.LoginScreen
import brain.alpha.controller.LoginController
import brain.alpha.controller.memoryControllers.CouplesController
import brain.alpha.view.HomeScreen
import brain.alpha.view.SignUpScreen
import brain.alpha.view.memoryViews.CouplesScreen
import controller.MentalMathController
import model.MentalMathModel
import view.mentalMathView


@Composable
fun App() {
    MaterialTheme {
        var currentScreen by remember { mutableStateOf("login") }


        LoginController.navigateTo = { route -> currentScreen = route }


        when (currentScreen) {
            "login" -> LoginScreen(
                controller = LoginController,
                onNavigateToSignUp = { currentScreen = "signup" }
            )


            "signup" -> SignUpScreen(
                onSuccess = {
                    LoginController.clear() // Limpiar campos antes de volver
                    currentScreen = "login"
                },
                onFailure = { errorMessage ->
                    println("Error de registro: $errorMessage")
                },
                onGoToLogin = {
                    LoginController.clear() // También limpiar si vuelve manualmente
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
                controller = CouplesController(),

            )


            "agility" -> mentalMathView(
                controller = MentalMathController(MentalMathModel())
            )


            "concentration" -> Text("🧠 Pantalla de Concentración (TODO)")


            "logic" -> Text("🧠 Pantalla de Logica (TODO)")
        }
    }
}


