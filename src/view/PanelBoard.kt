package view

import model.Board
import model.BoardEvent
import java.awt.GridLayout
import javax.swing.JPanel

class PanelBoard(board: Board) : JPanel() {
    init {
        layout = GridLayout(board.sizeLine, board.sizeColumn)
        board.forEachFields { field ->
            val button = FieldButton(field)
            add(button)
        }
        board.onEvent(::explodeAllMine)
    }

    private fun explodeAllMine(event: BoardEvent) {
        if (event == BoardEvent.LOSE) {
            for (component in components) {
                if (component is FieldButton) {
                    if (component.field.mined && component.field.unmarked) component.applyStyleExploded()
                }
            }
        }
    }
}
