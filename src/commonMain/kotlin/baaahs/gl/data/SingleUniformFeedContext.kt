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
import kotlin.jvm.JvmName

// Somewhat delicate code follows, proceed with caution.
//
// Kotlin/JS can't discriminate at runtime between Int and Float values,
// so we can't funnel them through a single GlslUniform.set(Any) method.

@JvmName("singleUniformFeedContextBoolean")
fun <T: Boolean?> Feed.singleUniformFeedContext(id: String, getValue: (oldValue: T?) -> T?) =
    bindContext(id, getValue) { set(it) }

@JvmName("singleUniformFeedContextInt")
fun <T: Int?> Feed.singleUniformFeedContext(id: String, getValue: (oldValue: T?) -> T?) =
    bindContext(id, getValue) { set(it) }
@JvmName("singleUniformFeedContextVector2I")
fun <T: Vector2I?> Feed.singleUniformFeedContext(id: String, getValue: (oldValue: T?) -> T?) =
    bindContext(id, getValue) { set(it) }
//@JvmName("singleUniformFeedContextVector4I")
// fun <T: Vector3I?> Feed.singleUniformFeedContext(id: String, getValue: (oldValue: T?) -> T?) =
//    bindContext(id, getValue) { set(it) }
//@JvmName("singleUniformFeedContextVector4I")
// fun <T: Vector4I?> Feed.singleUniformFeedContext(id: String, getValue: (oldValue: T?) -> T?) =
//    bindContext(id, getValue) { set(it) }

@JvmName("singleUniformFeedContextFloat")
fun <T: Float?> Feed.singleUniformFeedContext(id: String, getValue: (oldValue: T?) -> T?) =
    bindContext(id, getValue) { set(it) }
@JvmName("singleUniformFeedContextVector2F")
fun <T: Vector2F?> Feed.singleUniformFeedContext(id: String, getValue: (oldValue: T?) -> T?) =
    bindContext(id, getValue) { set(it) }
@JvmName("singleUniformFeedContextVector3F")
fun <T: Vector3F?> Feed.singleUniformFeedContext(id: String, getValue: (oldValue: T?) -> T?) =
    bindContext(id, getValue) { set(it) }
@JvmName("singleUniformFeedContextVector4F")
fun <T: Vector4F?> Feed.singleUniformFeedContext(id: String, getValue: (oldValue: T?) -> T?) =
    bindContext(id, getValue) { set(it) }

@JvmName("singleUniformFeedContextMatrix4F")
fun <T: Matrix4F?> Feed.singleUniformFeedContext(id: String, getValue: (oldValue: T?) -> T?) =
    bindContext(id, getValue) { set(it) }
@JvmName("singleUniformFeedContextEulerAngle")
fun <T: EulerAngle?> Feed.singleUniformFeedContext(id: String, getValue: (oldValue: T?) -> T?) =
    bindContext(id, getValue) { set(it) }
@JvmName("singleUniformFeedContextTextureUnit")
fun <T: GlContext.TextureUnit?> Feed.singleUniformFeedContext(id: String, getValue: (oldValue: T?) -> T?) =
    bindContext(id, getValue) { set(it) }

@JvmName("singleUniformFeedContextColor")
fun <T: Color?> Feed.singleUniformFeedContext(id: String, getValue: (oldValue: T?) -> T?) =
    bindContext(id, getValue) { set(it.redF, it.greenF, it.blueF, it.alphaF) }

private fun <T: Any> Feed.bindContext(
    id: String,
    getValue: (oldValue: T?) -> T?,
    setUniform: GlslUniform.(value: T) -> Unit
): FeedContext = SingleUniformFeedContext(this, id, getValue, setUniform)

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

class SingleUniformFeedContext<T>(
    feed: Feed,
    private val id: String,
    private val getValue: (T?) -> T?,
    private val setUniform: GlslUniform.(T) -> Unit
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
                private var oldValue: T? = null

                override fun setOnProgram() {
                    val value = getValue(oldValue)
                    if (value != null/* && value != oldValue*/) {
                        try {
                            uniform?.setUniform(value)
                        } catch (e: Exception) {
                            logger.error(e) { "Failed to set uniform $type $varName for $id." }
                        }
                    }

                    oldValue = value
                }
            }
        }
    }

    companion object {
        private val logger = Logger<SingleUniformFeedContext<*>>()
    }
}