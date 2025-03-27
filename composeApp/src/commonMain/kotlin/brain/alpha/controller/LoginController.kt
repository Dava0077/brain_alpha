package brain.alpha.controller

import androidx.compose.runtime.*
import brain.alpha.controller.AuthController
import brain.alpha.model.UserSession
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * LoginController.kt
 *
 * Handles login-related logic, including user login, guest access,
 * error handling and navigation redirection.
 *
 * Fields:
 * - email, password: Bound to UI inputs.
 * - errorMessage: Holds any login error to be shown on screen.
 * - isLoading: Indicates loading state during authentication.
 *
 * Functions:
 * - onLoginClicked: Attempts login via AuthController.
 * - loginAsGuest: Initializes guest session.
 * - onSignUpClick: Navigates to sign-up screen.
 * - clear: Resets all state fields.
 */

object LoginController {

    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var errorMessage by mutableStateOf<String?>(null)
    var isLoading by mutableStateOf(false)

    // Navigation callback, assigned in App.kt
    lateinit var navigateTo: (String) -> Unit

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
                navigateTo("home")
            }.onFailure { error ->
                errorMessage = error.message ?: "Inicio de sesión fallido"
            }
        }
    }

    fun loginAsGuest() {
        // Set guest session manually
        UserSession.setGuestSession("Invitado")
        clear()
        navigateTo("home")
    }

    fun onSignUpClick() {
        clear()
        navigateTo("signup")
    }


    fun setNavigationCallback(callback: (String) -> Unit) {
        navigateTo = callback
    }

    fun navigateMemory() {
        navigateTo("memory")
    }

    fun clear() {
        email = ""
        password = ""
        errorMessage = null
        isLoading = false
    }

    // Reserved for future button logic
    fun buttonSettings() {}
    fun buttonUsername() {}
}
