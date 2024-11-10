package baaahs.gl.glsl

import baaahs.describe
import baaahs.gl.expects
import baaahs.gl.override
import baaahs.gl.patch.ContentType
import baaahs.gl.shader.InputPort
import baaahs.gl.shader.dialect.GenericShaderDialect
import baaahs.gl.shader.dialect.ShaderToyShaderDialect
import baaahs.gl.testPlugins
import baaahs.gl.testToolchain
import baaahs.glsl.Shaders
import baaahs.kotest.value
import baaahs.plugin.PluginRef
import baaahs.toBeSpecified
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.*
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.maps.shouldBeEmpty
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

class GlslAnalyzerSpec : DescribeSpec({
    describe<GlslAnalyzer> {
        context("given some GLSL code") {
            val glslParser by value { GlslParser() }
            val glslAnalyzer by value { GlslAnalyzer(testPlugins()) }
            val shaderText by value<String> { toBeSpecified() }

            context("#detectDialect") {
                override(shaderText) { "void main() {}" }

                val shaderAnalyzer by value { glslAnalyzer.detectDialect(glslParser.parse(shaderText)) }

                it("is generic") {
                    shaderAnalyzer.dialect.shouldBe(GenericShaderDialect)
                }

                context("for shaders having a mainImage function") {
                    override(shaderText) { "void mainImage() {}" }

                    it("is ShaderToy") {
                        shaderAnalyzer.dialect.shouldBe(ShaderToyShaderDialect)
                    }
                }
            }

            context("#validate") {
                val validationResult by value { glslAnalyzer.analyze(glslParser.parse(shaderText)) }

                context("when there are problems in the shader") {
                    override(shaderText) {
                        """
                            uniform float foo; // @type unknown-type
                            vec4 main(vec4 inColor) {
                                return inColor;
                            }
                        """.trimIndent()
                    }

                    it("should report them in the ValidationResult") {
                        validationResult.errors.toSet().shouldContainExactly(
                            GlslError("Input port \"foo\" content type is \"unknown/float\"", 1),
                            GlslError("Input port \"inColor\" content type is \"unknown/vec4\"", 2),
                            GlslError("Output port \"[return value]\" content type is \"unknown/vec4\"", 2)
                        )
                    }
                }
            }

            context("#import") {
                val importedShader by value { testToolchain.import(shaderText) }
                override(shaderText) {
                    /**language=glsl*/
                    """
                    // This Shader's Name
                    // Other stuff.
                    
                    void main() { }
                    """.trimIndent()
                }

                it("finds the title") {
                    importedShader.title.shouldBe("This Shader's Name")
                }
            }

            context("#openShader") {
                val openShader by value { glslAnalyzer.openShader(testToolchain.parse(shaderText)) }

                context("with generic shader") {
                    override(shaderText) {
                        /**language=glsl*/
                        """
                            // This Shader's Name
                            // Other stuff.
                            
                            uniform float time;
                            uniform vec2  resolution;
                            uniform float blueness;
                            uniform float bluenesses[3];
        
                            void main( void ) {
                                vec2 uv = gl_FragCoord.xy / resolution.xy;
                                gl_FragColor = vec4(uv.xy, 0., 1.);
                            }
                        """.trimIndent()
                    }

                    it("finds the entry point function") {
                        openShader.entryPoint.name.shouldBe("main")
                    }

                    it("creates inputs for implicit uniforms") {
                        openShader.inputPorts.map { it.copy(glslArgSite = null) }
                            .shouldContainExactly(
                                InputPort(
                                    "gl_FragCoord",
                                    ContentType.UvCoordinate,
                                    GlslType.Vec4,
                                    "Coordinates",
                                    isImplicit = true
                                ),
                                InputPort("time", ContentType.Time, GlslType.Float, "Time"),
                                InputPort("resolution", ContentType.Resolution, GlslType.Vec2, "Resolution"),
                                InputPort("blueness", ContentType.unknown(GlslType.Float), GlslType.Float, "Blueness"),
                                InputPort(
                                    "bluenesses",
                                    ContentType.unknown(GlslType.Float.arrayOf(3)),
                                    GlslType.Float.arrayOf(3),
                                    "Bluenesses"
                                )
                            )
                    }

                    context("with an abstract function with arguments") {
                        override(shaderText) {
                            /**language=glsl*/
                            """
                                // @param uv uv-coordinate                            
                                // @return color                            
                                vec4 channelA(vec2 uv);
            
                                void main( void ) {
                                    vec2 uv = gl_FragCoord.xy / resolution.xy;
                                    gl_FragColor = channelA(uv + 1.);
                                }
                            """.trimIndent()
                        }

                        it("creates an input for it") {
                            openShader.inputPorts.map { it.copy(glslArgSite = null) }
                                .shouldContainExactly(
                                    InputPort("gl_FragCoord", ContentType.UvCoordinate, GlslType.Vec4, "Coordinates", isImplicit = true),
                                    InputPort("channelA", ContentType.Color, GlslType.Vec4, "Channel A", isImplicit = false, injectedData = mapOf(
                                        "uv" to ContentType.UvCoordinate
                                    )),
                                    InputPort("resolution", ContentType.Resolution, GlslType.Vec2, "Resolution", isImplicit = false),
                                )
                        }
                    }

                    context("with an abstract function") {
                        override(shaderText) {
                            /**language=glsl*/
                            """
                                // This Shader
                                vec4 upstreamColor(); // @return color
                                void main() { gl_FragColor = upstreamColor(); }
                            """.trimIndent()
                        }

                        it("creates an input for it") {
                            openShader.inputPorts.shouldHaveSize(1)
                            val inputPort = openShader.inputPorts.first()

                            inputPort.copy(glslArgSite = null)
                                .shouldBe(
                                    InputPort(
                                        "upstreamColor", ContentType.Color, GlslType.Vec4, "Upstream Color",
                                        injectedData = emptyMap()
                                    )
                                )

                            inputPort.isAbstractFunction.shouldBeTrue()
                            inputPort.injectedData.shouldBeEmpty()
                        }

                        context("which takes arguments") {
                            override(shaderText) {
                                /**language=glsl*/
                                """
                                    // This Shader
                                    // @param uv uv-coordinate
                                    // @return color
                                    vec4 upstreamColor(vec2 uv);
                                    void main() { gl_FragColor = upstreamColor(); }
                                """.trimIndent()
                            }

                            it("creates an input for it") {
                                openShader.inputPorts.shouldHaveSize(1)
                                val inputPort = openShader.inputPorts.first()

                                inputPort.copy(glslArgSite = null)
                                    .shouldBe(
                                        InputPort(
                                            "upstreamColor", ContentType.Color, GlslType.Vec4, "Upstream Color",
                                            injectedData = mapOf("uv" to ContentType.UvCoordinate)
                                        )
                                    )

                                inputPort.isAbstractFunction.shouldBeTrue()
                            }
                        }
                    }
                }

                context("with shadertoy shader") {
                    override(shaderText) {
                        /**language=glsl*/
                        """
                        // This Shader's Name
                        // Other stuff
                        
                        uniform float blueness;
                        
                        void mainImage( out vec4 fragColor, in vec2 fragCoord )
                        {
                        	vec2 uv = fragCoord.xy / iResolution.xy;
                        	fragColor = vec4(uv * iTime, -uv.x * blueness, 1.0);
                        }
                        """.trimIndent()
                    }

                    it("identifies mainImage() as the entry point") {
                        openShader.entryPoint.name.shouldBe("mainImage")
                    }

                    it("creates inputs for implicit uniforms") {
                        expects(
                            listOf(
                                InputPort("blueness", ContentType.unknown(GlslType.Float), GlslType.Float, "Blueness"),
                                InputPort("fragCoord", ContentType.UvCoordinate, GlslType.Vec2, "U/V Coordinates"),
                                InputPort("iResolution", ContentType.Resolution, GlslType.Vec3, "Resolution", isImplicit = true),
                                InputPort("iTime", ContentType.Time, GlslType.Float, "Time", isImplicit = true)
                            )
                        ) { openShader.inputPorts.map { it.copy(glslArgSite = null) } }
                    }
                }

                context("with U/V translation shader") {
                    override(shaderText) { Shaders.cylindricalProjection.src }

                    it("identifies main() as the entry point") {
                        openShader.entryPoint.name.shouldBe("main")
                    }

                    it("creates inputs for implicit uniforms") {
                        expects(
                            listOf(
                                InputPort(
                                    "modelInfo", ContentType.ModelInfo,
                                    ContentType.ModelInfo.glslType, "Model Info"
                                ),
                                InputPort(
                                    "pixelLocation", ContentType.XyzCoordinate,
                                    GlslType.Vec3, "Pixel Location"
                                ),
                            )
                        ) { openShader.inputPorts.map { it.copy(glslArgSite = null) } }
                    }
                }

                context("with invalid shader") {
                    override(shaderText) { "" }

                    it("provides a fake entry point function") {
                        openShader.entryPoint.name
                            .shouldBe("invalid")
                    }
                }

                context("with a hinted input port matching a default (heartCenter -> uv)") {
                    override(shaderText) {
                        /**language=glsl*/
                        """
                            // http://mathworld.wolfram.com/HeartSurface.html
    
                            uniform vec4 inColor; // @type color
                            uniform float heartSize; // @@Slider default=1. min=0.25 max=2
                            uniform vec2 heartCenter; // @@XyPad
    
                            void mainImage( out vec4 fragColor, in vec2 fragCoord ) {
                                fragColor = inColor;
                            }
                        """.trimIndent()
                    }

                    it("creates a non-default input for it") {
                        openShader.inputPorts.map { it.copy(glslArgSite = null) }
                            .shouldContainExactly(
                                InputPort("inColor", ContentType.Color, GlslType.Vec4, "In Color"),
                                InputPort("heartSize", ContentType.Float, GlslType.Float, "Heart Size", PluginRef.from("Slider"), buildJsonObject {
                                    put("default", JsonPrimitive("1."))
                                    put("min", JsonPrimitive("0.25"))
                                    put("max", JsonPrimitive("2"))
                                }),
                                InputPort("heartCenter", ContentType.XyCoordinate, GlslType.Vec2, "Heart Center", PluginRef.from("XyPad"), buildJsonObject {}),
                                InputPort("fragCoord", ContentType.UvCoordinate, GlslType.Vec2, "U/V Coordinates")
                            )
                    }
                }
            }
        }
    }
})