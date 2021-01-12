package model

import java.util.*
import kotlin.collections.ArrayList

enum class BoardEvent  { WIN, LOSE }

class Board(val sizeLine: Int, val sizeColumn: Int, val sizeMine: Int) {

    private val fields = ArrayList<ArrayList<Field>>()
    private val callbacks = ArrayList<(BoardEvent) -> Unit>()

    init {
        makeFields()
        connectNeighBors()
        drawMines()
    }

    private fun drawMines() {
        val builder = Random()

        var lineDrawn = -1
        var columnDrawn = -1
        var actualSizeMines = 0

        while (actualSizeMines < sizeMine) {
            lineDrawn = builder.nextInt(sizeLine)
            columnDrawn = builder.nextInt(sizeColumn)

            val fieldDrawn = fields[lineDrawn][columnDrawn]

            if (fieldDrawn.safe) {
                fieldDrawn.addMine()
                actualSizeMines++
            }
        }
    }

    private fun connectNeighBors() {
        forEachFields { connectNeighBor(it) }
    }

    private fun connectNeighBor(field: Field) {
        val (line, column) = field

        val lines = arrayOf(line - 1, line, line + 1)
        val columns = arrayOf(column - 1, column, column + 1)

        lines.forEach { l ->
            columns.forEach { c ->
                val actual = fields.getOrNull(l)?.getOrNull(c)
                actual?.takeIf { field != it }?.let { field.addNeighbor(it) }
            }
        }
    }

    private fun makeFields() {
        for (line in 0 until sizeLine) {
            fields.add(ArrayList())
            for (column in 0 until sizeColumn) {
                val newField = Field(line, column)
                newField.onEvent(this::verifyGame)
                fields[line].add(newField)
            }
        }
    }

    private fun objectiveReached(): Boolean {
        var playerWin = true
        forEachFields { if(!it.objectiveReached) playerWin = false }
        return playerWin
    }

    private fun verifyGame(field: Field, event: FieldEvent) {
        if (event == FieldEvent.EXPLODED) {
            callbacks.forEach { it(BoardEvent.LOSE) }
        } else if (objectiveReached()) {
            callbacks.forEach { it(BoardEvent.WIN) }
        }
    }

    fun forEachFields(callback: (Field) -> Unit) {
        for (line in fields) {
            for (column in line) {
                callback(column)
            }
        }
    }

    fun onEvent(callback: (BoardEvent) -> Unit) {
        callbacks.add(callback)
    }

    fun restart() {
        forEachFields { it.restart() }
        drawMines()
    }
}
