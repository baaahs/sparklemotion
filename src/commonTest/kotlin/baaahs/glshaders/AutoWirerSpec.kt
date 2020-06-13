package baaahs.glshaders

import baaahs.glsl.GlslRenderer
import baaahs.ports.DataSourceRef
import baaahs.ports.ShaderInPortRef
import baaahs.ports.ShaderOutPortRef
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
            val patch by value { AutoWirer(Plugins.safe()).autoWire(shaders).resolve() }

            it("creates a reasonable guess patch") {
                expect(
                    setOf(
                        CorePlugin.Time("time"),
                        CorePlugin.Resolution("resolution"),
                        CorePlugin.SliderDataSource("blueness", "Blueness",
                            1f, 0f, 1f, 0.01f),
                        CorePlugin.ScreenUvCoord("gl_FragCoord")
                    )
                ) { patch.dataSources.toSet() }

                expect(
                    listOf(
                        DataSourceRef("time")
                                linkTo ShaderInPortRef("color", "time"),
                        DataSourceRef("resolution")
                                linkTo ShaderInPortRef("color", "resolution"),
                        DataSourceRef("blueness")
                                linkTo ShaderInPortRef("color", "blueness"),
                        DataSourceRef("gl_FragCoord")
                                linkTo ShaderInPortRef("color", "gl_FragCoord")
                    )
                ) { patch.links }
            }

            context("with a UV projection shader") {
                val uvShader = GlslRenderer.uvMapper

                override(shaders) {
                    mapOf("color" to shader, "uv" to uvShader)
                }

                it("creates a reasonable guess patch") {
                    expect(
                        setOf(
                            CorePlugin.Time("time"),
                            CorePlugin.Resolution("resolution"),
                            CorePlugin.SliderDataSource("blueness", "Blueness",
                                1f, 0f, 1f, 0.01f),
                            CorePlugin.UvCoordTexture("uvCoordsTexture")
                        )
                    ) { patch.dataSources.toSet() }

                    expects(
                        listOf(
                            DataSourceRef("time")
                                    linkTo ShaderInPortRef("color", "time"),
                            DataSourceRef("resolution")
                                    linkTo ShaderInPortRef("color", "resolution"),
                            DataSourceRef("blueness")
                                    linkTo ShaderInPortRef("color", "blueness"),
                            ShaderOutPortRef("uv")
                                    linkTo ShaderInPortRef("color", "gl_FragCoord"),
                            DataSourceRef("uvCoordsTexture")
//                            inputPortRef(
//                                "uvCoordsTexture",
//                                "sampler2D",
//                                "U/V Coordinates Texture",
//                                "baaahs.Core:uvCoords",
//                                varName = "in_uvCoordsTexture"
//                            )
                                    linkTo ShaderInPortRef("uv", "uvCoordsTexture")
                        )
                    ) { patch.links }
                }
            }
        }
    }
})