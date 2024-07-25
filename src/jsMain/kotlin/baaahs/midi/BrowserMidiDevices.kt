package baaahs.midi

import baaahs.util.Logger
import web.midi.MIDIAccess
import web.midi.MIDIInput
import web.navigator.navigator

class SimMidiDevices : MidiDevices {
    override suspend fun listTransmitters(): List<MidiPort> = emptyList()
}

class BrowserMidiDevices : MidiDevices {
    private lateinit var midiAccess: MIDIAccess;

    init {
        navigator.asDynamic().requestMIDIAccess().then {midiAccessResult: MIDIAccess ->
            midiAccess = midiAccessResult
            midiAccessResult
        }
    }

    override suspend fun listTransmitters(): List<MidiPort> {
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

    class JsMidiTransmitterPort(
        val id: String,
        private val input: MIDIInput
    ) : MidiPort {
        val name: String
            get() = input.name!!
        val vendor: String
            get() = input.manufacturer!!
        val description: String
            get() = ""
        val version: String
            get() = input.version!!

//        private var transmitter: Transmitter? = null

        fun listen(callback: (MidiMessage) -> Unit) {
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

        fun close() {
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