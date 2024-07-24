package baaahs.plugin.midi

import baaahs.plugin.PluginContext
import baaahs.util.globalLaunch

internal actual fun createMidiSystem(pluginContext: PluginContext): MidiSystem =
    JsMidiSystem(pluginContext.clock).also {
        globalLaunch {
            it.start()
        }
    }
