package baaahs.midi

import baaahs.util.Logger
import javax.sound.midi.MidiDevice
import javax.sound.midi.*

class JvmMidiDevices : MidiDevices {
    private val transmitters = mutableMapOf<String, MidiPort>()

    override suspend fun listTransmitters(): List<MidiPort> {
        println("listing transmitters")
        val ids = mutableMapOf<String, Counter>()

        return buildList {
            MidiSystem.getMidiDeviceInfo().mapNotNull { info ->
                println("${info.name}: ${info.javaClass.simpleName}\n  DESC=${info.description}\n  VENDOR=${info.vendor}\n  VERSION=${info.version}")
                println("hello???")
                val device = MidiSystem.getMidiDevice(info)
                val id = info.name.let {
                    val idNum = ids.getOrPut(it) { Counter() }.count()
                    if (idNum == 0) it else "it #$idNum"
                }

                val maxTransmitters = device.maxTransmitters
                if (maxTransmitters == -1 || maxTransmitters > 0) {
                    add(JvmMidiTransmitterPort(id, device))
                }
            }
        }
    }

    class JvmMidiTransmitterPort(
        val id: String,
        private val device: MidiDevice
    ) : MidiPort {
        val name: String
            get() = device.deviceInfo.name
        val vendor: String
            get() = device.deviceInfo.vendor
        val description: String
            get() = device.deviceInfo.description
        val version: String
            get() = device.deviceInfo.version

        private var transmitter: Transmitter? = null

        fun listen(callback: (MidiMessage) -> Unit) {
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

        fun close() {
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