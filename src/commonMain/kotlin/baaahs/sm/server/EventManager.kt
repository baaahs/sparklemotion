package baaahs.sm.server

import baaahs.PubSub
import baaahs.midi.MidiCommandPorts
import baaahs.midi.MidiDevices
import baaahs.midi.RemoteMidiDevices
import baaahs.plugin.Plugins

class EventManager(
    private val midiDevices: MidiDevices,
    private val pubSub: PubSub.Server,
    private val plugins: Plugins
) {
    private val commands = MidiCommandPorts(plugins)
    private val listTransmittersCommand = pubSub.listenOnCommandChannel(commands.listTransmittersCommandPort) {
        midiDevices.listTransmitters().map { RemoteMidiDevices.RemoteMidiDeviceInfo(it) }
    }

    private val listenToTransmitterCommand = pubSub.listenOnCommandChannel(commands.listenToTransmitterCommandPort) {

    }

    private val closeTransmitterCommand = pubSub.listenOnCommandChannel(commands.closeTransmitterCommandPort) {

    }

}