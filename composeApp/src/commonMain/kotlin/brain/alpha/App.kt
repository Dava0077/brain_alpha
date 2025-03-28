package brain.alpha

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*


import brain.alpha.view.LoginScreen
import brain.alpha.controller.LoginController
import brain.alpha.view.HomeScreen
import brain.alpha.view.SignUpScreen


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
                onSuccess = {currentScreen = "signup"},
                onFailure = {errorMessage -> println("Error de registro: $errorMessage")},
                onGoToLogin = {currentScreen = "login"}
            )


            "home" -> HomeScreen(
                controller = LoginController,
                onNavigateToMemory = { currentScreen = "memory" },
                onNavigateToLogic = { currentScreen = "logic" },
                onNavigateToConcentration = { currentScreen = "concentration" },
                onNavigateToAgility = { currentScreen = "agility" }
            )

            "memory" -> Text("🧠 Pantalla de Memoria (TODO)")


            "logic" -> Text("🧠 Pantalla de Lógica (TODO)")


            "concentration" -> Text("🧠 Pantalla de Concentración (TODO)")


            "agility" -> Text("🧠 Pantalla de Agilidad Mental (TODO)")
        }
    }
}


