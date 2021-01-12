package view

import model.Board
import model.BoardEvent
import javax.swing.JFrame
import javax.swing.JOptionPane
import javax.swing.SwingUtilities

class MainView : JFrame() {
    private val board: Board = Board(sizeLine=16, sizeColumn=30, sizeMine=89)
    private val panelBoard: PanelBoard = PanelBoard(board)

    init {
        board.onEvent(this::showResult)
        add(panelBoard)

        setSize(690, 438)
        setLocationRelativeTo(null)
        defaultCloseOperation = EXIT_ON_CLOSE
        title = "Campo Minado"
        isVisible = true
    }

    private fun showResult(event: BoardEvent) {
        SwingUtilities.invokeLater {
            val msg = when (event) {
                BoardEvent.WIN -> "Você ganhou"
                BoardEvent.LOSE -> "Você perdeu"
            }

            JOptionPane.showMessageDialog(this, msg)
            board.restart()
            panelBoard.repaint()
            panelBoard.validate()
        }
    }
}

fun main() {
    MainView()
}
