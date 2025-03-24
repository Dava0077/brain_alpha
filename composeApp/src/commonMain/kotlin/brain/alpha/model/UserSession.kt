package brain.alpha.model

import androidx.compose.runtime.mutableStateOf

object UserSession {
    val isLoggedIn = mutableStateOf(false)
    val userId = mutableStateOf<String?>(null)
    val email = mutableStateOf<String?>(null)

    fun clear() {
        isLoggedIn.value = false
        userId.value = null
        email.value = null
    }
}