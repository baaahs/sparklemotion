package baaahs.midi

import kotlinx.serialization.Serializable

@Serializable
data class MidiDevice(
    val id: String,
    val name: String,
    val vendor: String,
    val description: String,
    val version: String
)