package baaahs.plugin.midi

import baaahs.midi.MidiDevice
import baaahs.util.Clock
import baaahs.util.Logger
import js.objects.jso
import js.promise.catch
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
        navigator.requestMIDIAccess(jso { sysex = true }).then { midiAccess ->
            logger.info { "Got MIDI access." }
            midiAccess.onstatechange = { event ->
                val port = event.port
                logger.info { "Found MIDI input during subsequent update: ${port?.id}" }
                if (port is MIDIInput) {
                    receivedPortUpdate(port.id, port)
                }
            }
            midiAccess.inputs.forEach { midiInput, key ->
                console.log("midiInput:", midiInput, "key:", "key")
                logger.info { "Found MIDI input during initial request: $key, ${midiInput.id}" }
                receivedPortUpdate(key, midiInput)
            }

            logger.info { "Started." }
        }.catch { e ->
            logger.error(e) { "Error getting MIDI access." }
        }
    }

    private fun receivedPortUpdate(key: String, midiInput: MIDIInput) {
        knownInputs.add(JsMidiPort(midiInput))
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

            port.onmidimessage = ::onMidiMessage
            port.onstatechange = ::onStateChange

            port.open().then {
                logger.info { "Opened ${port.id} ${port.name} (${port.type}, ${port.state})." }
            }.catch {
                logger.info { "Error opening ${port.id} ${port.name} (${port.type}, ${port.state})." }
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