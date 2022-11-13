package baaahs.util

import baaahs.camelize
import baaahs.hyphenize
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

    describe("String.hyphenize") {
        it("hyphenizes") {
            "Some String".hyphenize().shouldBe("some-string")
            "Someone's_ABC  String!".hyphenize().shouldBe("someones-abc-string")
            "thisIsAString".hyphenize().shouldBe("this-is-a-string")
        }
    }
})
