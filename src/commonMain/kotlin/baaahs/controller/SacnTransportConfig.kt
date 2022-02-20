package baaahs.controller

import baaahs.fixtures.TransportConfig
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("SACN")
data class SacnTransportConfig(
    val startChannel: Int,
    val endChannel: Int,
    val componentsStartAtUniverseBoundaries: Boolean = true
) : TransportConfig