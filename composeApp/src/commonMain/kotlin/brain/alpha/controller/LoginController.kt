package brain.alpha.controller

import androidx.compose.runtime.*
import brain.alpha.controller.AuthController
import brain.alpha.model.UserSession
import brain.alpha.model.UserSession.clear
import brain.alpha.services.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * LoginController.kt
 *
 * Handles login-related logic in a stateful singleton, including:
 * - Managing input states for login fields.
 * - Handling user authentication (login and guest).
 * - Propagating navigation between screens.
 * - Managing error and loading states.
 */

object LoginController {

    // Stores the current email input
    var email by mutableStateOf("")

    // Stores the current password input
    var password by mutableStateOf("")

    // Holds error message to be shown on screen (if any)
    var errorMessage by mutableStateOf<String?>(null)

    // Indicates if the login operation is in progress
    var isLoading by mutableStateOf(false)

    // Navigation lambda set in App.kt
    lateinit var navigateTo: (String) -> Unit


    /**
     * Called when the login button is clicked.
     * Validates input, attempts login, updates state and navigates.
     */
    fun onLoginClicked() {
        if (email.isBlank() || password.isBlank()) {
            errorMessage = "Todos los campos son obligatorios."
            return
        }

        isLoading = true
        errorMessage = null

        CoroutineScope(Dispatchers.Default).launch {
            val result = AuthController.signIn(email.trim(), password.trim())
            isLoading = false
            result.onSuccess {
                clear()
                val user = SupabaseClientProvider.client.auth.currentUserOrNull()
                if (user != null) {
                    UserSession.setUserId(user.id)
                }
                navigateTo("home")
            }.onFailure { error ->
                val message = when {
                    error.message?.contains("Invalid login credentials", ignoreCase = true) == true -> {
                        "Email o contraseña incorrectos."
                    }

                    error.message?.contains("Email not confirmed", ignoreCase = true) == true -> {
                        "Debes confirmar tu email antes de iniciar sesión."
                    }

                    error.message?.contains("rate limit", ignoreCase = true) == true -> {
                        "Demasiados intentos. Intenta más tarde."
                    }


                    else -> error.message ?: "Error desconocido durante el login."
                }
                errorMessage = message
            }
        }
    }

    /**
     * Starts a guest session and navigates to home.
     */
    fun loginAsGuest() {
        // Set guest session manually
        UserSession.setGuestSession("Invitado")
        clear()
        navigateTo("home")
    }
    fun goLeaderboard(){
        clear()
        navigateTo("leaderboard")
    }

    /**
     * Navigates to sign-up screen and clears form state.
     */
    fun onSignUpClick() {
        clear()
        navigateTo("signup")
    }


    /**
     * Sets navigation lambda from App.kt to decouple navigation logic.
     */
    fun setNavigationCallback(callback: (String) -> Unit) {
        navigateTo = callback
    }

    // Shortcut navigation helpers
    fun navigateMemory() = navigateTo("memory")
    fun navigateLogic() = navigateTo("logic")
    fun navigateConcentration() = navigateTo("concentration")
    fun navigateAgility() = navigateTo("agility")

    /**
     * Clears login state and error messages.
     */
    fun clear() {
        email = ""
        password = ""
        errorMessage = null
        isLoading = false
    }

    // Reserved callbacks for user/settings buttons
    fun buttonSettings() {
        navigateTo("settings")
    }
    fun buttonUsername() {
        navigateTo("profile")
    }
}
