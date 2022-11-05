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
import baaahs.plugin.PluginRef
import baaahs.toBeSpecified
import baaahs.toEqual
import ch.tutteli.atrium.api.fluent.en_GB.containsExactly
import ch.tutteli.atrium.api.fluent.en_GB.hasSize
import ch.tutteli.atrium.api.fluent.en_GB.isEmpty
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import org.spekframework.spek2.Spek

object GlslAnalyzerSpec : Spek({
    describe<GlslAnalyzer> {
        context("given some GLSL code") {
            val glslParser by value { GlslParser() }
            val glslAnalyzer by value { GlslAnalyzer(testPlugins()) }
            val shaderText by value<String> { toBeSpecified() }

            context("#detectDialect") {
                override(shaderText) { "void main() {}" }

                val shaderAnalyzer by value { glslAnalyzer.detectDialect(glslParser.parse(shaderText)) }

                it("is generic") {
                    expect(shaderAnalyzer.dialect).toBe(GenericShaderDialect)
                }

                context("for shaders having a mainImage function") {
                    override(shaderText) { "void mainImage() {}" }

                    it("is ShaderToy") {
                        expect(shaderAnalyzer.dialect).toBe(ShaderToyShaderDialect)
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
                        expect(validationResult.errors.toSet()).containsExactly(
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
                    expect(importedShader.title).toBe("This Shader's Name")
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
        
                            void main( void ) {
                                vec2 uv = gl_FragCoord.xy / resolution.xy;
                                gl_FragColor = vec4(uv.xy, 0., 1.);
                            }
                        """.trimIndent()
                    }

                    it("finds the entry point function") {
                        expect(openShader.entryPoint.name).toBe("main")
                    }

                    it("creates inputs for implicit uniforms") {
                        expect(openShader.inputPorts.map { it.copy(glslArgSite = null) })
                            .containsExactly(
                                InputPort("gl_FragCoord", ContentType.UvCoordinate, GlslType.Vec4, "Coordinates", isImplicit = true),
                                InputPort("time", ContentType.Time, GlslType.Float, "Time"),
                                InputPort("resolution", ContentType.Resolution, GlslType.Vec2, "Resolution"),
                                InputPort("blueness", ContentType.unknown(GlslType.Float), GlslType.Float, "Blueness")
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
                            expect(openShader.inputPorts.map { it.copy(glslArgSite = null) })
                                .containsExactly(
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
                            expect(openShader.inputPorts).hasSize(1)
                            val inputPort = openShader.inputPorts.first()

                            expect(inputPort.copy(glslArgSite = null))
                                .toEqual(
                                    InputPort(
                                        "upstreamColor", ContentType.Color, GlslType.Vec4, "Upstream Color",
                                        injectedData = emptyMap()
                                    )
                                )

                            expect(inputPort.isAbstractFunction).toBe(true)
                            expect(inputPort.injectedData).isEmpty()
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
                                expect(openShader.inputPorts).hasSize(1)
                                val inputPort = openShader.inputPorts.first()

                                expect(inputPort.copy(glslArgSite = null))
                                    .toEqual(
                                        InputPort(
                                            "upstreamColor", ContentType.Color, GlslType.Vec4, "Upstream Color",
                                            injectedData = mapOf("uv" to ContentType.UvCoordinate)
                                        )
                                    )

                                expect(inputPort.isAbstractFunction).toBe(true)
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
                        expect(openShader.entryPoint.name).toBe("mainImage")
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
                        expect(openShader.entryPoint.name).toBe("main")
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
                        expect(openShader.entryPoint.name)
                            .toEqual("invalid")
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
                        expect(openShader.inputPorts.map { it.copy(glslArgSite = null) })
                            .containsExactly(
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