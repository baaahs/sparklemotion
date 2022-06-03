package baaahs.midi

interface MidiDevices {
    suspend fun listTransmitters(): List<MidiTransmitter>
}

class NullMidiDevices : MidiDevices {
    override suspend fun listTransmitters(): List<MidiTransmitter> = emptyList()
}