package brain.alpha.controller.memoryController

object memoryHomeController {
    lateinit var navigateTo: (String) -> Unit

    fun goHome(){
        navigateTo("home")
    }
    fun goCouples(){
        navigateTo("couples")
    }
    fun goSimon(){
        navigateTo("simon")
    }
}