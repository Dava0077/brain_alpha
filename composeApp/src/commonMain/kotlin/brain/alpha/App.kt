package brain.alpha

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import brain.alpha.controller.LeaderboardController
import brain.alpha.controller.LoginController
import brain.alpha.controller.SettingsController
import brain.alpha.controller.UserProfileController
import brain.alpha.controller.concentrationController.concentrationHomeController
import brain.alpha.controller.concentrationController.oddEmojiController
import brain.alpha.controller.concentrationController.stroopController
import brain.alpha.controller.logicController.logicHomeController
import brain.alpha.controller.logicController.sudokuController
import brain.alpha.controller.logicController.wordSearchController
import brain.alpha.controller.memoryController.pairsController
import brain.alpha.controller.memoryController.memoryHomeController
import brain.alpha.controller.memoryController.simonController
import brain.alpha.controller.mentalAgilityController.clickNumberController
import brain.alpha.controller.mentalAgilityController.mentalHomeController
import brain.alpha.controller.mentalAgilityController.mentalMathController
import brain.alpha.model.UserSession
import brain.alpha.services.SupabaseClientProvider
import brain.alpha.view.*
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
        val currentScreenState = remember { mutableStateOf("login") }
        val currentScreen by currentScreenState




        //LOGIN
        LoginController.navigateTo = { route -> currentScreenState.value = route }


        //Persistent log session
        LaunchedEffect(Unit) {
            val session = SupabaseClientProvider.client.auth.sessionManager.loadSession()
            val user = SupabaseClientProvider.client.auth.currentUserOrNull()

            if (session != null && user != null) {
                UserSession.setSession(
                    id = user.id,
                    emailValue = user.email ?: "desconocido@correo.com",
                    usernameValue = user.userMetadata?.get("username")?.toString()
                        ?: user.email?.substringBefore('@')?.replaceFirstChar { it.uppercase() }
                        ?: "Usuario"
                )
                LoginController.clear()
                LoginController.navigateTo("home")
            }
        }


        // Assigns a navigation callback for use in controllers

        //SETTINGS
        SettingsController.navigateTo = { route -> currentScreenState.value = route }
        UserProfileController.navigateTo = {route -> currentScreenState.value = route}
        LeaderboardController.navigateTo = {route -> currentScreenState.value = route}

        //MEMORY GAMES
        memoryHomeController.navigateTo = { route -> currentScreenState.value = route }
        pairsController.navigateTo = { route -> currentScreenState.value = route }
        simonController.navigateTo = { route -> currentScreenState.value = route }

        //MENTAL AGILTY
        mentalMathController.navigateTo = { route -> currentScreenState.value = route }
        clickNumberController.navigateTo = { route -> currentScreenState.value = route }
        mentalHomeController.navigateTo = { route -> currentScreenState.value = route }

        //CONCENTRATION
        concentrationHomeController.navigateTo = { route -> currentScreenState.value = route}
        stroopController.navigateTo = { route -> currentScreenState.value = route }
        oddEmojiController.navigateTo = {route -> currentScreenState.value = route}

        //LOGIC GAMES
        sudokuController.navigateTo = { route -> currentScreenState.value = route }
        logicHomeController.navigateTo = { route -> currentScreenState.value = route }
        wordSearchController.navigateTo = { route -> currentScreenState.value = route }


        // Controls which screen to display based on currentScreen
        when (currentScreenState.value) {
            "login" -> {
                LoginController.clear() // Clear fields on login screen load
                LoginScreen(
                    controller = LoginController,
                    onNavigateToSignUp = { currentScreenState.value = "signup" }
                )
            }


            "signup" -> SignUpScreen(
                onSuccess = {
                    LoginController.clear() // Clear fields on successful signup
                    currentScreenState.value = "login"
                },
                onFailure = { errorMessage ->
                    println("Error de registro: $errorMessage")
                },
                onGoToLogin = {
                    LoginController.clear() // Clear fields if manually returning
                    currentScreenState.value = "login"
                }
            )

            //HOME
            "home" -> HomeScreen(
                controller = LoginController,
                onNavigateToMemory = { currentScreenState.value = "memory" },
                onNavigateToLogic = { currentScreenState.value = "logic" },
                onNavigateToConcentration = { currentScreenState.value = "concentration" },
                onNavigateToAgility = { currentScreenState.value = "agility" }
            )
            //SETTINGS
            "settings" -> SettingsScreen(
                controller = SettingsController
            )
            //PROFILE
            "profile" -> UserProfileScreen(
                onBack = {currentScreenState.value = "home"}
            )
            //LEADERBOARD
            "leaderboard" -> leaderboardScreen(
                controller = LeaderboardController
            )
            //MEMORY
            "memory" -> memoryHomeScreen(
                controller = memoryHomeController)
            "couples" -> couplesScreen(
                controller = pairsController,

            )
            "simon" -> simonScreen(
                controller = simonController,

            )

            //MENTAL AGILITY
            "agility" -> mentalHomeScreen(
                controller = mentalHomeController,
                onNavigateToHome = {currentScreenState.value = "home"},
                onNavigateToMath = {currentScreenState.value = "math"},
                onNavigateToClick = {currentScreenState.value = "click"}
            )
            "math" -> mentalMathView(
                controller = mentalMathController,
                onNavigateToHome = {currentScreenState.value = "agility"}
            )
            "click" -> clickNumberScreen(
                controller = clickNumberController,
                onNavigateToHome = {currentScreenState.value = "agility"}
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
                onNavigateToHome = {currentScreenState.value = "home"},
                onNavigateToSudoku = {currentScreenState.value = "sudoku"},
                onNavigateToWordS = {currentScreenState.value = "wordS"}
            )
            "sudoku" -> sudokuScreen(
                controller = sudokuController,
                onNavigateToHome = {currentScreenState.value = "logic"}
            )
            "wordS" -> wordSearchScreen(
                controller = wordSearchController,
                onNavigateToHome = {currentScreenState.value = "logic"}
            )
        }
    }
}


