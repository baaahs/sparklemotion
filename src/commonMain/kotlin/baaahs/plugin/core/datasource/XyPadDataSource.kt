package baaahs.plugin.core.datasource

import baaahs.ShowPlayer
import baaahs.control.MutableXyPadControl
import baaahs.gadgets.XyPad
import baaahs.geom.Vector2F
import baaahs.gl.GlContext
import baaahs.gl.data.EngineFeedContext
import baaahs.gl.data.FeedContext
import baaahs.gl.data.ProgramFeedContext
import baaahs.gl.data.SingleUniformFeedContext
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.gl.shader.InputPort
import baaahs.plugin.PluginRef
import baaahs.plugin.classSerializer
import baaahs.plugin.core.CorePlugin
import baaahs.show.DataSource
import baaahs.show.DataSourceBuilder
import baaahs.show.mutable.MutableControl
import baaahs.util.Logger
import baaahs.util.RefCounted
import baaahs.util.RefCounter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.float
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive

@Serializable
@SerialName("baaahs.Core:XyPad")
data class XyPadDataSource(
    override val title: String,
    val initialValue: Vector2F = Vector2F.origin,
    val minValue: Vector2F = Vector2F.origin - Vector2F.unit2d,
    val maxValue: Vector2F = Vector2F.unit2d,
    val varPrefix: String? = null
) : DataSource {
    override val pluginPackage: String get() = CorePlugin.id
    override fun getType(): GlslType = GlslType.Vec2
    override val contentType: ContentType
        get() = ContentType.XyCoordinate

    override fun buildControl(): MutableControl =
        MutableXyPadControl(title, initialValue, minValue, maxValue, this)

    override fun open(showPlayer: ShowPlayer, id: String): FeedContext {
        val xyPad = showPlayer.useGadget(this)
            ?: showPlayer.useGadget(id)
            ?: run {
                logger.debug { "No control gadget registered for datasource $id, creating one. This is probably busted." }
                XyPad(title, initialValue, minValue, maxValue)
            }

        return object : FeedContext, RefCounted by RefCounter() {
            override fun bind(gl: GlContext): EngineFeedContext = object : EngineFeedContext {
                override fun bind(glslProgram: GlslProgram): ProgramFeedContext {
                    return SingleUniformFeedContext(glslProgram, this@XyPadDataSource, id) { uniform ->
                        uniform.set(xyPad.position)
                    }
                }
            }
        }
    }

    companion object : DataSourceBuilder<XyPadDataSource> {
        override val title: String get() = "X/Y Pad"
        override val description: String get() = "A user-adjustable two-dimensional input pad."
        override val resourceName: String get() = "XyPad"
        override val contentType: ContentType get() = ContentType.XyCoordinate
        override val serializerRegistrar get() = classSerializer(serializer())
        val pluginRef = PluginRef(CorePlugin.id, resourceName)

        override fun build(inputPort: InputPort): XyPadDataSource {
            val config = inputPort.pluginConfig
            return XyPadDataSource(
                inputPort.title,
                initialValue = config.getVector2F("default") ?: Vector2F.origin,
                minValue = config.getVector2F("min") ?: -Vector2F.unit2d,
                maxValue = config.getVector2F("max") ?: Vector2F.unit2d
            )
        }

        private fun JsonObject?.getVector2F(key: String): Vector2F? {
            return try {
                this?.get(key)?.jsonArray?.let {
                    Vector2F(it[0].jsonPrimitive.float, it[1].jsonPrimitive.float)
                }
            } catch (e: NumberFormatException) {
                logger.debug(e) {
                    "Invalid value for key \"$key\": ${this?.get(key)}"
                }
                null
            }
        }

        private val logger = Logger<XyPadDataSource>()
    }
}