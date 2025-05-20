package brain.alpha.services

import brain.alpha.model.UserSession
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.FlowType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.compose.auth.ComposeAuth
import io.github.jan.supabase.compose.auth.googleNativeLogin


import io.github.jan.supabase.postgrest.Postgrest

// Singleton object that provides a configured instance of SupabaseClient
object SupabaseClientProvider {
    // Lazy-initialized Supabase client
    val client: SupabaseClient by lazy {
        // Create and configure the Supabase client with URL and public key
        createSupabaseClient(
            supabaseUrl = "https://uzzuzjbavzntdbbrczla.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InV6enV6amJhdnpudGRiYnJjemxhIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDE4OTI1MjgsImV4cCI6MjA1NzQ2ODUyOH0.FBcwuReRKXlwxQuc-64u_o0DsRJcvlskqB3gq2yGn1g"
        ) {
            // Install the Auth plugin with PKCE flow and auto-load session from storage
            install(Auth) {
                autoLoadFromStorage = true
                flowType = FlowType.PKCE


            }

            // Install ComposeAuth plugin to enable login from Jetpack Compose
            install(ComposeAuth){
                googleNativeLogin(
                    serverClientId = "788528220622-f80rt9dfvbalp386fflrbs1i5dda56qj.apps.googleusercontent.com",
                    isSupported = true ,
                    associateLinkedAccounts = null,
                    handleSignOut = {
                        // Clear both Supabase session and app session when signing out
                        SupabaseClientProvider.client.auth.signOut()
                        UserSession.clear()
                    }
                )

            }

            // Install Postgrest plugin to allow querying tables
            install(Postgrest)
        }
    }
}