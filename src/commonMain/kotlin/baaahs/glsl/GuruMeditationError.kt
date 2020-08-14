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
import baaahs.show.Shader
import baaahs.show.ShaderChannel
import baaahs.show.ShaderType
import baaahs.show.live.ShowOpener
import baaahs.show.mutable.MutableShow
import baaahs.show.mutable.ShowBuilder

object GuruMeditationError {
    private val shader = Shader(
        "Ω Guru Meditation Error Ω",
        ShaderType.Paint,
        /**language=glsl*/
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

    private val fakeShowPlayer = object : BaseShowPlayer(Plugins.safe(), ModelInfo.Empty) {
        override val glContext: GlContext get() = error("not implemented")
        override fun <T : Gadget> createdGadget(id: String, gadget: T): Unit = error("not implemented")
        override fun <T : Gadget> useGadget(id: String): T = error("not implemented")
    }

    private val linkedPatch: LinkedPatch?
    private val dataFeeds: Map<String, GlslProgram.DataFeed>

    init {
        val autoWirer = AutoWirer(Plugins.safe())
        val showBuilder = ShowBuilder()
        val show = MutableShow("error").apply {
            addPatch(autoWirer.autoWire(shader).resolve())
        }.build(showBuilder)

        @Suppress("CAST_NEVER_SUCCEEDS")
        val openShow = ShowOpener(show, fakeShowPlayer).openShow()
        dataFeeds = openShow.dataFeeds
        val openPatch = openShow.patches.only("patch")
        linkedPatch = autoWirer.buildPortDiagram(openPatch)
            .resolvePatch(ShaderChannel.Main, ContentType.ColorStream)
    }

    fun createRenderPlan(gl: GlContext): RenderPlan {
        linkedPatch ?: error("Couldn't build guru meditation error patch.")

        return RenderPlan(
            listOf(
                linkedPatch to linkedPatch.createProgram(gl, dataFeeds)
            )
        )
    }
}