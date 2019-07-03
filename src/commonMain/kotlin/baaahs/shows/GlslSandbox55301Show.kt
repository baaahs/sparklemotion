package baaahs.shows

import baaahs.SheepModel
import baaahs.Show
import baaahs.ShowRunner
import baaahs.shaders.GlslSandbox55301Shader

object GlslSandbox55301Show : Show("GlslSandbox 55301") {
    override fun createRenderer(sheepModel: SheepModel, showRunner: ShowRunner): Renderer {
        val shader = GlslSandbox55301Shader()
        showRunner.allSurfaces.map { showRunner.getShaderBuffer(it, shader) }

        return object : Renderer {
            override fun nextFrame() {
            }
        }
    }
}