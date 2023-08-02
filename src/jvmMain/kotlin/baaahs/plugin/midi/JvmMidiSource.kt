package baaahs.plugin.midi

import baaahs.ui.Observable
import baaahs.util.Logger
import javax.sound.midi.MidiDevice
import javax.sound.midi.MidiMessage
import javax.sound.midi.Receiver
import javax.sound.midi.ShortMessage

/**
 * Listens to a midi controller
 *
 * We pick the midi to sync shows to based on the following:
 * 1) Hardcoded name of device on boot
 */
class JvmMidiSource(
    private val device: MidiDevice
) : Observable(), MidiSource {
    override val name: String
        get() = device.deviceInfo.name

    @Volatile
    var currentMidiData: MidiData = MidiData(0)

    private val logger = Logger("JvmMidiSource")

    fun start() {
        logger.info { "Starting Midi" }

        device.open()
        val transmitter = device.transmitter
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

        logger.info { "Started" }
    }


    override fun getMidiData(): MidiData {
        return currentMidiData
    }
}