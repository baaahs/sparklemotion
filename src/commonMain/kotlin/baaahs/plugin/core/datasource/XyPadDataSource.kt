package baaahs.plugin.core.datasource

import baaahs.RefCounted
import baaahs.RefCounter
import baaahs.ShowPlayer
import baaahs.camelize
import baaahs.control.MutableXyPadControl
import baaahs.gadgets.XyPad
import baaahs.geom.Vector2F
import baaahs.gl.GlContext
import baaahs.gl.data.EngineFeed
import baaahs.gl.data.Feed
import baaahs.gl.data.ProgramFeed
import baaahs.gl.data.SingleUniformFeed
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.gl.shader.InputPort
import baaahs.plugin.classSerializer
import baaahs.plugin.core.CorePlugin
import baaahs.show.DataSource
import baaahs.show.DataSourceBuilder
import baaahs.show.mutable.MutableControl
import baaahs.util.Logger
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
    override fun suggestId(): String = "$title XY Pad".camelize()

    fun createGadget(): XyPad =
        XyPad(title, initialValue, minValue, maxValue)

    override fun buildControl(): MutableControl? =
        MutableXyPadControl(title, initialValue, minValue, maxValue, this)

    override fun createFeed(showPlayer: ShowPlayer, id: String): Feed {
        val xyPad = showPlayer.useGadget(this)
            ?: run {
                logger.debug { "No control gadget registered for datasource $id, creating one. This is probably busted." }
                createGadget()
            }

        return object : Feed, RefCounted by RefCounter() {
            override fun bind(gl: GlContext): EngineFeed = object : EngineFeed {
                override fun bind(glslProgram: GlslProgram): ProgramFeed {
                    return SingleUniformFeed(glslProgram, this@XyPadDataSource, id) { uniform ->
                        uniform.set(xyPad.position)
                    }
                }
            }

            override fun release() = Unit
        }
    }

    companion object : DataSourceBuilder<XyPadDataSource> {
        override val title: String get() = "X/Y Pad"
        override val description: String get() = "A user-adjustable two-dimensional input pad."
        override val resourceName: String get() = "XyPad"
        override val contentType: ContentType get() = ContentType.XyCoordinate
        override val serializerRegistrar get() = classSerializer(serializer())

        override fun build(inputPort: InputPort): XyPadDataSource =
            XyPadDataSource(inputPort.title)

        private val logger = Logger<XyPadDataSource>()
    }
}