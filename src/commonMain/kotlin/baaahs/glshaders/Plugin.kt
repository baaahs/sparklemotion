package baaahs.glshaders

import baaahs.ShowContext
import baaahs.glsl.GlslContext

interface Plugin {
    val packageName: String
    val name: String

    fun matchUniformProvider(
        name: String,
        uniformPort: Patch.UniformPortRef,
        showContext: ShowContext,
        glslContext: GlslContext
    ): GlslProgram.DataSourceProvider?
}