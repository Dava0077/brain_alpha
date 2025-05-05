package brain.alpha.controller

import brain.alpha.model.UserSession
import brain.alpha.services.SupabaseClientProvider
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.OAuthProvider
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserInfo
import io.github.jan.supabase.compose.auth.ComposeAuth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.result.PostgrestResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put


/**
 * AuthController.kt
 *
 * Handles authentication operations using Supabase, including:
 * - Signing in users with email and password.
 * - Registering new users and inserting them into a custom "users" table.
 * - Signing out and clearing user session.
 *
 * The session state is stored globally via the UserSession singleton.
 * All operations are suspend functions using Dispatchers.Default for async behavior.
 */


object AuthController {

    // Access Supabase Auth module
    private val auth get() = SupabaseClientProvider.client.auth

    /**
     * Signs in a user using email and password.
     * On success, updates the UserSession with retrieved user data.
     */
    suspend fun signIn(email: String, password: String): Result<UserInfo> = withContext(Dispatchers.Default) {
        runCatching {
            auth.signInWith(Email) {
                this.email = email
                this.password = password
            }

            val user = auth.currentUserOrNull()
                ?: throw IllegalStateException("No se pudo obtener el usuario")

            if (user.emailConfirmedAt == null) {
                throw IllegalStateException("Debes confirmar tu correo electrónico antes de iniciar sesión.")
            }

            UserSession.setSession(
                id = user.id,
                emailValue = user.email ?: "desconocido@correo.com",
                usernameValue = user.userMetadata?.get("username")?.toString() ?: "Usuario"
            )

            user
        }
    }

    /**
     * Registers a new user with validation, authentication, and insertion into the custom "users" table.
     * @throws IllegalArgumentException if inputs are invalid.
     */
    suspend fun signUp(
        email: String,
        username: String,
        password: String,
        repeatPassword: String
    ): Result<UserInfo> = withContext(Dispatchers.Default) {
        runCatching {

            // Input validations
            if (email.isBlank() || username.isBlank() || password.isBlank() || repeatPassword.isBlank()) {
                throw IllegalArgumentException("Todos los campos son obligatorios.")
            }

            if (!isValidEmail(email)) {
                throw IllegalArgumentException("El email no es válido.")
            }

            if (password.length < 6) {
                throw IllegalArgumentException("La contraseña debe tener al menos 6 caracteres.")
            }

            if (password != repeatPassword) {
                throw IllegalArgumentException("Las contraseñas no coinciden.")
            }

            // Register user with metadata
            auth.signUpWith(Email) {
                this.email = email
                this.password = password
                data = buildJsonObject {
                    put("username", username)
                }
            }

            // Immediately sign in after registration
            auth.signInWith(Email) {
                this.email = email
                this.password = password
            }

            val user = auth.currentUserOrNull()
                ?: throw IllegalStateException("No se pudo obtener el usuario tras el registro.")

            // Insert into custom users table using PostgREST
            SupabaseClientProvider.client.postgrest["users"].insert(
                buildJsonObject {
                    put("id", user.id)
                    put("email", user.email ?: "")
                    put("name", username)
                }
            )

            UserSession.setSession(
                id = user.id,
                emailValue = user.email ?: "desconocido@correo.com",
                usernameValue = username
            )

            user
        }
    }


    /**
     * Signs out the current user and clears session state.
     */
    suspend fun signOut() {
        auth.signOut()
        UserSession.clear()
    }

    /**
     * Validates basic email format with a regular expression.
     */
    fun isValidEmail(email: String): Boolean {
        val regex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
        return regex.toRegex().matches(email)
    }

    suspend fun sendPasswordReset(email: String): Result<Unit> = withContext(Dispatchers.Default) {
        runCatching {
            if (!isValidEmail(email)) {
                throw IllegalArgumentException("El email ingresado no es válido.")
            }

            SupabaseClientProvider.client.auth.resetPasswordForEmail(email)
        }
    }

    //Delete user function
//    suspend fun deleteAccount(): Result<Unit> = withContext(Dispatchers.Default) {
//        runCatching {
//            SupabaseClientProvider.client.auth.
//            UserSession.clear()
//        }
//    }

    suspend fun verifyCurrentPassword(email: String, password: String): Result<Unit> = withContext(Dispatchers.Default) {
        runCatching {
            SupabaseClientProvider.client.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
        }
    }

    suspend fun resendVerification(email: String): Result<Unit> = withContext(Dispatchers.Default) {
        runCatching {
            SupabaseClientProvider.client.auth.resendEmail(
                type = OtpType.Email.EMAIL,
                email = email
            )
        }
    }


//    suspend fun signInWithGoogle(): Result<PostgrestResult> = withContext(Dispatchers.Default) {
//        runCatching {
//            val result = SupabaseClientProvider.client.auth.
//
//            val user = SupabaseClientProvider.client.auth.currentUserOrNull()
//                ?: throw IllegalStateException("No se pudo obtener el usuario tras el login con Google.")
//
//            val username = user.userMetadata?.get("name")?.toString() ?: "Usuario"
//
//            UserSession.setSession(
//                id = user.id,
//                emailValue = user.email ?: "desconocido@correo.com",
//                usernameValue = username
//            )
//
//            // Opcional: sincroniza con tabla `users` si deseas asegurar consistencia
//            SupabaseClientProvider.client.postgrest["users"].upsert(
//                buildJsonObject {
//                    put("id", user.id)
//                    put("email", user.email ?: "")
//                    put("name", username)
//                }
//            )
//        }
//    }

}

