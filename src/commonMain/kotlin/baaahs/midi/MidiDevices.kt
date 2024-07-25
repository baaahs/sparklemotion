package baaahs.midi

import baaahs.rpc.Service

@Service
interface MidiDevices {
    suspend fun listTransmitters(): List<MidiPort>

    companion object {
        val IMPL by lazy { MidiDevices.getImpl("pinky/midiDevices") }
    }
}

//class MidiManager(
//    rpcEndpoint: RpcEndpoint
//) : MidiDevices {
//    val midiDevicesService = MidiDevices.IMPL.createReceiver(rpcEndpoint, object : MidiDevices {
//        override suspend fun listTransmitters(): List<MidiPort> {
//            TODO("not implemented")
//        }
//    })
//}
class NullMidiDevices : MidiDevices {
    override suspend fun listTransmitters(): List<MidiPort> = emptyList()
}