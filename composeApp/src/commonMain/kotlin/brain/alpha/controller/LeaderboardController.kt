package brain.alpha.controller

object LeaderboardController {
    lateinit var navigateTo: (String) -> Unit

    fun goHome(){
        navigateTo("home")
    }
}