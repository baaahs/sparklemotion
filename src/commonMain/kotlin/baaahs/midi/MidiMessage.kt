package baaahs.midi

import kotlinx.serialization.Serializable

@Serializable
data class MidiMessage(
    val channel: Int,
    val command: Int,
    val data1: Int,
    val data2: Int
)