package model

enum class FieldEvent { OPEN, MARKED, UNMARKED, EXPLODED, RESTART }

data class Field(val line: Int, val column: Int) {
    private val neighbors = ArrayList<Field>()
    private val callbacks = ArrayList<(Field, FieldEvent) -> Unit>()

    var marked: Boolean = false
    var opened: Boolean = false
    var mined: Boolean = false

    val unmarked: Boolean get() = !marked
    val closed: Boolean get() = !opened
    val safe: Boolean get() = !mined

    val objectiveReached: Boolean get() = safe && opened || mined && marked
    val sizeMinedNeighbours: Int get() = neighbors.filter { it.mined }.size
    val safeNeighborhood: Boolean
        get() = neighbors.map { it.safe }.reduce { result, safe -> result && safe }

    fun addNeighbor(neighbor: Field) { neighbors.add(neighbor) }

    fun onEvent(callback: (Field, FieldEvent) -> Unit) {
        callbacks.add(callback)
    }

    fun open() {
        if (closed) {
            opened = true
            if (mined) {
                callbacks.forEach { it(this, FieldEvent.EXPLODED) }
            } else {
                for (callback in callbacks) {
                    callback(this, FieldEvent.OPEN)
                    if (safeNeighborhood) neighbors.filter { it.closed && it.safe }.forEach { it.open() }
                }
            }
        }
    }

    fun alterMarking() {
        if (closed) {
            marked = !marked
            val event = if (marked) FieldEvent.MARKED else FieldEvent.UNMARKED
            callbacks.forEach { it(this, event) }
        }
    }

    fun addMine() {
        mined = true
    }

    fun restart() {
        opened = false
        marked = false
        mined = false
        callbacks.forEach { it(this, FieldEvent.RESTART) }
    }
}
