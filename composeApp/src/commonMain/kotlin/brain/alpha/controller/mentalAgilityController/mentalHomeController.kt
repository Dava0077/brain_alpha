package brain.alpha.controller.mentalAgilityController

object mentalHomeController {
    lateinit var navigateTo: (String) -> Unit

    fun goHome(){
        navigateTo("home")
    }
}