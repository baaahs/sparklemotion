package baaahs.glshaders

import baaahs.glsl.Shaders.cylindricalUvMapper
import baaahs.show.DataSourceEditor
import baaahs.show.OutputPortEditor
import baaahs.show.ShaderEditor
import baaahs.show.ShaderOutPortRef
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
            val colorShader by value { GlslAnalyzer().asShader(shaderText) as ColorShader }
            val shaders by value { arrayOf(colorShader) }
            val patch by value { AutoWirer(Plugins.safe()).autoWire(*shaders).resolve() }

            it("creates a reasonable guess patch") {
                expect(
                    listOf(
                        DataSourceEditor(CorePlugin.Time())
                                linkTo ShaderEditor(colorShader.shader).inputPort("time"),
                        DataSourceEditor(CorePlugin.Resolution())
                                linkTo ShaderEditor(colorShader.shader).inputPort("resolution"),
                        DataSourceEditor(
                            CorePlugin.SliderDataSource("Blueness", 1f, 0f, 1f, null))
                                linkTo ShaderEditor(colorShader.shader).inputPort("blueness"),
                        DataSourceEditor(CorePlugin.ScreenUvCoord())
                                linkTo ShaderEditor(colorShader.shader).inputPort("gl_FragCoord"),
                        ShaderEditor(colorShader.shader).outputPort("gl_FragColor")
                                linkTo OutputPortEditor(GlslProgram.PixelColor.portId)
                    )
                ) { patch.links }
            }

            context("with a UV projection shader") {
                val uvShader = cylindricalUvMapper

                override(shaders) { arrayOf(colorShader, uvShader) }

                it("creates a reasonable guess patch") {
                    expects(
                        listOf(
                            DataSourceEditor(CorePlugin.Time())
                                    linkTo ShaderEditor(colorShader.shader).inputPort("time"),
                            DataSourceEditor(CorePlugin.Resolution())
                                    linkTo ShaderEditor(colorShader.shader).inputPort("resolution"),
                            DataSourceEditor(CorePlugin.SliderDataSource("Blueness", 1f, 0f, 1f, null))
                                    linkTo ShaderEditor(colorShader.shader).inputPort("blueness"),
                            ShaderEditor(uvShader.shader).outputPort(ShaderOutPortRef.ReturnValue)
                                    linkTo ShaderEditor(colorShader.shader).inputPort("gl_FragCoord"),
                            DataSourceEditor(CorePlugin.PixelCoordsTexture())
                                    linkTo ShaderEditor(uvShader.shader).inputPort("pixelCoordsTexture"),
                            DataSourceEditor(CorePlugin.ModelInfoDataSource("ModelInfo"))
                                    linkTo ShaderEditor(uvShader.shader).inputPort("modelInfo"),
                            ShaderEditor(colorShader.shader).outputPort("gl_FragColor")
                                    linkTo OutputPortEditor(GlslProgram.PixelColor.portId)
                        )
                    ) { patch.links }
                }
            }
        }
    }
})