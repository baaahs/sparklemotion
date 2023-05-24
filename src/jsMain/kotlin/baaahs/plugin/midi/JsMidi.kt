package baaahs.plugin.midi

import baaahs.plugin.PluginContext

internal actual fun createServerMidiSource(pluginContext: PluginContext): MidiSource =
    error("baaahs.plugin.midi.createServerMidiSource() not implemented in JS")
