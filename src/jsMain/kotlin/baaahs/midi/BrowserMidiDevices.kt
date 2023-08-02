package baaahs.midi

import baaahs.util.Logger
import external.midi.MIDIAccess
import external.midi.MIDIInput
import web.navigator.navigator

class SimMidiDevices : MidiDevices {
    override suspend fun listTransmitters(): List<MidiTransmitter> = emptyList()
}

class BrowserMidiDevices : MidiDevices {
    private lateinit var midiAccess: MIDIAccess;

    init {
        navigator.asDynamic().requestMIDIAccess().then {midiAccessResult: MIDIAccess ->
            midiAccess = midiAccessResult
            midiAccessResult
        }
    }

    override suspend fun listTransmitters(): List<MidiTransmitter> {
        val ids = mutableMapOf<String, Counter>()


        return buildList {
//            midiAccess.inputs.forEach { inputEntry ->
//                val input = inputEntry.value
//                println("${input.name}: DESC=${input.description}\n  VENDOR=${input.manufacturer}\n  VERSION=${input.version}")
//                val id = input.name.let {
//                    val idNum = ids.getOrPut(it) { Counter() }.count()
//                    if (idNum == 0) it else "it #$idNum"
//                }
//
//                add(JsMidiTransmitterTransmitter(id, input))
//            }
        }
    }

    class JsMidiTransmitterTransmitter(
        override val id: String,
        private val input: MIDIInput
    ) : MidiTransmitter {
        override val name: String
            get() = input.name
        override val vendor: String
            get() = input.manufacturer
        override val description: String
            get() = ""
        override val version: String
            get() = input.version

//        private var transmitter: Transmitter? = null

        override fun listen(callback: (MidiMessage) -> Unit) {
//            if (transmitter == null) {
//                transmitter = run {
//                    input.open()
//                }
//            }

//            transmitter!!.receiver = object : Receiver {
//                override fun close() {
//                    logger.debug { "$name closed." }
//                }
//
//                override fun send(message: javax.sound.midi.MidiMessage?, timeStamp: Long) {
//                    when (message) {
//                        is ShortMessage -> {
//                            with(message) {
//                                logger.debug {
//                                    "MIDI [$name]: channel=${channel} command=${command} data1=${data1} data2=${data2}"
//                                }
//                                callback(MidiMessage(channel, command, data1, data2))
//                            }
//                        }
//                        else -> logger.warn { "send! unknown message $message $timeStamp." }
//                    }
//                }
//            }
        }

        override fun close() {
//            if (transmitter != null) {
//                input.close()
//            }
            logger.debug { "$name closed." }
        }

    }

    private class Counter(var value: Int = 0) {
        fun count(): Int = value++
    }

    companion object {
        private val logger = Logger<BrowserMidiDevices>()
    }
}