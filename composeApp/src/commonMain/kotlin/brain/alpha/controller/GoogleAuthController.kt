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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

object GoogleAuthController {
    lateinit var navigateTo: (String) -> Unit

    @Composable
    fun googleSignInAction(
        scope: CoroutineScope,
        onSuccess: (UserInfo) -> Unit,
        onError: (Throwable) -> Unit = {}
    ) = SupabaseClientProvider.client.composeAuth.rememberSignInWithGoogle(
        onResult = { result ->
            when (result) {
                is NativeSignInResult.Success -> {
                    scope.launch {
                        SupabaseClientProvider.client.auth.sessionManager.loadSession()

                        // ✅ Esperar hasta que haya usuario
                        val user = withTimeoutOrNull(5000) {
                            while (SupabaseClientProvider.client.auth.currentUserOrNull() == null) {
                                delay(100)
                            }
                            SupabaseClientProvider.client.auth.currentUserOrNull()
                        }

                        if (user == null) {
                            onError(Exception("Sesión no disponible tras login."))
                            return@launch
                        }

                        UserSession.setSession(
                            id = user.id,
                            emailValue = user.email ?: "desconocido@correo.com",
                            usernameValue = user.userMetadata?.get("username")?.toString()
                                ?: user.email?.substringBefore('@')?.replaceFirstChar { it.uppercase() }
                                ?: "Usuario"
                        )

                        try {
                            SupabaseClientProvider.client.postgrest["users"].upsert(
                                buildJsonObject {
                                    put("id", user.id)
                                    put("email", user.email ?: "")
                                    put("name", user.userMetadata?.get("username")?.toString() ?: "GoogleUser")
                                }
                            )
                        } catch (_: Exception) {}

                        onSuccess(
                            UserInfo(
                                id = user.id,
                                email = user.email ?: "desconocido@correo.com",
                                aud = user.aud ?: "authenticated"
                            )
                        )

                        navigateTo("home")

                    }
                }

                is NativeSignInResult.Error -> onError(result.exception ?: Exception("Error desconocido"))
                is NativeSignInResult.NetworkError -> onError(Exception("Error de red"))
                is NativeSignInResult.ClosedByUser -> {}
            }
        }
    )


}