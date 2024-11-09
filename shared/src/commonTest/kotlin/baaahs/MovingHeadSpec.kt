package baaahs

import baaahs.dmx.Dmx
import baaahs.kotest.value
import baaahs.plugin.core.MovingHeadParams
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.*
import kotlin.math.PI

object MovingHeadSpec : DescribeSpec({
    describe<MovingHeadParams> {
        val testMovingHead by value { TestMovingHeadAdapter() }
        val channels by value { Dmx.Buffer(ByteArray(16)) }
        val buffer by value { testMovingHead.newBuffer(channels) }

        describe("ranges") {
            val range by value { -.5f .. .5f }

            describe("scale") {
                it("scales a stored value in 0..1 to min..max") {
                    range.scale(0f).shouldBe(-.5f)
                    range.scale(1f).shouldBe(.5f)
                    range.scale(.5f).shouldBe(0f)
                }
            }

            describe("unscale") {
                it("unscales a value in min..max to 0..1") {
                    range.unscale(-.5f).shouldBe(0f)
                    range.unscale(.5f).shouldBe(1f)
                    range.unscale(0f).shouldBe(.5f)
                }
            }

            describe("clamp") {
                it("clamps values to min..max") {
                    range.clamp(-.51f).shouldBe(-.5f)
                    range.clamp(.51f).shouldBe(.5f)
                    range.clamp(-1f).shouldBe(-.5f)
                    range.clamp(1f).shouldBe(.5f)
                    range.clamp(0f).shouldBe(0f)
                }
            }
        }

        it("scales pan to the proper range") {
            buffer.pan = 0f
            channels[testMovingHead.panChannel]
                .shouldBe(0)
            channels[testMovingHead.panFineChannel]
                .shouldBe(0)

            buffer.pan = (PI / 2).toFloat()
            channels[testMovingHead.panChannel]
                .shouldBe(42.toByte()) // 255 / 6
            channels[testMovingHead.panFineChannel]
                .shouldBe(-86)

            buffer.pan = (PI * 2).toFloat()
            channels[testMovingHead.panChannel]
                .shouldBe((255 * 2 / 3).toByte())
            channels[testMovingHead.panFineChannel]
                .shouldBe((255 * 2 / 3).toByte())

            buffer.pan = (PI * 3).toFloat()
            channels[testMovingHead.panChannel]
                .shouldBe(255.toByte())
            channels[testMovingHead.panFineChannel]
                .shouldBe(255.toByte())
        }

        it("scales tilt to the proper range") {
            buffer.tilt = 0f
            channels[testMovingHead.tiltChannel]
                .shouldBe(127)
            channels[testMovingHead.tiltFineChannel]
                .shouldBe(-1)

            buffer.tilt = toRadians(-90f)
            channels[testMovingHead.tiltChannel]
                .shouldBe(42)
            channels[testMovingHead.tiltFineChannel]
                .shouldBe(-86)

            buffer.tilt = toRadians(-135f)
            channels[testMovingHead.tiltChannel]
                .shouldBe(0)
            channels[testMovingHead.tiltFineChannel]
                .shouldBe(0)

            buffer.tilt = toRadians(135f)
            channels[testMovingHead.tiltChannel]
                .shouldBe(255.toByte())
            channels[testMovingHead.tiltFineChannel]
                .shouldBe(255.toByte())
        }

        it("includes prism control") {
            buffer.prism = false
            buffer.prismRotation = 0f
            channels[testMovingHead.prismChannel]
                .shouldBe(0)
            channels[testMovingHead.prismRotationChannel]
                .shouldBe(-65)

            buffer.prism = true
            buffer.prismRotation = .7f
            channels[testMovingHead.prismChannel]
                .shouldBe(255.toByte())
            channels[testMovingHead.prismRotationChannel]
                .shouldBe(-22)

            buffer.prism = true
            buffer.prismRotation = .3f
            channels[testMovingHead.prismChannel]
                .shouldBe(255.toByte())
            channels[testMovingHead.prismRotationChannel]
                .shouldBe(-47)
        }
    }
})