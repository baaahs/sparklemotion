package baaahs

import baaahs.plugin.Plugin
import baaahs.plugin.beatlink.BeatLinkPlugin
import baaahs.plugin.sound_analysis.SoundAnalysisPlugin
import baaahs.plugin.webcam.VideoInPlugin
import baaahs.plugin.osc.OscPlugin

object Pluggables {
    val plugins = listOf<Plugin<*>>(
        OscPlugin,
        BeatLinkPlugin,
        SoundAnalysisPlugin,
        VideoInPlugin,
    )
}
