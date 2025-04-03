package brain.alpha

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.rememberWindowState
import brain.alpha.controller.LoginController
import brain.alpha.view.LoginScreen

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "BrainAlpha",
        state = rememberWindowState(
            width = 1024.dp,
            height = 960.dp,
            placement = WindowPlacement.Floating
        ),
        resizable = true
    ) {
        App()
    }
}