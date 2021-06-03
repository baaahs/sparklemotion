package baaahs.gl.render

import baaahs.describe
import baaahs.toEqual
import ch.tutteli.atrium.api.verbs.expect
import org.spekframework.spek2.Spek

object QuadSpec : Spek(({
    describe<Quad> {
        context("for a full-coverage rect") {
            val rect by value { Quad.Rect(0f, 0f, 4f, 1024f) }

            it("should cover the entire UV space") {
                expect(rect.scaleToUv(1024, 4))
                    .toEqual(Quad.Rect(1f, -1f, -1f, 1f))
            }

            it("weird case") {
                val rect = Quad.Rect(top = 0.0f, left = 512.0f, bottom = 1.0f, right = 939.0f)
                expect(rect.scaleToUv(1024, 4))
                    .toEqual(Quad.Rect(top = 1.0f, left = -1.0f, bottom = 0.5f, right = 0.8339844f))
            }
        }
    }
}))