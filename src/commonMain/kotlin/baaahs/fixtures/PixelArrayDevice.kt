package baaahs.fixtures

import baaahs.Color
import baaahs.Pixels
import baaahs.geom.Vector3F
import baaahs.gl.render.RenderTarget

object PixelArrayDevice : DeviceType {
    override val id: String get() = "PixelArray"
    override val title: String get() = "Pixel Array"

    override val params: List<Param> = listOf(
        FloatsPixelParam("in_pixelCoordsTexture", "Pixel Coords", 3)
    )

    override val resultParams: List<ResultParam> = listOf(
        ResultParam("Pixel Color", ColorResultType)
    )

    override fun setFixtureParamUniforms(renderTarget: RenderTarget, paramBuffers: List<ParamBuffer>) {
    }

    override fun initPixelParams(renderTarget: RenderTarget, paramBuffers: List<ParamBuffer>) {
        val fixture = renderTarget.fixture
        val pixelLocations = fixture.pixelLocations
        val defaultPixelLocation = renderTarget.modelInfo.center
        val pixelCoordsBuffer = getPixelCoordsBuffer(paramBuffers)
        fillPixelLocations(
            pixelCoordsBuffer,
            renderTarget.pixel0Index,
            pixelLocations,
            defaultPixelLocation
        )
    }

    fun getPixelCoordsBuffer(paramBuffers: List<ParamBuffer>) =
        paramBuffers[0] as FloatsParamBuffer

    private fun fillPixelLocations(
        buffer: FloatsParamBuffer,
        pixel0Index: Int,
        pixelLocations: List<Vector3F?>,
        defaultPixelLocation: Vector3F
    ) {
        pixelLocations.forEachIndexed { i, pixelLocation ->
            val bufOffset = (pixel0Index + i) * buffer.stride
            val (x, y, z) = pixelLocation ?: defaultPixelLocation
            buffer.floats[bufOffset] = x     // x
            buffer.floats[bufOffset + 1] = y // y
            buffer.floats[bufOffset + 2] = z // z
        }
    }

    fun getColorResults(resultViews: List<ResultView>) =
        resultViews[0] as ColorResultType.ColorResultView
}

class FixturePixels(
    private val pixelBuffer: ColorResultType.Buffer,
    override val size: Int,
    private val bufferOffset: Int
) : Pixels {
    override fun get(i: Int): Color = pixelBuffer[bufferOffset + i]

    override fun set(i: Int, color: Color): Unit = TODO("set not implemented")
    override fun set(colors: Array<Color>): Unit = TODO("set not implemented")
}