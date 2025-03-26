package brain.alpha.controller


import brain.alpha.model.UserSession
import brain.alpha.services.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


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


            UserSession.isLoggedIn.value = true
            UserSession.userId.value = user.id
            UserSession.email.value = user.email


            user
        }
    }


    suspend fun signUp(email: String, password: String): Result<UserInfo> = withContext(Dispatchers.Default) {
        runCatching {
            val result = auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }


            val user = auth.currentUserOrNull()
                ?: throw IllegalStateException("No se pudo obtener el usuario tras el registro")


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
