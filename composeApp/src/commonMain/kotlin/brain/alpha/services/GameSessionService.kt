package brain.alpha.services

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.Serializable

@Serializable
data class GameSessionInsert(
    val user_id: String,
    val game_id: String,
    val score: Int
)

object GameSessionService {

    suspend fun savePairGameResult(
        userId: String,
        gameId: String,
        score: Int,
        supabase: SupabaseClient
    ) {
        try {
            supabase.postgrest["game_sessions"].insert(
                GameSessionInsert(user_id = userId, game_id = gameId, score = score)
            )
        } catch (e: Exception) {
            println("❌ Error saving game session: ${e.message}")
        }
    }
}

