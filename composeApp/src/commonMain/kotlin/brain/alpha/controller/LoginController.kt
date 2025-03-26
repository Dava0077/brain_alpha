package brain.alpha.controller


import androidx.compose.runtime.*
import brain.alpha.controller.AuthController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


object LoginController {


    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var errorMessage by mutableStateOf<String?>(null)
    var isLoading by mutableStateOf(false)


    lateinit var navigateTo: (String) -> Unit


    fun onLoginClicked() {
        isLoading = true
        errorMessage = null


        CoroutineScope(Dispatchers.Default).launch {
            val result = AuthController.signIn(email.trim(), password.trim())
            isLoading = false
            result.onSuccess {
                navigateTo("home")
            }.onFailure { error ->
                errorMessage = error.message ?: "Login failed"
            }
        }
    }


    fun onSignUpClick() {
        navigateTo("signup")
    }


    fun clear() {
        email = ""
        password = ""
        errorMessage = null
        isLoading = false
    }

    fun buttonSettings(){

    }

    fun buttonUsername(){

    }
}
