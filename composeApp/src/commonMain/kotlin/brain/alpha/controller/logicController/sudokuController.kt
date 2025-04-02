package brain.alpha.controller.logicController

import brain.alpha.model.logicModel.sudokuModel
import brain.alpha.model.memoryModels.couplesModel

object sudokuController {
    private val model = sudokuModel
    lateinit var navigateTo: (String) -> Unit

    fun startGame() {
        sudokuModel.initBoard()
    }

    fun getCellValue(row: Int, col: Int): Int {
        return sudokuModel.getValue(row, col)
    }

    fun isCellInitial(row: Int, col: Int): Boolean {
        return sudokuModel.isInitial(row, col)
    }
    fun goHome(){
        navigateTo("home")
    }

    fun setCellValue(row: Int, col: Int, value: Int) {
        sudokuModel.setValue(row, col, value)
    }
}