package brain.alpha

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import brain.alpha.Controller.LoginController
import brain.alpha.View.LoginScreen

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "BrainAlpha",
    ) {
        val controller = LoginController()
        LoginScreen(controller)
    }
}