package baaahs.sim.bridge

import baaahs.plugin.sound_analysis.AudioInput
import kotlinx.serialization.Serializable

@Serializable
data class BridgeAudioInput(
    override val id: String,
    override val title: String
) : AudioInput