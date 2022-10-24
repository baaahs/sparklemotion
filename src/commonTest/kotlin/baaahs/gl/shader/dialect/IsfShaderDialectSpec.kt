package baaahs.gl.shader.dialect

import baaahs.describe
import baaahs.gl.glsl.GlslError
import baaahs.gl.glsl.GlslType
import baaahs.gl.override
import baaahs.gl.patch.ContentType
import baaahs.gl.shader.InputPort
import baaahs.gl.shader.OutputPort
import baaahs.gl.testToolchain
import baaahs.plugin.PluginRef
import baaahs.toBeSpecified
import baaahs.toEqual
import ch.tutteli.atrium.api.fluent.en_GB.contains
import ch.tutteli.atrium.api.fluent.en_GB.containsExactly
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
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
                        "DESCRIPTION": "Demonstrates a float input",
                        "CREDIT": "by VIDVOX",
                        "ISFVSN": "2.0",
                        "CATEGORIES": [
                            "TEST-GLSL"
                        ],
                        "INPUTS": [
                            {
                                "NAME": "level",
                                "TYPE": "float",
                                "LABEL": "Gray Level",
                                "DEFAULT": 0.5,
                                "MIN": 0.0,
                                "MAX": 1.0
                            }
                        ]
                    }*/
    
                    void main() {
                        gl_FragColor = vec4(level,level,level,1.0);
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
                        "level", ContentType.Float, GlslType.Float, "Gray Level",
                        pluginRef = PluginRef("baaahs.Core", "Slider"),
                        pluginConfig = buildJsonObject {
                            put("min", JsonPrimitive(0f))
                            put("max", JsonPrimitive(1f))
                            put("default", JsonPrimitive(.5f))
                        },
                        isImplicit = true
                    )
                )
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
                        GlslError("Unexpected JSON token at offset 2: Expected quotation mark '\"', but had '\"' instead at path: \$\n" +
                                "JSON input: { \"DESC }", 1)
                    )
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
