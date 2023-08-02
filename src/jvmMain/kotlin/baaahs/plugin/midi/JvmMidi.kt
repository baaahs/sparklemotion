package baaahs.plugin.midi

import baaahs.plugin.PluginContext

internal actual fun createMidiSystem(pluginContext: PluginContext): MidiSystem =
    JvmMidiSystem()
