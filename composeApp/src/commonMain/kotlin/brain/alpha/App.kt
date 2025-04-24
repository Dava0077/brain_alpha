package brain.alpha

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import brain.alpha.view.LoginScreen
import brain.alpha.controller.LoginController
import brain.alpha.controller.SettingsController
import brain.alpha.controller.concentrationController.concentrationHomeController
import brain.alpha.controller.concentrationController.oddEmojiController
import brain.alpha.controller.concentrationController.stroopController
import brain.alpha.controller.logicController.logicHomeController
import brain.alpha.controller.logicController.sudokuController
import brain.alpha.controller.logicController.wordSearchController
import brain.alpha.controller.memoryController.couplesController
import brain.alpha.controller.memoryController.memoryHomeController
import brain.alpha.controller.memoryController.simonController
import brain.alpha.controller.mentalAgilityController.clickNumberController
import brain.alpha.controller.mentalAgilityController.mentalHomeController
import brain.alpha.controller.mentalAgilityController.mentalMathController
import brain.alpha.model.UserSession
import brain.alpha.services.SupabaseClientProvider
import brain.alpha.view.HomeScreen
import brain.alpha.view.SettingsScreen
import brain.alpha.view.SignUpScreen
import brain.alpha.view.concentrationView.concentrationHomeScreen
import brain.alpha.view.concentrationViews.OddEmojiScreen
import brain.alpha.view.concentrationViews.StroopScreen
import brain.alpha.view.logicView.logicHomeScreen
import brain.alpha.view.logicView.sudokuScreen
import brain.alpha.view.logicView.wordSearchScreen
import brain.alpha.view.memoryView.couplesScreen
import brain.alpha.view.memoryView.memoryHomeScreen
import brain.alpha.view.memoryView.simonScreen
import brain.alpha.view.mentalAgilityView.clickNumberScreen
import brain.alpha.view.mentalAgilityView.mentalHomeScreen
import io.github.jan.supabase.auth.auth

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


        //Persistent log session
        LaunchedEffect(Unit) {
            val session = SupabaseClientProvider.client.auth.sessionManager.loadSession()
            val user = SupabaseClientProvider.client.auth.currentUserOrNull()

            if (session != null && user != null) {
                UserSession.setSession(
                    id = user.id,
                    emailValue = user.email ?: "desconocido@correo.com",
                    usernameValue = user.userMetadata?.get("username")?.toString() ?: "Usuario"
                )
                LoginController.clear()
                LoginController.navigateTo("home")
            }
        }


        // Assigns a navigation callback for use in controllers
        //LOGIN
        LoginController.navigateTo = { route -> currentScreen = route }
        //SETTINGS
        SettingsController.navigateTo = { route -> currentScreen = route }

        //MEMORY GAMES
        memoryHomeController.navigateTo = { route -> currentScreen = route }
        couplesController.navigateTo = { route -> currentScreen = route }
        simonController.navigateTo = { route -> currentScreen = route }

        //MENTAL AGILTY
        mentalMathController.navigateTo = { route -> currentScreen = route }
        clickNumberController.navigateTo = { route -> currentScreen = route }
        mentalHomeController.navigateTo = { route -> currentScreen = route }

        //CONCENTRATION
        concentrationHomeController.navigateTo = { route -> currentScreen = route}
        stroopController.navigateTo = { route -> currentScreen = route }
        oddEmojiController.navigateTo = {route -> currentScreen = route}

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
            //SETTINGS
            "settings" -> SettingsScreen(
                controller = SettingsController
            )
            //MEMORY
            "memory" -> memoryHomeScreen(
                controller = memoryHomeController)
            "couples" -> couplesScreen(
                controller = couplesController,

            )
            "simon" -> simonScreen(
                controller = simonController,

            )

            //MENTAL AGILITY
            "agility" -> mentalHomeScreen(
                controller = mentalHomeController,
                onNavigateToHome = {currentScreen = "home"},
                onNavigateToMath = {currentScreen = "math"},
                onNavigateToClick = {currentScreen = "click"}
            )
            "math" -> mentalMathView(
                controller = mentalMathController,
                onNavigateToHome = {currentScreen = "agility"}
            )
            "click" -> clickNumberScreen(
                controller = clickNumberController,
                onNavigateToHome = {currentScreen = "agility"}
            )


            //CONCENTRATION
            "concentration" -> concentrationHomeScreen(
                controller = concentrationHomeController
            )

            "stroop" -> StroopScreen(
                controller = stroopController
            )

            "emoji" -> OddEmojiScreen(
                controller = oddEmojiController
            )

            //LOGIC
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


