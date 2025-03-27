package brain.alpha.model

import androidx.compose.runtime.mutableStateOf

/**
 * UserSession.kt
 *
 * Singleton object responsible for holding the current user session state.
 * This state is observable via Compose using mutableStateOf.
 *
 * Fields:
 * - isLoggedIn: Indicates whether the user is authenticated.
 * - isGuest: Indicates whether the session belongs to a guest user.
 * - userId: Stores the Supabase user ID.
 * - email: Stores the user's email address.
 * - username: Stores the user's display name or username.
 *
 * Functions:
 * - setSession: Sets the session state for an authenticated user.
 * - setGuestSession: Sets the session state for a guest user.
 * - clear: Clears all session data, effectively logging out the user.
 */


object UserSession {
    val isLoggedIn = mutableStateOf(false)
    val isGuest = mutableStateOf(false)
    val userId = mutableStateOf<String?>(null)
    val email = mutableStateOf<String?>(null)
    val username = mutableStateOf<String?>(null)


    fun setSession(id: String, emailValue: String, usernameValue: String) {
        isLoggedIn.value = true
        isGuest.value = false
        userId.value = id
        email.value = emailValue
        username.value = usernameValue
    }

    fun setGuestSession(guestName: String = "Invitado") {
        isLoggedIn.value = true
        isGuest.value = true
        userId.value = null
        email.value = null
        username.value = guestName
    }


    fun clear() {
        isLoggedIn.value = false
        isGuest.value = false
        userId.value = null
        email.value = null
        username.value = null
    }
}