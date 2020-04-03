package baaahs.glshaders

import baaahs.testing.override
import baaahs.testing.value
import org.spekframework.spek2.Spek
import org.spekframework.spek2.dsl.Skip
import org.spekframework.spek2.meta.*
import org.spekframework.spek2.style.specification.Suite
import org.spekframework.spek2.style.specification.describe
import kotlin.test.expect

object ShaderFragmentSpec : Spek({
    describe("shader fragments") {
        val globalVars by value {
            listOf(
                ShaderFragment.GlslVar("float", "time", isConst = false, isUniform = false),
                ShaderFragment.GlslVar("vec2", "resolution", isConst = false, isUniform = false),
                ShaderFragment.GlslVar("float", "blueness", isConst = false, isUniform = false)
            )
        }
        val functions by value {
            listOf(
                ShaderFragment.GlslFunction(
                    "void", "anotherFunc", "(vec2 position)",
                    "{ return position.x / position.y; }"
                ),
                ShaderFragment.GlslFunction(
                    "void", "main", "()", """
                    {
                      vec2 p = gl_FragCoord / resolution;
                      gl_FragColor = vec4(anotherFunc(p), 1. - anotherFunc(p), blueness, 1.);
                    }""".trimIndent()
                )
            )
        }
        val fragment by value { ShaderFragment("name", globalVars, functions) }
        val namespaced by value { fragment.namespace("p0") }

        describe("#namespace") {
            it("prefixes global variable names") {
                expect(
                    listOf(
                        ShaderFragment.GlslVar("float", "p0_time", isConst = false, isUniform = false),
                        ShaderFragment.GlslVar("vec2", "p0_resolution", isConst = false, isUniform = false),
                        ShaderFragment.GlslVar("float", "p0_blueness", isConst = false, isUniform = false)
                    )
                ) { namespaced.globalVars }
            }

            it("prefixes function names and symbol references") {
                expect(
                    listOf(
                        ShaderFragment.GlslFunction(
                            "void", "p0_anotherFunc", "(vec2 position)",
                            "{ return position.x / position.y; }"
                        ),
                        ShaderFragment.GlslFunction(
                            "void", "p0_main", "()", """
                    {
                      vec2 p = gl_FragCoord / p0_resolution;
                      gl_FragColor = vec4(p0_anotherFunc(p), 1. - p0_anotherFunc(p), p0_blueness, 1.);
                    }""".trimIndent()
                        )
                    )
                ) { namespaced.functions }
            }
        }
    }

    describe("statements") {
        val text by value { undefined<String>() }
        val statement by value { GlslAnalyzer.GlslStatement(text) }

        context("variables") {
            val variable by value { statement.asVarOrNull() }

            context("unqualified") {
                override(text) { "int i;" }
                expectValue(ShaderFragment.GlslVar("int", "i")) { variable }
            }

            context("const") {
                override(text) { "const int i = 3;" }
                expectValue(ShaderFragment.GlslVar("int", "i", isConst = true)) { variable }
            }

            context("uniform") {
                override(text) { "uniform vec3 vector;" }
                expectValue(ShaderFragment.GlslVar("vec3", "vector", isUniform = true)) { variable }
            }
        }
    }
})


@Synonym(SynonymType.TEST)
@Descriptions(Description(DescriptionLocation.VALUE_PARAMETER, 0))
fun <T> Suite.expectValue(expected: T, skip: Skip = Skip.No, actual: () -> T) {
    delegate.test("should equal", skip, delegate.defaultTimeout) {
        expect(expected, actual)
    }
}

fun <T> undefined(): T = throw NotImplementedError("value not given")