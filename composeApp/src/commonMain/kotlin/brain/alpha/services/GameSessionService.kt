package brain.alpha.services

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.serialization.Serializable

@Serializable
data class GameSession(
    val user_id: String,
    val game_id: String,
    val score: Int,
    val difficulty: String
)
@Serializable
data class GameSessionWithUser(
    val score: Int,
    val difficulty: String,
    val name: String // <- viene de users.name
)

object GameSessionService {

    suspend fun savePairGameResult(
        userId: String,
        gameId: String,
        score: Int,
        difficulty: String,
        supabase: SupabaseClient
    ) {
        try {
            supabase.postgrest["game_sessions"].insert(
                GameSession(user_id = userId, game_id = gameId, score = score, difficulty = difficulty)
            )
        } catch (e: Exception) {
            println("❌ Error saving game session: ${e.message}")
        }
    }

    suspend fun showPairGameResult(
        supabase: SupabaseClient,
        gameId: String
    ): List<GameSessionWithUser> {
        return try {
            supabase.postgrest["game_sessions"]
                .select(
                    columns = Columns.raw("score, difficulty, users(name)"),
                ) {
                    filter {
                        eq("game_id", gameId)
                    }
                }
                .decodeList<GameSessionWithUser>()
        } catch (e: Exception) {
            println("❌ Error fetching leaderboard: ${e.message}")
            emptyList()
        }
    }
}

