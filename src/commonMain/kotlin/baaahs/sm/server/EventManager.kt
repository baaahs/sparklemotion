package baaahs.sm.server

import baaahs.midi.MidiDevice
import baaahs.midi.MidiDevices
import baaahs.midi.MidiEvent
import baaahs.midi.MidiGateway
import baaahs.plugin.Plugins
import baaahs.rpc.RpcEndpoint

class EventManager(
    private val midiDevices: MidiDevices,
    private val pubSub: RpcEndpoint,
    private val plugins: Plugins
) {
    val midiGatewayHandler = object : MidiGateway {
        override suspend fun receivedEvent(midiDevice: MidiDevice, midiEvent: MidiEvent) {
            TODO("not implemented")
        }

        override suspend fun deviceOffline(midiDevice: MidiDevice) {
            TODO("not implemented")
        }

    }
}