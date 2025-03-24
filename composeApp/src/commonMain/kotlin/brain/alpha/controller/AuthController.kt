package brain.alpha.controller


import androidx.compose.ui.text.input.KeyboardType.Companion.Email
import brain.alpha.model.UserSession
import brain.alpha.services.SupabaseClientProvider
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import io.github.jan.supabase.auth.user.UserInfo
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth

object AuthController {

    private val auth: Auth get() = SupabaseClientProvider.client.auth

    suspend fun signIn(email: String, password: String): Result<UserInfo> = withContext(Dispatchers.Default) {
        runCatching {
            val result = auth.signInWith(Email {
                this.email = email
                this.password = password
            })

            val user = auth.currentUserOrNull()
                ?: throw IllegalStateException("No se pudo obtener el usuario")

            UserSession.isLoggedIn.value = true
            UserSession.userId.value = user.id
            UserSession.email.value = user.email

            user
        }
    }

    suspend fun signUp(email: String, password: String): Result<UserInfo> = withContext(Dispatchers.Default) {
        runCatching {
            val response = auth.signUpWith(Email {
                this.email = email
                this.password = password
            })

            val user = response.user
                ?: throw Exception("No se pudo crear el usuario")

            UserSession.isLoggedIn.value = true
            UserSession.userId.value = user.id
            UserSession.email.value = user.email

            user
        }
    }

    suspend fun signOut() {
        auth.signOut()
        UserSession.clear()
    }
}