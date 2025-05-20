package brain.alpha.controller

import androidx.compose.runtime.*
import brain.alpha.controller.AuthController
import brain.alpha.model.UserModel
import brain.alpha.model.UserSession
import brain.alpha.model.UserSession.clear
import brain.alpha.services.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.user.UserInfo
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
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

    // Stores the current email entered in the login form
    var email by mutableStateOf("")

    // Stores the current password entered in the login form
    var password by mutableStateOf("")

    // Holds error messages to be shown in the UI
    var errorMessage by mutableStateOf<String?>(null)

    // Indicates whether login process is currently active
    var isLoading by mutableStateOf(false)

    // Navigation lambda, injected from App.kt, used for routing
    lateinit var navigateTo: (String) -> Unit

    // Tracks if Google login succeeded
    val googleLoginSuccess = mutableStateOf(false)


    /**
     * Called when the login button is pressed.
     * Validates form, attempts sign in, sets session and navigates on success.
     */
    fun onLoginClicked() {
        // Basic validation
        if (email.isBlank() || password.isBlank()) {
            errorMessage = "Todos los campos son obligatorios."
            return
        }

        isLoading = true
        errorMessage = null

        CoroutineScope(Dispatchers.Default).launch {
            // Try sign-in with Supabase
            val result = AuthController.signIn(email.trim(), password.trim())
            isLoading = false
            result.onSuccess {
                clear()
                // Retrieve current user from Supabase session
                val user = SupabaseClientProvider.client.auth.currentUserOrNull()
                if (user != null) {
                    UserSession.setUserId(user.id)

                    // Query for user's profile
                    val profileList = SupabaseClientProvider.client
                        .postgrest["users"]
                        .select {
                            filter { eq("id", user.id) }
                        }
                        .decodeList<UserModel>()

                    if (profileList.isEmpty()) {
                        errorMessage = "Tu perfil aún no está disponible. Intenta de nuevo en unos segundos."
                        return@launch
                    }

                    // Update local session
                    val profile = profileList.first()
                    UserSession.setSession(
                        id = user.id,
                        emailValue = profile.email,
                        usernameValue = profile.name
                    )

                }

                // Navigate to home screen
                navigateTo("home")
            }.onFailure { error ->
                // Handle common auth errors with user-friendly messages
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
     * Sets guest session and navigates to home.
     */
    fun loginAsGuest() {
        // Set guest session manually
        UserSession.setGuestSession("Invitado")
        clear()
        navigateTo("home")
    }

    /**
     * Clears form and navigates to leaderboard.
     */
    fun goLeaderboard(){
        clear()
        navigateTo("leaderboard")
    }

    /**
     * Navigates to the sign-up screen.
     */
    fun onSignUpClick() {
        clear()
        navigateTo("signup")
    }


    /**
     * Injects navigation callback from App.kt
     */
    fun setNavigationCallback(callback: (String) -> Unit) {
        navigateTo = callback
    }

    // Quick navigation shortcuts for different game modules
    fun navigateMemory() = navigateTo("memory")
    fun navigateLogic() = navigateTo("logic")
    fun navigateConcentration() = navigateTo("concentration")
    fun navigateAgility() = navigateTo("agility")

    /**
     * Clears email, password, loading and error states.
     */
    fun clear() {
        email = ""
        password = ""
        errorMessage = null
        isLoading = false
    }

    // Navigation methods for settings and profile
    fun buttonSettings() {
        navigateTo("settings")
    }
    fun buttonUsername() {
        navigateTo("profile")
    }

    /**
     * Resets the Google login success flag.
     */
    fun resetGoogleFlag() {
        googleLoginSuccess.value = false
    }

    /**
     * Initializes the user session from a UserInfo object (used in OAuth flows).
     */
    fun setUser(userInfo: UserInfo) {
        UserSession.setSession(
            id = userInfo.id,
            emailValue = userInfo.email ?: "desconocido@correo.com",
            usernameValue = userInfo.email?.substringBefore("@")?.replaceFirstChar { it.uppercase() } ?: "Usuario"
        )
    }



}
