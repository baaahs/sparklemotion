package baaahs.gl.patch

import baaahs.device.PixelLocationDataSource
import baaahs.gl.*
import baaahs.gl.glsl.GlslCode
import baaahs.gl.glsl.GlslType
import baaahs.gl.render.FixtureTypeForTest
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
import baaahs.show.Stream
import baaahs.show.live.FakeOpenShader
import baaahs.show.live.LinkedPatch
import baaahs.show.mutable.MutablePatch
import baaahs.show.mutable.MutableShader
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
        val fixtureType by value {
            FixtureTypeForTest(
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
            val stream by value { Stream.Main }
            val suggestions by value {
                autoWirer.autoWire(shaders, stream = stream, fixtureTypes = listOf(fixtureType))
            }
            val patches by value { suggestions.acceptSuggestedLinkOptions().confirm() }
            val mutableLinks by value { patches.mutablePatches.only().incomingLinks }
            val portLink by value { mutableLinks[portId] }

            context("for content types of artifacts in potential render pipelines") {
                override(portId) { "uv" }
                override(portContentType) { ContentType.UvCoordinate }

                it("suggests the shader's channel") {
                    expect(portLink).toBe(Stream.Main.editor())
                }

                context("even when it's on on a different channel") {
                    override(stream) { Stream("other") }

                    it("suggests the main stream") {
                        expect(portLink).toBe(Stream.Main.editor())
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
                            override(stream) { Stream("time") }

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

                it("suggests pulling from the stream, making it a filter") {
                    expect(portLink).toBe(Stream.Main.editor())
                }

                context("when shader is on another stream") {
                    override(stream) { Stream("other") }

                    it("suggests pulling from that stream, making it a filter") {
                        expect(portLink).toBe(Stream("other").editor())
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
                    expect(portLink).toBe(Stream("main").editor())
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
            val patchSet by value {
                autoWirer.autoWire(shaders.open(toolchain), fixtureTypes = listOf(fixtureType))
                    .acceptSuggestedLinkOptions().confirm()
            }
            val linkedPatch by value { patchSet.openForPreview(toolchain, ContentType.Color)!! }
            val rootProgramNode by value { linkedPatch.rootNode as LinkedPatch }
            val mutableLinks by value { patchSet.mutablePatches.only().incomingLinks }
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

            it("picks Main stream for gl_FragCoord") {
                expect(mutableLinks["gl_FragCoord"])
                    .toBe(Stream.Main.editor())

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
                            MutablePatch(
                                MutableShader(mainShader),
                                hashMapOf(
                                    "fragCoord" to Stream.Main.editor(),
                                    "resolution" to ResolutionDataSource().editor(),
                                ),
                                stream = Stream.Main.editor(),
                                priority = 0f
                            )
                        )
                    ) { patchSet.mutablePatches }
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
                            MutablePatch(
                                MutableShader(mainShader),
                                hashMapOf(
                                    "iTime" to TimeDataSource().editor(),
                                    "blueness" to SliderDataSource("Blueness", 1f, 0f, 1f, null).editor(),
                                    "iResolution" to ResolutionDataSource().editor(),
                                    "fragCoord" to Stream.Main.editor()
                                ),
                                stream = Stream.Main.editor(),
                                priority = 0f
                            )
                        )
                    ) { patchSet.mutablePatches }
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
                val uvPatch by value { MutablePatch(MutableShader(uvShader)) }

                override(shaders) { arrayOf(mainShader, uvShader) }

                it("creates a reasonable guess patch") {
                    expects(
                        listOf(
                            MutablePatch(
                                MutableShader(mainShader),
                                hashMapOf(
                                    "time" to TimeDataSource().editor(),
                                    "resolution" to ResolutionDataSource().editor(),
                                    "blueness" to SliderDataSource("Blueness", 1f, 0f, 1f, null).editor(),
                                    "gl_FragCoord" to Stream.Main.editor()
                                ),
                                stream = Stream.Main.editor()
                            ),
                            uvPatch.apply {
                                incomingLinks.putAll(
                                    mapOf(
                                        "modelInfo" to ModelInfoDataSource().editor(),
                                        "pixelLocation" to PixelLocationDataSource().editor()
                                    )
                                )
                                stream = Stream.Main.editor()
                            }
                        )
                    ) { patchSet.mutablePatches }
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
                            MutablePatch(
                                MutableShader(filterShader),
                                hashMapOf(
                                    "brightness" to SliderDataSource("Brightness", 1f, 0f, 1f, null)
                                        .editor(),
                                    "colorIn" to Stream.Main.editor()
                                ),
                                stream = Stream.Main.editor()
                            )
                        )
                    ) { patchSet.mutablePatches }
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
                            MutablePatch(
                                MutableShader(filterShader),
                                hashMapOf(
                                    "uvIn" to Stream.Main.editor()
                                ),
                                stream = Stream.Main.editor()
                            )
                        )
                    ) { patchSet.mutablePatches }
                }
            }

            // TODO: fix!
            context("with a shader for a non-PixelArrayDevice fixture with an incomplete struct specification", Skip.Yes("This should work too.")) {
                override(shaderText) {
                    /**language=glsl*/
                    """
                        struct FixtureInfo {
                            vec3 position;
                            vec3 rotation;
                        };
                        
                        uniform FixtureInfo fixtureInfo;
                        
                        vec4 main() {
                            return vec4(fixtureInfo.rotation.xy, fixtureInfo.position.xy);
                        }
                    """.trimIndent()
                }

                it("creates a reasonable guess patch") {
                    expect(patchSet.mutablePatches)
                        .containsExactly(
                            MutablePatch(
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
                            vec3 position;
                            vec3 rotation;
                            mat4 transformation;
                        };
                        
                        uniform FixtureInfo fixtureInfo;
                        
                        vec4 main() {
                            return vec4(fixtureInfo.rotation.xy, fixtureInfo.position.xy);
                        }
                    """.trimIndent()
                }

                it("creates a reasonable guess patch") {
                    expect(patchSet.mutablePatches)
                        .containsExactly(
                            MutablePatch(
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