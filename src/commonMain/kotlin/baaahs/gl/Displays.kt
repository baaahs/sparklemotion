package baaahs.gl

import baaahs.PubSub
import baaahs.sm.webapi.Topics
import baaahs.ui.Observable
import baaahs.ui.addObserver
import kotlinx.serialization.Serializable

class Displays : Observable() {
    private val knownDisplays = arrayListOf<Display>()

    val all get() = knownDisplays.toList()

    fun add(display: Display) {
        knownDisplays.add(display)
        notifyChanged()
    }

    fun remove(display: Display) {
        knownDisplays.remove(display)
    }

    inner class Channel(pubSub: PubSub.Endpoint) {
        private var displaysChannel = pubSub.openChannel(Topics.displays, all) {
            knownDisplays.clear()
            knownDisplays.addAll(it)
            notifyChanged()
        }

        init {
            this@Displays.addObserver { displaysChannel.onChange(all) }
        }
    }
}

@Serializable
data class DisplayInfo(
    val displayProviderId: String,
    val name: String,
    val modes: List<Mode>,
    val defaultMode: Mode,
    val isPrimary: Boolean
)

class Display(
    val id: Long,
    val displayInfo: DisplayInfo
) {
    val name get() = displayInfo.name
    val modes get() = displayInfo.modes
}

@Serializable
data class Mode(val width: Int, val height: Int) {
    override fun toString(): String = "$width x $height"
}