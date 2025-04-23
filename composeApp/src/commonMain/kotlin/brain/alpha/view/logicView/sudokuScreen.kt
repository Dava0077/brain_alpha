package brain.alpha.view.logicView

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import brain.alpha.controller.logicController.sudokuController
import brain.alpha.strings.Strings
import brainalpha.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import brain.alpha.controller.concentrationController.stroopController
import brain.alpha.model.logicModel.sudokuModel
import brain.alpha.model.shared.Difficulty
import brain.alpha.view.memoryView.InstructionsCouples


@Composable
fun sudokuScreen(controller: sudokuController,onNavigateToHome: () -> Unit) {
    val gameStarted by remember { derivedStateOf { !controller.showInstructions.value } }
    val model = sudokuModel

    var selectedRow by remember { mutableStateOf(-1) }
    var selectedCol by remember { mutableStateOf(-1) }
    var gridVersion by remember { mutableStateOf(0) }


    val showInstructions by controller.showInstructions


    // Show instructions first
    if (showInstructions) {
        InstructionsSudoku(
            onStart = { controller.beginGame() },
            controller = sudokuController
        )
        return
    }

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
                    onClick = { brain.alpha.controller.LoginController.buttonSettings() },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 8.dp)
                        .size(55.dp)
                ) {
                    Image(painter = painterResource(Res.drawable.ajustes), contentDescription = "Settings")
                }
            }
            Spacer(modifier = Modifier.height(5.dp)) // Espacio tras el logo

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween

            ){
                Button(
                    onClick = {controller.toggleInstructions(true)},
                    modifier = Modifier
                        .height(46.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.elevation(4.dp),
                    border = BorderStroke(1.dp, Color(0xFFB0BEC5))
                ){
                    Text(Strings.t("instructions"), fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.Black)
                }
                Button(
                    onClick = { controller.goHomeLogic() },
                    modifier = Modifier
                        .height(46.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.elevation(4.dp),
                    border = BorderStroke(1.dp, Color(0xFFB0BEC5))
                ) {
                    Text(Strings.t("back"), fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.Black)
                }
            }

            val selectedDifficulty = remember { mutableStateOf(controller.getDifficulty()) }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                Difficulty.values().forEach { level ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedDifficulty.value == level,
                            onClick = {
                                selectedDifficulty.value = level
                                controller.setDifficulty(level)
                            },
                            enabled = !controller.hasStarted() // deshabilitar si ya empezó
                        )
                        Text(text = Strings.translateDifficulty(level)) // si tienes traducción
                    }
                }
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
                Button(
                    onClick = {
                        controller.startGame()
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                ) {
                    Text(Strings.t("play"), color = Color.Black)
                }
            } else {
                // Sudoku Grid
                key(gridVersion){
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
                                            .clickable(enabled = controller.hasStarted() && isInitial) {
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
                }

                if (controller.hasStarted() && selectedRow in 0..8 && selectedCol in 0..8) {
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
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                            ) {
                                Text("$num", color = Color.Black)
                            }
                        }

                        Button(
                            onClick = {
                                controller.setCellValue(selectedRow, selectedCol, 0)
                                selectedRow = -1
                                selectedCol = -1
                            },
                            modifier = Modifier.size(40.dp),
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                        ) {
                            Text("🗑", color = Color.Black)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        controller.startGame()
                        selectedRow = -1
                        selectedCol = -1
                        gridVersion++
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)
                ) {
                    Text(if (model.started) Strings.t("restart") else Strings.t("play"))

                }
                if (controller.isBoardComplete()) {
                    Spacer(modifier = Modifier.height(16.dp))

                    val isValid = controller.isBoardValid()
                    val message = if (isValid) Strings.t("correct_sudoku") else Strings.t("incorrect_sudoku")
                    val color = if (isValid) Color(0xFF4CAF50) else Color(0xFFFF5252)

                    Text(
                        text = message,
                        fontSize = 20.sp,
                        color = color
                    )

                    if (isValid) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                controller.startGame()
                                selectedRow = -1
                                selectedCol = -1
                            },
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF0647D7)),
                        ) {
                            Text(Strings.t("play_again"), color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun InstructionsSudoku(onStart: () -> Unit, controller: sudokuController) {
    Box(modifier = Modifier.fillMaxSize()) {

        // Background image
        Image(
            painter = painterResource(Res.drawable.fondoLogic),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Same HEADER as game screen (user icon, logo, settings)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .background(Color(0xFF222937), shape = RoundedCornerShape(4.dp))
                    .border(BorderStroke(2.dp, Color(0xFF4CE3E8))))
            {
                IconButton(
                    onClick = { /* Dummy user action */ },
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 8.dp)
                        .size(48.dp)
                ) {
                    Image(
                        painter = painterResource(Res.drawable.user),
                        contentDescription = "Usuario"
                    )
                }

                Image(
                    painter = painterResource(Res.drawable.logo),
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .align(Alignment.Center)
                        .scale(1.2f)
                )

                IconButton(
                    onClick = { brain.alpha.controller.LoginController.buttonSettings() },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 8.dp)
                        .size(48.dp)
                ) {
                    Image(
                        painter = painterResource(Res.drawable.ajustes),
                        contentDescription = "Ajustes"
                    )
                }
            }

            Spacer(modifier = Modifier.height(5.dp))
            // Back Button (same design as in game)
            Button(
                onClick = {controller.goHomeLogic()},
                modifier = Modifier
                    .align(Alignment.End)
                    .height(46.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.elevation(4.dp),
                border = BorderStroke(1.dp, Color(0xFFB0BEC5))
            ){
                Text(Strings.t("back"), fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.Black)
            }

            Spacer(modifier = Modifier.height(100.dp))

            // Title of instruction section
            Text(
                Strings.t("instructions"),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121)
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Game explanation using styled text
            Box(
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .background(Color.White, shape = RoundedCornerShape(16.dp))
                    .padding(16.dp) // Espaciado interno del fondo
            ) {
                Text(
                    buildAnnotatedString {
                        append(Strings.t("insK1"))
                        append(Strings.t("insK2"))

                        append(Strings.t("insK3"))
                        append(Strings.t("insK4"))
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(Strings.t("insK5"))
                        }

                        append(Strings.t("insK6")+"\n\n")
                        append(Strings.t("insK7"))
                    },
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF424242),
                    lineHeight = 24.sp
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Start game button
            Button(
                onClick = { controller.goToGameScreen() },
                modifier = Modifier
                    .height(48.dp)
                    .width(200.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.elevation(4.dp),
                border = BorderStroke(1.dp, Color(0xFFB0BEC5))
            ) {
                Text(Strings.t("start_game"), fontWeight = FontWeight.Medium, color = Color.Black)
            }
        }
    }
}