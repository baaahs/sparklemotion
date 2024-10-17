package baaahs.plugin.midi

import baaahs.util.Clock
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
    clock: Clock
) : MidiSource("JVM MIDI Source", clock) {
    override suspend fun start() {
        logger.info { "Starting MIDI…" }

        javax.sound.midi.MidiSystem.getMidiDeviceInfo().mapNotNull { info ->
            logger.info { "${info.name}: ${info.javaClass.simpleName}\n  DESC=${info.description}\n  VENDOR=${info.vendor}\n  VERSION=${info.version}" }
            val device = javax.sound.midi.MidiSystem.getMidiDevice(info)

            val maxTransmitters = device.maxTransmitters
            if (maxTransmitters == -1 || maxTransmitters > 0) {
                JvmMidiInput(device)
            } else null
        }

        logger.info { "Started" }
    }

    inner class JvmMidiInput(private val device: MidiDevice) : MidiPort(
        baaahs.midi.MidiDevice(
            device.deviceInfo.name,
            device.deviceInfo.name,
            device.deviceInfo.vendor,
            device.deviceInfo.description,
            device.deviceInfo.version
        )
    ) {
        init {
            logger.info { "Have MIDI device: $midiDevice" }

            device.open()
            val transmitter = device.transmitter
            transmitter.receiver = object : Receiver {
                override fun send(message: MidiMessage, timeStamp: Long) {
                    when (message) {
                        is ShortMessage ->
                            onEvent(message.status, message.data1, message.data2)
                        else ->
                            logger.warn { "huh? $message $timeStamp" }
                    }
                }

                override fun close() {
                    logger.warn { "close! on ${device.deviceInfo.name}" }
                }
            }
        }
    }

    companion object {
        private val logger = Logger<JvmMidiSource>()
    }
}