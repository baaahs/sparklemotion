package baaahs.gl

import baaahs.PubSub
import baaahs.sm.webapi.Topics
import baaahs.ui.Observable
import baaahs.ui.addObserver
import kotlinx.serialization.Serializable

class Monitors : Observable() {
    private val knownMonitors = arrayListOf<Monitor>()

    val all get() = knownMonitors.toList()

    fun add(monitor: Monitor) {
        knownMonitors.add(monitor)
        notifyChanged()
    }

    fun remove(id: Long) {
        knownMonitors.removeAll { it.id == id }
    }

    inner class Channel(pubSub: PubSub.Endpoint) {
        private var monitorsChannel = pubSub.openChannel(Topics.monitors, all) {
            knownMonitors.clear()
            knownMonitors.addAll(it)
            notifyChanged()
        }

        init {
            this@Monitors.addObserver { monitorsChannel.onChange(all) }
        }
    }
}

@Serializable
data class Monitor(
    val id: Long,
    val name: String,
    val modes: List<Mode>,
    val defaultMode: Mode,
    val isPrimary: Boolean
)

@Serializable
data class Mode(val width: Int, val height: Int) {
    override fun toString(): String = "$width x $height"
}