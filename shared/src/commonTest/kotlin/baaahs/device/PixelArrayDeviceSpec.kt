package baaahs.device

import baaahs.describe
import baaahs.glsl.LinearSurfacePixelStrategy
import baaahs.kotest.value
import ch.tutteli.atrium.api.fluent.en_GB.isA
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import io.kotest.core.spec.style.DescribeSpec

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
                expect(defaultOptions).toBe(PixelArrayDevice.Options(null, null, null, null, null))
            }

            it("can be merged with another set of options") {
                val mergedOptions = defaultOptions + options

                expect(mergedOptions.pixelCount).toBe(123)
                expect(mergedOptions.pixelFormat).toBe(PixelFormat.RGB8)
                expect(mergedOptions.gammaCorrection).toBe(0.5f)
                expect(mergedOptions.pixelArrangement).isA<LinearSurfacePixelStrategy>()
                expect(mergedOptions.pixelLocations).toBe(listOf())
            }

            it("when merged with defaults, original values are preserved") {
                val mergedOptions = options + defaultOptions

                expect(mergedOptions.pixelCount).toBe(123)
                expect(mergedOptions.pixelFormat).toBe(PixelFormat.RGB8)
                expect(mergedOptions.gammaCorrection).toBe(0.5f)
                expect(mergedOptions.pixelArrangement).isA<LinearSurfacePixelStrategy>()
                expect(mergedOptions.pixelLocations).toBe(listOf())
            }
        }
    }
})