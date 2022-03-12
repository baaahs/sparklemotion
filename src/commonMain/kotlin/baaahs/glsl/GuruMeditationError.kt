package baaahs.glsl

import baaahs.BaseShowPlayer
import baaahs.Gadget
import baaahs.device.FixtureType
import baaahs.gl.RootToolchain
import baaahs.gl.Toolchain
import baaahs.gl.autoWire
import baaahs.gl.patch.LinkedPatch
import baaahs.gl.patch.PatchResolver
import baaahs.model.ModelInfo
import baaahs.only
import baaahs.plugin.Plugins
import baaahs.scene.SceneMonitor
import baaahs.show.DataSource
import baaahs.show.ShaderChannel
import baaahs.show.live.ShowOpener
import baaahs.show.mutable.MutableShow
import baaahs.show.mutable.ShowBuilder

class GuruMeditationError(fixtureType: FixtureType) {
    private val shader = fixtureType.errorIndicatorShader
    val linkedPatch: LinkedPatch

    init {
        val plugins = Plugins.safe(Plugins.dummyContext)
        val toolchain = RootToolchain(plugins)
        val showBuilder = ShowBuilder()
        val mutablePatch = toolchain.autoWire(shader)
            .acceptSuggestedLinkOptions()
            .confirm()
        val show = MutableShow("error").apply {
            addPatch(mutablePatch)
        }.build(showBuilder)

        val showPlayer = FakeShowPlayer(toolchain)
        val openShow = ShowOpener(toolchain, show, showPlayer).openShow()
        val openPatch = openShow.patches.only("patch")
        linkedPatch = PatchResolver.buildPortDiagram(openPatch)
            .resolvePatch(ShaderChannel.Main, fixtureType.resultContentType, openShow.allDataSources)
            ?: error("Couldn't build guru meditation error patch.")
    }
}

private class FakeShowPlayer(toolchain: Toolchain) : BaseShowPlayer(toolchain, SceneMonitor(ModelInfo.EmptyScene)) {
    override fun <T : Gadget> registerGadget(id: String, gadget: T, controlledDataSource: DataSource?): Unit = error("not implemented")
    override fun <T : Gadget> useGadget(id: String): T = error("not implemented")
}
