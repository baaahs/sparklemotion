package baaahs.control

import baaahs.describe
import baaahs.gadgets.XyPad
import baaahs.geom.Vector2F
import baaahs.kotest.value
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.*

class XyPadControlSpec : DescribeSpec({
    describe<XyPad.Helper> {
        val xyPad by value { XyPad("Brightness") }
        val padSize by value { Vector2F(200f, 200f) }
        val knobSize by value { Vector2F(20f, 20f) }
        val knobBufferZone by value { arrayOf(true) }
        val helper by value { XyPad.Helper(xyPad, padSize, knobSize, knobBufferZone[0]) }

        it("calculates crosshair and knob positions, taking knob size into account") {
            helper.crosshairPositionPx.shouldBe(Vector2F(100f, 100f))
            helper.knobPositionPx.shouldBe(Vector2F(90f, 90f))

            xyPad.position = Vector2F(-1f, 1f)
            helper.crosshairPositionPx.shouldBe(Vector2F(10f, 10f))
            helper.knobPositionPx.shouldBe(Vector2F(0f, 0f))

            xyPad.position = Vector2F(1f, -1f)
            helper.crosshairPositionPx.shouldBe(Vector2F(190f, 190f))
            helper.knobPositionPx.shouldBe(Vector2F(180f, 180f))
        }

        it("clamps knob position to bounds") {
            xyPad.position = Vector2F(-1.5f, 1.5f)
            helper.knobPositionPx.shouldBe(Vector2F(0f, 0f))

            xyPad.position = Vector2F(1.5f, -1.5f)
            helper.knobPositionPx.shouldBe(Vector2F(180f, 180f))
        }

        it("calculates position from px") {
            helper.positionFromPx(Vector2F(100f, 100f))
                .shouldBe(Vector2F(0f, 0f))
        }

        it("maintains a buffer zone corresponding to the knob size") {
            helper.positionFromPx(Vector2F(0f, 0f))
                .shouldBe(Vector2F(-1f, 1f))
            helper.positionFromPx(Vector2F(10f, 10f))
                .shouldBe(Vector2F(-1f, 1f))
            helper.positionFromPx(Vector2F(-5f, -5f))
                .shouldBe(Vector2F(-1f, 1f))

            helper.positionFromPx(Vector2F(190f, 190f))
                .shouldBe(Vector2F(1f, -1f))
            helper.positionFromPx(Vector2F(200f, 200f))
                .shouldBe(Vector2F(1f, -1f))
            helper.positionFromPx(Vector2F(210f, 210f))
                .shouldBe(Vector2F(1f, -1f))
        }

        context("when knob buffer zone is disabled") {
            // wtf? Making knobBufferZone a boolean causes spec to fail.
            beforeEach { knobBufferZone[0] = false }

            it("calculates crosshair and knob positions, taking knob size into account") {
                helper.crosshairPositionPx.shouldBe(Vector2F(100f, 100f))
                helper.knobPositionPx.shouldBe(Vector2F(90f, 90f))

                xyPad.position = Vector2F(-1f, 1f)
                helper.crosshairPositionPx.shouldBe(Vector2F(0f, 0f))
                helper.knobPositionPx.shouldBe(Vector2F(-10f, -10f))

                xyPad.position = Vector2F(1f, -1f)
                helper.crosshairPositionPx.shouldBe(Vector2F(200f, 200f))
                helper.knobPositionPx.shouldBe(Vector2F(190f, 190f))
            }


            it("clamps knob position to bounds") {
                xyPad.position = Vector2F(-1.5f, 1.5f)
                helper.knobPositionPx.shouldBe(Vector2F(-10f, -10f))

                xyPad.position = Vector2F(1.5f, -1.5f)
                helper.knobPositionPx.shouldBe(Vector2F(190f, 190f))
            }

            it("calculates position from px") {
                helper.positionFromPx(Vector2F(100f, 100f))
                    .shouldBe(Vector2F(0f, 0f))
            }

            it("has no buffer zone corresponding to the knob size") {
                helper.positionFromPx(Vector2F(0f, 0f))
                    .shouldBe(Vector2F(-1f, 1f))
                helper.positionFromPx(Vector2F(10f, 10f))
                    .shouldBe(Vector2F(-.9f, .9f))
                helper.positionFromPx(Vector2F(-5f, -5f))
                    .shouldBe(Vector2F(-1f, 1f))

                helper.positionFromPx(Vector2F(190f, 190f))
                    .shouldBe(Vector2F(.9f, -.9f))
                helper.positionFromPx(Vector2F(200f, 200f))
                    .shouldBe(Vector2F(1f, -1f))
                helper.positionFromPx(Vector2F(210f, 210f))
                    .shouldBe(Vector2F(1f, -1f))
            }
        }
    }
})