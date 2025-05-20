package brain.alpha.controller.logicController

// Controller for the logic home screen.
// Handles navigation to different screens within the logic category.
object logicHomeController {
    // Function reference to navigate between routes/screens.
    // Must be set from the UI layer.
    lateinit var navigateTo: (String) -> Unit
    // Navigate to the main home screen
    fun goHome(){
        navigateTo("home")
    }
}