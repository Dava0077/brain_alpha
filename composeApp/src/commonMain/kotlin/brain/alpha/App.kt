package brain.alpha

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode.Companion.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview


import brainalpha.composeapp.generated.resources.Res
import brainalpha.composeapp.generated.resources.logo




import androidx.compose.material.*
import androidx.compose.runtime.*
import brain.alpha.view.LoginScreen
import brain.alpha.controller.LoginController
import brain.alpha.view.HomeScreen
import brain.alpha.view.SignUpScreen


@Composable
fun App() {
    MaterialTheme {
        var currentScreen by remember { mutableStateOf("login") }


        LoginController.navigateTo = { route -> currentScreen = route }


        when (currentScreen) {
            "login" -> LoginScreen(
                controller = LoginController,
                onNavigateToSignUp = { currentScreen = "home" }
            )


            "signup" -> SignUpScreen(
                onSuccess = {currentScreen = "signup"},
                onFailure = {errorMessage -> println("Sign up error: $errorMessage")},
                onGoToLogin = {currentScreen = "login"}
            )


            "home" -> HomeScreen(
                controller = LoginController,
                onNavigateToMemory = { currentScreen = "memory" }
            )
        }
    }
}


