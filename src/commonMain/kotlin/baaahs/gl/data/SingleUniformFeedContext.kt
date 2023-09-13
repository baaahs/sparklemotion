package baaahs.gl.data

import baaahs.gl.GlContext
import baaahs.gl.glsl.GlslProgram
import baaahs.glsl.GlslUniform
import baaahs.show.Feed
import baaahs.util.Logger
import baaahs.util.RefCounted
import baaahs.util.RefCounter

class SingleUniformFeedContext(
    feed: Feed,
    val id: String,
    val setUniform: (GlslUniform) -> Unit
) : FeedContext, RefCounted by RefCounter() {
    private val type: Any = feed.getType()
    private val varName = feed.getVarName(id)

    override fun bind(gl: GlContext): EngineFeedContext = object : EngineFeedContext {
        override fun bind(glslProgram: GlslProgram): ProgramFeedContext {
            val uniform = glslProgram.getUniform(varName)

            return object : ProgramFeedContext {
                override val isValid: Boolean get() = uniform != null

                override fun setOnProgram() {
                    try {
                        uniform?.let { setUniform(it) }
                    } catch (e: Exception) {
                        logger.error(e) { "failed to set uniform $type $varName for $id" }
                    }
                }
            }
        }
    }

    companion object {
        private val logger = Logger<SingleUniformFeedContext>()
    }
}