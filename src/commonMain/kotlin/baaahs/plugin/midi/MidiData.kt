package baaahs.plugin.midi

import baaahs.ui.IObservable
import baaahs.ui.Observable
import kotlinx.serialization.Serializable

@Serializable
data class MidiData(
    val sustainPedalCount: Int = 0,
    val noteCount: Int = 0
) {
    companion object {
        val UNKNOWN = MidiData(0, 0)
    }
}


interface MidiSource : IObservable {
    val name: String

    fun getMidiData(): MidiData

    object None : Observable(), MidiSource {
        override val name = "None"

        private val none = MidiData(0, 0)

        override fun getMidiData(): MidiData = none
    }
}

interface MidiSystem : IObservable {
    val midiSources: List<MidiSource>

    suspend fun start() {}

    object None : Observable(), MidiSystem {
        override val midiSources: List<MidiSource>
            get() = emptyList()
    }
}