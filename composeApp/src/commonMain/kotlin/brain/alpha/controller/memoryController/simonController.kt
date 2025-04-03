package brain.alpha.controller.memoryController


object simonController {
    lateinit var navigateTo: (String) -> Unit

    fun goHome(){
        navigateTo("memory")
    }
}