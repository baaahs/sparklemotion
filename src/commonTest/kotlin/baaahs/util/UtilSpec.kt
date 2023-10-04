package baaahs.util

import baaahs.camelize
import baaahs.hyphenize
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object UtilSpec : Spek({
    describe("baaahs.util") {
        describe("String.camelize") {
            it("camelizes") {
                expect("Some String".camelize()).toBe("someString")
                expect("Some_ABC  String!".camelize()).toBe("someAbcString")
                expect("ThisIsAString".camelize()).toBe("thisIsAString")
            }
        }

        describe("String.hyphenize") {
            it("hyphenize") {
                expect("Some String".hyphenize()).toBe("some-string")
                expect("Someone's_ABC  String!".hyphenize()).toBe("someones-abc-string")
                expect("thisIsAString".hyphenize()).toBe("this-is-a-string")
            }
        }
    }
})