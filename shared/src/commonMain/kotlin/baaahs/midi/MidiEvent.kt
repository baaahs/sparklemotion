package baaahs.midi

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class MidiEvent(
    val instant: Instant,
    val channel: Int,
    val command: Int,
    val data1: Int,
    val data2: Int
)