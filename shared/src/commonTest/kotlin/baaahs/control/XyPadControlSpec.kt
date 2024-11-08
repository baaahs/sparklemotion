package baaahs.control

import baaahs.describe
import baaahs.gadgets.XyPad
import baaahs.geom.Vector2F
import baaahs.kotest.value
import baaahs.toEqual
import ch.tutteli.atrium.api.verbs.expect
import io.kotest.core.spec.style.DescribeSpec

object XyPadControlSpec : DescribeSpec({
    describe<XyPad.Helper> {
        val xyPad by value { XyPad("Brightness") }
        val padSize by value { Vector2F(200f, 200f) }
        val knobSize by value { Vector2F(20f, 20f) }
        val knobBufferZone by value { arrayOf(true) }
        val helper by value { XyPad.Helper(xyPad, padSize, knobSize, knobBufferZone[0]) }

        it("calculates crosshair and knob positions, taking knob size into account") {
            expect(helper.crosshairPositionPx).toEqual(Vector2F(100f, 100f))
            expect(helper.knobPositionPx).toEqual(Vector2F(90f, 90f))

            xyPad.position = Vector2F(-1f, 1f)
            expect(helper.crosshairPositionPx).toEqual(Vector2F(10f, 10f))
            expect(helper.knobPositionPx).toEqual(Vector2F(0f, 0f))

            xyPad.position = Vector2F(1f, -1f)
            expect(helper.crosshairPositionPx).toEqual(Vector2F(190f, 190f))
            expect(helper.knobPositionPx).toEqual(Vector2F(180f, 180f))
        }

        it("clamps knob position to bounds") {
            xyPad.position = Vector2F(-1.5f, 1.5f)
            expect(helper.knobPositionPx).toEqual(Vector2F(0f, 0f))

            xyPad.position = Vector2F(1.5f, -1.5f)
            expect(helper.knobPositionPx).toEqual(Vector2F(180f, 180f))
        }

        it("calculates position from px") {
            expect(helper.positionFromPx(Vector2F(100f, 100f)))
                .toEqual(Vector2F(0f, 0f))
        }

        it("maintains a buffer zone corresponding to the knob size") {
            expect(helper.positionFromPx(Vector2F(0f, 0f)))
                .toEqual(Vector2F(-1f, 1f))
            expect(helper.positionFromPx(Vector2F(10f, 10f)))
                .toEqual(Vector2F(-1f, 1f))
            expect(helper.positionFromPx(Vector2F(-5f, -5f)))
                .toEqual(Vector2F(-1f, 1f))

            expect(helper.positionFromPx(Vector2F(190f, 190f)))
                .toEqual(Vector2F(1f, -1f))
            expect(helper.positionFromPx(Vector2F(200f, 200f)))
                .toEqual(Vector2F(1f, -1f))
            expect(helper.positionFromPx(Vector2F(210f, 210f)))
                .toEqual(Vector2F(1f, -1f))
        }

        context("when knob buffer zone is disabled") {
            // wtf? Making knobBufferZone a boolean causes spec to fail.
            beforeEach { knobBufferZone[0] = false }

            it("calculates crosshair and knob positions, taking knob size into account") {
                expect(helper.crosshairPositionPx).toEqual(Vector2F(100f, 100f))
                expect(helper.knobPositionPx).toEqual(Vector2F(90f, 90f))

                xyPad.position = Vector2F(-1f, 1f)
                expect(helper.crosshairPositionPx).toEqual(Vector2F(0f, 0f))
                expect(helper.knobPositionPx).toEqual(Vector2F(-10f, -10f))

                xyPad.position = Vector2F(1f, -1f)
                expect(helper.crosshairPositionPx).toEqual(Vector2F(200f, 200f))
                expect(helper.knobPositionPx).toEqual(Vector2F(190f, 190f))
            }


            it("clamps knob position to bounds") {
                xyPad.position = Vector2F(-1.5f, 1.5f)
                expect(helper.knobPositionPx).toEqual(Vector2F(-10f, -10f))

                xyPad.position = Vector2F(1.5f, -1.5f)
                expect(helper.knobPositionPx).toEqual(Vector2F(190f, 190f))
            }

            it("calculates position from px") {
                expect(helper.positionFromPx(Vector2F(100f, 100f)))
                    .toEqual(Vector2F(0f, 0f))
            }

            it("has no buffer zone corresponding to the knob size") {
                expect(helper.positionFromPx(Vector2F(0f, 0f)))
                    .toEqual(Vector2F(-1f, 1f))
                expect(helper.positionFromPx(Vector2F(10f, 10f)))
                    .toEqual(Vector2F(-.9f, .9f))
                expect(helper.positionFromPx(Vector2F(-5f, -5f)))
                    .toEqual(Vector2F(-1f, 1f))

                expect(helper.positionFromPx(Vector2F(190f, 190f)))
                    .toEqual(Vector2F(.9f, -.9f))
                expect(helper.positionFromPx(Vector2F(200f, 200f)))
                    .toEqual(Vector2F(1f, -1f))
                expect(helper.positionFromPx(Vector2F(210f, 210f)))
                    .toEqual(Vector2F(1f, -1f))
            }
        }
    }
})