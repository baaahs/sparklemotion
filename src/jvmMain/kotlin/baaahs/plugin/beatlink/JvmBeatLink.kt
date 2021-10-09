package baaahs.plugin.beatlink

import baaahs.plugin.PluginContext

internal actual fun createServerBeatSource(pluginContext: PluginContext): BeatSource =
    BeatLinkBeatSource(pluginContext.clock).also { it.start() }
