package brain.alpha.controller.logicController

import brain.alpha.model.logicModel.wordSearchModel
import brain.alpha.view.logicView.wordSearchScreen

object wordSearchController {
    private val model = wordSearchModel
    lateinit var navigateTo: (String) -> Unit



    fun getGrid() = model.getGrid()
    fun getWordsToFind() = model.getWordsToFind()

    fun goHomeLogic() {
        navigateTo("logic")
    }

    fun startGame() {
        model.generateGame()
    }
}
