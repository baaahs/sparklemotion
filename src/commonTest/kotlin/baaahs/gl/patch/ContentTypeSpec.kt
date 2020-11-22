package baaahs.gl.patch

import baaahs.describe
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.toEqual
import ch.tutteli.atrium.api.verbs.expect
import org.spekframework.spek2.Spek

@Suppress("unused")
object ContentTypeSpec : Spek({
    describe(ContentType::stream) {
        it("should append \"-stream\" to the id") {
            expect(ContentType("color", "Color", GlslType.Vec4).stream())
                .toEqual(ContentType("color-stream", "Color Stream", GlslType.Vec4, isStream = true))
        }
    }
})