package brain.alpha.view.memoryView


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
import org.jetbrains.compose.resources.painterResource
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import androidx.compose.foundation.clickable
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import brain.alpha.controller.memoryController.couplesController
import brainalpha.composeapp.generated.resources.*



@Composable
fun CouplesScreen(
    controller: couplesController,
    onNavigateToHome: () -> Unit) {

    val revealed = remember { mutableStateListOf<Boolean>().apply { addAll(controller.getRevealedStates()) } }
    val matched = remember { mutableStateListOf<Boolean>().apply { addAll(controller.getMatchedStates()) } }
    var selectedIndices by remember { mutableStateOf(controller.getSelectedIndices()) }
    var gameStarted by remember { mutableStateOf(controller.hasStarted()) }
    var gameFinished by remember { mutableStateOf(false) }
    var remainingTime by remember { mutableStateOf(60) }
    var gameLost by remember { mutableStateOf(false) }

    val shuffledCards = List(16) { index -> controller.getCardValue(index) }
    var timerTrigger by remember { mutableStateOf(0) }



    LaunchedEffect(selectedIndices) {
        if (selectedIndices.size == 2) {
            delay(1000)
            controller.processTurnEnd()
            revealed.clear(); revealed.addAll(controller.getRevealedStates())
            matched.clear(); matched.addAll(controller.getMatchedStates())
            selectedIndices = controller.getSelectedIndices()
            if (controller.isGameFinished()) gameFinished = true
        }
    }
    LaunchedEffect(timerTrigger) {
        if (gameStarted) {
            remainingTime = 60
            gameLost = false
            while (remainingTime > 0 && !gameFinished) {
                delay(1000)
                remainingTime--
            }
            if (!gameFinished && remainingTime == 0) {
                gameLost = true
                gameStarted = false
            }
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
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

                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF0647D7)),
                ){
                Text("BACK", color = Color.White)
            }

            Spacer(modifier = Modifier.height(30.dp)) // Espacio tras el logo

            Text(
                text = "COUPLES GAMES",
                fontSize = 40.sp,
                color = Color.Black,
                fontFamily = FontFamily.SansSerif,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(30.dp))


            Text(
                text = "Time: $remainingTime",
                fontSize = 20.sp,
                color = Color.Black,
                fontFamily = FontFamily.SansSerif,
                textAlign = TextAlign.Center

            )
            Spacer(modifier = Modifier.height(10.dp))

            for (row in 0 until 4) {
                Row {
                    for (col in 0 until 4) {
                        val index = row * 4 + col
                        val isVisible = revealed.getOrNull(index) == true || matched.getOrNull(index) == true

                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .size(64.dp)
                                .background(if (isVisible) Color(0xFF5D3FD3) else Color.Cyan, shape = RoundedCornerShape(8.dp))
                                .clickable(enabled = gameStarted && !isVisible && selectedIndices.size < 2) {
                                    if (controller.selectCard(index)) {
                                        revealed.clear(); revealed.addAll(controller.getRevealedStates())
                                        matched.clear(); matched.addAll(controller.getMatchedStates())
                                        selectedIndices = controller.getSelectedIndices()
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isVisible && index < shuffledCards.size) {
                                Text(
                                    text = shuffledCards[index],
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            if (!gameStarted || gameFinished || gameLost) {
                Button(onClick = {
                    controller.startGame()
                    revealed.clear(); revealed.addAll(controller.getRevealedStates())
                    matched.clear(); matched.addAll(controller.getMatchedStates())
                    selectedIndices = controller.getSelectedIndices()
                    gameStarted = true
                    gameFinished = false
                    gameLost = false
                    remainingTime = 60
                    timerTrigger++ // fuerza reinicio del temporizador
                },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF0647D7)),) {
                    Text("PLAY", color = Color.White)
                }
            }

            if (gameFinished) {
                Spacer(modifier = Modifier.height(16.dp))
                Text("¡Has ganado!", fontSize = 20.sp)
            } else if (gameLost) {
                Spacer(modifier = Modifier.height(16.dp))
                Text("¡Has perdido!", fontSize = 20.sp, color = Color.Red)
            }
        }



    }
}





