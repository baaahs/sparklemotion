package baaahs.gl.shader

import baaahs.describe
import baaahs.gl.glsl.GlslAnalyzer
import baaahs.gl.override
import baaahs.gl.testPlugins
import baaahs.show.ShaderType
import baaahs.toBeSpecified
import baaahs.toEqual
import ch.tutteli.atrium.api.verbs.expect
import org.spekframework.spek2.Spek

@Suppress("unused")
object FilterShaderSpec : Spek({
    describe<FilterShader> {
        val shaderText by value { toBeSpecified<String>() }
        val analyzer by value { GlslAnalyzer(testPlugins()) }
        val shaderAnalysis by value { analyzer.validate(shaderText) }
        val openShader by value { analyzer.openShader(shaderText) }
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
                expect(shaderType.matches(shaderAnalysis))
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
                expect(shaderType.matches(shaderAnalysis))
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
    }
})
