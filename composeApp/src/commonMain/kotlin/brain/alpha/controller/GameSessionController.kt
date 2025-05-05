package brain.alpha.controller

import brain.alpha.model.UserSession
import brain.alpha.services.SupabaseClientProvider
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.*
import kotlinx.datetime.Clock

data class LeaderboardEntry(
    val username: String,
    val score: Long,
    val timeAvg: Double?,
    val timeLeft: Double?,
    val playedAt: String
)

data class UserProgressEntry(
    val gameName: String,
    val score: Long,
    val lastPlayed: String
)

data class LeaderboardByDifficulty(
    val easy: List<LeaderboardEntry>,
    val medium: List<LeaderboardEntry>,
    val hard: List<LeaderboardEntry>
)

object GameSessionController {

    // Registra una nueva partida y actualiza el progreso del usuario
    suspend fun registerGameSession(
        gameId: String, // usa directamente el ID
        difficulty: String,
        score: Int,
        timeAvg: Double? = null,
        timeLeft: Double? = null
    ): Result<Unit> = withContext(Dispatchers.Default) {
        runCatching {
            val allowedDifficulties = setOf("EASY", "MEDIUM", "HARD")
            require(score >= 0) { "El score no puede ser negativo." }
            require(difficulty.uppercase() in allowedDifficulties) { "Dificultad no válida." }

            val userId = UserSession.userId.value ?: error("Usuario no autenticado")

            SupabaseClientProvider.client.postgrest["game_sessions"].insert(
                buildJsonObject {
                    put("user_id", userId)
                    put("game_id", gameId)
                    put("score", score)
                    put("difficulty", difficulty)
                    timeAvg?.let { put("time_avg", it) }
                    timeLeft?.let { put("time_left", it) }
                }
            )

            upsertUserProgress(userId, gameId, score)
        }
    }


    // Consulta el leaderboard filtrado por juego, dificultad y orden
    suspend fun getLeaderboard(
        gameName: String,
        difficulty: String,
        orderBy: String = "score",
        ascending: Boolean = false,
        limit: Int = 20
    ): Result<List<LeaderboardEntry>> = withContext(Dispatchers.Default) {
        runCatching {
            val gameId = getGameIdByName(gameName)
                ?: throw IllegalStateException("Juego no encontrado: $gameName")

            val order = if (ascending) "asc" else "desc"

            val response = SupabaseClientProvider.client.postgrest["game_sessions"]
                .select {
                    filter {
                        eq("game_id", gameId)
                        eq("difficulty", difficulty)
                    }
                    val orderType = if (ascending) Order.ASCENDING else Order.DESCENDING

                    order(orderBy, orderType)
                    limit(limit.toLong())
                }

            val sessions = response.decodeList<JsonObject>()

            sessions.mapNotNull { item ->
                val obj = item.jsonObject
                val uid = obj["user_id"]?.jsonPrimitive?.contentOrNull ?: return@mapNotNull null
                val user = getUsernameById(uid)
                LeaderboardEntry(
                    username = user ?: "Usuario",
                    score = obj["score"]?.jsonPrimitive?.long ?: 0,
                    timeAvg = obj["time_avg"]?.jsonPrimitive?.doubleOrNull,
                    timeLeft = obj["time_left"]?.jsonPrimitive?.doubleOrNull,
                    playedAt = obj["played_at"]?.jsonPrimitive?.content ?: ""
                )
            }
        }
    }

    // Consulta el ID del juego por su nombre
    private suspend fun getGameIdByName(gameName: String): String? {
        val response = SupabaseClientProvider.client.postgrest["games"]
            .select {
                filter { eq("name", gameName) }
                limit(1)
            }

        val items = response.decodeList<JsonObject>()
        return items.firstOrNull()?.get("id")?.jsonPrimitive?.contentOrNull
    }

    // Obtiene el nombre del usuario (campo 'name') por user_id
    private suspend fun getUsernameById(userId: String): String? {
        val response = SupabaseClientProvider.client.postgrest["users"]
            .select {
                filter { eq("id", userId) }
                limit(1)
            }

        val items = response.decodeList<JsonObject>()
        return items.firstOrNull()?.get("name")?.jsonPrimitive?.contentOrNull
    }

    // Crea o actualiza la entrada de progreso del usuario
    private suspend fun upsertUserProgress(userId: String, gameId: String, score: Int) {
        // Supabase upsert emulado
        val existing = SupabaseClientProvider.client.postgrest["user_progress"]
            .select {
                filter {
                    eq("user_id", userId)
                    eq("game_id", gameId)
                }
                limit(1)
            }

        val existingItems = existing.decodeList<JsonObject>()
        if (existingItems.isEmpty()) {
            SupabaseClientProvider.client.postgrest["user_progress"].insert(
                buildJsonObject {
                    put("user_id", userId)
                    put("game_id", gameId)
                    put("score", score)
                    put("last_played", Clock.System.now().toString())
                }
            )
        } else {
            val existingItem = existingItems.firstOrNull()
            val existingScore = existingItem?.get("score")?.jsonPrimitive?.longOrNull ?: 0

            if (score > existingScore) {
                val progressId = existingItem?.get("id")?.jsonPrimitive?.contentOrNull ?: return

                SupabaseClientProvider.client.postgrest["user_progress"].update(
                    buildJsonObject {
                        put("score", score)
                        put("last_played", Clock.System.now().toString())
                    }
                ) {
                    filter { eq("id", progressId) }
                }
            }

        }
    }

    suspend fun getUserProgress(userId: String): Result<List<UserProgressEntry>> = withContext(Dispatchers.Default) {
        runCatching {
            val response = SupabaseClientProvider.client.postgrest["user_progress"]
                .select {
                    filter { eq("user_id", userId) }
                }

            val progressItems = response.decodeList<JsonObject>()

            progressItems.mapNotNull { item ->
                val gameId = item["game_id"]?.jsonPrimitive?.contentOrNull ?: return@mapNotNull null
                val gameName = getGameNameById(gameId) ?: "Juego desconocido"

                UserProgressEntry(
                    gameName = gameName,
                    score = item["score"]?.jsonPrimitive?.long ?: 0,
                    lastPlayed = item["last_played"]?.jsonPrimitive?.content ?: ""
                )
            }
        }
    }

    // Helper para obtener nombre de juego por ID
    private suspend fun getGameNameById(gameId: String): String? {
        val response = SupabaseClientProvider.client.postgrest["games"]
            .select {
                filter { eq("id", gameId) }
                limit(1)
            }

        val items = response.decodeList<JsonObject>()
        return items.firstOrNull()?.get("name")?.jsonPrimitive?.contentOrNull
    }

    suspend fun getLeaderboardGroupedByDifficulty(gameName: String): Result<LeaderboardByDifficulty> = withContext(Dispatchers.Default) {
        runCatching {
            val easy = getLeaderboard(gameName, "EASY").getOrThrow()
            val medium = getLeaderboard(gameName, "MEDIUM").getOrThrow()
            val hard = getLeaderboard(gameName, "HARD").getOrThrow()

            LeaderboardByDifficulty(
                easy = easy,
                medium = medium,
                hard = hard
            )
        }
    }
}
