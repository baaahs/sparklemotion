package baaahs.util

import baaahs.camelize
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.*

class UtilSpec : DescribeSpec({
    describe("baaahs.util") {
        describe("String.camelize") {
            it("camelizes") {
                "Some String".camelize().shouldBe("someString")
                "Some_ABC  String!".camelize().shouldBe("someAbcString")
            }
        }
    }
})