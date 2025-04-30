package brain.alpha.controller

object UserProfileController {
    lateinit var navigateTo: (String) -> Unit

    fun goHome() {
        navigateTo("home")
    }
}