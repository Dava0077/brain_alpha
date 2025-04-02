package brain.alpha.view.logicView

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import brain.alpha.controller.logicController.sudokuController
import brain.alpha.controller.logicController.wordSearchController
import brainalpha.composeapp.generated.resources.*
import brainalpha.composeapp.generated.resources.Res
import brainalpha.composeapp.generated.resources.ajustes
import brainalpha.composeapp.generated.resources.fondoLogic
import brainalpha.composeapp.generated.resources.logo
import brainalpha.composeapp.generated.resources.user
import org.jetbrains.compose.resources.painterResource
import kotlin.math.abs

@Composable
fun wordSearchScreen(controller: wordSearchController, onNavigateToHome: () -> Unit){
    val grid = controller.getGrid()
    val words = controller.getWordsToFind()
    val selectedCells = remember { mutableStateListOf<Pair<Int, Int>>() }
    var selectionDirection by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var gameStarted by remember { mutableStateOf(false) }
    val wordsToFind = controller.getWordsToFind()





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
                onClick = { controller.goHomeLogic() },
                modifier = Modifier.align(Alignment.End),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
            ) {
                Text("BACK", color = Color.Black)
            }

            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = "WORD SEARCH",
                fontSize = 40.sp,
                color = Color.Black,
                fontFamily = FontFamily.SansSerif,
                textAlign = TextAlign.Center
            )

            if (!gameStarted) {
                Button(onClick = {
                    controller.resetSelection()
                    selectedCells.clear()
                    gameStarted = true
                }) {
                    Text("PLAY")
                }
            }

            if (gameStarted) {
                Column {
                    for (i in 0 until grid.size) {
                        Row {
                            for (j in 0 until grid[i].size) {
                                val isSelected = selectedCells.contains(Pair(i, j))
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(
                                            if (isSelected) Color.Yellow else Color.White,
                                            shape = RoundedCornerShape(4.dp)
                                        )
                                        .border(1.dp, Color.Black)
                                        .clickable {
                                            val pos = Pair(i, j)
                                            if (selectedCells.isEmpty()) {
                                                selectedCells.add(pos)
                                            } else {
                                                val (prevRow, prevCol) = selectedCells.last()
                                                val dx = i - prevRow
                                                val dy = j - prevCol

                                                if (selectionDirection == null && abs(dx) <= 1 && abs(dy) <= 1 && (dx != 0 || dy != 0)) {
                                                    selectionDirection = Pair(dx, dy)
                                                    selectedCells.add(pos)
                                                } else if (selectionDirection != null) {
                                                    val expected = Pair(
                                                        selectedCells.first().first + selectionDirection!!.first * selectedCells.size,
                                                        selectedCells.first().second + selectionDirection!!.second * selectedCells.size
                                                    )
                                                    if (pos == expected) selectedCells.add(pos)
                                                }
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = grid[i][j].toString(),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                val foundWords = remember { mutableStateListOf<String>() }

                Spacer(modifier = Modifier.height(20.dp))

                Text("Palabras a buscar:", fontSize = 20.sp, color = Color.Black)
                controller.getWordsToFind().forEach { word ->
                    val isFound = word in foundWords
                    Text(
                        text = word,
                        fontSize = 16.sp,
                        color = if (isFound) Color.Gray else Color.Black,
                        textDecoration = if (isFound) TextDecoration.LineThrough else TextDecoration.None
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(onClick = {
                        controller.resetSelection()
                        selectedCells.clear()
                    },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)
                    ) {
                        Text("RESET", color = Color.Black)
                    }

                    Button(
                        onClick = {
                            val word = selectedCells.map { (r, c) -> grid[r][c] }.joinToString("")
                            if (controller.getWordsToFind().contains(word) && word !in foundWords) {
                                println("✅ Palabra correcta: $word")
                                foundWords.add(word)
                            } else {
                                println("❌ Palabra incorrecta: $word")
                            }
                            selectedCells.clear()
                            selectionDirection = null
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)
                    ) {
                        Text("VALIDAR", color = Color.Black)
                    }
                }
            }

        }
    }
}