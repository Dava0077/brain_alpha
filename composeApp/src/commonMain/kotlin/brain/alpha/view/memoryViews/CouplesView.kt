package brain.alpha.view.memoryViews

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import brain.alpha.controller.LoginController
import brain.alpha.controller.memoryControllers.CouplesController
import brainalpha.composeapp.generated.resources.Res
import brainalpha.composeapp.generated.resources.fondoMain
import brainalpha.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun CouplesView(controller: LoginController) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        // Fondo general
        Image(
            painter = painterResource(Res.drawable.memoryFondo),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )


        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .background(Color(0xFF222937))
                    .border(BorderStroke(2.dp, Color(0xFF4CE3E8)))


            ) {
                IconButton(
                    onClick = { controller.buttonUsername() },
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 8.dp)
                        .size(55.dp)
                        .background(Color(0xFF222937))
                ) {
                    Image(
                        painter = painterResource(Res.drawable.user),
                        contentDescription = "User"
                    )
                }


                IconButton(
                    onClick = { controller.buttonSettings() },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 8.dp)
                        .size(55.dp)
                        .background(Color(0xFF222937))
                ) {
                    Image(
                        painter = painterResource(Res.drawable.ajustes),
                        contentDescription = "Settings"
                    )
                }
            }
        }
    }
}

@Composable
fun Hello() {
    Text("Hello")
}

@Preview
@Composable
fun HelloPreview() {
    Hello()
}
