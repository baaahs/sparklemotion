package baaahs.gl.patch

import baaahs.describe
import baaahs.gl.glsl.GlslType
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import org.spekframework.spek2.Spek

object ContentTypeSpec : Spek({
    describe<ContentType> {
        context("unknown content types") {
            it("constructs an unknown content type from a GLSL type") {
                expect(ContentType.unknown(GlslType.Float))
                    .toBe(ContentType("unknown/float", "Unknown float", GlslType.Float, false))
                expect(ContentType.unknown(GlslType.Vec4))
                    .toBe(ContentType("unknown/vec4", "Unknown vec4", GlslType.Vec4, false))
            }

            it("#isUnknown is true") {
                expect(ContentType.unknown(GlslType.Float).isUnknown()).toBe(true)
                expect(ContentType.Unknown.isUnknown()).toBe(true)
                expect(ContentType.Color.isUnknown()).toBe(false)
            }
        }
    }
})