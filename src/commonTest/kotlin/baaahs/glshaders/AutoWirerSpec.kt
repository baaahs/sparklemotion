package baaahs.glshaders

import baaahs.glsl.Shaders.cylindricalUvMapper
import baaahs.show.*
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
            val colorShader by value { Shader(shaderText) }
            val colorShaderInst by value { ShaderInstanceEditor(ShaderEditor(colorShader)) }
            val shaders by value { arrayOf(colorShader) }
            val patch by value { AutoWirer(Plugins.safe()).autoWire(*shaders).resolve() }

            it("creates a reasonable guess patch") {
                expect(
                    listOf(
                        ShaderInstanceEditor(
                            ShaderEditor(colorShader),
                            hashMapOf(
                                "time" to DataSourceEditor(CorePlugin.Time()),
                                "resolution" to DataSourceEditor(CorePlugin.Resolution()),
                                "blueness" to DataSourceEditor(
                                    CorePlugin.SliderDataSource("Blueness", 1f, 0f, 1f, null)),
                                "gl_FragCoord" to DataSourceEditor(CorePlugin.ScreenUvCoord())
                            ),
                            role = ShaderRole.Paint
                        )
                    )
                ) { patch.shaderInstances }
            }

            context("with a UV projection shader") {
                val uvShader = cylindricalUvMapper.shader
                val uvShaderInst by value { ShaderInstanceEditor(ShaderEditor(uvShader)) }

                override(shaders) { arrayOf(colorShader, uvShader) }

                it("creates a reasonable guess patch") {
                    expects(
                        listOf(
                            ShaderInstanceEditor(
                                ShaderEditor(colorShader),
                                hashMapOf(
                                    "time" to DataSourceEditor(CorePlugin.Time()),
                                    "resolution" to DataSourceEditor(CorePlugin.Resolution()),
                                    "blueness" to DataSourceEditor(
                                        CorePlugin.SliderDataSource("Blueness", 1f, 0f, 1f, null)),
                                    "gl_FragCoord" to ShaderOutPortEditor(uvShaderInst, ShaderOutPortRef.ReturnValue)
                                ),
                                role = ShaderRole.Paint
                            ),
                            uvShaderInst.apply {
                                incomingLinks.putAll(mapOf(
                                    "pixelCoordsTexture" to DataSourceEditor(CorePlugin.PixelCoordsTexture()),
                                    "modelInfo" to DataSourceEditor(CorePlugin.ModelInfoDataSource("ModelInfo"))
                                ))
                                role = ShaderRole.Projection
                            }
                        )
                    ) { patch.shaderInstances }
                }
            }
        }
    }
})