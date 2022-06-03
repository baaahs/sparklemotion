package baaahs.midi

interface MidiTransmitter {
    /** A unique id for the device. */
    val id: String

    /** The device's name. */
    val name: String

    /** The name of the company who provides the device. */
    val vendor: String

    /** A description of the device. */
    val description: String

    /** Device version. */
    val version: String

    fun listen(callback: (MidiMessage) -> Unit)
    fun close()
}