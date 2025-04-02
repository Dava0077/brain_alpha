package brain.alpha.view.concentrationViews

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import brain.alpha.controller.concentrationController.stroopController
import brainalpha.composeapp.generated.resources.*
import brainalpha.composeapp.generated.resources.Res
import brainalpha.composeapp.generated.resources.logo
import brainalpha.composeapp.generated.resources.memoryFondo
import brainalpha.composeapp.generated.resources.user
import org.jetbrains.compose.resources.painterResource

/**
 * StroopScreen.kt
 *
 * Main UI for the Stroop Test. Displays game or instructions.
 */

@Composable
fun StroopScreen(controller: stroopController = stroopController,onNavigateToHome: () -> Unit,) {

    val timeLeft by controller.timeLeft
    val colorWord by controller.currentColorWord
    val displayColor by controller.colorToShow
    val score by controller.score
    val isGameOver by controller.showResult
    val options = controller.options
    val showInstructions by controller.showInstructions

    if (showInstructions) {
        InstructionsScreen(onStart = { controller.beginGame() })
        return
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .background(Color(0xFF222937), shape = RoundedCornerShape(4.dp))
                    .border(BorderStroke(2.dp, Color(0xFF4CE3E8))))
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
                onClick = {controller.goHome()},
                modifier = Modifier.align(Alignment.End),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF0647D7)),
            ){
                Text("BACK", color = Color.White)
            }

            Spacer(modifier = Modifier.height(30.dp))


            if (isGameOver) {
                AlertDialog(
                    onDismissRequest = {},
                    title = { Text("Fin del juego") },
                    text = { Text("¡Obtuviste $score puntos!") },
                    confirmButton = {
                        TextButton(onClick = { controller.beginGame() }) {
                            Text("Volver a jugar")
                        }
                    }
                )
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Text("Stroop Test", fontSize = 24.sp, fontWeight = FontWeight.Bold)

                Text("Tiempo restante: $timeLeft s", fontSize = 16.sp)

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = colorWord.uppercase(),
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = displayColor
                )

                Spacer(modifier = Modifier.height(32.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    options.forEach { colorOption ->
                        val colorName = controller.getColorName(colorOption)

                        Button(
                            onClick = { controller.checkAnswer(colorOption) },
                            modifier = Modifier
                                .width(200.dp)
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = colorOption,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = colorName.uppercase(),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text("Puntuación: $score", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            }

        }

    }
}

@Composable
fun InstructionsScreen(onStart: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Instrucciones", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Debes seleccionar el color en que está escrita la palabra, no el nombre que dice. Tienes 30 segundos para acertar el mayor número posible.",
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onStart) {
            Text("Empezar juego")
        }
    }
}
