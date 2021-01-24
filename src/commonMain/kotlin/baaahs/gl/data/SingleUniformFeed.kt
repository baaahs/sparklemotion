package baaahs.gl.data

import baaahs.gl.glsl.GlslProgram
import baaahs.gl.render.RenderTarget
import baaahs.glsl.Uniform
import baaahs.show.DataSource
import baaahs.show.UpdateMode
import baaahs.util.Logger

class SingleUniformFeed(
    glslProgram: GlslProgram,
    dataSource: DataSource,
    val id: String,
    val setUniform: (Uniform) -> Unit
) : ProgramFeed {
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
        private val logger = Logger<SingleUniformFeed>()
    }
}

class SingleUniformFixtureFeed(
    glslProgram: GlslProgram,
    dataSource: DataSource,
    val id: String,
    val setUniform: (Uniform, RenderTarget) -> Unit
) : ProgramFeed {
    override val updateMode: UpdateMode get() = UpdateMode.PER_FIXTURE

    private val type: Any = dataSource.getType()
    private val varName = dataSource.getVarName(id)
    private val uniformLocation = glslProgram.getUniform(varName)

    override val isValid: Boolean get() = uniformLocation != null

    override fun setOnProgram(renderTarget: RenderTarget) {
        try {
            uniformLocation?.let { setUniform(it, renderTarget) }
        } catch (e: Exception) {
            logger.error(e) { "failed to set uniform $type $varName for $id" }
        }
    }

    companion object {
        private val logger = Logger<SingleUniformFeed>()
    }
}