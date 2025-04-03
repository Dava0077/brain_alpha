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
import androidx.compose.runtime.*
import androidx.compose.ui.draw.scale
import brain.alpha.controller.memoryController.simonController
import brain.alpha.view.memoryView.SimonUiState
import brainalpha.composeapp.generated.resources.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch




@Composable
fun simonScreen(
    controller: simonController,
    onNavigateToHome: () -> Unit) {

    val uiState by controller.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val highlightColor by controller.highlightedColor.collectAsState()


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
                    onClick = { /* Dummy action */ },
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
                onClick = { controller.goHome() },
                modifier = Modifier.align(Alignment.End),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF0647D7)),
            ) {
                Text("ATRAS", color = Color.White)
            }

            Spacer(modifier = Modifier.height(30.dp)) // Espacio tras el logo

            Text(
                text = "SIMON DICE",
                fontSize = 40.sp,
                color = Color.Black,
                fontFamily = FontFamily.SansSerif,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(30.dp))
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

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(colorFromName(highlightColor ?: ""))
                        )

                Spacer(modifier = Modifier.height(32.dp))

                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        ColorButton( Color.Red, uiState.isUserTurn, highlightColor == "Rojo") {
                            controller.onColorPressed("Rojo")
                        }
                        ColorButton( Color.Green, uiState.isUserTurn, highlightColor == "Verde") {
                            controller.onColorPressed("Verde")
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        ColorButton( Color.Blue, uiState.isUserTurn, highlightColor == "Azul") {
                            controller.onColorPressed("Azul")
                        }
                        ColorButton( Color.Yellow, uiState.isUserTurn, highlightColor == "Amarillo") {
                            controller.onColorPressed("Amarillo")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(onClick = {
                    controller.onStartPressed()

                }) {
                    Text("JUGAR")
                }
            }
        }
    }

}

@Composable
fun ColorButton(
    color: Color,
    enabled: Boolean,
    highlighted: Boolean,
    onClick: () -> Unit
) {
    val displayColor = if (highlighted) Color.White else color
    Box(
        modifier = Modifier
            .size(100.dp)
            .background(displayColor)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
    }
}
fun colorFromName(name: String): Color = when (name) {
    "Rojo" -> Color.Red
    "Verde" -> Color.Green
    "Azul" -> Color.Blue
    "Amarillo" -> Color.Yellow
    else -> Color.Transparent
}





