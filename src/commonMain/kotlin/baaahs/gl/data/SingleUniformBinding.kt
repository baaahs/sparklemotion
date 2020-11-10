package baaahs.gl.data

import baaahs.gl.glsl.GlslProgram
import baaahs.glsl.Uniform
import baaahs.show.DataSource
import baaahs.util.Logger

class SingleUniformBinding(
    glslProgram: GlslProgram,
    dataSource: DataSource,
    val id: String,
    override val feed: Feed,
    val setUniform: (Uniform) -> Unit
) : Binding {
    private val type: Any = dataSource.getType()
    private val varName = dataSource.getVarName(id)
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
        private val logger = Logger<SingleUniformBinding>()
    }
}