package brain.alpha.controller.mentalAgilityController

// Controller for the mentalAgility home screen.
// Handles navigation to different screens within the mentalAgility category.
object mentalHomeController {
    // Function reference to navigate between routes/screens.
    // Must be set from the UI layer.
    lateinit var navigateTo: (String) -> Unit
    // Navigate to the main home screen
    fun goHome(){
        navigateTo("home")
    }
}