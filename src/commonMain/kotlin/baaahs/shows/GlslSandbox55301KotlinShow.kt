package baaahs.shows

import baaahs.Model
import baaahs.Show
import baaahs.ShowRunner
import baaahs.shaders.GlslSandbox55301Shader

object GlslSandbox55301KotlinShow : Show("GlslSandbox 55301 (kt)") {
    override fun createRenderer(model: Model<*>, showRunner: ShowRunner): Renderer {
        val shader = GlslSandbox55301Shader()
        showRunner.allSurfaces.map { showRunner.getShaderBuffer(it, shader) }

        return object : Renderer {
            override fun nextFrame() {
            }
        }
    }
}
