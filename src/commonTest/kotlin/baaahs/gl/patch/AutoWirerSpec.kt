package baaahs.gl.patch

import baaahs.gl.expects
import baaahs.gl.override
import baaahs.glsl.Shaders.cylindricalProjection
import baaahs.plugin.CorePlugin
import baaahs.plugin.Plugins
import baaahs.show.Shader
import baaahs.show.ShaderChannel
import baaahs.show.ShaderType
import baaahs.show.live.LiveShaderInstance
import baaahs.show.mutable.MutableDataSourcePort
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
            val patch by value { autoWirer.autoWire(*shaders).acceptSuggestedLinkOptions().resolve() }
            val linkedPatch by value { patch.openForPreview(autoWirer)!! }
            val liveShaderInstance by value { linkedPatch.shaderInstance }

            it("creates a reasonable guess patch") {
                expect(
                    listOf(
                        MutableShaderInstance(
                            MutableShader(paintShader),
                            hashMapOf(
                                "time" to MutableDataSourcePort(CorePlugin.TimeDataSource()),
                                "blueness" to MutableDataSourcePort(
                                    CorePlugin.SliderDataSource("Blueness", 1f, 0f, 1f, null)
                                ),
                                "resolution" to MutableDataSourcePort(CorePlugin.ResolutionDataSource()),
                                "gl_FragCoord" to MutableShaderChannel(ShaderChannel.Main.id)
                            ),
                            shaderChannel = MutableShaderChannel(ShaderChannel.Main.id),
                            priority = 0f
                        )
                    )
                ) { patch.mutableShaderInstances }
            }

            it("builds a linked patch") {
                expect(
                    mapOf(
                        "gl_FragCoord" to LiveShaderInstance.NoOpLink,
                        "time" to LiveShaderInstance.DataSourceLink(
                            CorePlugin.TimeDataSource(), "time"
                        ),
                        "resolution" to LiveShaderInstance.DataSourceLink(
                            CorePlugin.ResolutionDataSource(), "resolution"
                        ),
                        "blueness" to LiveShaderInstance.DataSourceLink(
                            CorePlugin.SliderDataSource("Blueness", 1f, 0f, 1f, null), "bluenessSlider"
                        )
                    )
                ) { liveShaderInstance.incomingLinks }
            }

            context("with a ShaderToy shader") {
                override(shaderText) {
                    /**language=glsl*/
                    """
                        // This Shader's Name
                        // Other stuff.
                        
                        uniform float blueness;
                        int someGlobalVar;
                        const int someConstVar = 123;
                        
                        int anotherFunc(int i) { return i; }
                        
                        void mainImage( out vec4 fragColor, in vec2 fragCoord ) {
                            vec2 uv = fragCoord.xy / iResolution.xy;
                            someGlobalVar = anotherFunc(someConstVar) + int(iTime);
                            fragColor = vec4(uv.xy, blueness, 1.);
                        }
                    """.trimIndent()
                }

                it("creates a reasonable guess patch") {
                    expects(
                        listOf(
                            MutableShaderInstance(
                                MutableShader(paintShader),
                                hashMapOf(
                                    "iTime" to MutableDataSourcePort(CorePlugin.TimeDataSource()),
                                    "blueness" to MutableDataSourcePort(
                                        CorePlugin.SliderDataSource("Blueness", 1f, 0f, 1f, null)
                                    ),
                                    "iResolution" to MutableDataSourcePort(CorePlugin.ResolutionDataSource()),
                                    "fragCoord" to MutableShaderChannel(ShaderChannel.Main.id)
                                ),
                                shaderChannel = MutableShaderChannel(ShaderChannel.Main.id),
                                priority = 0f
                            )
                        )
                    ) { patch.mutableShaderInstances }
                }

                it("builds a linked patch") {
                    liveShaderInstance.incomingLinks.forEach { (port, link) ->
                        println("port $port -> $link")
                    }
                    expects(
                        mapOf(
                            "blueness" to LiveShaderInstance.DataSourceLink(
                                CorePlugin.SliderDataSource("Blueness", 1f, 0f, 1f, null), "bluenessSlider"
                            ),
                            "iResolution" to LiveShaderInstance.DataSourceLink(
                                CorePlugin.ResolutionDataSource(), "resolution"
                            ),
                            "iTime" to LiveShaderInstance.DataSourceLink(
                                CorePlugin.TimeDataSource(), "time"
                            ),
                            "fragCoord" to LiveShaderInstance.NoOpLink
                        )
                    ) { liveShaderInstance.incomingLinks }
                }
            }

            context("with a UV projection shader") {
                val uvShader = cylindricalProjection
                val uvShaderInst by value { MutableShaderInstance(MutableShader(uvShader)) }

                override(shaders) { arrayOf(paintShader, uvShader) }

                it("creates a reasonable guess patch") {
                    expects(
                        listOf(
                            MutableShaderInstance(
                                MutableShader(paintShader),
                                hashMapOf(
                                    "time" to MutableDataSourcePort(CorePlugin.TimeDataSource()),
                                    "resolution" to MutableDataSourcePort(CorePlugin.ResolutionDataSource()),
                                    "blueness" to MutableDataSourcePort(
                                        CorePlugin.SliderDataSource("Blueness", 1f, 0f, 1f, null)
                                    ),
                                    "gl_FragCoord" to MutableShaderChannel(ShaderChannel.Main.id)
                                ),
                                shaderChannel = MutableShaderChannel(ShaderChannel.Main.id)
                            ),
                            uvShaderInst.apply {
                                incomingLinks.putAll(
                                    mapOf(
                                        "pixelCoordsTexture" to MutableDataSourcePort(
                                            CorePlugin.PixelCoordsTextureDataSource()
                                        ),
                                        "modelInfo" to MutableDataSourcePort(
                                            CorePlugin.ModelInfoDataSource()
                                        )
                                    )
                                )
                                shaderChannel = MutableShaderChannel(ShaderChannel.Main.id)
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
                    """.trimIndent()
                )

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
                                    "gl_FragColor" to MutableShaderChannel(ShaderChannel.Main.id)
                                ),
                                shaderChannel = MutableShaderChannel(ShaderChannel.Main.id)
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
                    """.trimIndent()
                )


                override(shaders) { arrayOf(filterShader) }

                it("creates a reasonable guess patch") {
                    expects(
                        listOf(
                            MutableShaderInstance(
                                MutableShader(filterShader),
                                hashMapOf(
                                    "gl_FragCoord" to MutableShaderChannel(ShaderChannel.Main.id)
                                ),
                                shaderChannel = MutableShaderChannel(ShaderChannel.Main.id)
                            )
                        )
                    ) { patch.mutableShaderInstances }
                }
            }
        }
    }
})