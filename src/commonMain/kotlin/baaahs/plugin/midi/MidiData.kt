package baaahs.plugin.midi

import baaahs.ui.IObservable
import baaahs.ui.Observable
import kotlinx.serialization.Serializable

@Serializable
data class MidiData(
    val sustainPedalCount: Int = 0
) {
    companion object {
        val UNKNOWN = MidiData(0)
    }
}


interface MidiSource : IObservable {
    fun getMidiData(): MidiData

    object None : Observable(), MidiSource {
        val none = MidiData(0)

        override fun getMidiData(): MidiData = none
    }
}

