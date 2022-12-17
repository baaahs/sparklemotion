package baaahs.plugin.core.datasource

import baaahs.ShowPlayer
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
import baaahs.util.RefCounted
import baaahs.util.RefCounter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * This data source provides `gl_FragColor` for quad previews.
 *
 * Because `gl_FragColor` is always given as absolute pixels relative to the bottom-left
 * of the screen/canvas, _not_ relative to the the viewport, and we might be rendering
 * into a `SharedGlContext` (which adjusts the viewport to a rectangle within the shared
 * canvas), we need to adjust `gl_FragColor` to account for any offset.
 */
@Serializable
@SerialName("baaahs.Core:RasterCoordinate")
data class RasterCoordinateFeed(@Transient val `_`: Boolean = true) : Feed {
    companion object : FeedBuilder<RasterCoordinateFeed> {
        override val title: String get() = "Raster Coordinate"
        override val description: String get() = "Internal use only."
        override val resourceName: String get() = "RasterCoordinate"
        override val contentType: ContentType get() = ContentType.RasterCoordinate
        override val serializerRegistrar get() = classSerializer(serializer())
        override val internalOnly: Boolean = true

        override fun looksValid(inputPort: InputPort, suggestedContentTypes: Set<ContentType>): Boolean = false
        override fun build(inputPort: InputPort): RasterCoordinateFeed =
            RasterCoordinateFeed()
    }

    override val pluginPackage: String get() = CorePlugin.id
    override val title: String get() = "Raster Coordinate"
    override fun getType(): GlslType = GlslType.Vec4
    override val contentType: ContentType
        get() = ContentType.RasterCoordinate

    override fun open(showPlayer: ShowPlayer, id: String): FeedContext =
        object : FeedContext, RefCounted by RefCounter() {
            override fun bind(gl: GlContext): EngineFeedContext = object : EngineFeedContext {
                val offsetUniformId = "ds_${id}_offset"
                override fun bind(glslProgram: GlslProgram): ProgramFeedContext =
                    object : ProgramFeedContext {
                        private val uniform = glslProgram.getUniform(offsetUniformId)
                        override val isValid: Boolean = uniform != null

                        override fun setOnProgram() {
                            val rasterOffset = gl.rasterOffset
                            uniform?.set(rasterOffset.left.toFloat(), rasterOffset.bottom.toFloat())
                        }
                    }
            }
        }

    override fun isImplicit(): Boolean = true

    override fun appendDeclaration(buf: StringBuilder, id: String) {
        val offsetUniformId = "ds_${id}_offset"
        val varName = getVarName(id)
        buf.append("""
            uniform vec2 $offsetUniformId;
            vec4 $varName;
            
        """.trimIndent())
    }

    override fun invocationGlsl(varName: String): String {
        val offsetUniformId = "ds_${varName}_offset"
        return "${getVarName(varName)} = gl_FragCoord - vec4($offsetUniformId, 0., 0.)"
    }
}