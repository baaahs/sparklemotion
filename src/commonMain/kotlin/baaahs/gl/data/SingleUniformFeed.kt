package baaahs.gl.data

import baaahs.glsl.Uniform
import baaahs.show.DataSource
import baaahs.util.Logger

class SingleUniformFeed<T: Uniform<*>>(
    val id: String,
    dataSource: DataSource,
    findUniform: (String) -> T,
    private val setUniform: (T) -> Unit
) : ProgramFeed {
    private val type: Any = dataSource.getType()
    private val varName = dataSource.getVarName(id)
    private val uniform = findUniform(varName)

    override val isValid: Boolean get() = uniform.exists

    override fun setOnProgram() {
        try {
            setUniform(uniform)
        } catch (e: Exception) {
            logger.error(e) { "failed to set uniform $type $varName for $id" }
        }
    }

    companion object {
        private val logger = Logger<SingleUniformFeed<*>>()
    }
}