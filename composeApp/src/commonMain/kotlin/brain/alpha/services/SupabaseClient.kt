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
            supabaseKey = "tu_clave_anon"
        ) {
            install(Auth) {
                autoLoadFromStorage = true
                flowType = FlowType.PKCE

            }

            install(ComposeAuth){
                googleNativeLogin(
                    serverClientId = "788528220622-5b573riarvub9ombp93v9klg3ohm99ag.apps.googleusercontent.com",
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