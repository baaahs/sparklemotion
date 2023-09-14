package baaahs.gl.data

import baaahs.gl.GlContext
import baaahs.gl.glsl.GlslProgram
import baaahs.glsl.GlslUniform
import baaahs.show.Feed
import baaahs.util.Logger
import baaahs.util.RefCounted
import baaahs.util.RefCounter

fun <T: Any> Feed.singleUniformFeedContext(
    id: String,
    getValue: (oldValue: T?) -> T?
): FeedContext {
    var oldValue: T? = null
    return SingleUniformFeedContext(this, id) { uniform ->
        val newValue = getValue(oldValue)

        if (newValue != null && newValue != oldValue)
            uniform.set(newValue)

        oldValue = newValue
    }
}

internal class SingleUniformFeedContext(
    feed: Feed,
    private val id: String,
    private val setUniform: (GlslUniform) -> Unit
) : FeedContext, RefCounted by RefCounter() {
    private val type: Any = feed.getType()
    private val varName = feed.getVarName(id)

    override fun bind(gl: GlContext): EngineFeedContext = object : EngineFeedContext {
        override fun bind(glslProgram: GlslProgram): ProgramFeedContext {
            val uniform = glslProgram.getUniform(varName)
            if (uniform == null) {
                logger.info { "No such uniform $type $varName for $id." }
            }

            return object : ProgramFeedContext {
                override val isValid: Boolean get() = uniform != null

                override fun setOnProgram() {
                    try {
                        uniform?.let { setUniform(it) }
                    } catch (e: Exception) {
                        logger.error(e) { "Failed to set uniform $type $varName for $id." }
                    }
                }
            }
        }
    }

    companion object {
        private val logger = Logger<SingleUniformFeedContext>()
    }
}