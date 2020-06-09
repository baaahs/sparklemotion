package baaahs.glshaders

import baaahs.glsl.GlslRenderer
import baaahs.ports.ShaderInPortRef
import baaahs.ports.ShaderOutPortRef
import baaahs.ports.inputPortRef
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.expect

object AutoWirerSpec : Spek({

    describe("AutoWirer") {
        describe(".autoWire") {
            val shaderText by value {
                /**language=glsl*/
                """
                // This Shader's Name
                // Other stuff.
                
                uniform float time;
                uniform vec2  resolution;
                uniform float blueness;
                int someGlobalVar;
                const int someConstVar = 123;
                
                int anotherFunc(int i) { return i; }
                
                void main( void ) {
                    vec2 uv = gl_FragCoord.xy / resolution.xy;
                    someGlobalVar = anotherFunc(someConstVar);
                    gl_FragColor = vec4(uv.xy, blueness, 1.);
                }
                """.trimIndent()
            }
            val shader by value { GlslAnalyzer().asShader(shaderText) as ColorShader }
            val shaders by value { mapOf("color" to shader) }
            val patch by value { AutoWirer(Plugins.safe()).autoWire(shaders) }

            it("creates a reasonable guess patch") {
                expect(
                    listOf(
                        GlslProgram.Time
                                linkTo ShaderInPortRef("color", "time"),
                        GlslProgram.Resolution
                                linkTo ShaderInPortRef("color", "resolution"),
                        inputPortRef("bluenessSlider", "float", "Blueness", "baaahs.Core:Slider", varName = "in_bluenessSlider")
                                linkTo ShaderInPortRef("color", "blueness"),
                        GlslProgram.GlFragCoord
                                linkTo ShaderInPortRef("color", "gl_FragCoord")
                    )
                ) { patch.links }
            }

            context("with a UV projection shader") {
                val uvShader= GlslRenderer.uvMapper

                override(shaders) {
                    mapOf("color" to shader, "uv" to uvShader)
                }

                it("creates a reasonable guess patch") {
                    expects(
                        listOf(
                            GlslProgram.Time
                                    linkTo ShaderInPortRef("color", "time"),
                            GlslProgram.Resolution
                                    linkTo ShaderInPortRef("color", "resolution"),
                            inputPortRef("bluenessSlider", "float", "Blueness", "baaahs.Core:Slider", varName = "in_bluenessSlider")
                                    linkTo ShaderInPortRef("color", "blueness"),
                            ShaderOutPortRef("uv")
                                    linkTo ShaderInPortRef("color", "gl_FragCoord"),
                            inputPortRef("uvCoordsTexture", "sampler2D", "U/V Coordinates Texture", "baaahs.Core:uvCoords", varName = "in_uvCoordsTexture")
                                    linkTo ShaderInPortRef("uv", "uvCoordsTexture")
                        )
                    ) { patch.links }
                }
            }
        }
    }
})