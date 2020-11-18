package baaahs.util

import baaahs.describe
import org.spekframework.spek2.Spek
import kotlin.test.expect

object TaggedExprSpec : Spek({
    describe<TaggedExpr> {
        it("parses") {
            expect() { TaggedExpr.from("a | b") }

        }

    }

})