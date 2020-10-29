package baaahs.fixtures

import baaahs.Color
import baaahs.Pixels
import baaahs.gl.render.FixtureRenderPlan

object PixelArrayDevice: DeviceType {
    override val resultParams: List<ResultParam> =
        listOf(
            ResultParam("Pixel Color", ColorResultType)
        )

    fun getPixels(fixtureRenderPlan: FixtureRenderPlan): Pixels {
        val buffer = fixtureRenderPlan.resultBuffers[0] as ColorResultType.Buffer
        return FixturePixels(buffer, fixtureRenderPlan.pixelCount, fixtureRenderPlan.pixel0Index)
    }
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