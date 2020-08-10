package baaahs.glshaders

import baaahs.glsl.Shaders.cylindricalProjection
import baaahs.show.Shader
import baaahs.show.ShaderChannel
import baaahs.show.ShaderType
import baaahs.show.mutable.MutableDataSource
import baaahs.show.mutable.MutableShader
import baaahs.show.mutable.MutableShaderChannel
import baaahs.show.mutable.MutableShaderInstance
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.expect

object AutoWirerSpec : Spek({
    describe("AutoWirer") {
        val autoWirer by value { AutoWirer(Plugins.safe()) }

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
            val paintShader by value { autoWirer.glslAnalyzer.import(shaderText) }
            val shaders by value { arrayOf(paintShader) }
            val patch by value { autoWirer.autoWire(*shaders).acceptSymbolicChannelLinks().resolve() }

            it("creates a reasonable guess patch") {
                expect(
                    listOf(
                        MutableShaderInstance(
                            MutableShader(paintShader),
                            hashMapOf(
                                "time" to MutableDataSource(CorePlugin.Time()),
                                "blueness" to MutableDataSource(
                                    CorePlugin.SliderDataSource("Blueness", 1f, 0f, 1f, null)
                                ),
                                "resolution" to MutableDataSource(CorePlugin.Resolution()),
                                "gl_FragCoord" to MutableShaderChannel(ShaderChannel.Main)
                            ),
                            shaderChannel = ShaderChannel.Main,
                            priority = 0f
                        )
                    )
                ) { patch.mutableShaderInstances }
            }

            context("with a UV projection shader") {
                val uvShader = cylindricalProjection
                val uvShaderInst by value {
                    MutableShaderInstance(
                        MutableShader(uvShader)
                    )
                }

                override(shaders) { arrayOf(paintShader, uvShader) }

                it("creates a reasonable guess patch") {
                    expects(
                        listOf(
                            MutableShaderInstance(
                                MutableShader(paintShader),
                                hashMapOf(
                                    "time" to MutableDataSource(CorePlugin.Time()),
                                    "resolution" to MutableDataSource(CorePlugin.Resolution()),
                                    "blueness" to MutableDataSource(
                                        CorePlugin.SliderDataSource("Blueness", 1f, 0f, 1f, null)
                                    ),
                                    "gl_FragCoord" to MutableShaderChannel(ShaderChannel.Main)
                                ),
                                shaderChannel = ShaderChannel.Main
                            ),
                            uvShaderInst.apply {
                                incomingLinks.putAll(mapOf(
                                    "pixelCoordsTexture" to MutableDataSource(
                                        CorePlugin.PixelCoordsTexture()
                                    ),
                                    "modelInfo" to MutableDataSource(
                                        CorePlugin.ModelInfoDataSource("ModelInfo")
                                    )
                                ))
                                shaderChannel = ShaderChannel.Main
                            }
                        )
                    ) { patch.mutableShaderInstances }
                }
            }

            context("with a filter shader") {
                val filterShader = Shader(
                    "Brightness Filter",
                    ShaderType.Filter,
                    """
                        uniform float brightness; // @@Slider min=0 max=1 default=1
                        vec4 mainFilter(vec4 colorIn) {
                          colorOut = colorIn * brightness;
                        }
                    """.trimIndent())

                override(shaders) { arrayOf(filterShader) }

                it("creates a reasonable guess patch") {
                    expects(
                        listOf(
                            MutableShaderInstance(
                                MutableShader(filterShader),
                                hashMapOf(
                                    "brightness" to MutableDataSource(
                                        CorePlugin.SliderDataSource("Brightness", 1f, 0f, 1f, null)
                                    ),
                                    "gl_FragColor" to MutableShaderChannel(ShaderChannel.Main)
                                ),
                                shaderChannel = ShaderChannel.Main
                            )
                        )
                    ) { patch.mutableShaderInstances }
                }
            }

            context("with a distortion shader") {
                val filterShader = Shader(
                    "Flip Y",
                    ShaderType.Distortion,
                    """
                        vec2 mainDistortion(vec2 uvIn) {
                          return vec2(uvIn.x, 1.0 - uvIn.y);
                        }
                    """.trimIndent())


                override(shaders) { arrayOf(filterShader) }

                it("creates a reasonable guess patch") {
                    expects(
                        listOf(
                            MutableShaderInstance(
                                MutableShader(filterShader),
                                hashMapOf(
                                    "gl_FragCoord" to MutableShaderChannel(ShaderChannel.Main)
                                ),
                                shaderChannel = ShaderChannel.Main
                            )
                        )
                    ) { patch.mutableShaderInstances }
                }
            }
        }
    }
})