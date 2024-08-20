package baaahs.client

import baaahs.midi.MidiDevice
import baaahs.plugin.midi.MidiCommands
import baaahs.plugin.midi.MidiManager
import baaahs.util.Logger

class EventManager(
    private val midiManager: MidiManager
) {
    private val sliderListeners = mutableListOf<SliderListener>()
    private val deviceStates = mutableMapOf<MidiDevice, State>()

    init {
        midiManager.addEventListener { midiDevice, midiEvent ->
            val state = deviceStates.getOrPut(midiDevice) { State() }

            when (midiEvent.command) {
                MidiCommands.NOTE_ON -> { // 144
                    val channel = midiEvent.channel
                    val note = midiEvent.data1
                    val velocity = midiEvent.data2
                    if (velocity == 0) {
                        logger.debug {"EventManager: NOTE_OFF: $channel $note" }
                    } else {
                        logger.debug {"EventManager: NOTE_ON: $channel $note $velocity" }
                    }
                }
                MidiCommands.CONTROL_CHANGE -> { // 224
                    val channel = midiEvent.channel
                    val data1 = midiEvent.data1
                    val data2 = midiEvent.data2
                    logger.debug {"EventManager: CONTROL_CHANGE $channel $data1 $data2(data = ${midiEvent.data1} ${midiEvent.data2})" }
                    state.onControlChange(channel, data1, data2)
                }
                MidiCommands.PITCH_BEND -> { // 224
                    val channel = midiEvent.channel
                    val value = (midiEvent.data1 * 128 + midiEvent.data2).toFloat() / 0x3fff
                    logger.debug {"PITCH_BEND $channel $value (data = ${midiEvent.data1} ${midiEvent.data2})" }
                    sliderListeners.forEach { it.onSliderChange(channel, value) }
                }
                else -> {
                    logger.debug {"unknown MIDI event: $midiEvent from ${midiDevice.id}" }
                }
            }
        }
        logger.warn { "initialized" }
    }

    suspend fun start() {
        midiManager.start()
    }

    fun addSliderListener(listener: SliderListener) {
        sliderListeners.add(listener)
    }

    fun removeSliderListener(listener: SliderListener) {
        sliderListeners.remove(listener)
    }

    fun interface SliderListener {
        fun onSliderChange(channel: Int, value: Float)
    }

    class State {
        val controls = mutableMapOf<Int, Int>()

        fun onControlChange(channel: Int, data1: Int, data2: Int) {
            val controlChannel = data1
            val change = data2
        }
    }

    companion object {
        private val logger = Logger<EventManager>()
    }
}