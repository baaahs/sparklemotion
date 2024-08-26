package baaahs.show.live

interface ControlDisplay {
    val show: OpenShow
    val unplacedControls: Set<OpenControl>
    val relevantUnplacedControls: List<OpenControl>

    fun release()
}