@file:Suppress("FINAL_UPPER_BOUND")

package baaahs.gl.data

import baaahs.Color
import baaahs.geom.*
import baaahs.gl.GlContext
import baaahs.gl.glsl.GlslProgram
import baaahs.glsl.GlslUniform
import baaahs.show.Feed
import baaahs.util.Logger
import baaahs.util.RefCounted
import baaahs.util.RefCounter

// Somewhat delicate code follows, proceed with caution.
//
// Kotlin/JS can't discriminate at runtime between Int and Float values,
// so we can't funnel them through a single GlslUniform.set(Any) method.

fun <T: Boolean> Feed.singleUniformFeedContext(id: String, getValue: (oldValue: T?) -> T?) =
    bindContext(id, getValue) { set(it) }

fun <T: Int> Feed.singleUniformFeedContext(id: String, getValue: (oldValue: T?) -> T?) =
    bindContext(id, getValue) { set(it) }
fun <T: Vector2I> Feed.singleUniformFeedContext(id: String, getValue: (oldValue: T?) -> T?) =
    bindContext(id, getValue) { set(it) }
// <T: Vector3I>fun Feed.singleUniformFeedContext(id: String, getValue: (oldValue: T?) -> T?) =
//    innerSingleUniformFeedContext(id, getValue) { set(it) }
// <T: Vector4I>fun Feed.singleUniformFeedContext(id: String, getValue: (oldValue: T?) -> T?) =
//    innerSingleUniformFeedContext(id, getValue) { set(it) }

 fun <T: Float> Feed.singleUniformFeedContext(id: String, getValue: (oldValue: T?) -> T?) =
    bindContext(id, getValue) { set(it) }
fun <T: Vector2F> Feed.singleUniformFeedContext(id: String, getValue: (oldValue: T?) -> T?) =
    bindContext(id, getValue) { set(it) }
fun <T: Vector3F> Feed.singleUniformFeedContext(id: String, getValue: (oldValue: T?) -> T?) =
    bindContext(id, getValue) { set(it) }
fun <T: Vector4F> Feed.singleUniformFeedContext(id: String, getValue: (oldValue: T?) -> T?) =
    bindContext(id, getValue) { set(it) }

fun <T: Matrix4F> Feed.singleUniformFeedContext(id: String, getValue: (oldValue: T?) -> T?) =
    bindContext(id, getValue) { set(it) }
fun <T: EulerAngle> Feed.singleUniformFeedContext(id: String, getValue: (oldValue: T?) -> T?) =
    bindContext(id, getValue) { set(it) }
fun <T: GlContext.TextureUnit> Feed.singleUniformFeedContext(id: String, getValue: (oldValue: T?) -> T?) =
    bindContext(id, getValue) { set(it) }

fun <T: Color> Feed.singleUniformFeedContext(id: String, getValue: (oldValue: T?) -> T?) =
    bindContext(id, getValue) { set(it.redF, it.greenF, it.blueF, it.alphaF) }

private fun <T: Any> Feed.bindContext(
    id: String,
    getValue: (oldValue: T?) -> T?,
    setUniform: GlslUniform.(value: T) -> Unit
): FeedContext {
    var oldValue: T? = null
    return SingleUniformFeedContext(this, id) { uniform ->
        val value = getValue(oldValue)

        if (value != null && value != oldValue) {
            uniform.setUniform(value)
        }

        oldValue = value
    }
}

// We used to do this, but it fails obscurely in Kotlin/JS:
//fun GlslUniform.set(value: Any) {
//    when (value) {
//        is Boolean -> set(value)
//
//        is Int -> set(value)
//        is Vector2I -> set(value)
////        is Vector3I -> uniform.set(value)
////        is Vector4I -> uniform.set(value)
//
//        is Float -> set(value)
//        is Vector2F -> set(value)
//        is Vector3F -> set(value)
//        is Vector4F -> set(value)
//
//        is Matrix4F -> set(value)
//        is EulerAngle -> set(value)
//        is GlContext.TextureUnit -> set(value)
//
//        is Color -> {
//            value as Color
//            set(value.redF, value.greenF, value.blueF, value.alphaF)
//        }
//
//        else -> error("unsupported uniform type ${value::class.simpleName}")
//    }
//}

class SingleUniformFeedContext(
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