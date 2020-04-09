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
                GlslCode.GlslVar("float", "time", isConst = false, isUniform = false),
                GlslCode.GlslVar("vec2", "resolution", isConst = false, isUniform = false),
                GlslCode.GlslVar("float", "blueness", isConst = false, isUniform = false)
            )
        }
        val functions by value {
            listOf(
                GlslCode.GlslFunction(
                    "void", "anotherFunc", "(vec2 position)",
                    "{ return position.x / position.y; }"
                ),
                GlslCode.GlslFunction(
                    "void", "main", "()", """
                    {
                      vec2 p = gl_FragCoord / resolution;
                      gl_FragColor = vec4(anotherFunc(p), 1. - anotherFunc(p), blueness, 1.);
                    }""".trimIndent()
                )
            )
        }
        val fragment by value { GlslCode("name", "desc", globalVars, functions) }

        describe("#namespace") {
            val namespaced by value { fragment.namespace("p0") }

            it("prefixes global variable names") {
                expect(
                    listOf(
                        GlslCode.GlslVar("float", "p0_time", isConst = false, isUniform = false),
                        GlslCode.GlslVar("vec2", "p0_resolution", isConst = false, isUniform = false),
                        GlslCode.GlslVar("float", "p0_blueness", isConst = false, isUniform = false)
                    )
                ) { namespaced.globalVars }
            }

            it("prefixes function names, symbol references, and special globals") {
                expect(
                    listOf(
                        GlslCode.GlslFunction(
                            "void", "p0_anotherFunc", "(vec2 position)",
                            "{ return position.x / position.y; }"
                        ),
                        GlslCode.GlslFunction(
                            "void", "p0_main", "()", """
                    {
                      vec2 p = p0_gl_FragCoord / p0_resolution;
                      p0_gl_FragColor = vec4(p0_anotherFunc(p), 1. - p0_anotherFunc(p), p0_blueness, 1.);
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
                expectValue(GlslCode.GlslVar("int", "i")) { variable }
            }

            context("const") {
                override(text) { "const int i = 3;" }
                expectValue(GlslCode.GlslVar("int", "i", isConst = true)) { variable }
            }

            context("uniform") {
                override(text) { "uniform vec3 vector;" }
                expectValue(GlslCode.GlslVar("vec3", "vector", isUniform = true)) { variable }
            }
        }

        context("functions") {
            val variable by value { statement.asFunctionOrNull() }

            context("simple") {
                override(text) { "float rand(vec2 uv) { return fract(sin(dot(uv.xy,vec2(12.9898,78.233))) * 43758.5453); }" }
                expectValue(
                    GlslCode.GlslFunction(
                        "float", "rand", "vec2 uv",
                        "{ return fract(sin(dot(uv.xy,vec2(12.9898,78.233))) * 43758.5453); }"
                    )
                ) { variable }
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