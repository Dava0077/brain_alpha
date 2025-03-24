package brain.alpha.controller

class LoginController {
    fun onLoginClick(username: String, password: String) {
        // Aquí podrías validar o llamar al modelo
        println("Login with $username / $password")
    }

    fun onSignUpClick() {
        println("Navigating to Sign Up")
    }
}