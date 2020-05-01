package baaahs.glshaders

interface Plugin {
    val name: String

    fun matchUniformProvider(
        type: String,
        name: String,
        program: GlslProgram
    ): GlslProgram.UniformProvider?

    companion object {
        fun factoryOf(fn: (GlslProgram) -> GlslProgram.UniformProvider): UniformProviderFactory =
            object : UniformProviderFactory {
                override fun create(program: GlslProgram) = fn(program)
            }
    }

    interface UniformProviderFactory {
        fun create(program: GlslProgram): GlslProgram.UniformProvider
    }
}