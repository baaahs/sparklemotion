package baaahs.gl.shader.dialect

import baaahs.app.ui.editor.stringify
import baaahs.describe
import baaahs.device.PixelArrayDevice
import baaahs.gl.autoWire
import baaahs.gl.autoWireWithDefaults
import baaahs.gl.glsl.GlslCode
import baaahs.gl.glsl.GlslError
import baaahs.gl.glsl.GlslExpr
import baaahs.gl.glsl.GlslType
import baaahs.gl.override
import baaahs.gl.patch.ContentType
import baaahs.gl.shader.InputPort
import baaahs.gl.shader.OutputPort
import baaahs.gl.shader.ShaderSubstitutions
import baaahs.gl.testToolchain
import baaahs.only
import baaahs.plugin.PluginRef
import baaahs.plugin.core.feed.SelectFeed
import baaahs.show.live.LinkedPatch
import baaahs.toBeSpecified
import baaahs.toEqual
import baaahs.ui.diagnostics.DotDag
import ch.tutteli.atrium.api.fluent.en_GB.contains
import ch.tutteli.atrium.api.fluent.en_GB.containsExactly
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import org.spekframework.spek2.Spek

@Suppress("unused")
object IsfShaderDialectSpec : Spek({
    describe<IsfShaderDialect> {
        val fileName by value<String?> { null }
        val src by value<String> { toBeSpecified() }
        val dialect by value { IsfShaderDialect }
        val glslCode by value { testToolchain.parse(src, fileName) }
        val analyzer by value { dialect.match(glslCode, testToolchain.plugins) }
        val matchLevel by value { analyzer.matchLevel }
        val shaderAnalysis by value { analyzer.analyze() }
        val openShader by value { testToolchain.openShader(shaderAnalysis) }

        context("a shader having an ISF block at the top") {
            override(fileName) { "Float Input.fs" }
            override(src) {
                """
                    /*{
                        "DESCRIPTION": "Demonstrates a float and long input",
                        "CREDIT": "by VIDVOX",
                        "ISFVSN": "2.0",
                        "CATEGORIES": [
                            "TEST-GLSL"
                        ],
                        "INPUTS": [
                            {
                                "NAME": "level",
                                "TYPE": "float",
                                "LABEL": "Color Level",
                                "DEFAULT": 0.5,
                                "MIN": 0.0,
                                "MAX": 1.0
                            },
                            {
                                "DEFAULT": 1,
                                "LABEL": "Color Channel",
                                "LABELS": [ "Red", "Green", "Blue" ],
                                "NAME": "channel",
                                "TYPE": "long",
                                "VALUES": [ 1, 2, 3 ]
                            }
                        ]
                    }*/
    
                    void main() {
                        gl_FragColor = vec4(
                            channel == 1 ? level : 0.,
                            channel == 2 ? level : 0.,
                            channel == 3 ? level : 0.,
                            1.0
                        );
                    }
                """.trimIndent()
            }

            it("is an excellent match") {
                expect(matchLevel).toEqual(MatchLevel.Excellent)
            }

            it("gets its title from the file name") {
                expect(openShader.title).toEqual("Float Input")
            }

            it("finds the input port") {
                expect(openShader.inputPorts).containsExactly(
                    InputPort(
                        "level", ContentType.Float, GlslType.Float, "Color Level",
                        pluginRef = PluginRef("baaahs.Core", "Slider"),
                        pluginConfig = buildJsonObject {
                            put("min", 0f.json)
                            put("max", 1f.json)
                            put("default", .5f.json)
                        },
                        isImplicit = true
                    ),
                    InputPort(
                        "channel", ContentType.Int, GlslType.Int, "Color Channel",
                        pluginRef = PluginRef("baaahs.Core", "Select"),
                        pluginConfig = buildJsonObject {
                            put("labels", listOf("Red".json, "Green".json, "Blue".json).json)
                            put("values", listOf(1.json, 2.json, 3.json).json)
                            put("default", 1.json)
                        },
                        isImplicit = true
                    )
                )
            }

            it("generates correct data sources") {
                expect(SelectFeed.build(openShader.inputPorts[1]))
                    .toEqual(SelectFeed("Color Channel", listOf(1 to "Red", 2 to "Green", 3 to "Blue"), 0))
            }

            it("finds the output port") {
                expect(shaderAnalysis.outputPorts).containsExactly(
                    OutputPort(ContentType.Color, description = "Output Color", id = "gl_FragColor")
                )
            }

            context("when a shader refers to gl_FragCoord") {
                override(src) {
                    """
                        /*{}*/
                        void main() {
                            gl_FragColor = vec4(gl_FragCoord.xy, 0., 1.);
                        }
                    """.trimIndent()
                }

                it("includes it as an input port") {
                    expect(openShader.inputPorts).containsExactly(
                        InputPort(
                            "gl_FragCoord", ContentType.UvCoordinate, GlslType.Vec4,
                            "Coordinates", isImplicit = true
                        )
                    )
                }
            }

            context("when a shader refers to isf_FragNormCoord") {
                override(src) {
                    """
                        /*{ }*/
                        void main() {
                            gl_FragColor = vec4(isf_FragNormCoord.xy, 0., 1.);
                        }
                    """.trimIndent()
                }

                it("includes it as an input port") {
                    expect(openShader.inputPorts).containsExactly(
                        InputPort(
                            "isf_FragNormCoord", ContentType.UvCoordinate, GlslType.Vec2,
                            "U/V Coordinate", isImplicit = true
                        )
                    )
                }
            }

            context("using TIME") {
                override(src) {
                    """
                        /*{ }*/
                        void main() { gl_FragColor = vec4(TIME, TIME, TIME, 0.); }
                    """.trimIndent()

                }

                it("includes it as an input port") {
                    expect(openShader.inputPorts).containsExactly(
                        InputPort("TIME", ContentType.Time, GlslType.Float, "Time", isImplicit = true)
                    )
                }

                context("from the right hand side of a global variable") {
                    override(src) {
                        """
                        /*{ }*/
                        float theTime = TIME;
                    """.trimIndent()

                    }

                    it("includes it as an input port") {
                        expect(openShader.inputPorts).containsExactly(
                            InputPort("TIME", ContentType.Time, GlslType.Float, "Time", isImplicit = true)
                        )
                    }
                }
            }

            context("using IMG_PIXEL etc.") {
                /*language=glsl*/
                override(src) {
                    """
                        /*{
                            "INPUTS": [
                                { "NAME": "inputImage", "TYPE": "image" }
                            ]
                        }*/
        
                        void main() {
                            gl_FragColor = IMG_NORM_PIXEL(inputImage, gl_FragCoord.xy);
                        }
                    """.trimIndent()

                }

                it("includes it as an input port") {
                    expect(openShader.inputPorts).containsExactly(
                        InputPort("gl_FragCoord", ContentType.UvCoordinate, GlslType.Vec4, "Coordinates",
                            isImplicit = true),
                        InputPort("inputImage", ContentType.Color, GlslType.Vec4, "Input Image",
                            isImplicit = true,
                            injectedData = mapOf("uv" to ContentType.UvCoordinate),
                            glslArgSite = GlslCode.GlslFunction(
                                "inputImage", GlslType.Vec4,
                                listOf(GlslCode.GlslParam("uv", GlslType.Vec2, true)),
                                "vec4 inputImage(vec2 uv);",
                                isAbstract = true, isGlobalInput = true
                            )
                        )
                    )
                    expect(openShader.inputPorts.last().isAbstractFunction).toBe(true)
                }

                it("causes function bodies to be rewritten") {
                    expect(
                        openShader.toGlsl(
                            null,
                            ShaderSubstitutions(
                                openShader,
                                GlslCode.Namespace("pfx_"),
                                mapOf("inputImage" to GlslExpr("pfx_inputImage(gl_FragCoord.xy)"))
                            )
                        )
                    ).toEqual(
                        """
                            #line 7
                            void pfx__main() {
                                gl_FragColor = pfx_inputImage(gl_FragCoord.xy);
                            }
                        """.trimIndent()
                    )
                }

                context("linking") {
                    val upstreamShader by value {
                        testToolchain.import(
                            """
                                // Pinks
                                uniform vec2 resolution;
                                void main(void) {
                                    gl_FragColor = vec4(gl_FragCoord.xy / resolution, 0.0, 1.0);
                                }
                            """.trimIndent()
                        )
                    }
                    val suggestions by value { autoWire(upstreamShader, openShader.shader) }
                    val patches by value { suggestions.acceptSuggestedLinkOptions().confirm() }
                    val mutableLinks by value { patches.mutablePatches.only().incomingLinks }

                    it("identifies the shader as a filter") {
                        expect(openShader.isFilter).toBe(true)
                    }

                    it("auto links inputImage to upstream (main stream) image") {
                        suggestions.dumpOptions()
                    }

                    it("suggests sensible options") {
                        expect(suggestions.find { it.mutableShader.title == openShader.title }!!
                            .linkOptionsFor("inputImage").stringify()).toBe("""
                                Data Source:
                                - BeatLink
                                - Custom slider Slider (advanced)
                                - Pixel Distance from Edge
                                * The Scale Slider
                                - Time
                                Stream:
                                - Main Stream
                            """.trimIndent())
                    }

                    context("program generation") {
                        val linkedProgram by value {
                            autoWireWithDefaults(upstreamShader, openShader.shader) {
                                editShader(openShader.title) {
                                    // TODO: STOPSHIP: 11/1/22 it should be auto prioritized higher because it's a filter
//                                    priority = 1f
                                }
                            }
                            patches.openForPreview(testToolchain, ContentType.Color)!!
                        }
                        val glsl by value { linkedProgram.toGlsl().trim() }

                        it("generates a valid GLSL program") {
                            val linkNodes = linkedProgram.linkNodes
                                .filter { (p,l) -> p is LinkedPatch }
                                .values
                                .sortedBy { it.index }
                                .map { it.toString() }
                            println("linkNodes = \n$linkNodes")

                            DotDag().apply {
                                visit(PixelArrayDevice, linkedProgram)
                            }.text.also { println(it) }

                            /*language=glsl*/
                            val expected = """
                                #ifdef GL_ES
                                precision mediump float;
                                #endif

                                // SparkleMotion-generated GLSL

                                layout(location = 0) out vec4 sm_result;

                                // Feed: Resolution
                                uniform vec2 in_resolution;

                                // Shader: Pinks; namespace: p0
                                // Pinks

                                vec4 p0_pinks_gl_FragColor = vec4(0., 0., 0., 1.);
                                vec2 p0_global_gl_FragCoord = vec2(0.);

                                #line 3 0
                                void p0_pinks_main(void) {
                                    p0_pinks_gl_FragColor = vec4(p0_global_gl_FragCoord.xy / in_resolution, 0.0, 1.0);
                                }

                                // Shader: Float Input; namespace: p1
                                // Float Input

                                vec4 p1_floatInput_gl_FragColor = vec4(0., 0., 0., 1.);

                                vec4 p1_floatInput_inputImage(vec2 uv) {
                                    // Invoke Pinks
                                    p0_global_gl_FragCoord = uv;
                                    p0_pinks_main();

                                    return p0_pinks_gl_FragColor;
                                }

                                #line 7 1
                                void p1_floatInput_main() {
                                    p1_floatInput_gl_FragColor = p1_floatInput_inputImage(gl_FragCoord.xy);
                                }


                                #line 10001
                                void main() {
                                    // Invoke Float Input
                                    p1_floatInput_main();

                                    sm_result = p1_floatInput_gl_FragColor;
                                }
                            """.trimIndent()
                            expect(glsl).toEqual(expected)
                        }
                    }
                }
            }

            context("with multiple out parameters") {
                override(src) {
                    "void main(in vec2 fragCoord, out vec4 fragColor, out float other) { gl_FragColor = vec4(gl_FragCoord, 0., 1.); };"
                }

                it("fails to validate") {
                    expect(shaderAnalysis.isValid).toBe(false)
                    expect(shaderAnalysis.errors).contains(
                        GlslError("Too many output ports found: [fragColor, gl_FragColor, other].", row = 1)
                    )
                }
            }

            context("which isn't valid JSON") {
                override(src) {
                    """
                        /*{ "DESC }*/
                        void main() { gl_FragColor = vec4(level,level,level,1.0); }
                    """.trimIndent()
                }

                it("fails to validate") {
                    expect(shaderAnalysis.isValid).toBe(false)
                    expect(shaderAnalysis.errors).containsExactly(
                        GlslError(
                            "Unexpected JSON token at offset 2: Expected quotation mark '\"', but had '\"' instead at path: \$\n" +
                                    "JSON input: { \"DESC }", 1
                        )
                    )
                }
            }

            context("with leading whitespace") {
                override(src) { " /*{}*/\nvoid main() { gl_FragColor = vec4(level,level,level,1.0); }" }
                it("is an excellent match") {
                    expect(matchLevel).toEqual(MatchLevel.Excellent)
                }
            }

            context("with whitespace before the JSON begins") {
                override(src) { "/*\n{}\n*/\nvoid main() { gl_FragColor = vec4(level,level,level,1.0); }" }
                it("is an excellent match") {
                    expect(matchLevel).toEqual(MatchLevel.Excellent)
                }
            }
        }

        context("a shader without an ISF block at the top") {
            override(src) { "void main() {\n  gl_FragColor = vec4(level,level,level,1.0);\n}" }
            it("is not a match") {
                expect(matchLevel).toEqual(MatchLevel.NoMatch)
            }
        }
    }
})

private val Boolean.json get() = JsonPrimitive(this)
private val Int.json get() = JsonPrimitive(this)
private val Float.json get() = JsonPrimitive(this)
private val String.json get() = JsonPrimitive(this)
private val Array<JsonElement>.json get() = JsonArray(this.toList())
private val List<JsonElement>.json get() = JsonArray(this)
