package baaahs.gl.data

import baaahs.gl.glsl.GlslProgram
import baaahs.glsl.Uniform
import baaahs.show.Feed
import baaahs.util.Logger

class SingleUniformFeedContext(
    glslProgram: GlslProgram,
    feed: Feed,
    val id: String,
    val setUniform: (Uniform) -> Unit
) : ProgramFeedContext {
    private val type: Any = feed.getType()
    private val varName = feed.getVarName(id)
    private val uniformLocation = glslProgram.getUniform(varName)

    override val isValid: Boolean get() = uniformLocation != null

    override fun setOnProgram() {
        try {
            uniformLocation?.let { setUniform(it) }
        } catch (e: Exception) {
            logger.error(e) { "failed to set uniform $type $varName for $id" }
        }
    }

    companion object {
        private val logger = Logger<SingleUniformFeedContext>()
    }
}