package baaahs.glsl

import baaahs.BaseShowPlayer
import baaahs.Gadget
import baaahs.fixtures.DeviceType
import baaahs.gl.patch.AutoWirer
import baaahs.gl.patch.LinkedPatch
import baaahs.gl.patch.PatchResolver
import baaahs.model.ModelInfo
import baaahs.only
import baaahs.plugin.Plugins
import baaahs.show.DataSource
import baaahs.show.ShaderChannel
import baaahs.show.live.ShowOpener
import baaahs.show.mutable.MutableShow
import baaahs.show.mutable.ShowBuilder

class GuruMeditationError(deviceType: DeviceType) {
    private val shader = deviceType.errorIndicatorShader
    val linkedPatch: LinkedPatch

    init {
        val plugins = Plugins.safe(Plugins.dummyContext)
        val autoWirer = AutoWirer(plugins)
        val showBuilder = ShowBuilder()
        val mutablePatch = autoWirer.autoWire(shader)
            .acceptSuggestedLinkOptions()
            .resolve()
        val show = MutableShow("error").apply {
            addPatch(mutablePatch)
        }.build(showBuilder)

        val showPlayer = FakeShowPlayer(plugins)
        val openShow = ShowOpener(autoWirer.glslAnalyzer, show, showPlayer).openShow()
        val openPatch = openShow.patches.only("patch")
        linkedPatch = PatchResolver.buildPortDiagram(openShow.allDataSources, openPatch)
            .resolvePatch(ShaderChannel.Main, deviceType.resultContentType)
            ?: error("Couldn't build guru meditation error patch.")
    }
}

private class FakeShowPlayer(plugins: Plugins) : BaseShowPlayer(plugins, ModelInfo.Empty) {
    override fun <T : Gadget> registerGadget(id: String, gadget: T, controlledDataSource: DataSource?): Unit = error("not implemented")
    override fun <T : Gadget> useGadget(id: String): T = error("not implemented")
}
