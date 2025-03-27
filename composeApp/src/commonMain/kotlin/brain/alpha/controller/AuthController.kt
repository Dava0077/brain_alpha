package brain.alpha.controller

import brain.alpha.model.UserSession
import brain.alpha.services.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

/**
 * AuthController.kt
 *
 * Handles user authentication using Supabase, including login, sign-up, and logout logic.
 *
 * Responsibilities:
 * - signIn: Authenticates the user with email and password, and updates the global session state.
 * - signUp: Registers a new user with email, password, and username. Performs local validation and
 *           stores user metadata in Supabase.
 * - signOut: Logs the user out and clears the session data from UserSession.
 *
 * This controller uses coroutines for non-blocking asynchronous operations.
 * Session state is managed via the singleton UserSession object.
 */


object AuthController {

    private val auth get() = SupabaseClientProvider.client.auth

    suspend fun signIn(email: String, password: String): Result<UserInfo> = withContext(Dispatchers.Default) {
        runCatching {
            auth.signInWith(Email) {
                this.email = email
                this.password = password
            }

            val user = auth.currentUserOrNull()
                ?: throw IllegalStateException("No se pudo obtener el usuario")

            UserSession.setSession(
                id = user.id,
                emailValue = user.email ?: "desconocido@correo.com",
                usernameValue = user.userMetadata?.get("username")?.toString() ?: "Usuario"
            )

            user
        }
    }

    suspend fun signUp(
        email: String,
        username: String,
        password: String,
        repeatPassword: String
    ): Result<UserInfo> = withContext(Dispatchers.Default) {
        runCatching {
            // Validación básica
            if (email.isBlank() || username.isBlank() || password.isBlank() || repeatPassword.isBlank()) {
                throw IllegalArgumentException("Todos los campos son obligatorios.")
            }

            if (password != repeatPassword) {
                throw IllegalArgumentException("Las contraseñas no coinciden.")
            }

            // Registro con metadata
            auth.signUpWith(Email) {
                this.email = email
                this.password = password
                data = buildJsonObject {
                    put("username", username)
                }
            }

            val user = auth.currentUserOrNull()
                ?: throw IllegalStateException("No se pudo obtener el usuario tras el registro")

            UserSession.setSession(
                id = user.id,
                emailValue = user.email ?: "desconocido@correo.com",
                usernameValue = username
            )

            user
        }
    }

    suspend fun signOut() {
        auth.signOut()
        UserSession.clear()
    }
}

