package baaahs.gl.patch

import baaahs.fixtures.MovingHeadInfoDataSource
import baaahs.gl.expects
import baaahs.gl.override
import baaahs.gl.shader.DistortionShader
import baaahs.gl.shader.FilterShader
import baaahs.gl.testPlugins
import baaahs.glsl.Shaders.cylindricalProjection
import baaahs.plugin.CorePlugin
import baaahs.show.Shader
import baaahs.show.ShaderChannel
import baaahs.show.live.LinkedShaderInstance
import baaahs.show.live.link
import baaahs.show.mutable.MutableShader
import baaahs.show.mutable.MutableShaderInstance
import baaahs.show.mutable.editor
import ch.tutteli.atrium.api.fluent.en_GB.containsExactly
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object AutoWirerSpec : Spek({
    describe("AutoWirer") {
        val autoWirer by value { AutoWirer(testPlugins()) }

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
            val mainShader by value { autoWirer.glslAnalyzer.import(shaderText) }
            val shaders by value { arrayOf(mainShader) }
            val patch by value { autoWirer.autoWire(*shaders).acceptSuggestedLinkOptions().confirm() }
            val linkedPatch by value { patch.openForPreview(autoWirer)!! }
            val rootProgramNode by value { linkedPatch.rootNode as LinkedShaderInstance }

            it("creates a reasonable guess patch") {
                expect(patch.mutableShaderInstances).containsExactly(
                    MutableShaderInstance(
                        MutableShader(mainShader),
                        hashMapOf(
                            "time" to CorePlugin.TimeDataSource().editor(),
                            "blueness" to CorePlugin.SliderDataSource("Blueness", 1f, 0f, 1f, null).editor(),
                            "resolution" to CorePlugin.ResolutionDataSource().editor(),
                            "gl_FragCoord" to ShaderChannel.Main.editor()
                        ),
                        shaderChannel = ShaderChannel.Main.editor(),
                        priority = 0f
                    )
                )
            }

            it("builds a linked patch") {
                expect(rootProgramNode.incomingLinks)
                    .toBe(
                        mapOf(
                            "gl_FragCoord" to DefaultValueNode(ContentType.UvCoordinateStream),
                            "time" to CorePlugin.TimeDataSource().link("time"),
                            "resolution" to CorePlugin.ResolutionDataSource().link("resolution"),
                            "blueness" to CorePlugin.SliderDataSource("Blueness", 1f, 0f, 1f, null).link("bluenessSlider"))
                    )
            }

            context("with a uv-coordinate input port") {
                override(shaderText) {
                    /**language=glsl*/
                    """
                        uniform vec2  resolution;
                        vec2 anotherFunc(vec2 fragCoord) { return fragCoord; }
                        void main(vec2 fragCoord) {
                            vec2 uv = anotherFunc(fragCoord) / resolution.xy;
                            fragColor = vec4(uv.xy, 0., 1.);
                        }
                    """.trimIndent()
                }

                it("should pull from something") {
                    expects(
                        listOf(
                            MutableShaderInstance(
                                MutableShader(mainShader),
                                hashMapOf(
                                    "fragCoord" to ShaderChannel.Main.editor(),
                                    "resolution" to CorePlugin.ResolutionDataSource().editor(),
                                ),
                                shaderChannel = ShaderChannel.Main.editor(),
                                priority = 0f
                            )
                        )
                    ) { patch.mutableShaderInstances }
                }
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
                                MutableShader(mainShader),
                                hashMapOf(
                                    "iTime" to CorePlugin.TimeDataSource().editor(),
                                    "blueness" to CorePlugin.SliderDataSource("Blueness", 1f, 0f, 1f, null).editor(),
                                    "iResolution" to CorePlugin.ResolutionDataSource().editor(),
                                    "fragCoord" to ShaderChannel.Main.editor()
                                ),
                                shaderChannel = ShaderChannel.Main.editor(),
                                priority = 0f
                            )
                        )
                    ) { patch.mutableShaderInstances }
                }

                it("builds a linked patch") {
                    rootProgramNode.incomingLinks.forEach { (port, link) ->
                        println("port $port -> $link")
                    }
                    expects(
                        mapOf(
                            "blueness" to CorePlugin.SliderDataSource("Blueness", 1f, 0f, 1f, null).link("bluenessSlider"),
                            "iResolution" to CorePlugin.ResolutionDataSource().link("resolution"),
                            "iTime" to CorePlugin.TimeDataSource().link("time"),
                            "fragCoord" to DefaultValueNode(ContentType.UvCoordinateStream)
                        )
                    ) { rootProgramNode.incomingLinks }
                }
            }

            context("with a UV projection shader") {
                val uvShader = cylindricalProjection
                val uvShaderInst by value { MutableShaderInstance(MutableShader(uvShader)) }

                override(shaders) { arrayOf(mainShader, uvShader) }

                it("creates a reasonable guess patch") {
                    expects(
                        listOf(
                            MutableShaderInstance(
                                MutableShader(mainShader),
                                hashMapOf(
                                    "time" to CorePlugin.TimeDataSource().editor(),
                                    "resolution" to CorePlugin.ResolutionDataSource().editor(),
                                    "blueness" to CorePlugin.SliderDataSource("Blueness", 1f, 0f, 1f, null).editor(),
                                    "gl_FragCoord" to ShaderChannel.Main.editor()
                                ),
                                shaderChannel = ShaderChannel.Main.editor()
                            ),
                            uvShaderInst.apply {
                                incomingLinks.putAll(
                                    mapOf(
                                        "pixelCoordsTexture" to CorePlugin.PixelCoordsTextureDataSource().editor(),
                                        "modelInfo" to CorePlugin.ModelInfoDataSource().editor(),
                                        "rasterCoord" to CorePlugin.RasterCoordinateDataSource().editor()
                                    )
                                )
                                shaderChannel = ShaderChannel.Main.editor()
                            }
                        )
                    ) { patch.mutableShaderInstances }
                }
            }

            context("with a filter shader") {
                val filterShader = Shader(
                    "Brightness Filter",
                    FilterShader,
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
                                    "brightness" to CorePlugin.SliderDataSource("Brightness", 1f, 0f, 1f, null).editor(),
                                    "colorIn" to ShaderChannel.Main.editor()
                                ),
                                shaderChannel = ShaderChannel.Main.editor()
                            )
                        )
                    ) { patch.mutableShaderInstances }
                }
            }

            context("with a distortion shader") {
                val filterShader = Shader(
                    "Flip Y",
                    DistortionShader,
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
                                    "uvIn" to ShaderChannel.Main.editor()
                                ),
                                shaderChannel = ShaderChannel.Main.editor()
                            )
                        )
                    ) { patch.mutableShaderInstances }
                }
            }

            context("with a shader for a non-PixelArrayDevice fixture") {
                override(shaderText) {
                    /**language=glsl*/
                    """
                        struct MovingHeadInfo {
                            vec3 origin;
                            vec3 heading;
                        };
                        
                        uniform MovingHeadInfo movingHeadInfo;
                        
                        vec4 mainMover() {
                            return vec4(movingHeadInfo.heading.xy, movingHeadInfo.origin.xy);
                        }
                    """.trimIndent()
                }

                it("creates a reasonable guess patch") {
                    expect(patch.mutableShaderInstances)
                        .containsExactly(
                            MutableShaderInstance(
                                MutableShader(mainShader),
                                hashMapOf(
                                    "movingHeadInfo" to MovingHeadInfoDataSource().editor()
                                )
                            )
                        )
                }
            }
        }
    }
})