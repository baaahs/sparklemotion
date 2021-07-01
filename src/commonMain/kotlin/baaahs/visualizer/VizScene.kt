package baaahs.visualizer

expect class VizScene {
    fun add(obj: VizObj)
    fun remove(obj: VizObj)
}

expect class VizObj