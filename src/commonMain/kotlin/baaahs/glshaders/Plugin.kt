package baaahs.glshaders

import baaahs.ShowContext

interface Plugin {
    val packageName: String
    val name: String

    fun matchUniformProvider(
        name: String,
        uniformPort: Patch.UniformPort,
        program: GlslProgram,
        showContext: ShowContext
    ): GlslProgram.UniformProvider?

    interface UniformProviderFactory {
        fun create(program: GlslProgram): GlslProgram.UniformProvider
    }
}