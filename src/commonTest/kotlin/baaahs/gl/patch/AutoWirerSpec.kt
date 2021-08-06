package baaahs.gl.patch

import baaahs.device.PixelLocationDataSource
import baaahs.gl.*
import baaahs.gl.glsl.GlslCode
import baaahs.gl.glsl.GlslType
import baaahs.gl.render.DeviceTypeForTest
import baaahs.gl.shader.InputPort
import baaahs.gl.shader.OpenShader
import baaahs.gl.shader.OutputPort
import baaahs.glsl.Shaders.cylindricalProjection
import baaahs.only
import baaahs.plugin.core.FixtureInfoDataSource
import baaahs.plugin.core.datasource.ModelInfoDataSource
import baaahs.plugin.core.datasource.ResolutionDataSource
import baaahs.plugin.core.datasource.SliderDataSource
import baaahs.plugin.core.datasource.TimeDataSource
import baaahs.show.Shader
import baaahs.show.ShaderChannel
import baaahs.show.live.FakeOpenShader
import baaahs.show.live.LinkedShaderInstance
import baaahs.show.live.link
import baaahs.show.mutable.MutableShader
import baaahs.show.mutable.MutableShaderInstance
import baaahs.show.mutable.editor
import ch.tutteli.atrium.api.fluent.en_GB.containsExactly
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import org.spekframework.spek2.Spek
import org.spekframework.spek2.dsl.Skip
import org.spekframework.spek2.style.specification.describe

object AutoWirerSpec : Spek({
    describe("AutoWirer") {
        val autoWirer by value { AutoWirer(testPlugins()) }
        val toolchain by value { RootToolchain(testPlugins(), autoWirer = autoWirer) }
        val deviceType by value {
            DeviceTypeForTest(
                likelyPipelines = listOf(
                    ContentType.XyzCoordinate to ContentType.UvCoordinate,
                    ContentType.UvCoordinate to ContentType.Color
                )
            )
        }

        describe(".autoWire") {
            val portId by value { "brightness" }
            val portContentType by value { ContentType.Float }
            val portGlslType by value { portContentType.glslType }
            val portGlslArgSite by value<GlslCode.GlslArgSite?> { null }
            val outContentType by value { ContentType.Color }
            val mainShader by value {
                FakeOpenShader(
                    listOf(InputPort(portId, portContentType, portGlslType, glslArgSite = portGlslArgSite)),
                    OutputPort(outContentType)
                )
            }
            val shaders by value { listOf<OpenShader>(mainShader) }
            val shaderChannel by value { ShaderChannel.Main }
            val suggestions by value {
                autoWirer.autoWire(shaders, shaderChannel = shaderChannel, deviceTypes = listOf(deviceType))
            }
            val patch by value { suggestions.acceptSuggestedLinkOptions().confirm() }
            val mutableLinks by value { patch.mutableShaderInstances.only().incomingLinks }
            val portLink by value { mutableLinks[portId] }

            context("for content types of artifacts in potential render pipelines") {
                override(portId) { "uv" }
                override(portContentType) { ContentType.UvCoordinate }

                it("suggests the shader's channel") {
                    expect(portLink).toBe(ShaderChannel.Main.editor())
                }

                context("even when it's on on a different channel") {
                    override(shaderChannel) { ShaderChannel("other") }

                    it("suggests the main channel") {
                        expect(portLink).toBe(ShaderChannel.Main.editor())
                    }
                }
            }

            context("when there's a good datasource match") {
                context("e.g. for time") {
                    override(portId) { "time" }
                    override(portContentType) { ContentType.Time }

                    it("suggests the data source's channel") {
                        expect(portLink).toBe(TimeDataSource().editor())
                    }

                    // TODO: get these working?
                    context("when the output type matches", skip = Skip.Yes("Not working yet")) {
                        override(outContentType) { portContentType }

                        it("suggests the data source's channel") {
                            expect(portLink).toBe(TimeDataSource().editor())
                        }

                        context("and the shader's channel matches the data source's channel") {
                            override(shaderChannel) { ShaderChannel("time") }

                            it("suggests the data source's channel") {
                                expect(portLink).toBe(TimeDataSource().editor())
                            }
                        }
                    }
                }

                context("for an arbitrary float") {
                    override(portId) { "brightness" }
                    override(portContentType) { ContentType.Float }

                    it("suggests a Slider data source channel link") {
                        expect(portLink)
                            .toBe(SliderDataSource("Brightness", 1f, 0f, 1f).editor())
                    }
                }

// TODO:                context("for an arbitrary unknown/float") {
// TODO:                    override(portId) { "brightness" }
// TODO:                    override(portContentType) { ContentType.unknown(Float) }
//
// TODO:                    it("suggests a Slider data source channel link") {
// TODO:                        expect(portLink)
// TODO:                            .toBe(CorePlugin.SliderDataSource("Brightness", 1f, 0f, 1f).editor())
// TODO:                    }
// TODO:                }
            }

            context("when input port's content type matches the shader's output type") {
                override(outContentType) { portContentType }

                it("suggests pulling from the shader channel, making it a filter") {
                    expect(portLink).toBe(ShaderChannel.Main.editor())
                }

                context("when shader is on another channel") {
                    override(shaderChannel) { ShaderChannel("other") }

                    it("suggests pulling from that shader channel, making it a filter") {
                        expect(portLink).toBe(ShaderChannel("other").editor())
                    }
                }
            }

            context("when an input port is an abstract function") {
                override(portId) { "imageChannelA" }
                override(portGlslType) { GlslType.Vec4 }
                override(portContentType) { ContentType.Color }
                override(portGlslArgSite) {
                    GlslCode.GlslFunction(portId, portGlslType, emptyList(), "", isAbstract = true)
                }

                it("suggests pulling from channel") {
                    expect(portLink).toBe(ShaderChannel("main").editor())
                }
            }
        }

        describe(".autoWire integration") {
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
            val mainShader by value { toolchain.import(shaderText) }
            val shaders by value { arrayOf(mainShader) }
            val patch by value {
                autoWirer.autoWire(shaders.open(toolchain), deviceTypes = listOf(deviceType))
                    .acceptSuggestedLinkOptions().confirm()
            }
            val linkedPatch by value { patch.openForPreview(toolchain, ContentType.Color)!! }
            val rootProgramNode by value { linkedPatch.rootNode as LinkedShaderInstance }
            val mutableLinks by value { patch.mutableShaderInstances.only().incomingLinks }
            val links by value { rootProgramNode.incomingLinks }

            it("picks TimeDataSource for time") {
                expect(mutableLinks["time"])
                    .toBe(TimeDataSource().editor())

                expect(links["time"])
                    .toBe(TimeDataSource().link("time"))
            }

            it("picks ResolutionDataSource for resolution") {
                expect(mutableLinks["resolution"])
                    .toBe(ResolutionDataSource().editor())

                expect(links["resolution"])
                    .toBe(ResolutionDataSource().link("resolution"))
            }

            it("picks a Slider for blueness") {
                expect(links["blueness"])
                    .toBe(
                        SliderDataSource("Blueness", 1f, 0f, 1f, null)
                            .link("bluenessSlider")
                    )
            }

            it("picks Main channel for gl_FragCoord") {
                expect(mutableLinks["gl_FragCoord"])
                    .toBe(ShaderChannel.Main.editor())

                expect(links["gl_FragCoord"])
                    .toBe(DefaultValueNode(ContentType.UvCoordinate))
            }

            it("builds a linked patch") {
                expect(rootProgramNode.incomingLinks)
                    .toBe(
                        mapOf(
                            "gl_FragCoord" to DefaultValueNode(ContentType.UvCoordinate),
                            "time" to TimeDataSource().link("time"),
                            "resolution" to ResolutionDataSource().link("resolution"),
                            "blueness" to SliderDataSource("Blueness", 1f, 0f, 1f, null)
                                .link("bluenessSlider")
                        )
                    )
            }

            context("with a uv-coordinate input port") {
                override(shaderText) {
                    /**language=glsl*/
                    """
                        uniform vec2  resolution;
                        vec2 anotherFunc(vec2 fragCoord) { return fragCoord; }
                        // @param fragCoord uv-coordinate
                        void main(vec2 fragCoord) {
                            vec2 uv = anotherFunc(fragCoord) / resolution.xy;
                            gl_FragColor = vec4(uv.xy, 0., 1.);
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
                                    "resolution" to ResolutionDataSource().editor(),
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
                                    "iTime" to TimeDataSource().editor(),
                                    "blueness" to SliderDataSource("Blueness", 1f, 0f, 1f, null).editor(),
                                    "iResolution" to ResolutionDataSource().editor(),
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
                            "blueness" to SliderDataSource("Blueness", 1f, 0f, 1f, null)
                                .link("bluenessSlider"),
                            "iResolution" to ResolutionDataSource().link("resolution"),
                            "iTime" to TimeDataSource().link("time"),
                            "fragCoord" to DefaultValueNode(ContentType.UvCoordinate)
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
                                    "time" to TimeDataSource().editor(),
                                    "resolution" to ResolutionDataSource().editor(),
                                    "blueness" to SliderDataSource("Blueness", 1f, 0f, 1f, null).editor(),
                                    "gl_FragCoord" to ShaderChannel.Main.editor()
                                ),
                                shaderChannel = ShaderChannel.Main.editor()
                            ),
                            uvShaderInst.apply {
                                incomingLinks.putAll(
                                    mapOf(
                                        "modelInfo" to ModelInfoDataSource().editor(),
                                        "pixelLocation" to PixelLocationDataSource().editor()
                                    )
                                )
                                shaderChannel = ShaderChannel.Main.editor()
                            }
                        )
                    ) { patch.mutableShaderInstances }
                }
            }

            context("with a filter shader") {
                val filterShader by value {
                    Shader(
                        "Brightness Filter",
                        """
                            uniform float brightness; // @@Slider min=0 max=1 default=1
                            
                            // @param colorIn color
                            void main(vec4 colorIn) {
                              gl_FragColor = colorIn * brightness;
                            }
                        """.trimIndent()
                    )
                }

                override(shaders) { arrayOf(filterShader) }

                it("creates a reasonable guess patch") {
                    expects(
                        listOf(
                            MutableShaderInstance(
                                MutableShader(filterShader),
                                hashMapOf(
                                    "brightness" to SliderDataSource("Brightness", 1f, 0f, 1f, null)
                                        .editor(),
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
                    """
                        // @return uv-coordinate
                        // @param uvIn uv-coordinate
                        vec2 main(vec2 uvIn) {
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

            // TODO: fix!
            context("with a shader for a non-PixelArrayDevice fixture with an incomplete struct specification", Skip.Yes("This should work too.")) {
                override(shaderText) {
                    /**language=glsl*/
                    """
                        struct FixtureInfo {
                            vec3 origin;
                            vec3 heading;
                        };
                        
                        uniform FixtureInfo fixtureInfo;
                        
                        vec4 main() {
                            return vec4(fixtureInfo.heading.xy, fixtureInfo.origin.xy);
                        }
                    """.trimIndent()
                }

                it("creates a reasonable guess patch") {
                    expect(patch.mutableShaderInstances)
                        .containsExactly(
                            MutableShaderInstance(
                                MutableShader(mainShader),
                                hashMapOf(
                                    "fixtureInfo" to FixtureInfoDataSource().editor()
                                )
                            )
                        )
                }
            }

            context("with a shader for a non-PixelArrayDevice fixture") {
                override(shaderText) {
                    /**language=glsl*/
                    """
                        struct FixtureInfo {
                            vec3 origin;
                            vec3 heading;
                            mat4 matrix;
                        };
                        
                        uniform FixtureInfo fixtureInfo;
                        
                        vec4 main() {
                            return vec4(fixtureInfo.heading.xy, fixtureInfo.origin.xy);
                        }
                    """.trimIndent()
                }

                it("creates a reasonable guess patch") {
                    expect(patch.mutableShaderInstances)
                        .containsExactly(
                            MutableShaderInstance(
                                MutableShader(mainShader),
                                hashMapOf(
                                    "fixtureInfo" to FixtureInfoDataSource().editor()
                                )
                            )
                        )
                }
            }
        }
    }
})

private fun Array<Shader>.open(toolchain: Toolchain): Collection<OpenShader> =
    map { toolchain.openShader(it) }