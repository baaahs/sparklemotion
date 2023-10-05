package baaahs

import baaahs.plugin.Plugin
import baaahs.plugin.beatlink.BeatLinkPlugin
import baaahs.plugin.midi.MidiPlugin
import baaahs.plugin.sound_analysis.SoundAnalysisPlugin
import baaahs.plugin.webcam.VideoInPlugin

object Pluggables {
    val plugins = listOf<Plugin<*>>(
        BeatLinkPlugin,
        MidiPlugin,
        SoundAnalysisPlugin,
        VideoInPlugin
    )
}