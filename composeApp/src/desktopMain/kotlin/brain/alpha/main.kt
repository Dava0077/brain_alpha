package brain.alpha

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import brain.alpha.controller.LoginController
import brain.alpha.view.LoginScreen

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "BrainAlpha",
    ) {
        val controller = LoginController()
        LoginScreen(controller)
    }
}