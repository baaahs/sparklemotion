package baaahs.plugin.midi

import baaahs.midi.MidiEvent
import baaahs.ui.Observable
import baaahs.util.Clock
import baaahs.util.Logger
import js.objects.jso
import web.midi.MIDIInput
import web.navigator.navigator
import kotlin.experimental.and
import kotlin.jvm.Volatile

class JsMidiSystem(
    clock: Clock,
//    private val midiEventReceiver: MidiEventReceiver
) : MidiSystem, Observable() {
    private val jsMidiSource = JsMidiSource(clock)

    override val midiSources: List<MidiSource> =
        listOf(jsMidiSource)

    override suspend fun start() {
        jsMidiSource.start()
    }
}

/**
 * Listens to a midi controller
 *
 * We pick the midi to sync shows to based on the following:
 * 1) Hardcoded name of device on boot
 */
class JsMidiSource(
    private val clock: Clock
) : Observable(), MidiSource {
    override val name: String
        get() = "JS MIDI"

    @Volatile
    var currentMidiData: MidiData = MidiData(0)
    private val logger = Logger("JsMidiSource")

    fun start() {
        console.log("Starting Midi")

        val names = arrayOf(
            "iCON iControls V2.04 Port 1",
            "KOMPLETE KONTROL S88 MK2 Port 1",
            "Keystation 61 MK3 (USB MIDI)"
        )

        navigator.requestMIDIAccess(jso { sysex = true }).then { midiAccess ->
            midiAccess.onstatechange = { event ->
                console.log(event)
                val port = event.port
                if (port is MIDIInput) {
                    JsMidiInput(port)
                }
            }
            val inputs = buildMap {
                midiAccess.inputs.forEach { midiInput, key ->
                    receivedPortUpdate(key, midiInput)
                    put(key, midiInput)
                }
            }
            val transmitterDevice = JsMidiInput(
                inputs.entries.firstOrNull { it.value.name in names }?.value!!
            )

            console.log("Started")
        }
    }

    inner class JsMidiInput(private val port: MIDIInput) {
        init {
            port.onmidimessage = { messageEvent ->
                val data = messageEvent.data!!
                val status = data[0]
                val channel = (status and (0x0F).toByte()).toInt()
                val command = status - channel
                val data1 = data[1].toInt()
                val data2 = data[2].toInt()
                MidiEvent(clock.now(), channel, command, data1, data2)
                console.log(messageEvent)
                console.log("MIDI: " +
                        "channel=${channel} command=${command} " +
                        "data1=${data1} data2=${data2}")
                if (command == 144) { // Key pressed (NOTE_ON)
                    currentMidiData = MidiData(currentMidiData.sustainPedalCount, currentMidiData.noteCount + 1)
                    notifyChanged()
                } else if (command == 176 && data1 == 64 && data2 > 63) { // Sustain pedal pressed down more than half way (CONTROL_CHANGE)
                    currentMidiData = MidiData(currentMidiData.sustainPedalCount + 1, currentMidiData.noteCount)
                    notifyChanged()
                }
            }

            port.open().then {
                console.log("Opened ${port.name}.")
            }
        }
    }

    private fun receivedPortUpdate(key: String, midiInput: MIDIInput) {
        TODO("not implemented")
    }


    override fun getMidiData(): MidiData {
        return currentMidiData
    }
}