package baaahs.plugin.core

import baaahs.ShowPlayer
import baaahs.geom.EulerAngle
import baaahs.geom.Matrix4
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
    "origin" to GlslType.Vec3,
    "heading" to GlslType.Vec3,
    "matrix" to GlslType.Matrix4
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

            private val originUniform = glslProgram.getUniform("$id.origin")
            private val headingUniform = glslProgram.getUniform("$id.heading")
            private val matrixUniform = glslProgram.getUniform("$id.matrix")

            override val isValid: Boolean get() =
                originUniform != null || headingUniform != null || matrixUniform != null

            override fun setOnProgram(renderTarget: RenderTarget) {
                val fixtureInfo = renderTarget.fixture.modelEntity as? Model.FixtureInfo
                originUniform?.set(fixtureInfo?.position ?: Vector3F.origin)
                headingUniform?.set(fixtureInfo?.rotation ?: EulerAngle.identity)
                matrixUniform?.set(fixtureInfo?.matrix ?: Matrix4())
            }
        }
    }
}