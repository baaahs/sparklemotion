package baaahs.plugin.beatlink

import baaahs.plugin.ArgsProvider
import baaahs.plugin.PluginContext
import com.xenomachina.argparser.default

actual fun provideArgs(argsProvider: ArgsProvider): BeatLinkPluginArgs {
    return JvmBeatLinkPluginArgs(argsProvider)
}

class JvmBeatLinkPluginArgs(argsProvider: ArgsProvider) : BeatLinkPluginArgs {
    private val parser = argsProvider.parser

    override val enableBeatLink by parser.flagging("Enable beat detection").default(true)
}

actual fun createPlatformBeatSource(pluginContext: PluginContext, args: BeatLinkPluginArgs): BeatSource {
    return BeatLinkBeatSource(pluginContext.clock)
}
