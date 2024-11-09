package baaahs.util

import baaahs.camelize
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import io.kotest.core.spec.style.DescribeSpec

object UtilSpec : DescribeSpec({
    describe("baaahs.util") {
        describe("String.camelize") {
            it("camelizes") {
                expect("Some String".camelize()).toBe("someString")
                expect("Some_ABC  String!".camelize()).toBe("someAbcString")
            }
        }
    }
})