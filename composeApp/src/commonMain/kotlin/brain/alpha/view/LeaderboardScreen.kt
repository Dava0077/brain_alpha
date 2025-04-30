package brain.alpha.view

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import brain.alpha.controller.LeaderboardController
import brain.alpha.model.UserSession
import brain.alpha.services.GameSession
import brain.alpha.services.GameSessionService
import brain.alpha.services.GameSessionWithUser
import brain.alpha.services.SupabaseClientProvider
import brain.alpha.strings.Strings
import brainalpha.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource

@Composable
fun leaderboardScreen(controller: LeaderboardController) {
    val displayName = UserSession.getDisplayName()

    val categories = listOf(
        "Memoria" to listOf("Juego de parejas", "Simón dice"),
        "Lógica" to listOf("Sudoku", "Sopa de letras"),
        "Concentración" to listOf("Stroop test", "Adivinar emojis"),
        "Agilidad Mental" to listOf("Mates", "Click number")
    )

    var expandedCategory by remember { mutableStateOf<String?>(null) }
    var expandedGame by remember { mutableStateOf<String?>(null) }
    val leaderboard = remember { mutableStateListOf<GameSessionWithUser>() } // ✅

    LaunchedEffect(Unit) {
        val results = GameSessionService.showPairGameResult(
            supabase = SupabaseClientProvider.client,
            gameId = "24bc262b-ab64-4c71-9c45-8d4fa04a549e"
        )
        println("Results: $results")
        leaderboard.clear()
        leaderboard.addAll(results)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
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
                    onClick = { },
                    modifier = Modifier.align(Alignment.CenterStart).padding(start = 8.dp).size(55.dp)
                ) {
                    Image(painter = painterResource(Res.drawable.user), contentDescription = "User")
                }
                Image(
                    painter = painterResource(Res.drawable.logo),
                    contentDescription = null,
                    modifier = Modifier.size(120.dp).align(Alignment.Center).scale(1.5f)
                )
                IconButton(
                    onClick = { brain.alpha.controller.LoginController.buttonSettings() },
                    modifier = Modifier.align(Alignment.CenterEnd).padding(end = 8.dp).size(55.dp)
                ) {
                    Image(painter = painterResource(Res.drawable.ajustes), contentDescription = "Settings")
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

            Button(
                onClick = { controller.goHome() },
                modifier = Modifier
                    .align(Alignment.End)
                    .height(46.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF303030)),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.elevation(4.dp),
                border = BorderStroke(1.dp, Color(0xFFB0BEC5))
            ) {
                Text(Strings.t("back"), fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.White)
            }

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = Strings.t("leaderboard"),
                fontSize = 40.sp,
                color = Color.Black,
                fontFamily = FontFamily.SansSerif,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            categories.forEach { (categoryName, games) ->
                ExpandableCategory(
                    title = categoryName,
                    isExpanded = expandedCategory == categoryName,
                    onExpandToggle = {
                        expandedCategory = if (expandedCategory == categoryName) null else categoryName
                        expandedGame = null
                    }
                ) {
                    games.forEach { game ->
                        val gameId = when (game) {
                            "Juego de Parejas" -> "24bc262b-ab64-4c71-9c45-8d4fa04a549e"
                            else -> "" // Placeholder para otros juegos
                        }
                        ExpandableGame(
                            title = game,
                            isExpanded = expandedGame == game,
                            onExpandToggle = {
                                expandedGame = if (expandedGame == game) null else game
                            },
                            gameId = gameId
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExpandableCategory(
    title: String,
    isExpanded: Boolean,
    onExpandToggle: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
            .clickable { onExpandToggle() }
            .padding(12.dp)
    ) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        if (isExpanded) {
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}
@Composable
fun ExpandableGame(
    title: String,
    isExpanded: Boolean,
    onExpandToggle: () -> Unit,
    gameId: String
) {
    // Usamos remember y mutableStateListOf para mantener el estado del leaderboard entre recomposiciones
    val leaderboard = remember { mutableStateListOf<GameSessionWithUser>() }

    LaunchedEffect(isExpanded) {
        if (isExpanded && gameId.isNotBlank()) {
            val results = GameSessionService.showPairGameResult(
                supabase = SupabaseClientProvider.client,
                gameId = "24bc262b-ab64-4c71-9c45-8d4fa04a549e"
            )
            println("Results: $results")  // Verifica los resultados antes de agregarlos
            leaderboard.clear()
            leaderboard.addAll(results)
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(title, fontWeight = FontWeight.Bold)
        Button(onClick = { onExpandToggle() }) {
            Text(if (isExpanded) "Cerrar" else "Expandir")
        }

        if (isExpanded) {
            // Mostrar los resultados del leaderboard
            LazyColumn {
                items(leaderboard) { entry ->
                    Text(entry.name)
                    Text(entry.score.toString())
                    Text(entry.difficulty)
                      // Si usas 'email' en lugar de 'name', cámbialo aquí
                }
            }
        }
    }
}


