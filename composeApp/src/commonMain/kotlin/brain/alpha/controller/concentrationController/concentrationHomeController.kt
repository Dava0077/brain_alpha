package brain.alpha.controller.concentrationController

object concentrationHomeController {
    lateinit var navigateTo: (String) -> Unit

    fun goHome(){
        navigateTo("home")
    }
    fun goStroop(){
        navigateTo("stroop")
    }
    fun goEmoji(){
        navigateTo("emoji")
    }
}