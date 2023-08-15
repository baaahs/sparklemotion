package baaahs.midi

import baaahs.PubSub
import baaahs.plugin.Plugins
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer

class MidiCommandPorts(plugins: Plugins) {
    val listTransmittersCommandPort = PubSub.CommandPort(
        "/midi/listTransmitters",
        RemoteMidiDevices.ListTransmittersCommand.serializer(),
        ListSerializer(RemoteMidiDevices.RemoteMidiDeviceInfo.serializer()),
        plugins.serialModule
    )
    val listenToTransmitterCommandPort = PubSub.CommandPort(
        "/midi/listenToTransmitter", RemoteMidiDevices.ListenToTransmitterCommand.serializer(), Unit.serializer(),
        plugins.serialModule
    )
    val midiEventCommandPort = PubSub.CommandPort(
        "/midi/event", RemoteMidiDevices.MidiEventCommand.serializer(), Unit.serializer(),
        plugins.serialModule
    )
    val closeTransmitterCommandPort = PubSub.CommandPort(
        "/midi/closeTransmitter", RemoteMidiDevices.CloseTransmitterCommand.serializer(), Unit.serializer(),
        plugins.serialModule
    )
}