package brain.alpha.services

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseClientProvider {
    val client: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = "https://uzzuzjbavzntdbbrczla.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InV6enV6amJhdnpudGRiYnJjemxhIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDE4OTI1MjgsImV4cCI6MjA1NzQ2ODUyOH0.FBcwuReRKXlwxQuc-64u_o0DsRJcvlskqB3gq2yGn1g"
        ) {
            install(Auth)
            install(Postgrest)
        }
    }
}