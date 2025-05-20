package brain.alpha.model

import androidx.compose.runtime.mutableStateOf
import brain.alpha.services.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import kotlinx.serialization.Serializable


/**
 * UserModel represents the user data structure stored in the 'users' table.
 */
@Serializable
data class UserModel(
    val id: String,
    val name: String,
    val email: String
)

/**
 * UserSession is a singleton responsible for managing the current user's session state.
 *
 * Features:
 * - Holds login status (authenticated or guest)
 * - Exposes reactive values via mutableStateOf for use in Compose UI
 * - Stores user ID, email, and display name
 * - Provides helpers to set, clear, and check session state
 */
object UserSession {
    // Whether a user is logged in
    val isLoggedIn = mutableStateOf(false)

    // Whether the current session is for a guest user
    val isGuest = mutableStateOf(false)

    // Supabase user ID for the current session (nullable)
    val userId = mutableStateOf<String?>(null)

    // Email address of the user (nullable)
    val email = mutableStateOf<String?>(null)

    // Display name or username (nullable)
    val username = mutableStateOf<String?>(null)


    /**
     * Sets session state for a logged-in user (not a guest).
     */
    fun setSession(id: String, emailValue: String, usernameValue: String) {
        isLoggedIn.value = true
        isGuest.value = false
        userId.value = id
        email.value = emailValue
        username.value = usernameValue
    }


    /**
     * Sets session state for a guest user.
     * Uses a default name like "Invitado".
     */
    fun setGuestSession(guestName: String = "Invitado") {
        isLoggedIn.value = true
        isGuest.value = true
        userId.value = null
        email.value = null
        username.value = guestName
    }


    /**
     * Clears all session information (logs the user out).
     */
    fun clear() {
        isLoggedIn.value = false
        isGuest.value = false
        userId.value = null
        email.value = null
        username.value = null
    }

    /**
     * Returns true if user is logged in and not a guest.
     */
    fun isAuthenticated(): Boolean {
        return isLoggedIn.value && !isGuest.value
    }

    /**
    * Returns the username or defaults to "Usuario".
    */
    fun getDisplayName(): String {
        return username.value ?: "Usuario"
    }

    // Internal fallback user ID cache
    private var currentUserId: String? = null

    /**
     * Sets user ID manually (used for caching outside Compose state)
     */
    fun setUserId(id: String) {
        currentUserId = id
    }

    /**
     * Returns the current user ID from local cache or from Supabase client session.
     */
    fun getUserId(): String? {
        return currentUserId
            ?: SupabaseClientProvider.client.auth.currentUserOrNull()?.id
    }
}
