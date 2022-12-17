package baaahs.plugin.core.datasource

import baaahs.ShowPlayer
import baaahs.control.ButtonControl
import baaahs.control.MutableButtonControl
import baaahs.gadgets.Switch
import baaahs.gl.GlContext
import baaahs.gl.data.EngineFeedContext
import baaahs.gl.data.FeedContext
import baaahs.gl.data.ProgramFeedContext
import baaahs.gl.data.SingleUniformFeedContext
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.gl.shader.InputPort
import baaahs.plugin.classSerializer
import baaahs.plugin.core.CorePlugin
import baaahs.show.DataSource
import baaahs.show.DataSourceBuilder
import baaahs.show.mutable.MutableControl
import baaahs.show.mutable.MutableShow
import baaahs.util.Logger
import baaahs.util.RefCounted
import baaahs.util.RefCounter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

@Serializable
@SerialName("baaahs.Core:Switch")
data class SwitchDataSource(
    @SerialName("title")
    val buttonTitle: String,
    val initiallyEnabled: Boolean,
    val activationType: ButtonControl.ActivationType = ButtonControl.ActivationType.Toggle
) : DataSource {
    override val title: String get() = "$buttonTitle $resourceName"
    override val pluginPackage: String get() = CorePlugin.id
    override fun getType(): GlslType = GlslType.Bool
    override val contentType: ContentType
        get() = ContentType.Boolean

    fun createGadget(): ButtonControl =
        ButtonControl(buttonTitle, activationType)

    override fun buildControl(): MutableControl {
        return MutableButtonControl(createGadget(), MutableShow("Temp Show"), this)
    }

    override fun open(showPlayer: ShowPlayer, id: String): FeedContext {
        val switch = showPlayer.useGadget(this)
            ?: showPlayer.useGadget(id)
            ?: run {
                logger.debug { "No control gadget registered for datasource $id, creating one. This is probably busted." }
                Switch(buttonTitle, initiallyEnabled)
            }

        return object : FeedContext, RefCounted by RefCounter() {
            override fun bind(gl: GlContext): EngineFeedContext = object : EngineFeedContext {
                override fun bind(glslProgram: GlslProgram): ProgramFeedContext {
                    return SingleUniformFeedContext(glslProgram, this@SwitchDataSource, id) { uniform ->
                        uniform.set(if (switch.enabled) 1 else 0)
                    }
                }
            }
        }
    }

    companion object : DataSourceBuilder<SwitchDataSource> {
        override val title: String get() = "Switch"
        override val description: String get() = "A user-adjustable on/off switch."
        override val resourceName: String get() = "Switch"
        override val contentType: ContentType get() = ContentType.Boolean
        override val serializerRegistrar get() = classSerializer(serializer())

        override fun build(inputPort: InputPort): SwitchDataSource {
            val config = inputPort.pluginConfig
            return SwitchDataSource(
                inputPort.title,
                initiallyEnabled = config.getBoolean("default") ?: true
            )
        }

        private fun JsonObject?.getBoolean(key: String): Boolean? {
            return try {
                this?.get(key)?.jsonPrimitive?.boolean
            } catch (e: NumberFormatException) {
                logger.debug(e) {
                    "Invalid number for key \"$key\": ${this?.get(key)?.jsonPrimitive?.contentOrNull}"
                }
                null
            }
        }

        private val logger = Logger<SwitchDataSource>()
    }
}