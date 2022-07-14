package baaahs.control

import baaahs.describe
import baaahs.gadgets.XyPad
import baaahs.geom.Vector2F
import baaahs.toEqual
import ch.tutteli.atrium.api.verbs.expect
import org.spekframework.spek2.Spek

object XyPadControlSpec : Spek({
    describe<XyPad.Helper> {
        val xyPad by value { XyPad("Brightness") }
        val padSize by value { Vector2F(200f, 200f) }
        val knobSize by value { Vector2F(20f, 20f) }
        val helper by value { XyPad.Helper(xyPad, padSize, knobSize) }

        it("calculates knob positions, taking knob size into account") {
            expect(helper.knobPositionPx).toEqual(Vector2F(90f, 90f))

            xyPad.position = Vector2F(-1f, 1f)
            expect(helper.knobPositionPx).toEqual(Vector2F(0f, 0f))

            xyPad.position = Vector2F(1f, -1f)
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
    }
})