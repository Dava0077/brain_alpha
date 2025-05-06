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

object SupabaseClientProvider {
    val client: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = "https://uzzuzjbavzntdbbrczla.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InV6enV6amJhdnpudGRiYnJjemxhIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDE4OTI1MjgsImV4cCI6MjA1NzQ2ODUyOH0.FBcwuReRKXlwxQuc-64u_o0DsRJcvlskqB3gq2yGn1g"
        ) {
            install(Auth) {
                autoLoadFromStorage = true
                flowType = FlowType.PKCE


            }

            install(ComposeAuth){
                googleNativeLogin(
                    serverClientId = "788528220622-f80rt9dfvbalp386fflrbs1i5dda56qj.apps.googleusercontent.com",
                    isSupported = true ,
                    associateLinkedAccounts = null,
                    handleSignOut = {
                        SupabaseClientProvider.client.auth.signOut()
                        UserSession.clear()
                    }
                )

            }

            install(Postgrest)
        }
    }
}