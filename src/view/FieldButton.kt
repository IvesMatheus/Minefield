package view

import model.Field
import model.FieldEvent
import java.awt.Color
import java.awt.Font
import javax.swing.BorderFactory
import javax.swing.JButton
import javax.swing.SwingUtilities

private val COLOR_BG_NORMAL = Color(184, 184, 184)
private val COLOR_BG_MARKED = Color(8, 179, 247)
private val COLOR_BG_EXPLODED = Color(189, 66, 68)
private val COLOR_TEXT_GREEN = Color(0, 100, 0)

class FieldButton (val field: Field) : JButton() {
    init {
        font = font.deriveFont(Font.BOLD)
        background = COLOR_BG_NORMAL
        border = BorderFactory.createBevelBorder(0)
        isOpaque = true
        addMouseListener(MouseClickListener(field, { if (it.marked) it.alterMarking() else it.open() }, { it.alterMarking() }))

        field.onEvent(this::applyStyle)
    }

    private fun applyStyle(field: Field, event: FieldEvent) {
        when (event) {
            FieldEvent.EXPLODED -> applyStyleExploded()
            FieldEvent.OPEN -> applyStyleOpen()
            FieldEvent.MARKED -> applyStyleMarked()
            else -> applyDefaultStyle()
        }

        SwingUtilities.invokeLater {
            repaint()
            validate()
        }
    }

    private fun applyDefaultStyle() {
        background = COLOR_BG_NORMAL
        border = BorderFactory.createBevelBorder(0)
        text = ""
    }

    private fun applyStyleMarked() {
        background = COLOR_BG_MARKED
        foreground = Color.BLACK
        text = "M"
    }

    private fun applyStyleOpen() {
        background = COLOR_BG_NORMAL
        border = BorderFactory.createLineBorder(Color.GRAY)
        foreground = when (field.sizeMinedNeighbours) {
            1 -> COLOR_TEXT_GREEN
            2 -> Color.BLUE
            3 -> Color.YELLOW
            4, 5, 6 -> Color.RED
            else -> Color.PINK
        }
        text = if (field.sizeMinedNeighbours > 0) field.sizeMinedNeighbours.toString() else ""
    }

    fun applyStyleExploded() {
        background = COLOR_BG_EXPLODED
        text = "X"
    }
}
