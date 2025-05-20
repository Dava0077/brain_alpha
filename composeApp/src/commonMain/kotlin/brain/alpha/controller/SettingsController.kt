package brain.alpha.controller

// Controller for handling settings-related navigation
object SettingsController {

    // Lambda for performing navigation, injected externally (e.g., from App.kt)
    lateinit var navigateTo: (String) -> Unit

    /**
     * Navigates the user back to the home screen.
     */
    fun goHome(){
        navigateTo("home")
    }

    /**
     * Navigates the user to their profile screen.
     */
    fun goUserScreen(){
        navigateTo("profile")
    }
}