package baaahs.fixtures

import baaahs.RefCounted
import baaahs.RefCounter
import baaahs.ShowPlayer
import baaahs.gl.GlContext
import baaahs.gl.data.*
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.gl.shader.InputPort
import baaahs.model.Model
import baaahs.plugin.CorePlugin
import baaahs.plugin.SerializerRegistrar
import baaahs.plugin.classSerializer
import baaahs.plugin.core.fixtureInfoStruct
import baaahs.show.DataSource
import baaahs.show.DataSourceBuilder
import baaahs.show.UpdateMode
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@SerialName("baaahs.Core:Edges")
data class EdgesDataSource(@Transient val `_`: Boolean = true) : DataSource {
    override val pluginPackage: String get() = CorePlugin.id
    override val title: String get() = "Edges"
    override fun getType(): GlslType = GlslType.Vec3.array
    override val contentType: ContentType
        get() = ContentType.XyzCoordinate

    override fun createFeed(showPlayer: ShowPlayer, id: String): Feed {
        val maxEdges = showPlayer.model.allSurfaces.maxByOrNull { surface ->
            surface.lines.sumOf { it.vertices.size - 1 }
        }

        println("maxEdges = $maxEdges")

        return object : Feed, RefCounted by RefCounter() {
            override fun bind(gl: GlContext): EngineFeed = object : EngineFeed {
                override fun bind(glslProgram: GlslProgram): ProgramFeed =
                    SingleUniformFixtureFeed(glslProgram, this@EdgesDataSource, id) { uniform, renderTarget ->
                        renderTarget.fixture.modelEntity?.let {
                            if (it is Model.Surface) {
                                it.lines.forEachIndexed { index, line ->
                                    if (index == 0) {

                                    }
                                }
                            }
                        }
                    }
            }

            override fun release() = super.release()
        }
    }

    override fun appendDeclaration(buf: StringBuilder, id: String) {
        val textureUniformId = """ds_${id}_texture"""
        val varName = getVarName(id)
        buf.append(
            """
            uniform sampler2D $textureUniformId;
            vec3 ds_${id}_getPixelCoords(vec2 rasterCoord) {
                return texelFetch($textureUniformId, ivec2(rasterCoord.xy), 0).xyz;
            }
            vec3 $varName;
            
        """.trimIndent()
        )
    }

    override fun invocationGlsl(varName: String): String {
        return "${getVarName(varName)} = ds_${varName}_getPixelCoords(gl_FragCoord.xy)"
    }

    companion object : DataSourceBuilder<EdgesDataSource> {
        val edgesStruct = GlslType.Struct("Edges",
            "count" to GlslType.Int,
            "pairs" to GlslType.Vec3.array
        )

        override val resourceName: String
            get() = "Edges"
        override val contentType: ContentType =
            ContentType("edges", "Edges", edgesStruct)
        override val serializerRegistrar: SerializerRegistrar<EdgesDataSource>
            get() = classSerializer(serializer())

        override fun build(inputPort: InputPort): EdgesDataSource =
            EdgesDataSource()
    }
}
