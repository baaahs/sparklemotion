package baaahs.plugin.midi

import baaahs.util.Logger

class MidiManager(
    private val midiSources: List<MidiSource>
) {
    private val eventListeners = mutableListOf<MidiEventListener>()

    init {
        logger.info { "Initializing MIDI..." }
        midiSources.forEach { midiSource ->
            midiSource.addEventListener { midiDevice, midiEvent ->
                eventListeners.forEach { it ->
                    it.onMidiEvent(midiDevice, midiEvent)
                }
            }
        }
    }

    fun addEventListener(listener: MidiEventListener) {
        eventListeners.add(listener)
    }

    suspend fun start() {
        midiSources.forEach { it.start() }
    }

    companion object {
        private val logger = Logger<MidiManager>()
    }
}