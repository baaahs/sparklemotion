package baaahs.plugin.beatlink

import baaahs.plugin.PluginContext
import baaahs.sim.BridgeClient

internal actual fun createServerBeatSource(pluginContext: PluginContext): BeatSource =
    error("baaahs.plugin.beatlink.createServerBeatSource() not implemented in JS")

internal actual fun createBridgeBeatSource(serverUrl: String): BeatSource =
    BridgeClient(serverUrl).beatSource
