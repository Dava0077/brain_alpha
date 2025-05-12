package brain.alpha.view.memoryView


import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import brain.alpha.controller.GameSessionController
import brain.alpha.controller.concentrationController.stroopController
import brain.alpha.model.UserSession
import brain.alpha.model.shared.Difficulty
import brain.alpha.services.SupabaseClientProvider
import brain.alpha.strings.Strings
import kotlinx.coroutines.launch

@Composable
fun simonScreen(
    controller: simonController,
    ) {
    var sessionSaved by remember { mutableStateOf(false) }
    val uiState by controller.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val showInstructions by controller.showInstructions
    val displayName = UserSession.getDisplayName() // Show guest or username
    val userId = runCatching { UserSession.getUserId() }.getOrNull()
    val totalScore = remember { mutableStateOf(0) }
    var selectedDifficulty by remember { mutableStateOf(Difficulty.EASY) }


    LaunchedEffect(userId) {
        if (userId != null) {
            val total = GameSessionController.getTotalScore(
                userId = userId,
                gameId = "2927d18f-b1ab-4376-990e-1275e5ba181e",
                supabase = SupabaseClientProvider.client
            )
            println("🧠 Score obtenido del servidor: $total")
            totalScore.value = total
        }
    }

    LaunchedEffect(totalScore.value) {
        if (!UserSession.isGuest.value) {
            selectedDifficulty = when (totalScore.value) {
                in 0..49 -> Difficulty.EASY
                in 50..149 -> Difficulty.MEDIUM
                else -> Difficulty.HARD
            }
            controller.setDifficulty(selectedDifficulty)
            println("🎯 Dificultad autoasignada para usuario: $selectedDifficulty")
        }
    }



    // Show instructions first
    if (showInstructions) {
        InstructionsSimon(
            onStart = { controller.beginGame() },
            controller = simonController
        )
        return
    }


    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(Res.drawable.fondoMemory),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
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
                Text(
                    text = "$displayName",
                    color = Color.White,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 72.dp)
                )

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
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF0647D7)),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.elevation(4.dp),
                    border = BorderStroke(1.dp, Color(0xFFB0BEC5))
                ){
                    Text(Strings.t("instructions"), fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.White)
                }
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .height(46.dp)
                        .background(Color(0xFF0647D7), shape = RoundedCornerShape(12.dp))
                        .border(BorderStroke(1.dp, Color(0xFFB0BEC5)), shape = RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "${totalScore.value} " + Strings.t("neurons"),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
                Button(
                    onClick = {controller.goHome()},
                    modifier = Modifier
                        .height(46.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF0647D7)),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.elevation(4.dp),
                    border = BorderStroke(1.dp, Color(0xFFB0BEC5))
                ){
                    Text(Strings.t("back"), fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.White)
                }
            }

            if (UserSession.isGuest.value) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 16.dp)
                ) {
                    Difficulty.values().forEach { level ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = selectedDifficulty == level,
                                onClick = {
                                    selectedDifficulty = level
                                    controller.setDifficulty(level)
                                },
                                enabled = !controller.gameInProgress.value
                            )
                            Text(text = Strings.translateDifficulty(level))
                        }
                    }
                }
            }


            Text(
                text = Strings.t("difficulty") + ": " + Strings.translateDifficulty(selectedDifficulty),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )

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
                    sessionSaved = false
                },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF0647D7)),) {
                    Text(if (uiState.gameOver) Strings.t("restart") else Strings.t("play"), color = Color.White)
                }
                if (uiState.gameOver) {
                    Text(
                        text = Strings.t("you_won")+ "${uiState.score} "+Strings.t("neurons"),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF2E7D32)
                    )
                    val scope = rememberCoroutineScope()
                    if(uiState.gameOver && !sessionSaved){
                        scope.launch {
                            val userId = runCatching { UserSession.getUserId() }.getOrNull()
                            if (userId != null) {
                                GameSessionController.registerGameSession(
                                    gameId = "2927d18f-b1ab-4376-990e-1275e5ba181e",
                                    score = uiState.score,
                                    difficulty = selectedDifficulty.name,
                                ).onSuccess {
                                    sessionSaved = false
                                    println("✅ Partida guardada correctamente: Score ${uiState.score} | Dificultad $selectedDifficulty")

                                }.onFailure {
                                    println("❌ Error guardando partida: ${it.message}")
                                }
                            } else {
                                println("⚠ Usuario no logueado, no se puede guardar la partida")
                            }
                        }
                    }
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
@Composable
fun InstructionsSimon(onStart: () -> Unit, controller: simonController) {
    val displayName = UserSession.getDisplayName() // Show guest or username

    Box(modifier = Modifier.fillMaxSize()) {

        // Background image
        Image(
            painter = painterResource(Res.drawable.fondoMemory),
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
                Text(
                    text = "$displayName",
                    color = Color.White,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 72.dp)
                )
            }

            Spacer(modifier = Modifier.height(5.dp))
            // Back Button (same design as in game)

            Spacer(modifier = Modifier.height(100.dp))

            // Title of instruction section
            Text(
                Strings.t("instructions"),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121)
            )

            Spacer(modifier = Modifier.height(30.dp))

            Box(
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .background(Color(0xFF0647D7), shape = RoundedCornerShape(16.dp))
                    .padding(16.dp) // Padding interno
            ) {
                Text(
                    buildAnnotatedString {
                        append(Strings.t("insS1")+ "\n\n")

                        append(Strings.t("insS2"))
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(Strings.t("insS3")) }
                        append(Strings.t("insS4")+ "\n\n")
                        append(Strings.t("insS5"))

                    },
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Start game button
            Button(
                onClick = onStart,
                modifier = Modifier
                    .height(48.dp)
                    .width(200.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF0647D7)),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.elevation(4.dp),
                border = BorderStroke(1.dp, Color(0xFFB0BEC5))
            ) {
                Text(Strings.t("start_game"), fontWeight = FontWeight.Medium, color = Color.White)
            }
        }
    }
}





