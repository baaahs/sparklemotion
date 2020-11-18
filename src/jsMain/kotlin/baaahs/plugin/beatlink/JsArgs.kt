package baaahs.plugin.beatlink

import baaahs.plugin.ArgsProvider
import baaahs.plugin.PluginContext

actual fun provideArgs(argsProvider: ArgsProvider): BeatLinkPluginArgs =
    object : BeatLinkPluginArgs {
        override val enableBeatLink: Boolean
            get() = true
    }

actual fun createPlatformBeatSource(pluginContext: PluginContext, args: BeatLinkPluginArgs): BeatSource {
    return BeatSource.None
}
