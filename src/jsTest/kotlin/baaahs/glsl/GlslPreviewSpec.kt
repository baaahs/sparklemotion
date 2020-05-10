package baaahs.glsl

import org.spekframework.spek2.Spek
import org.spekframework.spek2.dsl.Skip
import org.spekframework.spek2.style.specification.describe
import kotlin.browser.document

object GlslPreviewSpec : Spek({
    describe("GlslPreview") {
        it("exists", skip = Skip.Yes()) {
            GlslPreview(
                document.createElement("div"),
                document.createElement("div"),
                "src"
            )
        }
    }
})