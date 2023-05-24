package baaahs.plugin.midi

import baaahs.ui.Observable
import baaahs.util.Clock
import baaahs.util.Logger
import javax.sound.midi.MidiMessage
import javax.sound.midi.MidiSystem
import javax.sound.midi.Receiver
import javax.sound.midi.ShortMessage

/**
 * Listens to a midi controller
 *
 * We pick the midi to sync shows to based on the following:
 * 1) Hardcoded name of device on boot
 */
class JvmMidiSource(
    private val clock: Clock
) : Observable(), MidiSource {

    @Volatile
    var currentMidiData: MidiData = MidiData(0)

    private val logger = Logger("JvmMidiSource")

    fun start() {
        logger.info { "Starting Midi" }

        val names = arrayOf(
            "iCON iControls V2.04 Port 1",
            "KOMPLETE KONTROL S88 MK2 Port 1",
            "Keystation 61 MK3 (USB MIDI)"
        );
        val transmitters = MidiSystem.getMidiDeviceInfo().mapNotNull { info ->
            println("${info.name}: ${info.javaClass.simpleName}\n  DESC=${info.description}\n  VENDOR=${info.vendor}\n  VERSION=${info.version}")
            val device = MidiSystem.getMidiDevice(info)
            val maxTransmitters = device.maxTransmitters
            if (maxTransmitters == -1 || maxTransmitters > 0) {
                device
            } else null
        }
        val transmitterDevice = transmitters.firstOrNull { it.deviceInfo.description in names }
        transmitterDevice?.let {
            it.open()
            val transmitter = it.transmitter
            transmitter.receiver = object : Receiver {
                override fun close() {
                    println("close!")
                }

                override fun send(message: MidiMessage?, timeStamp: Long) {
                    when (message) {
                        is ShortMessage -> {
                            println("MIDI: " +
                                    "channel=${message.channel} command=${message.command} " +
                                    "data1=${message.data1} data2=${message.data2}")


                            if (message.command == 144) { // Key pressed (NOTE_ON)
                                currentMidiData = MidiData(currentMidiData.sustainPedalCount, currentMidiData.noteCount + 1)
                                notifyChanged()
                            } else if (message.command == 176 && message.data1 == 64 && message.data2 > 63) { // Sustain pedal pressed down more than half way (CONTROL_CHANGE)
                                currentMidiData = MidiData(currentMidiData.sustainPedalCount + 1, currentMidiData.noteCount)
                                notifyChanged()
                            }
                        }
                        else -> println("send! $message $timeStamp")
                    }
                }
            }
        }

        logger.info { "Started" }
    }


    override fun getMidiData(): MidiData {
        return currentMidiData
    }
}