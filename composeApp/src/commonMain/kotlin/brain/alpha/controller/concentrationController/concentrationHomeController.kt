package brain.alpha.controller.concentrationController

// Controller for the concentration home screen.
// Handles navigation to different screens within the concentration category.
object concentrationHomeController {
    // Function reference to navigate between routes/screens.
    // Must be set from the UI layer.
    lateinit var navigateTo: (String) -> Unit

    // Navigate to the main home screen
    fun goHome(){
        navigateTo("home")
    }
    // Navigate to the Stroop test game screen
    fun goStroop(){
        navigateTo("stroop")
    }
    // Navigate to the Odd Emoji game screen
    fun goEmoji(){
        navigateTo("emoji")
    }
}