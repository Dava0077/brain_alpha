package brain.alpha.view.logicView

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import brain.alpha.controller.logicController.sudokuController
import brainalpha.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource

@Composable
fun sudokuScreen(controller: sudokuController,onNavigateToHome: () -> Unit) {
    var gameStarted by remember { mutableStateOf(false) }
    var selectedRow by remember { mutableStateOf(-1) }
    var selectedCol by remember { mutableStateOf(-1) }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(Res.drawable.fondoLogic),
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
            ) {
                IconButton(
                    onClick = { /* Dummy action */ },
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 8.dp)
                        .size(55.dp)
                ) {
                    Image(painter = painterResource(Res.drawable.user), contentDescription = "User")
                }
                Image(
                    painter = painterResource(Res.drawable.logo),
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
                    Image(painter = painterResource(Res.drawable.ajustes), contentDescription = "Settings")
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

            Text(
                text = "SUDOKU",
                fontSize = 40.sp,
                color = Color.Black,
                fontFamily = FontFamily.SansSerif,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(30.dp))

            if (!gameStarted) {
                Button(onClick = {controller.startGame()
                    gameStarted = true
                },colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF0647D7)),
                ) {
                    Text("PLAY", color = Color.White)
                }
            } else {
                // Sudoku Grid
                Column {
                    for (row in 0 until 9) {
                        Row {
                            for (col in 0 until 9) {
                                val value = controller.getCellValue(row, col)
                                val isInitial = controller.isCellInitial(row, col)

                                Box(
                                    modifier = Modifier
                                        .padding(2.dp)
                                        .size(36.dp)
                                        .background(
                                            when {
                                                isInitial -> Color.Gray
                                                selectedRow == row && selectedCol == col -> Color(0xFFB3E5FC) // azul claro para seleccionada
                                                else -> Color.White
                                            },
                                            shape = RoundedCornerShape(4.dp)
                                        )
                                        .border(1.dp, Color.Black)
                                        .clickable(enabled = !isInitial) {
                                            selectedRow = row
                                            selectedCol = col
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (value != 0) value.toString() else "",
                                        fontSize = 16.sp,
                                        color = if (isInitial) Color.White else Color.Black
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {controller.startGame()},
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF0647D7)),) {
                    Text("RESTART", color = Color.White)
                }
                if (selectedRow in 0..8 && selectedCol in 0..8) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        for (num in 1..9) {
                            Button(
                                onClick = {
                                    controller.setCellValue(selectedRow, selectedCol, num)
                                    selectedRow = -1
                                    selectedCol = -1
                                },
                                modifier = Modifier.size(40.dp),
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF0647D7)),
                            ) {
                                Text("$num", color = Color.White)
                            }
                        }

                        Button(
                            onClick = {
                                controller.setCellValue(selectedRow, selectedCol, 0)
                                selectedRow = -1
                                selectedCol = -1
                            },
                            modifier = Modifier.size(40.dp),
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF0647D7)),
                        ) {
                            Text("🗑", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
