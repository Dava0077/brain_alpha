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
    // Function to be called after successful login to navigate to a screen
    lateinit var navigateTo: (String) -> Unit


    /**
     * Handles Google sign-in result using Compose.
     * On success: waits for session, ensures user exists in DB, updates session, navigates home.
     * On failure: reports error.
     */
    @Composable
    fun googleSignInAction(
        scope: CoroutineScope,
        onSuccess: (UserInfo) -> Unit,
        onError: (Throwable) -> Unit = {}
    ) = SupabaseClientProvider.client.composeAuth.rememberSignInWithGoogle(
        onResult = { result ->
            when (result) {
                // Successful Google sign-in
                is NativeSignInResult.Success -> {
                    scope.launch {
                        // Wait up to 5 seconds for the session to initialize
                        val user = withTimeoutOrNull(5000) {
                            while (SupabaseClientProvider.client.auth.currentUserOrNull() == null) {
                                delay(100)
                            }
                            SupabaseClientProvider.client.auth.currentUserOrNull()
                        }

                        //  Session not ready — treat as failure
                        if (user == null) {
                            onError(Exception("Sesión no disponible tras login con Google."))
                            return@launch
                        }

                        // Extract fallback name from email if username metadata is missing
                        val fallbackName = user.email?.substringBefore('@')?.replaceFirstChar { it.uppercase() } ?: "Usuario"
                        val username = user.userMetadata?.get("username")?.toString() ?: fallbackName

                        try {
                            // Ensure user exists in public.users (upsert)
                            SupabaseClientProvider.client.postgrest["users"].upsert(
                                buildJsonObject {
                                    put("id", user.id)
                                    put("email", user.email ?: "")
                                    put("name", username)
                                }
                            )
                        } catch (_: Exception) {} // Ignore database insert errors silently

                        // Save session data locally
                        UserSession.setSession(
                            id = user.id,
                            emailValue = user.email ?: "desconocido@correo.com",
                            usernameValue = username
                        )

                        // Update LoginController state and navigate home
                        LoginController.setUser(
                            UserInfo(id = user.id, email = user.email ?: "unknown", aud = user.aud ?: "")
                        )
                        LoginController.clear()
                        LoginController.navigateTo("home")
                    }
                }

                //Login failed
                is NativeSignInResult.Error -> onError(result.exception ?: Exception("Error desconocido"))
                //Network failure during sign-in
                is NativeSignInResult.NetworkError -> onError(Exception("Error de red"))
                //User cancelled the login flow
                is NativeSignInResult.ClosedByUser -> {}
            }
        }
    )
}