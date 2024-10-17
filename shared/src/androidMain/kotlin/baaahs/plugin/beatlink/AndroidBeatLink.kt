package baaahs.plugin.beatlink

import baaahs.plugin.PluginContext
import baaahs.util.Clock

internal actual fun createServerBeatSource(pluginContext: PluginContext): BeatSource =
    BeatLinkBeatSource(pluginContext.clock).also { it.start() }

class BeatLinkBeatSource(private val clock: Clock) : BeatSource {
    fun start() {}

    override fun addListener(listener: BeatLinkListener) {
    }

    override fun removeListener(listener: BeatLinkListener) {
    }
}