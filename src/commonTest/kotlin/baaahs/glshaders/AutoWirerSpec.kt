package baaahs.glshaders

import baaahs.glsl.Shaders.cylindricalUvMapper
import baaahs.show.Shader
import baaahs.show.ShaderChannel
import baaahs.show.mutable.DataSourceEditor
import baaahs.show.mutable.ShaderChannelEditor
import baaahs.show.mutable.ShaderEditor
import baaahs.show.mutable.ShaderInstanceEditor
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
            val shaders by value { arrayOf(colorShader) }
            val patch by value { AutoWirer(Plugins.safe()).autoWire(*shaders).acceptSymbolicChannelLinks().resolve() }

            it("creates a reasonable guess patch") {
                expect(
                    listOf(
                        ShaderInstanceEditor(
                            ShaderEditor(colorShader),
                            hashMapOf(
                                "time" to DataSourceEditor(CorePlugin.Time()),
                                "blueness" to DataSourceEditor(
                                    CorePlugin.SliderDataSource("Blueness", 1f, 0f, 1f, null)
                                ),
                                "resolution" to DataSourceEditor(CorePlugin.Resolution()),
                                "gl_FragCoord" to ShaderChannelEditor(
                                    ShaderChannel.Main
                                )
                            ),
                            shaderChannel = ShaderChannel.Main
                        )
                    )
                ) { patch.shaderInstances }
            }

            context("with a UV projection shader") {
                val uvShader = cylindricalUvMapper.shader
                val uvShaderInst by value {
                    ShaderInstanceEditor(
                        ShaderEditor(uvShader)
                    )
                }

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
                                        CorePlugin.SliderDataSource("Blueness", 1f, 0f, 1f, null)
                                    ),
                                    "gl_FragCoord" to ShaderChannelEditor(
                                        ShaderChannel.Main
                                    )
                                ),
                                shaderChannel = ShaderChannel.Main
                            ),
                            uvShaderInst.apply {
                                incomingLinks.putAll(mapOf(
                                    "pixelCoordsTexture" to DataSourceEditor(
                                        CorePlugin.PixelCoordsTexture()
                                    ),
                                    "modelInfo" to DataSourceEditor(
                                        CorePlugin.ModelInfoDataSource(
                                            "ModelInfo"
                                        )
                                    )
                                ))
                                shaderChannel = ShaderChannel.Main
                            }
                        )
                    ) { patch.shaderInstances }
                }
            }

            context("with a filter shader") {
                val filterShader = Shader("""
                    // Brightness Filter
                    uniform float brightness; // @@Slider min=0 max=1 default=1
                    vec4 filterImage(vec4 colorIn) {
                      colorOut = colorIn * brightness;
                    }
                """.trimIndent())

                override(shaders) { arrayOf(filterShader) }

                it("creates a reasonable guess patch") {
                    expects(
                        listOf(
                            ShaderInstanceEditor(
                                ShaderEditor(filterShader),
                                hashMapOf(
                                    "brightness" to DataSourceEditor(
                                        CorePlugin.SliderDataSource("Brightness", 1f, 0f, 1f, null)
                                    ),
                                    "gl_FragColor" to ShaderChannelEditor(
                                        ShaderChannel.Main
                                    )
                                ),
                                shaderChannel = ShaderChannel.Main
                            )
                        )
                    ) { patch.shaderInstances }
                }
            }
        }
    }
})