package brain.alpha.view

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import brain.alpha.R // Importa el R de androidMain, no del módulo multiplataforma
import kotlinx.coroutines.delay
import kotlin.random.Random
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.material.ButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import brainalpha.composeapp.generated.resources.Res
import brainalpha.composeapp.generated.resources.logo

@Preview(showBackground = true)
@Composable
fun CouplesScreenPreview() {
    CouplesScreen()
}
@Composable
fun CouplesScreen() {
    val allCards = listOf("P", "Q", "R", "S", "T", "U", "V", "W")
    val shuffledCards = remember { (allCards + allCards).shuffled(Random(System.currentTimeMillis())) }
    var revealed by remember { mutableStateOf(List(16) { false }) }
    var matched by remember { mutableStateOf(List(16) { false }) }
    var selectedIndices by remember { mutableStateOf(emptyList<Int>()) }

    LaunchedEffect(selectedIndices) {
        if (selectedIndices.size == 2) {
            val (first, second) = selectedIndices
            if (shuffledCards[first] == shuffledCards[second]) {
                matched = matched.mapIndexed { index, value ->
                    value || index == first || index == second
                }
            } else {
                delay(1000)
                revealed = revealed.mapIndexed { index, value ->
                    if (index == first || index == second) false else value
                }
            }
            selectedIndices = emptyList()
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.memory_fondo),
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
                    .height(70.dp),

            ) {
                Box(
                    modifier = Modifier
                        .width(200.dp)
                        .height(30.dp)
                        .align(Alignment.TopCenter)
                        .background(Color(0xFF222937), shape = RoundedCornerShape(4.dp))
                        .border(BorderStroke(2.dp, Color(0xFF4CE3E8)))){

                }

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(100.dp)
                        .background(Color(0xFF222937), shape = RoundedCornerShape(0.dp))
                        .border(BorderStroke(2.dp, Color(0xFF4CE3E8)))){
                    IconButton(
                        onClick = { /* Dummy action */ },
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 8.dp)
                            .size(55.dp)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.user),
                            contentDescription = "User"
                        )
                    }
                }



                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .align(Alignment.CenterEnd)
                        .width(100.dp)
                        .background(Color(0xFF222937), shape = RoundedCornerShape(0.dp))
                        .border(BorderStroke(2.dp, Color(0xFF4CE3E8)))){

                    IconButton(
                        onClick = { /* Dummy action */ },
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 8.dp)
                            .size(55.dp)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ajustes),
                            contentDescription = "Settings"
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .width(220.dp)
                        .height(26.dp)
                        .align(Alignment.TopCenter)
                        .offset(y = 2.dp)
                        .background(Color(0xFF222937), shape = RoundedCornerShape(0.dp))
                ){

                }


            }
            Spacer(modifier = Modifier.height(70.dp)) // Espacio tras el logo


            Text(
                text = "COUPLES GAMES",
                fontSize = 40.sp,
                color = Color.Black,
                fontFamily = FontFamily.SansSerif,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(30.dp))

            for (row in 0 until 4) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    for (col in 0 until 4) {
                        val index = row * 4 + col
                        val isVisible = revealed[index] || matched[index]
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .size(64.dp)
                                .background(
                                    color = if (isVisible) MaterialTheme.colors.primary else MaterialTheme.colors.secondary,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable(enabled = !isVisible && selectedIndices.size < 2) {
                                    revealed = revealed.toMutableList().also { it[index] = true }
                                    selectedIndices = selectedIndices + index
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isVisible) {
                                Text(
                                    text = shuffledCards[index],
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colors.onPrimary
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = { /* Dummy action */ },
                modifier = Modifier
                    .width(100.dp)
                    .height(40.dp),
                //colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF0647D7)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("PLAY", color = Color.White)
            }

            if (matched.all { it }) {
                Spacer(modifier = Modifier.height(16.dp))
                Text("¡Has ganado!", style = MaterialTheme.typography.h5)
            }
        }
        Image(
            painter = org.jetbrains.compose.resources.painterResource(Res.drawable.logo),
            contentDescription = null,
            modifier = Modifier
                .size(120.dp)
                .align(Alignment.TopCenter)
                .absoluteOffset(y = 0.dp) // ajusta para que flote como en el diseño
                .scale(1.5f)
        )

    }
}


