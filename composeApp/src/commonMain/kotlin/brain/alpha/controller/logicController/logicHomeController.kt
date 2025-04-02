package brain.alpha.controller.logicController

object logicHomeController {
    lateinit var navigateTo: (String) -> Unit

    fun goHome(){
        navigateTo("home")
    }
}