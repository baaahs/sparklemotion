package baaahs.glsl

import baaahs.BaseShowPlayer
import baaahs.Gadget
import baaahs.RenderPlan
import baaahs.gl.GlContext
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.patch.AutoWirer
import baaahs.gl.patch.ContentType
import baaahs.gl.patch.LinkedPatch
import baaahs.model.ModelInfo
import baaahs.only
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
    private val dataFeeds: Map<DataSource, GlslProgram.DataFeed>

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
        dataFeeds = openShow.dataFeeds
        val openPatch = openShow.patches.only("patch")
        linkedPatch = autoWirer.buildPortDiagram(openPatch)
            .resolvePatch(ShaderChannel.Main, ContentType.ColorStream)
    }

    fun createRenderPlan(gl: GlContext): RenderPlan {
        linkedPatch ?: error("Couldn't build guru meditation error patch.")

        return RenderPlan(
            listOf(linkedPatch to linkedPatch.createProgram(gl, dataFeeds)),
            ActiveSet(emptyList())
        )
    }
}

private object FakeShowPlayer : BaseShowPlayer(Plugins.safe(), ModelInfo.Empty) {
    override val glContext: GlContext get() = error("not implemented")
    override fun <T : Gadget> registerGadget(id: String, gadget: T, controlledDataSource: DataSource?): Unit = error("not implemented")
    override fun <T : Gadget> useGadget(id: String): T = error("not implemented")
}
