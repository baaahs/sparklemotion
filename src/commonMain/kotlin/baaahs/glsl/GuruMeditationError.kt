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
import baaahs.plugin.Plugins
import baaahs.show.DataSource
import baaahs.show.Shader
import baaahs.show.ShaderChannel
import baaahs.show.ShaderType
import baaahs.show.live.ShowOpener
import baaahs.show.mutable.BuildContext
import baaahs.show.mutable.MutableShow

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

    private val linkedPatch: LinkedPatch?
    private val showPlayer = FakeShowPlayer()

    init {
        val autoWirer = AutoWirer(Plugins.safe())
        val show = MutableShow("error").apply {
            addPatch(autoWirer.autoWire(shader).resolve())
        }.build(BuildContext())

        val showContext = ShowOpener(autoWirer.glslAnalyzer, show)
        showContext.dataSources.values.forEach { showPlayer.openDataFeed(it.id, it) }
        val patch = show.patches.first()
        linkedPatch = autoWirer.buildPortDiagram(showContext, patch)
            .resolvePatch(ShaderChannel.Main, ContentType.ColorStream)
    }

    fun createRenderPlan(gl: GlContext): RenderPlan {
        linkedPatch ?: error("Couldn't build guru meditation error patch.")

        return RenderPlan(
            listOf(
                linkedPatch to linkedPatch.createProgram(gl, showPlayer)
            )
        )
    }
}

private class FakeShowPlayer : BaseShowPlayer(Plugins.safe(), ModelInfo.Empty) {
    override val glContext: GlContext get() = error("not implemented")
    override fun <T : Gadget> createdGadget(id: String, gadget: T): Unit = error("not implemented")
    override fun <T : Gadget> useGadget(id: String): T = error("not implemented")

}
