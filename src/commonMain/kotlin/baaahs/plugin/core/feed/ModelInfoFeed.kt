package baaahs.plugin.core.feed

import baaahs.ShowPlayer
import baaahs.geom.Vector3F
import baaahs.gl.GlContext
import baaahs.gl.data.EngineFeedContext
import baaahs.gl.data.FeedContext
import baaahs.gl.data.ProgramFeedContext
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.gl.shader.InputPort
import baaahs.plugin.classSerializer
import baaahs.plugin.core.CorePlugin
import baaahs.show.Feed
import baaahs.show.FeedBuilder
import baaahs.show.UpdateMode
import baaahs.ui.addObserver
import baaahs.util.RefCounted
import baaahs.util.RefCounter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@SerialName("baaahs.Core:ModelInfo")
data class ModelInfoFeed(@Transient val `_`: Boolean = true) : Feed {
    companion object : FeedBuilder<ModelInfoFeed> {
        override val title: String get() = "Model Info"
        override val description: String get() = "Information about the model."
        override val resourceName: String get() = "ModelInfo"
        override val contentType: ContentType get() = ContentType.ModelInfo
        override val serializerRegistrar get() = classSerializer(serializer())
        private val modelInfoType = ContentType.ModelInfo.glslType

        override fun build(inputPort: InputPort): ModelInfoFeed =
            ModelInfoFeed()
    }

    override val pluginPackage: String get() = CorePlugin.id
    override val title: String get() = "Model Info"
    override fun getType(): GlslType = modelInfoType
    override val contentType: ContentType
        get() = ContentType.ModelInfo

    override fun open(showPlayer: ShowPlayer, id: String): FeedContext {
        val sceneProvider = showPlayer.sceneProvider

        return object : FeedContext, RefCounted by RefCounter() {
            var center: Vector3F? = null
            var extents: Vector3F? = null
            val listener = sceneProvider.addObserver(fireImmediately = true) {
                val model = it.openScene?.model
                center = model?.center
                extents = model?.extents
            }

            private val varPrefix = getVarName(id)

            override fun bind(gl: GlContext): EngineFeedContext = object : EngineFeedContext {
                override fun bind(glslProgram: GlslProgram): ProgramFeedContext {
                    return object : ProgramFeedContext {
                        override val updateMode: UpdateMode get() = UpdateMode.PER_FRAME
                        val centerUniform = glslProgram.getUniform("${varPrefix}.center")
                        val extentsUniform = glslProgram.getUniform("${varPrefix}.extents")

                        override val isValid: Boolean
                            get() = centerUniform != null && extentsUniform != null

                        override fun setOnProgram() {
                            center?.let { centerUniform?.set(it) }
                            extents?.let { extentsUniform?.set(it) }
                        }
                    }
                }
            }

            override fun release() {
                sceneProvider.removeObserver(listener)
            }
        }
    }
}