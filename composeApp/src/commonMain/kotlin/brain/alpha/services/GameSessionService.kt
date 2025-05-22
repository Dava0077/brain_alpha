package brain.alpha.services

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.serialization.Serializable

// Data class representing a single game session row to be saved in Supabase
@Serializable
data class GameSession(
    val user_id: String,
    val game_id: String,
    val score: Int,
    val difficulty: String
)
// Data class used when querying game sessions joined with the user's name.
@Serializable
data class GameSessionWithUser(
    val score: Int,
    val difficulty: String,
    val name: String // <- viene de users.name
)
// Object that provides utility functions to save and retrieve game session data.
object GameSessionService {

    // Saves a new game session into the 'game_sessions' table in Supabase.
    suspend fun savePairGameResult(
        userId: String,
        gameId: String,
        score: Int,
        difficulty: String,
        supabase: SupabaseClient
    ) {
        try {
            // Insert a new row with session data
            supabase.postgrest["game_sessions"].insert(
                GameSession(user_id = userId, game_id = gameId, score = score, difficulty = difficulty)
            )
        } catch (e: Exception) {
            // Log failure
            println("❌ Error saving game session: ${e.message}")
        }
    }

    // Retrieves a list of scores with user names for a given game ID.
    // This includes a join with the 'users' table to get the player name.
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

