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


@Composable
@Preview
fun App() {
    MaterialTheme {
        var showContent by remember { mutableStateOf(false) }

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center){
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally,

                verticalArrangement = Arrangement.Top
            ) {
                Image(
                    painter = painterResource(Res.drawable.logo),
                    contentDescription = null,
                    modifier = Modifier.size(150.dp)
                )
                Text(
                    text = "Bienvenido a nuestra aplicación",
                    style = TextStyle(
                        fontSize = 50.sp,
                        color = androidx.compose.ui.graphics.Color.Blue,
                        fontFamily = FontFamily.Serif
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

        }



    }
}

