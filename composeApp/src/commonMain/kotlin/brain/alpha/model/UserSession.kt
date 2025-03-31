package brain.alpha.model

import androidx.compose.runtime.mutableStateOf


/**
 * UserSession.kt
 *
 * This singleton object manages the global session state of the current user.
 * It provides observable state values using Compose's `mutableStateOf`,
 * which makes it reactive and automatically updates the UI when the session changes.
 *
 * Responsibilities:
 * - Track login status, guest mode, user ID, email, and username.
 * - Provide helpers to set/reset session data.
 * - Offer derived states like authentication status and display name.
 */


object UserSession {
    // Indicates whether a user is logged in
    val isLoggedIn = mutableStateOf(false)

    // Indicates whether the session is for a guest user
    val isGuest = mutableStateOf(false)

    // Stores Supabase user ID
    val userId = mutableStateOf<String?>(null)

    // Stores user's email
    val email = mutableStateOf<String?>(null)

    // Stores user's display name or username
    val username = mutableStateOf<String?>(null)


    /**
     * Sets the session state for an authenticated user.
     * @param id Supabase user ID
     * @param emailValue User's email
     * @param usernameValue User's display name
     */
    fun setSession(id: String, emailValue: String, usernameValue: String) {
        isLoggedIn.value = true
        isGuest.value = false
        userId.value = id
        email.value = emailValue
        username.value = usernameValue
    }


    /**
     * Sets the session state for a guest user.
     * @param guestName Optional guest name (default: "Invitado")
     */
    fun setGuestSession(guestName: String = "Invitado") {
        isLoggedIn.value = true
        isGuest.value = true
        userId.value = null
        email.value = null
        username.value = guestName
    }


    /**
     * Clears all session data.
     * Effectively logs the user out.
     */
    fun clear() {
        isLoggedIn.value = false
        isGuest.value = false
        userId.value = null
        email.value = null
        username.value = null
    }

    /**
     * Checks whether the user is logged in and is not a guest.
     * @return true if authenticated session
     */
    fun isAuthenticated(): Boolean {
        return isLoggedIn.value && !isGuest.value
    }

    /**
     * Returns a friendly display name for the current session.
     * Defaults to "Usuario" if username is null.
     */
    fun getDisplayName(): String {
        return username.value ?: "Usuario"
    }
}
