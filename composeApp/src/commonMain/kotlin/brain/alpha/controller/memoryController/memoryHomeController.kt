package brain.alpha.controller.memoryController

// Controller for the memory game home screen.
// Handles navigation actions triggered from the UI.
object memoryHomeController {
    // Navigation function to update current screen route
    lateinit var navigateTo: (String) -> Unit

    // Navigate to the main home screen
    fun goHome(){
        navigateTo("home")
    }
    // Navigate to the Couples game screen
    fun goCouples(){
        navigateTo("couples")
    }
    // Navigate to the Simon game screen
    fun goSimon(){
        navigateTo("simon")
    }
}