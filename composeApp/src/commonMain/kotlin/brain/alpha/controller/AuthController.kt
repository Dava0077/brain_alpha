package brain.alpha.controller

import brain.alpha.model.UserModel
import brain.alpha.model.UserSession
import brain.alpha.services.SupabaseClientProvider
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.OAuthProvider
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserInfo
import io.github.jan.supabase.postgrest.postgrest
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import kotlinx.serialization.json.*

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

    suspend fun deleteUserFromAuth(userId: String): Boolean = withContext(Dispatchers.Default) {
        val serviceRoleKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InV6enV6amJhdnpudGRiYnJjemxhIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc0MTg5MjUyOCwiZXhwIjoyMDU3NDY4NTI4fQ._ShBWX1s5yrBMtJztoy4mI02cgJzOZXZTgVHFv0ERMA"
        val projectUrl = "https://uzzuzjbavzntdbbrczla.supabase.co"

        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                    isLenient = true
                })
            }
        }

        val authResponse = client.delete("$projectUrl/auth/v1/admin/users/$userId") {
            headers {
                append(HttpHeaders.Authorization, "Bearer $serviceRoleKey")
                append("apikey", serviceRoleKey)
            }
        }

        val authDeleted = authResponse.status.value in 200..299

        // Solo intenta borrar de public.users si el auth se eliminó correctamente
        if (authDeleted) {
            val clientSupabase = SupabaseClientProvider.client
            runCatching {
                clientSupabase.postgrest["users"].delete {
                    filter { eq("id", userId) }
                }
            }.onFailure {
                println("❌ Error al eliminar de public.users: ${it.message}")
            }
        }

        client.close()
        return@withContext authDeleted
    }

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
    /*suspend fun signUp(
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
    }*/
    suspend fun signUp(
        email: String,
        username: String,
        password: String,
        repeatPassword: String
    ): Result<UserInfo> = withContext(Dispatchers.Default) {
        runCatching {
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

            auth.signUpWith(Email) {
                this.email = email
                this.password = password
                data = buildJsonObject {
                    put("username", username)
                }
            }

            // Autenticamos inmediatamente después para obtener el ID de usuario
            auth.signInWith(Email) {
                this.email = email
                this.password = password
            }

            val user = auth.currentUserOrNull()
                ?: throw IllegalStateException("No se pudo obtener el usuario tras el registro.")

            // Insertamos en la tabla 'users' con su ID real
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
    suspend fun loadUserProfile(): UserModel? = withContext(Dispatchers.Default) {
        val client = SupabaseClientProvider.client
        val userId = client.auth.currentUserOrNull()?.id ?: return@withContext null

        runCatching {
            client.postgrest["users"]
                .select {
                    filter { eq("id", userId) }
                    single()
                }
                .decodeSingle<UserModel>()
        }.getOrNull()
    }



    suspend fun ensureUserExists(userId: String, email: String?, username: String?) {
        SupabaseClientProvider.client.postgrest["users"].upsert(
            buildJsonObject {
                put("id", userId)
                put("email", email ?: "")
                put("name", username ?: "GoogleUser")
            }
        )
    }
    suspend fun loadUserProfileByEmail(email: String): UserModel? = withContext(Dispatchers.Default) {
        SupabaseClientProvider.client.postgrest["users"]
            .select {
                filter { eq("email", email) }
            }
            .decodeSingleOrNull<UserModel>()
    }



}

