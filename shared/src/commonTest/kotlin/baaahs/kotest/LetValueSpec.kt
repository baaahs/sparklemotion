package baaahs.kotest

import io.kotest.core.spec.style.DescribeSpec
import kotlin.test.expect

class LetValueSpec : DescribeSpec({
    val topLevelValue by value { "top level value" }

    describe("level 1") {
        println("before str level 1")
        val str by value { "for level 1" }

        describe("level 2") {
            println("before str level 2")
            value(str) { "for level 2" }

            describe("level 3") {
                it("should permit values to be declared at the top level") {
                    println("about to test")
                    expect("for level 2") {
                        str
                    }
                    expect("top level value") { topLevelValue }
                }
            }
        }
    }

    describe("let variables") {
        println("before str")
        val str by value { "base" }
        val list by value { mutableListOf<String>() }
        val anotherList by value { mutableListOf<String>() }

        beforeEach { anotherList.add("1 $str") }

        it("should permit values to be declared at the top level") {
            expect("top level value") { topLevelValue }
        }

        it("should memoize the value within a single test") {
            println("running should memoize the value within a single test")
            list.add("a string")
            list.add("another string")

            expect(listOf("a string", "another string")) { list }
        }

        it("should regenerate the value for every test") {
            expect(emptyList<String>()) { list }
        }

        it("should return the specified value for this context") {
            expect("base") { str }
        }

        it("should use context values in befores") {
            expect(listOf("1 base")) { anotherList }
        }

        describe("evaluation of values") {
            var string = ""
            val valueWithString by value { "with $string" }

            beforeEach { string = "initial value" }

            it("should be lazy") {
                string = "modified value"
                expect("with modified value") { valueWithString }
            }
        }

        context("in another context") {
            beforeEach { anotherList.add("2A $str") }

            value(str) { "xyz context" }

            beforeEach { anotherList.add("2B $str") }

            it("should return the value for that context") {
                expect("xyz context") { str }
            }

            it("should use context-overridden values in outer befores") {
                expect(listOf("1 xyz context", "2A xyz context", "2B xyz context")) { anotherList }
            }
        }

        it("should return the correct value after a nested context override") {
            expect("base") { str }
        }

        /**
         * support calling a value for the first time from an `afterEachTest`.
         *
         * testCoroutineContext can't be used from beforeEachGroup or afterEachGroup
        java.lang.IllegalStateException: testCoroutineContext can't be used from beforeEachGroup or afterEachGroup
        at org.spekframework.spek2.runtime.lifecycle.LetValueGetter.getValue(LetValueCreator.kt:42)
        at baaahs.PubSubSpec$1$1$2.invoke(PubSubSpec.kt:32)
        at baaahs.PubSubSpec$1$1$2.invoke(PubSubSpec.kt:18)
        at org.spekframework.spek2.runtime.scope.Fixtures.invokeAfterTestFixtures(fixtures.kt:47)

         */

        context("with nullable values") {
            val nullable by value<Any?> { null }

            it("can return null") {
                expect(null) { nullable }
            }
        }
    }
})
