package brain.alpha.controller

// Controller for handling navigation from the user profile screen
object UserProfileController {

    // Lambda used to perform navigation actions (injected from outside)
    lateinit var navigateTo: (String) -> Unit

    /**
     * Navigates the user to the home screen.
     */
    fun goHome() {
        navigateTo("home")
    }
}