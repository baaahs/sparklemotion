package baaahs.gl.patch

import baaahs.describe
import baaahs.gl.glsl.GlslType
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.*
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue

object ContentTypeSpec : DescribeSpec({
    describe<ContentType> {
        context("unknown content types") {
            it("constructs an unknown content type from a GLSL type") {
                ContentType.unknown(GlslType.Float)
                    .shouldBe(ContentType("unknown/float", "Unknown float", GlslType.Float, false))
                ContentType.unknown(GlslType.Vec4)
                    .shouldBe(ContentType("unknown/vec4", "Unknown vec4", GlslType.Vec4, false))
            }

            it("#isUnknown is true") {
                ContentType.unknown(GlslType.Float).isUnknown().shouldBeTrue()
                ContentType.Unknown.isUnknown().shouldBeTrue()
                ContentType.Color.isUnknown().shouldBeFalse()
            }
        }
    }
})