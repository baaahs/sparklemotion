package baaahs.plugin.midi

import baaahs.plugin.PluginContext

internal actual fun createServerMidiSource(pluginContext: PluginContext): MidiSource =
    JsMidiSource(pluginContext.clock).also { it.start() }
