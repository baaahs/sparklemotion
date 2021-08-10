package baaahs.plugin.core.datasource

import baaahs.RefCounted
import baaahs.RefCounter
import baaahs.ShowPlayer
import baaahs.camelize
import baaahs.gl.GlContext
import baaahs.gl.data.EngineFeed
import baaahs.gl.data.Feed
import baaahs.gl.data.ProgramFeed
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.gl.shader.InputPort
import baaahs.plugin.classSerializer
import baaahs.plugin.core.CorePlugin
import baaahs.show.DataSource
import baaahs.show.DataSourceBuilder
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("baaahs.Core:XyPad")
data class XyPadDataSource(
    @SerialName("title")
    val gadgetTitle: String,
    val varPrefix: String
) : DataSource {
    companion object : DataSourceBuilder<XyPadDataSource> {
        override val resourceName: String get() = "XyPad"
        override val contentType: ContentType get() = ContentType.XyCoordinate
        override val serializerRegistrar get() = classSerializer(serializer())

        override fun build(inputPort: InputPort): XyPadDataSource =
            XyPadDataSource(inputPort.title, inputPort.suggestVarName())
    }

    override val pluginPackage: String get() = CorePlugin.id
    override val title: String get() = "XY Pad"
    override fun getType(): GlslType = GlslType.Vec2
    override val contentType: ContentType
        get() = ContentType.XyCoordinate
    override fun suggestId(): String = "$gadgetTitle XY Pad".camelize()

    override fun createFeed(showPlayer: ShowPlayer, id: String): Feed {
        return object : Feed, RefCounted by RefCounter() {
//                val xControl = showPlayer.useGadget<Slider>("${varPrefix}_x")
//                val yControl = showPlayer.useGadget<Slider>("${varPrefix}_y")

            override fun bind(gl: GlContext): EngineFeed = object : EngineFeed {
                override fun bind(glslProgram: GlslProgram): ProgramFeed {
                    return object : ProgramFeed {
                        override val isValid: Boolean
                            get() = false

                        override fun setOnProgram() {
                            //                            uniform.set(xControl.value, yControl.value)
                        }
                    }
                }
            }

            override fun release() = Unit
        }
    }
}