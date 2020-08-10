package baaahs.gl.glsl

import baaahs.gl.expectValue
import baaahs.gl.override
import baaahs.gl.undefined
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object GlslCodeSpec : Spek({
    describe("statements") {
        val text by value { undefined<String>() }
        val statement by value { GlslAnalyzer.GlslStatement(text) }

        context("variables") {
            val variable by value { statement.asVarOrNull() }

            context("unqualified") {
                override(text) { "int i;" }
                expectValue(GlslCode.GlslVar("int", "i", "int i;")) { variable }
            }

            context("const") {
                override(text) { "const int i = 3;" }
                expectValue(GlslCode.GlslVar("int", "i", "const int i = 3;", isConst = true)) { variable }
            }

            context("uniform") {
                override(text) { "uniform vec3 vector;" }
                expectValue(GlslCode.GlslVar("vec3", "vector", "uniform vec3 vector;", isUniform = true)) { variable }
            }
        }

        context("functions") {
            val function by value { statement.asFunctionOrNull() }

            context("simple") {
                override(text) { "float rand(vec2 uv) { return fract(sin(dot(uv.xy,vec2(12.9898,78.233))) * 43758.5453); }" }
                expectValue(
                    GlslCode.GlslFunction(
                        "float", "rand", "vec2 uv",
                        "float rand(vec2 uv) { return fract(sin(dot(uv.xy,vec2(12.9898,78.233))) * 43758.5453); }"
                    )
                ) { function }
            }
        }

        context("struct") {
            val struct by value { statement.asStructOrNull() }

            context("uniform") {
                override(text) { "struct MovingHead {\n  float pan;\n  float tilt;\n};" }
                expectValue(
                    GlslCode.GlslStruct(
                        "MovingHead", "struct MovingHead {\n  float pan;\n  float tilt;\n};"
                    )
                ) { struct }
            }
        }
    }
})
