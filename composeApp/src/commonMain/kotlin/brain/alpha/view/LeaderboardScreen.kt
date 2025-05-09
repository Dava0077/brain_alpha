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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import brain.alpha.controller.GameSessionController
import brain.alpha.controller.LeaderboardController
import brain.alpha.model.UserSession
import brain.alpha.controller.LeaderboardEntry
import brain.alpha.controller.LeaderboardByDifficulty
import brainalpha.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import kotlinx.coroutines.launch
import kotlin.math.round

enum class SortOption(val label: String) {
    SCORE("Puntaje"),
    TIME_AVG("Tiempo medio")
}


@Composable
fun LeaderboardScreen(controller: LeaderboardController) {
    val displayName = UserSession.getDisplayName()
    val categories = mapOf(
        "memory_games" to listOf("pairs_game", "simon_game"),
        "Logica" to listOf("sudoku_game", "wordSearch_game"),
        "Concentracion" to listOf("stroopTest_game", "oddEmoji_game"),
        "Agilidad Mental" to listOf("mental_math", "click_number")
    )

    val ascending = remember { mutableStateOf(false) }
    val expandedCategory = remember { mutableStateOf<String?>(null) }
    val expandedGame = remember { mutableStateOf<String?>(null) }
    val leaderboardMap = remember { mutableStateMapOf<String, LeaderboardByDifficulty>() }
    val sortOption = remember { mutableStateOf(SortOption.SCORE) }
    val sortBy = sortOption.value

    fun sort(entries: List<LeaderboardEntry>): List<LeaderboardEntry> {
        return when (sortBy) {
            SortOption.SCORE -> entries.sortedByDescending { it.score }
            SortOption.TIME_AVG -> entries.sortedBy { it.timeAvg ?: Double.MAX_VALUE }
        }
    }


    val coroutineScope = rememberCoroutineScope()

    // IDs de ejemplo para juegos
    val gameIdMap = mapOf(
        "pairs_game" to "24bc262b-ab64-4c71-9c45-8d4fa04a549e",
        "simon_game" to "2927d18f-b1ab-4376-990e-1275e5ba181e",
        "oddEmoji_game" to "498d59a6-7136-4676-9075-93d115d0395f",
        "sudoku_game" to "b012b362-d7d3-49f7-b14b-bce8ea297453",
        "wordSearch_game" to "d15d1344-df81-41d5-87f0-c468668be2d8",
        "click_number" to "ed29af2f-6346-4a55-bd4d-1b07c013d726",
        "mental_math" to "f017777c-f246-4770-bb73-a652a23e761d",
        "stroopTest_game" to "f68da1b6-6e34-422c-be12-579991dbbc54"

    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Top bar
        Box(
            modifier = Modifier.fillMaxWidth().height(70.dp)
                .background(Color(0xFF222937), shape = RoundedCornerShape(4.dp))
                .border(BorderStroke(2.dp, Color(0xFF4CE3E8)))
        ) {
            IconButton(onClick = {}, modifier = Modifier.align(Alignment.CenterStart).padding(start = 8.dp).size(55.dp)) {
                Image(painter = painterResource(Res.drawable.user), contentDescription = "User")
            }
            Image(painter = painterResource(Res.drawable.logo), contentDescription = null,
                modifier = Modifier.align(Alignment.Center).size(120.dp).scale(1.5f))
            Text(displayName, color = Color.White, fontSize = 16.sp,
                modifier = Modifier.align(Alignment.CenterStart).padding(start = 72.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { controller.goHome() },
            modifier = Modifier.align(Alignment.End),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.DarkGray),
            border = BorderStroke(1.dp, Color.Gray)
        ) {
            Text("Volver", color = Color.White)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Clasificación por juego", fontSize = 28.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Ordenar por:")
            SortOption.values().forEach { option ->
                Button(
                    onClick = { sortOption.value = option },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = if (sortOption.value == option) Color.Gray else Color.LightGray
                    )
                ) {
                    Text(option.label)
                }

            }

            Button(onClick = { ascending.value = !ascending.value }) {
                Text(if (ascending.value) "Asc" else "Desc")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        categories.forEach { (category, games) ->
            ExpandableCategory(
                title = category,
                isExpanded = expandedCategory.value == category,
                onExpandToggle = {
                    expandedCategory.value = if (expandedCategory.value == category) null else category
                    expandedGame.value = null
                }
            ) {
                games.forEach { game ->
                    val gameId = gameIdMap[game]
                    if (gameId.isNullOrBlank()) {
                        println("⚠️ No se encontró gameId válido para '$game'.")
                        return@forEach
                    }

                    ExpandableGame(
                        gameName = game,
                        gameId = gameId,
                        isExpanded = expandedGame.value == game,
                        onExpandToggle = {
                            expandedGame.value = if (expandedGame.value == game) null else game
                            if (expandedGame.value != null && !leaderboardMap.containsKey(gameId)) {
                                coroutineScope.launch {
                                    val data = GameSessionController.getGlobalLeaderboardByGameId(
                                        gameId = gameId,
                                        orderBy = sortOption.value.name.lowercase(),
                                        ascending = ascending.value
                                    )
                                    data.getOrNull()?.let { leaderboardMap[gameId] = it }
                                }
                            }
                        },
                        leaderboard = leaderboardMap[gameId]
                    )
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
        modifier = Modifier.fillMaxWidth()
            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
            .padding(8.dp)
            .clickable { onExpandToggle() }
    ) {
        Text(text = title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        if (isExpanded) {
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
fun ExpandableGame(
    gameName: String,
    gameId: String,
    isExpanded: Boolean,
    onExpandToggle: () -> Unit,
    leaderboard: LeaderboardByDifficulty?
) {
    Column(modifier = Modifier.fillMaxWidth().padding(start = 12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(gameName, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
            Button(onClick = onExpandToggle) {
                Text(if (isExpanded) "Cerrar" else "Ver")
            }
        }
        if (isExpanded) {
            if (leaderboard != null) {
                LeaderboardSection("Fácil", leaderboard.easy)
                LeaderboardSection("Medio", leaderboard.medium)
                LeaderboardSection("Difícil", leaderboard.hard)
            } else {
                Text("Cargando o sin datos disponibles", color = Color.Gray, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun LeaderboardSection(title: String, entries: List<LeaderboardEntry>) {
    Text("• $title", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(top = 12.dp))
    entries.forEachIndexed { i, entry ->
        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            elevation = 4.dp
        ) {
            Column(Modifier.padding(12.dp)) {
                Text("#${i + 1} ${entry.username}", fontWeight = FontWeight.Bold)
                Text("Puntos: ${entry.score}")
                entry.timeAvg?.let {
                    Text("Tiempo medio: ${round(it * 10) / 10} s")
                }
                entry.timeLeft?.let {
                    Text("Tiempo restante: ${round(it * 10) / 10} s")
                }
                Text("Fecha: ${entry.playedAt.take(10)}")
            }
        }
    }
}


