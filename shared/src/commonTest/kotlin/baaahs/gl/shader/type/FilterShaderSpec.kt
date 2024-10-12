package baaahs.gl.shader.type

import baaahs.describe
import baaahs.gl.openShader
import baaahs.gl.override
import baaahs.gl.testToolchain
import baaahs.show.Shader
import baaahs.toBeSpecified
import baaahs.toEqual
import ch.tutteli.atrium.api.verbs.expect
import org.spekframework.spek2.Spek

@Suppress("unused")
object FilterShaderSpec : Spek({
    describe<FilterShader> {
        val shaderText by value { toBeSpecified<String>() }
        val shader by value { Shader("Shader", shaderText) }
        val shaderAnalysis by value { testToolchain.analyze(shader) }
        val openShader by value { testToolchain.openShader(shader) }
        val shaderType by value { FilterShader }

        context("when return type and any input are color") {
            override(shaderText) {
                """
                    // @return color
                    // @param inColor color
                    vec4 main(vec4 inColor) { ... }
                """.trimIndent()
            }

            it("#match returns MatchAndFilter") {
                expect(FilterShader.matches(shaderAnalysis))
                    .toEqual(ShaderType.MatchLevel.MatchAndFilter)
            }
        }

        context("when return type is color and no input is color") {
            override(shaderText) {
                """
                    // @return color
                    vec4 main(vec4 foo) { ... }
                """.trimIndent()
            }

            it("#match returns NoMatch") {
                expect(FilterShader.matches(shaderAnalysis))
                    .toEqual(ShaderType.MatchLevel.NoMatch)
            }
        }

        context("#template") {
            override(shaderText) { shaderType.newShaderFromTemplate().src }

            it("generates a filter shader") {
                expect(openShader.shaderType)
                    .toEqual(shaderType)
            }
        }

        context("when an input port has an explicit PluginRef") {
            override(shaderText) {
                """
                    // @param uv uv-coordinate
                    // @@baaahs.TestPlugin:TestPlugin
                    vec4 videoIn(vec2 uv);
                    
                    // @return color
                    vec4 main() { ... }
                """.trimIndent()
            }

            it("shouldn't count as a filter") {
                expect(FilterShader.matches(shaderAnalysis))
                    .toEqual(ShaderType.MatchLevel.NoMatch)
            }
        }
    }
})