package baaahs.glshaders

import baaahs.glsl.Shaders.cylindricalUvMapper
import baaahs.show.Shader
import baaahs.show.ShaderChannel
import baaahs.show.ShaderType
import baaahs.show.mutable.MutableDataSourcePort
import baaahs.show.mutable.MutableShader
import baaahs.show.mutable.MutableShaderChannelPort
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
                                "time" to MutableDataSourcePort(CorePlugin.Time()),
                                "blueness" to MutableDataSourcePort(
                                    CorePlugin.SliderDataSource("Blueness", 1f, 0f, 1f, null)
                                ),
                                "resolution" to MutableDataSourcePort(CorePlugin.Resolution()),
                                "gl_FragCoord" to MutableShaderChannelPort(
                                    ShaderChannel.Main
                                )
                            ),
                            shaderChannel = ShaderChannel.Main,
                            priority = 0f
                        )
                    )
                ) { patch.mutableShaderInstances }
            }

            context("with a UV projection shader") {
                val uvShader = cylindricalUvMapper.shader
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
                                    "time" to MutableDataSourcePort(CorePlugin.Time()),
                                    "resolution" to MutableDataSourcePort(CorePlugin.Resolution()),
                                    "blueness" to MutableDataSourcePort(
                                        CorePlugin.SliderDataSource("Blueness", 1f, 0f, 1f, null)
                                    ),
                                    "gl_FragCoord" to MutableShaderChannelPort(
                                        ShaderChannel.Main
                                    )
                                ),
                                shaderChannel = ShaderChannel.Main
                            ),
                            uvShaderInst.apply {
                                incomingLinks.putAll(mapOf(
                                    "pixelCoordsTexture" to MutableDataSourcePort(
                                        CorePlugin.PixelCoordsTexture()
                                    ),
                                    "modelInfo" to MutableDataSourcePort(
                                        CorePlugin.ModelInfoDataSource(
                                            "ModelInfo"
                                        )
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
                        vec4 filterImage(vec4 colorIn) {
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
                                    "brightness" to MutableDataSourcePort(
                                        CorePlugin.SliderDataSource("Brightness", 1f, 0f, 1f, null)
                                    ),
                                    "gl_FragColor" to MutableShaderChannelPort(
                                        ShaderChannel.Main
                                    )
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