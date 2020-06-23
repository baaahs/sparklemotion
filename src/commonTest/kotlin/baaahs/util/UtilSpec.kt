package baaahs.util

import baaahs.camelize
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.expect

object UtilSpec : Spek({
    describe("baaahs.util") {
        describe("String.camelize") {
            it("camelizes") {
                expect("someString") { "Some String".camelize() }
                expect("someAbcString") { "Some_ABC  String!".camelize() }
            }
        }
    }
})