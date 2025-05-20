package brain.alpha.controller

// Controller responsible for handling navigation from the leaderboard screen
object LeaderboardController {
    // Lambda function used to navigate between screens (set externally)
    lateinit var navigateTo: (String) -> Unit

    /**
     * Navigates the user back to the home screen.
     */
    fun goHome(){
        navigateTo("home")
    }
}