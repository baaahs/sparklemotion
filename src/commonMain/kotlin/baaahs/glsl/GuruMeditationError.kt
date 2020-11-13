package baaahs.glsl

import baaahs.*
import baaahs.fixtures.PixelArrayDevice
import baaahs.gl.data.Feed
import baaahs.gl.patch.AutoWirer
import baaahs.gl.patch.ContentType
import baaahs.gl.patch.LinkedPatch
import baaahs.gl.patch.PatchResolver
import baaahs.gl.render.RenderManager
import baaahs.model.ModelInfo
import baaahs.plugin.Plugins
import baaahs.show.DataSource
import baaahs.show.Shader
import baaahs.show.ShaderChannel
import baaahs.show.ShaderType
import baaahs.show.live.ActiveSet
import baaahs.show.live.ShowOpener
import baaahs.show.mutable.MutableShow
import baaahs.show.mutable.ShowBuilder

object GuruMeditationError {
    private val shader = Shader(
        "Ω Guru Meditation Error Ω",
        ShaderType.Paint,
        /**language=glsl*/
        """
            uniform float time;
            void main() {
                gl_FragColor = (mod(time, 2.) < 1.)
                    ? vec4(.75, 0., 0., 1.)
                    : vec4(.25, 0., 0., 1.);
            }
        """.trimIndent()
    )

    private val linkedPatch: LinkedPatch?
    private val feeds: Map<DataSource, Feed>

    init {
        val autoWirer = AutoWirer(Plugins.safe())
        val showBuilder = ShowBuilder()
        val mutablePatch = autoWirer.autoWire(shader)
            .acceptSuggestedLinkOptions()
            .resolve()
        val show = MutableShow("error").apply {
            addPatch(mutablePatch)
        }.build(showBuilder)

        @Suppress("CAST_NEVER_SUCCEEDS")
        val openShow = ShowOpener(autoWirer.glslAnalyzer, show, FakeShowPlayer).openShow()
        feeds = openShow.feeds
        val openPatch = openShow.patches.only("patch")
        linkedPatch = PatchResolver.buildPortDiagram(openPatch)
            .resolvePatch(ShaderChannel.Main, ContentType.ColorStream)
    }

    fun createRenderPlan(renderManager: RenderManager): RenderPlan {
        linkedPatch ?: error("Couldn't build guru meditation error patch.")

        // TODO: Should maybe display error state for whatever device type failed? Or everywhere?
        return RenderPlan(
            mapOf(
                PixelArrayDevice to
                        listOf(linkedPatch to linkedPatch.createProgram(renderManager, PixelArrayDevice) { _, dataSource ->
                            feeds.getBang(dataSource, "data feed")
                        })
            ),
            ActiveSet(emptyList())
        )
    }
}

private object FakeShowPlayer : BaseShowPlayer(Plugins.safe(), ModelInfo.Empty) {
    override fun <T : Gadget> registerGadget(id: String, gadget: T, controlledDataSource: DataSource?): Unit = error("not implemented")
    override fun <T : Gadget> useGadget(id: String): T = error("not implemented")
}
