package baaahs.plugin.midi

import baaahs.ui.Observable
import baaahs.util.Clock
import baaahs.util.Logger
import external.midi.MIDIAccess
import external.midi.MIDIInput
import external.midi.MIDIMessageEvent
import kotlinx.js.jso
import org.khronos.webgl.get
import web.navigator.navigator
import kotlin.experimental.and
import kotlin.jvm.Volatile

/**
 * Listens to a midi controller
 *
 * We pick the midi to sync shows to based on the following:
 * 1) Hardcoded name of device on boot
 */
class JsMidiSource(
    private val clock: Clock
) : Observable(), MidiSource {

    @Volatile
    var currentMidiData: MidiData = MidiData(0)

    private val logger = Logger("JsMidiSource")
    fun start() {
        console.log("Starting Midi")

        val names = arrayOf(
            "iCON iControls V2.04 Port 1",
            "KOMPLETE KONTROL S88 MK2 Port 1",
            "Keystation 61 MK3 (USB MIDI)"
        );

        navigator.asDynamic().requestMIDIAccess(jso { sysex = true }).then { midiAccess: MIDIAccess ->
            midiAccess.onstatechange = {event ->
                console.log(event)
            }
            val inputs: List<MIDIInput> = buildList {
                midiAccess.inputs.asDynamic().forEach { inputEntry ->
                    add(inputEntry as MIDIInput)
                }
            }
            val transmitterDevice = inputs.firstOrNull { it.name in names }

            transmitterDevice?.let {
                it.open()
                it.onmidimessage = { messageEvent: MIDIMessageEvent ->
                    val channel = (messageEvent.data[0] and (0x0F).toByte()).toInt()
                    val command = messageEvent.data[0] - channel
                    val data1 = messageEvent.data[1].toInt()
                    val data2 = messageEvent.data[2].toInt()
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
            }
            console.log("Started")
        }
    }


    override fun getMidiData(): MidiData {
        return currentMidiData
    }
}