package baaahs.plugin.sound_analysis

import baaahs.plugin.PluginContext

internal actual fun createServerSoundAnalyzer(pluginContext: PluginContext): SoundAnalyzer =
    JvmSoundAnalyzer(pluginContext.clock)