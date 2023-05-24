package baaahs.plugin.midi

import baaahs.ui.Observable
import baaahs.util.Clock
import baaahs.util.Logger
import baaahs.util.Time
import org.deepsymmetry.beatlink.*
import javax.sound.midi.MidiMessage
import javax.sound.midi.MidiSystem
import javax.sound.midi.Receiver
import javax.sound.midi.ShortMessage
import kotlin.concurrent.thread
import kotlin.math.abs

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

        val name = "Keystation 61 MK3 (USB MIDI)"
        val transmitters = MidiSystem.getMidiDeviceInfo().mapNotNull { info ->
            println("${info.name}: ${info.javaClass.simpleName}\n  DESC=${info.description}\n  VENDOR=${info.vendor}\n  VERSION=${info.version}")
            val device = MidiSystem.getMidiDevice(info)
            val maxTransmitters = device.maxTransmitters
            if (maxTransmitters == -1 || maxTransmitters > 0) {
                device
            } else null
        }
        val transmitterDevice = transmitters.firstOrNull { it.deviceInfo.description == name }
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
                            val newMidiData = MidiData(currentMidiData.sustainPedalCount + 1)
                            currentMidiData = newMidiData
                            notifyChanged()
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