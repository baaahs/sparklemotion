package baaahs.device

import baaahs.describe
import baaahs.glsl.LinearSurfacePixelStrategy
import baaahs.kotest.value
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.*
import io.kotest.matchers.types.shouldBeTypeOf

@Suppress("unused")
object PixelArrayDeviceSpec : DescribeSpec({
    describe<PixelArrayDevice> {
        val defaultOptions by value {
            PixelArrayDevice.Options()
        }

        val options by value {
            PixelArrayDevice.Options(
                pixelCount = 123,
                pixelFormat = PixelFormat.RGB8,
                gammaCorrection = 0.5f,
                pixelArrangement = LinearSurfacePixelStrategy(),
                pixelLocations = listOf()
            )
        }

        describe("options for a pixel array device") {
            it("defaults to no values") {
                defaultOptions.shouldBe(PixelArrayDevice.Options(null, null, null, null, null))
            }

            it("can be merged with another set of options") {
                val mergedOptions = defaultOptions + options

                mergedOptions.pixelCount.shouldBe(123)
                mergedOptions.pixelFormat.shouldBe(PixelFormat.RGB8)
                mergedOptions.gammaCorrection.shouldBe(0.5f)
                mergedOptions.pixelArrangement.shouldBeTypeOf<LinearSurfacePixelStrategy>()
                mergedOptions.pixelLocations.shouldBe(listOf())
            }

            it("when merged with defaults, original values are preserved") {
                val mergedOptions = options + defaultOptions

                mergedOptions.pixelCount.shouldBe(123)
                mergedOptions.pixelFormat.shouldBe(PixelFormat.RGB8)
                mergedOptions.gammaCorrection.shouldBe(0.5f)
                mergedOptions.pixelArrangement.shouldBeTypeOf<LinearSurfacePixelStrategy>()
                mergedOptions.pixelLocations.shouldBe(listOf())
            }
        }
    }
})