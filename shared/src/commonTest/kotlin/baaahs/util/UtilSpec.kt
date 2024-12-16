package baaahs.util

import baaahs.camelize
import baaahs.dasherize
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

        describe("String.dasherize") {
            it("dasherizes") {
                "SomeString".dasherize().shouldBe("some-string")
                "Some String".dasherize().shouldBe("some-string")
                "Some_ABC  String".dasherize().shouldBe("some-abc-string")
            }
        }
    }
})