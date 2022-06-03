package baaahs.midi

import baaahs.util.Logger
import javax.sound.midi.*

class JvmMidiDevices : MidiDevices {
    private val transmitters = mutableMapOf<String, MidiTransmitter>()

    override suspend fun listTransmitters(): List<MidiTransmitter> {
        val ids = mutableMapOf<String, Counter>()

        return buildList {
            MidiSystem.getMidiDeviceInfo().mapNotNull { info ->
                println("${info.name}: ${info.javaClass.simpleName}\n  DESC=${info.description}\n  VENDOR=${info.vendor}\n  VERSION=${info.version}")
                val device = MidiSystem.getMidiDevice(info)
                val id = info.name.let {
                    val idNum = ids.getOrPut(it) { Counter() }.count()
                    if (idNum == 0) it else "it #$idNum"
                }

                val maxTransmitters = device.maxTransmitters
                if (maxTransmitters == -1 || maxTransmitters > 0) {
                    add(JvmMidiTransmitterTransmitter(id, device))
                }
            }
        }
    }

    class JvmMidiTransmitterTransmitter(
        override val id: String,
        private val device: MidiDevice
    ) : MidiTransmitter {
        override val name: String
            get() = device.deviceInfo.name
        override val vendor: String
            get() = device.deviceInfo.vendor
        override val description: String
            get() = device.deviceInfo.description
        override val version: String
            get() = device.deviceInfo.version

        private var transmitter: Transmitter? = null

        override fun listen(callback: (MidiMessage) -> Unit) {
            if (transmitter == null) {
                transmitter = run {
                    device.transmitter.also { device.open() }
                }
            }

            transmitter!!.receiver = object : Receiver {
                override fun close() {
                    logger.debug { "$name closed." }
                }

                override fun send(message: javax.sound.midi.MidiMessage?, timeStamp: Long) {
                    when (message) {
                        is ShortMessage -> {
                            with(message) {
                                logger.debug {
                                    "MIDI [$name]: channel=${channel} command=${command} data1=${data1} data2=${data2}"
                                }
                                callback(MidiMessage(channel, command, data1, data2))
                            }
                        }
                        else -> logger.warn { "send! unknown message $message $timeStamp." }
                    }
                }
            }
        }

        override fun close() {
            if (transmitter != null) {
                device.close()
            }
            logger.debug { "$name closed." }
        }

    }

    private class Counter(var value: Int = 0) {
        fun count(): Int = value++
    }

    companion object {
        private val logger = Logger<JvmMidiDevices>()
    }
}