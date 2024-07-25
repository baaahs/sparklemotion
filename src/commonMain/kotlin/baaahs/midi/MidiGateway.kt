package baaahs.midi

import baaahs.rpc.Service
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Service
interface MidiGatewayApi {
    suspend fun deviceOnline(midiDeviceId: Int, midiDevice: MidiDevice)
    suspend fun receivedEvent(midiDeviceId: Int, midiEvent: MidiEvent)
    suspend fun deviceOffline(midiDeviceId: Int)

    companion object {
        val IMPL by lazy { MidiGateway.getImpl("pinky/midiGateway") }
    }
}

@Service
interface MidiGateway {
    suspend fun receivedEvent(midiDevice: MidiDevice, midiEvent: MidiEvent)
    suspend fun deviceOffline(midiDevice: MidiDevice)

    companion object {
        val IMPL by lazy { MidiGateway.getImpl("pinky/midiGateway") }
    }
}

class MidiGatewayServer : MidiGateway {
    override suspend fun receivedEvent(midiDevice: MidiDevice, midiEvent: MidiEvent) {
        TODO("not implemented")
    }

    override suspend fun deviceOffline(midiDevice: MidiDevice) {
        TODO("not implemented")
    }
}

class MidiGatewayClient(
    private val midiGateway: MidiGateway,
    private val scope: CoroutineScope
) {
    fun receivedEvent(midiDevice: MidiDevice, midiEvent: MidiEvent) {
        scope.launch {
            midiGateway.receivedEvent(midiDevice, midiEvent)
        }
    }
}

@Serializable
data class MidiDevice(
    val id: String,
    val name: String,
    val vendor: String,
    val description: String,
    val version: String
)

@Serializable
data class MidiEvent(
    val instant: Instant,
    val channel: Int,
    val command: Int,
    val data1: Int,
    val data2: Int
)