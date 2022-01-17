package baaahs

import baaahs.plugin.Plugin
import baaahs.plugin.beatlink.BeatLinkPlugin
import baaahs.plugin.sound_analysis.SoundAnalysisPlugin

object Pluggables {
    val plugins = listOf<Plugin<*>>(
        BeatLinkPlugin,
        SoundAnalysisPlugin
    )
}