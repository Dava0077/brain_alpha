package brain.alpha.controller

import brain.alpha.model.UserSession
import brain.alpha.services.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object GoogleAuthController {

    /**
     * Attempts to log in with Google using ComposeAuth.
     * On success, sets the user session.
     */
    suspend fun loginWithGoogle(): Result<UserInfo> = withContext(Dispatchers.Default) {
        runCatching {
            SupabaseClientProvider.client.auth.signInWith(Google)

            val user = SupabaseClientProvider.client.auth.currentUserOrNull()
                ?: throw IllegalStateException("No se pudo obtener el usuario luego del login con Google")

            UserSession.setSession(
                id = user.id,
                emailValue = user.email ?: "desconocido@correo.com",
                usernameValue = user.userMetadata?.get("username")?.toString() ?: "Usuario"
            )

            user
        }
    }
}
