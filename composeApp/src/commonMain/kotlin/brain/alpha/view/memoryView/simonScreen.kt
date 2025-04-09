package brain.alpha.view.memoryView


import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import brain.alpha.controller.memoryController.simonController
import brainalpha.composeapp.generated.resources.*
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.shadow
import androidx.compose.animation.core.animateFloatAsState
import brain.alpha.strings.Strings

@Composable
fun simonScreen(
    controller: simonController,
    ) {

    val uiState by controller.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()


    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(Res.drawable.fondoMemory),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .background(Color(0xFF222937), shape = RoundedCornerShape(4.dp))
                    .border(BorderStroke(2.dp, Color(0xFF4CE3E8)))
            )
            {

                IconButton(
                    onClick = { /* Dummy action */ },
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 8.dp)
                        .size(55.dp)
                ) {
                    Image(
                        painter = painterResource(Res.drawable.user),
                        contentDescription = "User"
                    )
                }
                Image(
                    painter = org.jetbrains.compose.resources.painterResource(Res.drawable.logo),
                    contentDescription = null,
                    modifier = Modifier
                        .size(120.dp)
                        .align(Alignment.Center)
                        .scale(1.5f)
                )
                IconButton(
                    onClick = { brain.alpha.controller.LoginController.buttonSettings() },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 8.dp)
                        .size(55.dp)
                ) {
                    Image(
                        painter = painterResource(Res.drawable.ajustes),
                        contentDescription = "Settings"
                    )
                }

            }
            Spacer(modifier = Modifier.height(5.dp)) // Espacio tras el logo

            Button(
                onClick = {controller.goHome()},
                modifier = Modifier
                    .align(Alignment.End)
                    .height(46.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF0647D7)),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.elevation(4.dp),
                border = BorderStroke(1.dp, Color(0xFFB0BEC5))
            ){
                Text(Strings.t("back"), fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.White)
            }

            Spacer(modifier = Modifier.height(30.dp)) // Espacio tras el logo

            Text(
                text = Strings.t("title_simon"),
                fontSize = 40.sp,
                color = Color.Black,
                fontFamily = FontFamily.SansSerif,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(10.dp))
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = uiState.message,
                    style = MaterialTheme.typography.h5
                )

                Spacer(modifier = Modifier.height(16.dp))

                val colorAnimado by animateColorAsState(
                    targetValue = colorFromName(uiState.sequence.lastOrNull() ?: ""),
                    label = "colorAnimado"
                )

                Box(
                    modifier = Modifier
                        .width(200.dp)
                        .height(120.dp)
                        .background(colorAnimado)
                )

                Spacer(modifier = Modifier.height(15.dp))

                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        ColorButton( Color.Red, uiState.isUserTurn, ) {
                            controller.onColorPressed("Rojo")
                        }
                        ColorButton( Color.Green, uiState.isUserTurn, ) {
                            controller.onColorPressed("Verde")
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        ColorButton( Color.Blue, uiState.isUserTurn, ) {
                            controller.onColorPressed("Azul")
                        }
                        ColorButton( Color.Yellow, uiState.isUserTurn, ) {
                            controller.onColorPressed("Amarillo")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(onClick = {
                    controller.onStartPressed()

                },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF0647D7)),) {
                    Text(if (uiState.gameOver) Strings.t("reiniciar") else Strings.t("play"), color = Color.White)
                }
                if (uiState.gameOver) {
                    Text(
                        text = "Has conseguido: ${uiState.score} 🍀 Neuronas",
                        fontSize = 18.sp,
                        color = Color.Black
                    )
                }
            }
        }
    }

}

@Composable
fun ColorButton(
    color: Color,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (enabled) 1f else 0.95f,
        label = "buttonScale"
    )

    Box(
        modifier = Modifier
            .size(100.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(16.dp))
            .background(color)
            .border(2.dp, Color.White, RoundedCornerShape(16.dp))
            .shadow(8.dp, RoundedCornerShape(16.dp), clip = false)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) { }
}

fun colorFromName(name: String): Color = when (name) {
    "Rojo" -> Color.Red
    "Verde" -> Color.Green
    "Azul" -> Color.Blue
    "Amarillo" -> Color.Yellow
    else -> Color.Transparent
}





