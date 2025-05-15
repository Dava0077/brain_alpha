package brain.alpha.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*


import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import brain.alpha.strings.Strings
import brain.alpha.controller.*
import brain.alpha.model.UserSession
import brainalpha.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import brain.alpha.utils.isCompactScreen
import kotlinx.coroutines.launch
import kotlin.math.round

enum class SortOption(val label: String) {
    SCORE("Puntaje"),
    TIME_AVG("Tiempo medio")
}

val displayNamesMap = mapOf(
    "pairs_game" to "Juego de parejas",
    "simon_game" to "Simón Dice",
    "sudoku_game" to "Sudoku",
    "wordSearch_game" to "Sopa de Letras",
    "stroopTest_game" to "Stroop Game",
    "oddEmoji_game" to "Odd Emoji",
    "mental_math" to "Quick Maths",
    "click_number" to "Click Number"
)
val gamesWithDifficulty = setOf(
    "sudoku_game", "pairs_game", "simon_game", "wordSearch_game"
)






fun sort(entries: List<LeaderboardEntry>, sortBy: SortOption, ascending: Boolean): List<LeaderboardEntry> {
    val sorted = when (sortBy) {
        SortOption.SCORE -> entries.sortedBy { it.score }
        SortOption.TIME_AVG -> entries.sortedBy { it.timeAvg ?: Double.MAX_VALUE }
    }
    return if (ascending) sorted else sorted.reversed()
}








@Composable
fun LeaderboardScreen(controller: LeaderboardController) {
    val isCompact = isCompactScreen()


    val displayName = UserSession.getDisplayName()
    val categories = mapOf(
        "Memoria" to listOf("pairs_game", "simon_game"),
        "Logica" to listOf("sudoku_game", "wordSearch_game"),
        "Concentracion" to listOf("stroopTest_game", "oddEmoji_game"),
        "Agilidad Mental" to listOf("mental_math", "click_number")
    )

    val sortOption = remember { mutableStateOf(SortOption.SCORE) }

    val sortBy = sortOption.value

    val ascending = remember { mutableStateOf(false) }
    val expandedCategory = remember { mutableStateOf<String?>(null) }
    val expandedGame = remember { mutableStateOf<String?>(null) }
    val leaderboardMap = remember { mutableStateMapOf<String, LeaderboardByDifficulty>() }


    val reloadTrigger = remember { mutableStateOf(0) }

    val coroutineScope = rememberCoroutineScope()




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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(if (isCompact) 8.dp else 16.dp)
    ) {
        // Top bar
        Box(
            modifier = Modifier.fillMaxWidth().height(70.dp)
                .background(Color(0xFF222937), shape = RoundedCornerShape(4.dp))
                .border(BorderStroke(2.dp, Color(0xFF4CE3E8)))
        ) {
            IconButton(onClick = { LoginController.navigateTo("profile")}, modifier = Modifier.align(Alignment.CenterStart).padding(start = 8.dp).size(55.dp)) {
                Image(painter = painterResource(Res.drawable.user), contentDescription = "User")
            }

            Text(displayName, color = Color.White, fontSize = 16.sp,
                modifier = Modifier.align(Alignment.CenterStart).padding(start = 72.dp))

            Text("Clasificación", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center).padding(start = 72.dp))

            IconButton(onClick = { LoginController.navigateTo("settings")}, modifier = Modifier.align(Alignment.CenterEnd).padding(start = 8.dp).size(55.dp)) {
                Image(painter = painterResource(Res.drawable.ajustes), contentDescription = "Settings")
            }
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

        Text(
            "Clasificación por juego",
            fontSize = if (isCompact) 22.sp else 28.sp,
            fontWeight = FontWeight.Bold)

//        // 👇 DEBUG VISUAL: todas las entradas cargadas
//        leaderboardMap.forEach { (gameId, leaderboard) ->
//            Text("🧾 Leaderboard para juego: $gameId", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Magenta)
//            leaderboard.easy.forEach {
//                Text("🎮 EASY - ${it.username}: ${it.score}", fontSize = 13.sp)
//            }
//            leaderboard.medium.forEach {
//                Text("🧠 MEDIUM - ${it.username}: ${it.score}", fontSize = 13.sp)
//            }
//            leaderboard.hard.forEach {
//                Text("🔥 HARD - ${it.username}: ${it.score}", fontSize = 13.sp)
//            }
//            Spacer(Modifier.height(8.dp))
//        }

        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Ordenar por:")
            SortOption.values().forEach { option ->
                Button(
                    onClick = {
                        sortOption.value = option
                        reloadTrigger.value++  // Forzar recarga
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = if (sortOption.value == option) Color(0xFF4FC3F7) else Color.LightGray
                    )
                ) {
                    Text(option.label, color = Color.White)
                }
            }

            Button(
                onClick = {
                    ascending.value = !ascending.value
                    reloadTrigger.value++  // Forzar recarga
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF4FC3F7)
                )
            ) {
                Text(if (ascending.value) "Asc" else "Desc", color = Color.White)
            }
        }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.weight(1f)
            ) {
                DifficultyLegendItem(Res.drawable.easy, Strings.t("easy"), size = 32.dp)
                DifficultyLegendItem(Res.drawable.medium, Strings.t("medium"), size = 40.dp)
                DifficultyLegendItem(Res.drawable.hard, Strings.t("hard"), size = 48.dp)
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
//                games.forEach { game ->
//                    val gameId = gameIdMap[game]
//                    if (gameId.isNullOrBlank()) {
//                        println("⚠️ No se encontró gameId válido para '$game'.")
//                        return@forEach
//                    }

//                    ExpandableGame(
//                        gameName = game,
//                        gameId = gameId,
//                        isExpanded = expandedGame.value == game,
//                        onExpandToggle = {
//                            expandedGame.value = if (expandedGame.value == game) null else game
//                            if (expandedGame.value != null && !leaderboardMap.containsKey(gameId)) {
//                                coroutineScope.launch {
//                                    println("🔍 Intentando cargar leaderboard para juegoId=$gameId...")
//                                    val orderField = sortOption.value.name.lowercase()
//                                    println("📤 Solicitando leaderboard: gameId=$gameId, orderBy=$orderField, asc=${ascending.value}")
//                                    val data = GameSessionController.getGlobalLeaderboardByGameId(
//                                        gameId = gameId,
//                                        orderBy = orderField,
//                                        ascending = ascending.value
//                                    )
//                                    data.onFailure { error -> println("❌ Error al cargar leaderboard: ${error.message}") }
//                                    data.getOrNull()?.let {
//                                        println("✅ Leaderboard cargado con ${it.easy.size + it.medium.size + it.hard.size} entradas")
//                                        leaderboardMap[gameId] = it
//                                    }
//                                }
//                            }
//                        },
//                        leaderboard = leaderboardMap[gameId]
//                    )
                games.forEach { game ->
                    val gameId = gameIdMap[game] ?: return@forEach
                    val gameLabel = displayNamesMap[game] ?: game

                    ExpandableGame(
                        gameName = gameLabel,
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
                        leaderboard = leaderboardMap[gameId],
                        showDifficulty = game in gamesWithDifficulty,
                        sortOption = sortOption.value,
                        ascending = ascending.value

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
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
            .padding(8.dp)
            .clickable { onExpandToggle() }
    ) {
        Text(text = title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
//        if (isExpanded) {
//            Spacer(modifier = Modifier.height(8.dp))
//            content()
//        }
        AnimatedVisibility(visible = isExpanded, enter = fadeIn(), exit = fadeOut()) {
            Column {
                Spacer(modifier = Modifier.height(8.dp))
                content()
            }
        }
    }
}

@Composable
fun ExpandableGame(
    gameName: String,
    gameId: String,
    isExpanded: Boolean,
    onExpandToggle: () -> Unit,
    leaderboard: LeaderboardByDifficulty?,
    showDifficulty: Boolean,
    sortOption: SortOption,
    ascending: Boolean
) {
    Column(modifier = Modifier.fillMaxWidth().padding(start = 12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(gameName, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
            Button(
                onClick = onExpandToggle,
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF4FC3F7))
            ) {
                Text(if (isExpanded) "Cerrar" else "Ver", color = Color.White)
            }
        }
//        if (isExpanded) {
//            if (leaderboard != null) {
//                LeaderboardSection("Fácil", leaderboard.easy)
//                LeaderboardSection("Medio", leaderboard.medium)
//                LeaderboardSection("Difícil", leaderboard.hard)
//            } else {
//                if (leaderboard == null) {
//                    CircularProgressIndicator(
//                        modifier = Modifier.size(24.dp).padding(8.dp),
//                        color = Color.Gray
//                    )
//                }
//            }
//        }
        AnimatedVisibility(visible = isExpanded, enter = fadeIn(), exit = fadeOut()) {
            Column {
                if (leaderboard != null) {
                    if (showDifficulty) {
                        val combined = leaderboard.easy + leaderboard.medium + leaderboard.hard
                        val merged = mergeEntriesByUsername(combined)
                        LeaderboardSection("Clasificación", sort(merged, SortOption.SCORE, ascending), showDifficulty)

                    } else {
                        LeaderboardSection("Fácil", sort(leaderboard.easy, SortOption.SCORE, ascending),showDifficulty)
                        LeaderboardSection("Medio", sort(leaderboard.medium, SortOption.SCORE, ascending),showDifficulty)
                        LeaderboardSection("Difícil", sort(leaderboard.hard, SortOption.SCORE,ascending),showDifficulty)

                    }
                } else {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp).padding(8.dp),
                        color = Color.Gray
                    )
                }
            }
        }

    }
}

@Composable
fun LeaderboardSection(title: String, entries: List<LeaderboardEntry>, showDifficulty: Boolean) {
    val headerColor = when (title.lowercase()) {
        "fácil" -> Color(0xFFD6F5E3)
        "medio" -> Color(0xFFD6E9F5)
        "difícil" -> Color(0xFFF5D6E0)
        else -> Color(0xFFCCE5FF)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .background(Color(0xFFEAF4FF))
    ) {
        Text(
            text = "• $title",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.padding(8.dp)
        )

        HeaderRow(headerColor, showDifficulty)

        if (entries.isEmpty()) {
            Text(
                "Aún no hay partidas registradas",
                color = Color.Gray,
                modifier = Modifier.padding(12.dp)
            )
        } else {
            entries.forEachIndexed { i, entry ->
                EntryRow(index = i, entry = entry, showDifficulty = showDifficulty)
            }
        }
    }
}

@Composable
fun HeaderRow(bgColor: Color, showDifficulty: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor)
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("🏆", fontSize = 14.sp, modifier = Modifier.width(30.dp), textAlign = TextAlign.Center)
        Text("👤", fontSize = 14.sp, modifier = Modifier.width(100.dp), textAlign = TextAlign.Center)
        Text("🧠", fontSize = 14.sp, modifier = Modifier.width(60.dp), textAlign = TextAlign.Center)
        if (showDifficulty) {
            Text("\ud83d\udcca", fontSize = 14.sp, modifier = Modifier.width(60.dp), textAlign = TextAlign.Center)
        }else{
            Text("⏱", fontSize = 14.sp, modifier = Modifier.width(60.dp), textAlign = TextAlign.Center)
        }
        Text("📅", fontSize = 14.sp, modifier = Modifier.width(80.dp), textAlign = TextAlign.Center)
    }
}

@Composable
fun EntryRow(index: Int, entry: LeaderboardEntry, showDifficulty: Boolean) {
    val bgColor = if (index % 2 == 0) Color.White else Color(0xFFF5FAFF)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor)
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("#${index + 1}", fontSize = 13.sp, modifier = Modifier.width(30.dp), textAlign = TextAlign.Center)
        Text(entry.username, fontSize = 13.sp, modifier = Modifier.width(100.dp), textAlign = TextAlign.Center)
        Text(entry.score.toString(), fontSize = 13.sp, modifier = Modifier.width(60.dp), textAlign = TextAlign.Center)
        if (showDifficulty) {
            val difficultyIcon = when (entry.difficulty?.lowercase()) {
                "easy" -> Res.drawable.easy
                "medium" -> Res.drawable.medium
                "hard" -> Res.drawable.hard
                else -> Res.drawable.iconoInt
            }

            Image(
                painter = painterResource(difficultyIcon),
                contentDescription = "Dificultad: ${entry.difficulty}",
                modifier = Modifier
                    .width(60.dp)
                    .height(32.dp)
                    .padding(horizontal = 4.dp)
            )        }else{
            Text(entry.timeAvg?.let { "${round(it * 10) / 10} s" } ?: "-", fontSize = 13.sp, modifier = Modifier.width(60.dp), textAlign = TextAlign.Center)

        }
        Text(entry.playedAt.take(10), fontSize = 13.sp, modifier = Modifier.width(80.dp), textAlign = TextAlign.Center)
    }
}
fun mergeEntriesByUsername(entries: List<LeaderboardEntry>): List<LeaderboardEntry> {
    return entries
        .groupBy { it.username }
        .map { (username, group) ->
            val totalScore = group.sumOf { it.score }

            val difficulty = when {
                totalScore < 75 -> "EASY"
                totalScore < 150 -> "MEDIUM"
                else -> "HARD"
            }

            LeaderboardEntry(
                username = username,
                score = totalScore,
                difficulty = difficulty,
                timeAvg = group.mapNotNull { it.timeAvg }.average().takeIf { !it.isNaN() },
                timeLeft = group.mapNotNull { it.timeLeft }.average().takeIf { !it.isNaN() },
                playedAt = group.maxOfOrNull { it.playedAt } ?: ""
            )
        }
}






