package brain.alpha

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import brain.alpha.controller.*
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
 * Main entry point of the Compose UI.
 * Controls routing using a central screen state variable.
 */

@Composable
fun App() {
    MaterialTheme {
        // State that tracks the current screen by route name
        val currentScreenState = remember { mutableStateOf("login") }
        val currentScreen by currentScreenState

        // Set navigation callbacks for LoginController
        LoginController.navigateTo = { route -> currentScreenState.value = route }


        // Automatically restore session if one exists (using Supabase)
        LaunchedEffect(Unit) {
            val session = SupabaseClientProvider.client.auth.sessionManager.loadSession()
            val user = SupabaseClientProvider.client.auth.currentUserOrNull()

            if (session != null && user != null) {
                val userProfile = AuthController.loadUserProfile()

                val fallbackName = user.email?.substringBefore('@')?.replaceFirstChar { it.uppercase() } ?: "Usuario"
                val username = userProfile?.name?.takeIf { it.isNotBlank() } ?: fallbackName

                UserSession.setSession(
                    id = user.id,
                    emailValue = user.email ?: "desconocido@correo.com",
                    usernameValue = username
                )

                LoginController.clear()
                LoginController.navigateTo("home")
            }
        }

        // Inject navigation lambdas into each controller

        //SETTINGS
        SettingsController.navigateTo = { route -> currentScreenState.value = route }
        UserProfileController.navigateTo = {route -> currentScreenState.value = route}
        LeaderboardController.navigateTo = {route -> currentScreenState.value = route}
        GoogleAuthController.navigateTo ={route -> currentScreenState.value = route}

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


        // Handle navigation and render the corresponding screen
        when (currentScreenState.value) {

            // LOGIN & SIGNUP
            "login" -> {
                LoginController.clear()
                LoginScreen(
                    controller = LoginController,
                    onNavigateToSignUp = { currentScreenState.value = "signup" }
                )
            }


            "signup" -> SignUpScreen(
                onSuccess = {
                    LoginController.clear()
                    currentScreenState.value = "login"
                },
                onFailure = { errorMessage ->
                    println("Error de registro: $errorMessage")
                },
                onGoToLogin = {
                    LoginController.clear()
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
            "leaderboard" -> LeaderboardScreen(
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


