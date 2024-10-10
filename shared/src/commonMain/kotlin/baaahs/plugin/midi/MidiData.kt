package baaahs.plugin.midi

import baaahs.internalTimerClock
import baaahs.midi.MidiDevice
import baaahs.midi.MidiEvent
import baaahs.ui.Observable
import baaahs.util.Clock
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


abstract class MidiSource(
    val name: String,
    private val clock: Clock
) : Observable() {
    private val eventListeners = mutableListOf<MidiEventListener>()

    fun addEventListener(listener: MidiEventListener) {
        eventListeners.add(listener)
    }

    fun removeEventListener(listener: MidiEventListener) {
        eventListeners.remove(listener)
    }

    abstract suspend fun start()

    abstract inner class MidiPort(
        val midiDevice: MidiDevice
    ) {
        fun onEvent(status: Int, data1: Int, data2: Int) {
            val channel = status and 0x0F
            val command = status - channel

            val midiEvent = MidiEvent(clock.now(), channel, command, data1, data2)

            eventListeners.forEach {
                it.onMidiEvent(midiDevice, midiEvent)
            }
        }
    }

    object None : MidiSource("None", internalTimerClock) {
        override suspend fun start() {}
    }
}

fun interface MidiEventListener {
    fun onMidiEvent(midiDevice: MidiDevice, midiEvent: MidiEvent)
}

object MidiCommands {
    // Channel Voice Messages
    const val NOTE_OFF = 128         // 0x80
    const val NOTE_ON = 144          // 0x90
    const val POLY_PRESSURE = 160    // 0xA0 (Polyphonic Key Pressure/Aftertouch)
    const val CONTROL_CHANGE = 176   // 0xB0
    const val PROGRAM_CHANGE = 192   // 0xC0
    const val CHANNEL_PRESSURE = 208 // 0xD0 (Channel Pressure/Aftertouch)
    const val PITCH_BEND = 224       // 0xE0

    // System Common Messages
    const val SYSTEM_EXCLUSIVE = 240 // 0xF0 (Start of SysEx)
    const val MIDI_TIME_CODE = 241   // 0xF1
    const val SONG_POSITION_POINTER = 242 // 0xF2
    const val SONG_SELECT = 243      // 0xF3
    const val TUNE_REQUEST = 246     // 0xF6
    const val END_OF_SYSEX = 247     // 0xF7 (End of SysEx)

    // System Real-Time Messages
    const val TIMING_CLOCK = 248     // 0xF8
    const val START = 250            // 0xFA
    const val CONTINUE = 251         // 0xFB
    const val STOP = 252             // 0xFC
    const val ACTIVE_SENSING = 254   // 0xFE
    const val SYSTEM_RESET = 255     // 0xFF
}
