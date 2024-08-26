package baaahs.plugin.midi

import baaahs.ui.Observable
import baaahs.util.SystemClock

class JvmMidiSystem : MidiSystem, Observable() {
    override var midiSources = emptyList<MidiSource>()
        private set

    suspend fun start() {
        midiSources = javax.sound.midi.MidiSystem.getMidiDeviceInfo().mapNotNull { info ->
            println("${info.name}: ${info.javaClass.simpleName}\n  DESC=${info.description}\n  VENDOR=${info.vendor}\n  VERSION=${info.version}")
            val device = javax.sound.midi.MidiSystem.getMidiDevice(info)

            if (device.deviceInfo.description !in names)
                return@mapNotNull null

            val maxTransmitters = device.maxTransmitters
            if (maxTransmitters == -1 || maxTransmitters > 0) {
                JvmMidiSource(SystemClock)
            } else null
        }

        notifyChanged()
    }

    private val names = arrayOf(
        "iCON iControls V2.04 Port 1",
        "KOMPLETE KONTROL S88 MK2 Port 1",
        "Keystation 61 MK3 (USB MIDI)"
    )
}