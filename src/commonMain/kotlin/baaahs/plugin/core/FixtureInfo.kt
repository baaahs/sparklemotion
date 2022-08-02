package baaahs.plugin.core

import baaahs.ShowPlayer
import baaahs.geom.EulerAngle
import baaahs.geom.Matrix4F
import baaahs.geom.Vector3F
import baaahs.gl.GlContext
import baaahs.gl.data.EngineFeed
import baaahs.gl.data.Feed
import baaahs.gl.data.ProgramFeed
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.gl.render.RenderTarget
import baaahs.gl.shader.InputPort
import baaahs.model.Model
import baaahs.plugin.classSerializer
import baaahs.show.DataSource
import baaahs.show.DataSourceBuilder
import baaahs.show.UpdateMode
import baaahs.util.RefCounted
import baaahs.util.RefCounter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


val fixtureInfoStruct = GlslType.Struct(
    "FixtureInfo",
    GlslType.Field("position", GlslType.Vec3),
    GlslType.Field("rotation", GlslType.Vec3),
    GlslType.Field("transformation", GlslType.Matrix4),
    GlslType.Field("boundaryMin", GlslType.Vec3),
    GlslType.Field("boundaryMax", GlslType.Vec3)
)

val fixtureInfoContentType = ContentType("fixture-info", "Fixture Info", fixtureInfoStruct)

@Serializable
@SerialName("baaahs.Core:FixtureInfo")
data class FixtureInfoDataSource(@Transient val `_`: Boolean = true) : DataSource {
    override val pluginPackage: String get() = CorePlugin.id
    override val title: String get() = "Fixture Info"
    override fun getType(): GlslType = fixtureInfoStruct
    override val contentType: ContentType
        get() = fixtureInfoContentType

    override fun createFeed(showPlayer: ShowPlayer, id: String): Feed {
        return FixtureInfoFeed(getVarName(id))
    }

    companion object : DataSourceBuilder<FixtureInfoDataSource> {
        override val title: String get() = "Fixture Info"
        override val description: String get() =
            "Information about the fixture's position and orientation in the model."
        override val resourceName: String get() = "FixtureInfo"
        override val contentType: ContentType get() = fixtureInfoContentType
        override val serializerRegistrar get() = classSerializer(serializer())

        override fun build(inputPort: InputPort): FixtureInfoDataSource {
            return FixtureInfoDataSource()
        }
    }
}

class FixtureInfoFeed(
    private val id: String
) : Feed, RefCounted by RefCounter() {
    override fun bind(gl: GlContext): EngineFeed = object : EngineFeed {
        override fun bind(glslProgram: GlslProgram) = object : ProgramFeed {
            override val updateMode: UpdateMode get() = UpdateMode.PER_FIXTURE

            private val positionUniform = glslProgram.getUniform("$id.position")
            private val rotationUniform = glslProgram.getUniform("$id.rotation")
            private val transformationUniform = glslProgram.getUniform("$id.transformation")
            private val boundaryMinUniform = glslProgram.getUniform("$id.boundaryMin")
            private val boundaryMaxUniform = glslProgram.getUniform("$id.boundaryMax")

            override val isValid: Boolean get() =
                positionUniform != null || rotationUniform != null || transformationUniform != null ||
                        boundaryMinUniform != null || boundaryMaxUniform != null

            override fun setOnProgram(renderTarget: RenderTarget) {
                val fixtureInfo = renderTarget.fixture.modelEntity as? Model.FixtureInfo
                positionUniform?.set(fixtureInfo?.position ?: Vector3F.origin)
                rotationUniform?.set(fixtureInfo?.rotation ?: EulerAngle.identity)
                transformationUniform?.set(fixtureInfo?.transformation ?: Matrix4F.identity)
                val bounds = fixtureInfo?.bounds
                boundaryMinUniform?.set(bounds?.first ?: Vector3F.origin)
                boundaryMaxUniform?.set(bounds?.second ?: Vector3F.origin)
            }
        }
    }
}