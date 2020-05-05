package baaahs.glshaders

import baaahs.ShowContext

interface Plugin {
    val packageName: String
    val name: String

    fun matchUniformProvider(
        name: String,
        uniformPort: Patch.UniformPortRef,
        program: GlslProgram,
        showContext: ShowContext
    ): GlslProgram.UniformProvider?
}