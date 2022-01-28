package baaahs.plugin.core.datasource

import baaahs.ShowPlayer
import baaahs.geom.Vector3F
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
import baaahs.show.UpdateMode
import baaahs.ui.addObserver
import baaahs.util.RefCounted
import baaahs.util.RefCounter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@SerialName("baaahs.Core:ModelInfo")
data class ModelInfoDataSource(@Transient val `_`: Boolean = true) : DataSource {
    companion object : DataSourceBuilder<ModelInfoDataSource> {
        override val title: String get() = "Model Info"
        override val description: String get() = "Information about the model."
        override val resourceName: String get() = "ModelInfo"
        override val contentType: ContentType get() = ContentType.ModelInfo
        override val serializerRegistrar get() = classSerializer(serializer())
        private val modelInfoType = ContentType.ModelInfo.glslType

        override fun build(inputPort: InputPort): ModelInfoDataSource =
            ModelInfoDataSource()
    }

    override val pluginPackage: String get() = CorePlugin.id
    override val title: String get() = "Model Info"
    override fun getType(): GlslType = modelInfoType
    override val contentType: ContentType
        get() = ContentType.ModelInfo

    override fun createFeed(showPlayer: ShowPlayer, id: String): Feed {
        val sceneProvider = showPlayer.sceneProvider

        return object : Feed, RefCounted by RefCounter() {
            var center: Vector3F? = null
            var extents: Vector3F? = null
            val listener = sceneProvider.addObserver(fireImmediately = true) {
                val model = it.openScene?.model
                center = model?.center
                extents = model?.extents
            }

            private val varPrefix = getVarName(id)

            override fun bind(gl: GlContext): EngineFeed = object : EngineFeed {
                override fun bind(glslProgram: GlslProgram): ProgramFeed {
                    return object : ProgramFeed {
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