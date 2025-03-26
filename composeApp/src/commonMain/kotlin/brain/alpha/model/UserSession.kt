package brain.alpha.model

import androidx.compose.runtime.mutableStateOf

object UserSession {
    val isLoggedIn = mutableStateOf(false)
    val isGuest = mutableStateOf(false)
    val userId = mutableStateOf<String?>(null)
    val email = mutableStateOf<String?>(null)
    val username = mutableStateOf<String?>(null)

    fun clear() {
        isLoggedIn.value = false
        isGuest.value = false
        userId.value = null
        email.value = null
        username.value = null
    }
}