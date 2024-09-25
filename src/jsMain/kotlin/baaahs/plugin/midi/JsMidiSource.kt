package baaahs.plugin.midi

import baaahs.midi.MidiDevice
import baaahs.util.Clock
import baaahs.util.Logger
import baaahs.util.globalLaunch
import js.objects.jso
import web.events.EventHandler
import web.midi.MIDIConnectionEvent
import web.midi.MIDIInput
import web.midi.MIDIMessageEvent
import web.navigator.navigator

/**
 * Listens to a midi controller
 *
 * We pick the midi to sync shows to based on the following:
 * 1) Hardcoded name of device on boot
 */
class JsMidiSource(
    clock: Clock
) :  MidiSource("JS MIDI Source", clock) {
    private val knownInputs = mutableListOf<JsMidiPort>()

    override suspend fun start() {
        logger.info { "Starting MIDI…" }

        val names = arrayOf(
            "iCON iControls V2.04 Port 1",
            "KOMPLETE KONTROL S88 MK2 Port 1",
            "Keystation 61 MK3 (USB MIDI)"
        )

        if (navigator::requestMIDIAccess == undefined) {
            logger.error { "No MIDI access available." }
            return
        }
        try {
            navigator.requestMIDIAccess(jso { sysex = true }).let { midiAccess ->
                logger.info { "Got MIDI access." }
                midiAccess.onstatechange = EventHandler { event ->
                    val port = event.port
                    logger.info { "Found MIDI input during subsequent update: ${port?.id}" }
                    if (port is MIDIInput) {
                        globalLaunch { receivedPortUpdate(port.id, port) }
                    }
                }
                midiAccess.inputs.forEach { midiInput, key ->
                    console.log("midiInput:", midiInput, "key:", "key")
                    logger.info { "Found MIDI input during initial request: $key, ${midiInput.id}" }
                    globalLaunch { receivedPortUpdate(key, midiInput) }
                }

                logger.info { "Started." }
            }
        } catch (e: Exception) {
            logger.error(e) { "Error getting MIDI access." }
        }
    }

    private suspend fun receivedPortUpdate(key: String, midiInput: MIDIInput) {
        knownInputs.add(JsMidiPort(midiInput).apply { open() })
        logger.info { "Added Input: $key, $midiInput" }
//        TODO("not implemented")
    }


    inner class JsMidiPort(private val port: MIDIInput) : MidiPort(
        MidiDevice(
            port.id,
            port.name ?: "Unknown",
            port.manufacturer ?: "Unknown",
            port.name ?: "Unknown",
            port.version ?: "Unknown"
        )
    ) {
        init {
            logger.info { "Have MIDI device: $midiDevice" }

            port.onmidimessage = EventHandler { e -> onMidiMessage(e) }
            port.onstatechange = EventHandler { e -> onStateChange(e) }
        }

        suspend fun open() {
            try {
                port.open()
                logger.info { "Opened ${port.id} ${port.name} (${port.type}, ${port.state})." }
            } catch (e: Exception) {
                logger.error(e) { "Error opening ${port.id} ${port.name} (${port.type}, ${port.state})." }
            }
        }

        private fun onMidiMessage(messageEvent: MIDIMessageEvent) {
            console.log("MIDI message: ", messageEvent)
            val data = messageEvent.data!!
            val status = data[0].toInt() and 0xff
            val data1 = try { data[1].toInt() and 0xff } catch (e: Exception) { 0 }
            val data2 = try { data[2].toInt() and 0xff } catch (e: Exception) { 0 }
            onEvent(status, data1, data2)
        }

        private fun onStateChange(midiConnectionEvent: MIDIConnectionEvent) {
            logger.info { "MIDI state change: ${midiConnectionEvent.port?.state}" }
        }
    }

    companion object {
        private val logger = Logger<JsMidiSource>()
    }
}