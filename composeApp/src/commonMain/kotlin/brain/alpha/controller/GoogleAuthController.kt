package brain.alpha.controller

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import brain.alpha.model.UserSession
import brain.alpha.services.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.user.UserInfo
import io.github.jan.supabase.compose.auth.composable.NativeSignInResult
import io.github.jan.supabase.compose.auth.composable.rememberSignInWithGoogle
import io.github.jan.supabase.compose.auth.composeAuth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

object GoogleAuthController {

    @Composable
    fun googleSignInAction(
        scope: CoroutineScope,
        onSuccess: (UserInfo) -> Unit,
        onError: (Throwable) -> Unit = {}
    ) = SupabaseClientProvider.client.composeAuth.rememberSignInWithGoogle(
        onResult = { result ->
            when (result) {
                is NativeSignInResult.Success -> {
                    val user = SupabaseClientProvider.client.auth.currentUserOrNull()
                    if (user != null) {
                        UserSession.setSession(
                            id = user.id,
                            emailValue = user.email ?: "desconocido@correo.com",
                            usernameValue = user.userMetadata?.get("username")?.toString()
                                ?: user.email?.substringBefore('@')?.replaceFirstChar { it.uppercase() }
                                ?: "Usuario"
                        )

                        scope.launch {
                            try {
                                SupabaseClientProvider.client.postgrest["users"].insert(
                                    buildJsonObject {
                                        put("id", user.id)
                                        put("email", user.email ?: "")
                                        put("name", user.userMetadata?.get("username")?.toString() ?: "GoogleUser")
                                    }
                                )
                            } catch (_: Exception) {
                                // Ignorar si ya existe
                            }
                        }

                        // ⬇️ Mueve esto aquí
                        LoginController.clear()
                        LoginController.navigateTo("home")

                        // Callback extra si necesitas
                        onSuccess(user)
                    }
                }
                is NativeSignInResult.Error -> onError(result.exception ?: Exception("Error desconocido"))
                is NativeSignInResult.NetworkError -> onError(Exception("Error de red"))
                is NativeSignInResult.ClosedByUser -> {}
            }
        }
    )
}