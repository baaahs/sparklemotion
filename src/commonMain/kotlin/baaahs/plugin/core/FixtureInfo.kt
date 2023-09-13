package baaahs.plugin.core

import baaahs.ShowPlayer
import baaahs.geom.EulerAngle
import baaahs.geom.Matrix4F
import baaahs.geom.Vector3F
import baaahs.geom.identity
import baaahs.gl.GlContext
import baaahs.gl.data.EngineFeedContext
import baaahs.gl.data.FeedContext
import baaahs.gl.data.ProgramFeedContext
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.gl.render.RenderTarget
import baaahs.gl.shader.InputPort
import baaahs.model.Model
import baaahs.plugin.classSerializer
import baaahs.show.Feed
import baaahs.show.FeedBuilder
import baaahs.show.UpdateMode
import baaahs.util.RefCounted
import baaahs.util.RefCounter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


val fixtureInfoStruct = GlslType.Struct(
    "FixtureInfo",
    GlslType.Field(
        "position", GlslType.Vec3,
        "The fixture entity's position in the scene."
    ),
    GlslType.Field(
        "rotation", GlslType.Vec3,
        "The fixture entity's rotation in the scene."
    ),
    GlslType.Field(
        "transformation", GlslType.Matrix4,
        "The fixture entity's transformation in the scene."
    ),
    GlslType.Field(
        "boundaryMin", GlslType.Vec3,
        "The near-lower-leftmost coordinate of the fixture entity, after translation, in scene coordinates."
    ),
    GlslType.Field(
        "boundaryMax", GlslType.Vec3,
        "The far-upper-rightmost coordinate of the fixture entity, after translation, in scene coordinates."
    ),
    GlslType.Field(
        "normalizer", GlslType.Matrix4,
        "A transformation which normalizes the fixture entity's pixels to roughly face the camera " +
                "and fit in [(0,0,0)..(1,1,1)]."
    ),

    // TODO: Switch to `int[] name` when Kgl supports `uniform1iv`.
    // See: https://github.com/gergelydaniel/kgl/pull/15
    GlslType.Field("name", GlslType.Int.arrayOf(8), "ASCII values in the fixture entity's name, or 0's."),
//    GlslType.Field("name0", GlslType.Int, "The first ASCII value in the fixture entity's name, or 0."),
//    GlslType.Field("name1", GlslType.Int),
//    GlslType.Field("name2", GlslType.Int),
//    GlslType.Field("name3", GlslType.Int),
//    GlslType.Field("name4", GlslType.Int),
//    GlslType.Field("name5", GlslType.Int),
//    GlslType.Field("name6", GlslType.Int),
//    GlslType.Field("name7", GlslType.Int),
    GlslType.Field(
        "nameLength", GlslType.Int,
        "The length of the fixture entity's name."
    )
)

val fixtureInfoContentType = ContentType("fixture-info", "Fixture Info", fixtureInfoStruct)

@Serializable
@SerialName("baaahs.Core:FixtureInfo")
data class FixtureInfoFeed(@Transient val `_`: Boolean = true) : Feed {
    override val pluginPackage: String get() = CorePlugin.id
    override val title: String get() = "Fixture Info"
    override fun getType(): GlslType = fixtureInfoStruct
    override val contentType: ContentType
        get() = fixtureInfoContentType

    override fun open(showPlayer: ShowPlayer, id: String): FeedContext {
        return FixtureInfoFeedContext(getVarName(id))
    }

    companion object : FeedBuilder<FixtureInfoFeed> {
        override val title: String get() = "Fixture Info"
        override val description: String
            get() =
                "Information about the fixture's position and orientation in the model."
        override val resourceName: String get() = "FixtureInfo"
        override val contentType: ContentType get() = fixtureInfoContentType
        override val serializerRegistrar get() = classSerializer(serializer())

        override fun build(inputPort: InputPort): FixtureInfoFeed {
            return FixtureInfoFeed()
        }
    }
}

class FixtureInfoFeedContext(
    private val id: String
) : FeedContext, RefCounted by RefCounter() {
    override fun bind(gl: GlContext): EngineFeedContext = object : EngineFeedContext {
        override fun bind(glslProgram: GlslProgram) = object : ProgramFeedContext {
            override val updateMode: UpdateMode get() = UpdateMode.PER_FIXTURE

            private val positionUniform = glslProgram.getUniform("$id.position")
            private val rotationUniform = glslProgram.getUniform("$id.rotation")
            private val transformationUniform = glslProgram.getUniform("$id.transformation")
            private val boundaryMinUniform = glslProgram.getUniform("$id.boundaryMin")
            private val boundaryMaxUniform = glslProgram.getUniform("$id.boundaryMax")
            private val normalizerUniform = glslProgram.getUniform("$id.normalizer")
            private val nameUniforms = (0 until 16).map { glslProgram.getUniform("$id.name$it") }
            private val nameLengthUniform = glslProgram.getUniform("$id.nameLength")
            private val anyNameUniforms = nameUniforms.any { it != null } || nameLengthUniform != null

            override val isValid: Boolean
                get() =
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
                normalizerUniform?.set(fixtureInfo?.transformation?.inverse() ?: Matrix4F.identity)

                if (fixtureInfo?.name != null && anyNameUniforms) {
                    val chars = fixtureInfo.name.toCharArray()
                    nameUniforms.forEachIndexed { i, uniform ->
                        val char = if (chars.size >= i) chars[i] else Char(0)
                        uniform?.set(char.uppercaseChar().code)
                    }
                    nameLengthUniform?.set(chars.size)
                }
            }
        }
    }
}