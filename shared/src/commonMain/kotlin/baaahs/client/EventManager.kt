package baaahs.client

import baaahs.control.OpenSliderControl
import baaahs.gadgets.MidiStatus
import baaahs.gadgets.Slider
import baaahs.midi.MidiDevice
import baaahs.midi.MidiEvent
import baaahs.plugin.midi.MidiCommands
import baaahs.plugin.midi.MidiManager
import baaahs.scale
import baaahs.show.ShowProvider
import baaahs.show.live.ControlsInfo
import baaahs.util.Clock
import baaahs.util.Logger

class EventManager(
    private val midiManager: MidiManager,
    private val showProvider: ShowProvider,
    private val clock: Clock
) {
    val facade = Facade()
    private val deviceStates = mutableMapOf<MidiDevice, State>()
    private var hasExternalControllers: Boolean = false

    init {
        midiManager.addEventListener(::onMidiEvent)
        logger.warn { "initialized" }
    }

    suspend fun start() {
        midiManager.start()
        logger.info { "Started." }
    }

    private fun onMidiEvent(midiDevice: MidiDevice, midiEvent: MidiEvent) {
        if (!hasExternalControllers) {
            hasExternalControllers = true
            facade.notifyChanged()
        }

        val state = deviceStates.getOrPut(midiDevice) { State() }

        when (midiEvent.command) {
            MidiCommands.NOTE_ON -> { // 144
                val channel = midiEvent.channel
                val note = midiEvent.data1
                val velocity = midiEvent.data2
                if (velocity == 0) {
                    logger.debug { "EventManager: NOTE_OFF: $channel $note" }
                } else {
                    logger.debug { "EventManager: NOTE_ON: $channel $note $velocity" }
                }
            }

            MidiCommands.CONTROL_CHANGE -> { // 176
                val channel = midiEvent.channel
                val data1 = midiEvent.data1
                val data2 = midiEvent.data2
                logger.debug { "EventManager: CONTROL_CHANGE $channel $data1 $data2(data = ${midiEvent.data1} ${midiEvent.data2})" }
                state.onControlChange(channel, data1, data2)
            }

            MidiCommands.PITCH_BEND -> { // 224
                val channel = midiEvent.channel
                val value = (midiEvent.data1 * 128 + midiEvent.data2).toFloat() / 0x3fff
                logger.debug { "PITCH_BEND $channel $value (data = ${midiEvent.data1} ${midiEvent.data2})" }
                onSliderChange(channel, value)
            }

            else -> {
                logger.debug { "unknown MIDI event: $midiEvent from ${midiDevice.id}" }
            }
        }
    }

    private fun onSliderChange(channel: Int, value: Float) {
        val slider = findSliderForChannel(channel)
        slider?.latch?.maybeApplyChange(value, clock)
    }

    private fun findSliderForChannel(channel: Int): Slider? {
        val openShow = showProvider.openShow
        if (openShow != null) {
            val controlsInfo = openShow.getSnapshot().controlsInfo
            controlsInfo.midiChannelToControlMap[channel]?.let {
                return (it as? OpenSliderControl)?.slider
            }

            val visibleSliders = controlsInfo.visibleSliders()
            visibleSliders.map { it }

            val slider = try { visibleSliders[channel] } catch (e: Exception) { null }
            return slider?.slider
        } else return null
    }

    class State {
        val controls = mutableMapOf<Int, Int>()

        fun onControlChange(channel: Int, data1: Int, data2: Int) {
            val controlChannel = data1
            val change = data2
        }
    }

    inner class Facade : baaahs.ui.Facade() {
        val hasExternalControllers: Boolean =
            this@EventManager.hasExternalControllers
    }

    companion object {
        private val logger = Logger<EventManager>()

        fun ControlsInfo.visibleSliders(): List<OpenSliderControl> =
            orderedOnScreenControls
                .filterIsInstance<OpenSliderControl>()
    }
}

class MidiEventLatch(
    private val slider: Slider
) {
    private var lastUpdate: Float? = null

    fun maybeApplyChange(value: Float, clock: Clock) {
        val scaledValue = slider.domain.scale(value)
        val lastUpdate = this.lastUpdate
        when {
            lastUpdate == null -> { /* No-op. */ }
            lastUpdate == slider.position ->
                slider.position = scaledValue
            lastUpdate < slider.position && scaledValue >= slider.position ->
                slider.position = scaledValue
            lastUpdate > slider.position && scaledValue <= slider.position ->
                slider.position = scaledValue
            else -> { /* No-op. */ }
        }
        slider.midiStatus = MidiStatus(clock.now(), scaledValue)
        this.lastUpdate = scaledValue
    }

    fun reset() {
        lastUpdate = null
    }
}
