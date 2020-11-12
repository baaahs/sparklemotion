package baaahs.fixtures

import baaahs.RefCounted
import baaahs.RefCounter
import baaahs.ShowPlayer
import baaahs.gl.GlContext
import baaahs.gl.data.*
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.gl.render.RenderTarget
import baaahs.glsl.Uniform
import baaahs.plugin.CorePlugin
import baaahs.plugin.Plugin
import baaahs.show.DataSource
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.math.min

object PixelArrayDevice : DeviceType {
    override val id: String get() = "PixelArray"
    override val title: String get() = "Pixel Array"

    override val dataSources: List<DataSource> = listOf(
//        PixelLocationDataSource()
    )

    override val resultParams: List<ResultParam> = listOf(
        ResultParam("Pixel Color", ColorResultType)
    )

    fun getColorResults(resultViews: List<ResultView>) =
        resultViews[0] as ColorResultType.ColorResultView
}

@Serializable
@SerialName("baaahs.Core:PixelLocation")
data class PixelLocationDataSource(@Transient val `_`: Boolean = true) : DataSource {
    override val pluginPackage: String get() = CorePlugin.id
    override val title: String get() = "Pixel Location"
    override fun getType(): GlslType = GlslType.Vec3
    override fun getContentType(): ContentType = ContentType.XyzCoordinate

    override fun createFeed(showPlayer: ShowPlayer, plugin: Plugin, id: String): Feed {
        return createFixtureFeed()
    }

    override fun createFixtureFeed(): Feed {
        return PixelLocationFeed()
    }
}

class PixelLocationFeed(
    private val id: String = "fixture_pixelCoordsTexture",
    private val refCounter: RefCounter = RefCounter()
) : Feed, RefCounted by refCounter {
    override fun bind(gl: GlContext): EngineFeed = EngineFeed(gl)

    inner class EngineFeed(gl: GlContext) : PerPixelEngineFeed {
        override val buffer = FloatsParamBuffer(id, 3, gl)

        override fun setOnBuffer(renderTarget: RenderTarget) = run {
            val pixelLocations = renderTarget.fixture.pixelLocations
            buffer.scoped(renderTarget).also { view ->
                for (pixelIndex in 0 until min(pixelLocations.size, renderTarget.pixelCount)) {
                    val location = pixelLocations[pixelIndex]
                    view[pixelIndex, 0] = location.x
                    view[pixelIndex, 1] = location.y
                    view[pixelIndex, 2] = location.z
                }
            }
            Unit
        }

        override fun bind(glslProgram: GlslProgram) = ProgramFeed(glslProgram)

        inner class ProgramFeed(glslProgram: GlslProgram) : PerPixelProgramFeed(updateMode) {
            override val buffer: ParamBuffer get() = this@EngineFeed.buffer
            override val uniform: Uniform = glslProgram.getUniform(id) ?: error("no uniform $id")
            override val isValid: Boolean get() = true
        }
    }

    override fun release() {
        refCounter.release()
    }
}