package baaahs.midi

import baaahs.PubSub
import baaahs.getBang
import baaahs.plugin.Plugins
import baaahs.util.globalLaunch
import kotlinx.coroutines.Job
import kotlinx.serialization.Serializable

class RemoteMidiDevices(
    pubSub: PubSub.Client,
    plugins: Plugins
) : MidiDevices {
    private val midiCommandPorts = MidiCommandPorts(plugins)
    private val listTransmittersCommand = pubSub.openCommandChannel(midiCommandPorts.listTransmittersCommandPort)
    private val listenToTransmitterCommand = pubSub.openCommandChannel(midiCommandPorts.listenToTransmitterCommandPort)
    private val closeTransmitterCommand = pubSub.openCommandChannel(midiCommandPorts.closeTransmitterCommandPort)

    private val listeners = mutableMapOf<String, (MidiMessage) -> Unit>()
    init {
        pubSub.listenOnCommandChannel(midiCommandPorts.midiEventCommandPort) { command ->
            globalLaunch {
                listeners.getBang(command.transmitterId, "MIDI listener").invoke(command.midiMessage)
            }
        }
    }

    override suspend fun listTransmitters(): List<MidiTransmitter> =
        listTransmittersCommand.send(ListTransmittersCommand())
            .map { RemoteMidiTransmitter(it) }

    @Serializable
    class ListTransmittersCommand

    @Serializable
    data class RemoteMidiDeviceInfo(
        val id: String,
        val name: String,
        val vendor: String,
        val description: String,
        val version: String
    ) {
        constructor(midiTransmitter: MidiTransmitter) : this(
            midiTransmitter.id,
            midiTransmitter.name,
            midiTransmitter.vendor,
            midiTransmitter.description,
            midiTransmitter.version
        )
    }

    inner class RemoteMidiTransmitter(
        private val deviceInfo: RemoteMidiDeviceInfo,
    ) : MidiTransmitter {
        override val id get() = deviceInfo.id
        override val name get() = deviceInfo.name
        override val vendor get() = deviceInfo.vendor
        override val description get() = deviceInfo.description
        override val version get() = deviceInfo.version

        private var job: Job? = null

        override fun listen(callback: (MidiMessage) -> Unit ) {
            job = globalLaunch {
                listeners[id] = callback
                listenToTransmitterCommand.send(ListenToTransmitterCommand(id))
            }
        }

        override fun close() {
            job?.let {
                it.cancel()
                globalLaunch {
                    closeTransmitterCommand.send(CloseTransmitterCommand(id))
                }
            }
        }
    }

    @Serializable
    class ListenToTransmitterCommand(
        val id: String
    )

    @Serializable
    class MidiEventCommand(
        val transmitterId: String,
        val channel: Int,
        val command: Int,
        val data1: Int,
        val data2: Int
    ) {
        val midiMessage get() = MidiMessage(channel, command, data1, data2)
    }

    @Serializable
    class CloseTransmitterCommand(
        val id: String
    )

}