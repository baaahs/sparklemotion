package baaahs.visualizer

expect class VizObj {
    fun add(child: VizObj)
    fun remove(child: VizObj)
}