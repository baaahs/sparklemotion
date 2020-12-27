package baaahs.gl.glsl

import baaahs.describe
import baaahs.gl.expectStatements
import baaahs.gl.expects
import baaahs.gl.glsl.GlslCode.*
import baaahs.gl.override
import baaahs.gl.patch.ContentType
import baaahs.gl.shader.GenericShaderDialect
import baaahs.gl.shader.InputPort
import baaahs.gl.shader.ShaderToyShaderDialect
import baaahs.gl.testPlugins
import baaahs.glsl.Shaders
import baaahs.only
import baaahs.toBeSpecified
import baaahs.toEqual
import ch.tutteli.atrium.api.fluent.en_GB.containsExactly
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import org.spekframework.spek2.Spek

object GlslAnalyzerSpec : Spek({
    describe<GlslAnalyzer> {
        context("given some GLSL code") {
            val glslAnalyzer by value { GlslAnalyzer(testPlugins()) }
            val shaderText by value<String> { toBeSpecified() }

            context("#parse") {
                override(shaderText) {
                    /**language=glsl*/
                    """
                    // This Shader's Name
                    // Other stuff.
                    
                    precision mediump float;
                    uniform float time; // trailing comment
                    
                    // @@HintClass
                    //   key=value
                    //   key2=value2
                    uniform vec2  resolution;
                    
                    // @@AnotherClass key=value key2=value2
                    uniform struct MovingHeadInfo {
                        vec3 origin;
                        vec3 heading;
                    } leftEye;

                    void mainFunc( out vec4 fragColor, in vec2 fragCoord )
                    {
                        vec2 uv = fragCoord.xy / resolution.xy;
                        fragColor = vec4(uv.xy, 0., 1.);
                    }
                    
                    void main() {
                        mainFunc(gl_FragColor, gl_FragCoord);
                    }
                    """.trimIndent()
                }
                val glslCode by value { glslAnalyzer.parse(shaderText) }

                it("finds statements including line numbers") {
                    expectStatements(
                        listOf(
                            GlslOther(
                                "unknown",
                                "precision mediump float;",
                                lineNumber = 4,
                                listOf("This Shader's Name", "Other stuff.")
                            ),
                            GlslVar(
                                "time",
                                GlslType.Float,
                                "uniform float time;",
                                isUniform = true,
                                lineNumber = 5,
                                comments = listOf(" trailing comment")
                            ),
                            GlslVar(
                                "resolution",
                                GlslType.Vec2,
                                "uniform vec2  resolution;",
                                isUniform = true,
                                lineNumber = 10,
                                comments = listOf(" @@HintClass", "   key=value", "   key2=value2")
                            ),
                            GlslVar(
                                "leftEye",
                                GlslType.Struct(
                                    "MovingHeadInfo",
                                    mapOf("origin" to GlslType.Vec3, "heading" to GlslType.Vec3)),
                                "uniform struct MovingHeadInfo {\n" +
                                        "    vec3 origin;\n" +
                                        "    vec3 heading;\n" +
                                        "} leftEye;",
                                isUniform = true,
                                lineNumber = 13,
                                comments = listOf("@@AnotherClass key=value key2=value2")
                            ),
                            GlslFunction(
                                "mainFunc",
                                GlslType.Void,
                                params = listOf(
                                    GlslParam("fragColor", GlslType.Vec4, isOut = true),
                                    GlslParam("fragCoord", GlslType.Vec2, isIn = true)
                                ),
                                fullText = "void mainFunc( out vec4 fragColor, in vec2 fragCoord )\n" +
                                        "{\n" +
                                        "    vec2 uv = fragCoord.xy / resolution.xy;\n" +
                                        "    fragColor = vec4(uv.xy, 0., 1.);\n" +
                                        "}",
                                lineNumber = 18
                            ),
                            GlslFunction(
                                "mainFunc",
                                GlslType.Void,
                                params = emptyList(),
                                fullText = "void main() {\n" +
                                        "    mainFunc(gl_FragColor, gl_FragCoord);\n" +
                                        "}",
                                lineNumber = 24
                            )
                        ), { glslAnalyzer.findStatements(shaderText) }, true
                    )
                }

                it("finds the global variables") {
                    expect(glslCode.globalVars.toList())
                        .containsExactly(
                            GlslVar(
                                "time", GlslType.Float,
                                fullText = "uniform float time;", isUniform = true, lineNumber = 5,
                                comments = listOf(" trailing comment")
                            ), GlslVar(
                                "resolution", GlslType.Vec2,
                                fullText = "uniform vec2  resolution;", isUniform = true, lineNumber = 10,
                                comments = listOf(" @@HintClass", "   key=value", "   key2=value2")
                            ), GlslVar(
                                "leftEye",
                                GlslType.Struct(
                                    "MovingHeadInfo",
                                    mapOf("origin" to GlslType.Vec3, "heading" to GlslType.Vec3)
                                ),
                                fullText = "uniform MovingHeadInfo leftEye;", lineNumber = 13,
                                comments = listOf(" @@AnotherClass key=value key2=value2")
                            )
                        )
                }

                it("finds the functions") {
                    expect(glslCode.functions.map { it.prettify() })
                        .containsExactly(
                            "void mainFunc(out vec4 fragColor, in vec2 fragCoord)",
                            "void main()"
                        )
                }

                it("finds the structs") {
                    expect(glslCode.structs.map { "${it.lineNumber}: ${it.fullText}" })
                        .containsExactly(
                            "13: uniform struct MovingHeadInfo {\n    vec3 origin;\n    vec3 heading;\n} leftEye;"
                        )
                }

                context("with #ifdefs") {
                    override(shaderText) {
                        /**language=glsl*/
                        """
                        // Shader Name
                        
                        #ifdef NOT_DEFINED
                        uniform float shouldNotBeDefined;
                        #define A_NOT_DEFINED
                        #define DEF_VAL shouldNotBeThis
                        #else
                        uniform float shouldBeDefined;
                        #define B_IS_DEFINED
                        #define DEF_VAL shouldBeThis
                        #endif
                        #define PI 3.14159
                        
                        uniform vec2 DEF_VAL;
                        #ifdef A_NOT_DEFINED
                        void this_is_super_busted() {
                        #endif
                        #ifndef B_IS_DEFINED
                        }
                        #endif
                        
                        #ifdef B_IS_DEFINED
                        void mainFunc(out vec4 fragColor, in vec2 fragCoord) { fragColor = vec4(uv.xy, PI, 1.); }
                        #endif
                        #undef PI
                        void main() { mainFunc(gl_FragColor, gl_FragCoord); }
                        """.trimIndent()
                    }

                    it("finds the global variables and performs substitutions") {
                        expect(glslCode.globalVars.toList()).containsExactly(
                            GlslVar(
                                "shouldBeDefined", GlslType.Float,
                                fullText = "uniform float shouldBeDefined;", isUniform = true, lineNumber = 8
                            ),
                            GlslVar(
                                "shouldBeThis", GlslType.Vec2,
                                // TODO: 18 is wrong, should be 14!
                                fullText = "uniform vec2 shouldBeThis;", isUniform = true, lineNumber = 18
                            )
                        )
                    }

                    it("finds the functions and performs substitutions") {
                        expect(glslCode.functions.map { it.prettify() }).containsExactly(
                            "void mainFunc(out vec4 fragColor, in vec2 fragCoord)",
                            "void main()"
                        )
                    }

                    context("with define macros") {
                        override(shaderText) {
                            /**language=glsl*/
                            """
                                #define iResolution resolution
                                #define dividedBy(a,b) (a / b)
                                #define circle(U, r) smoothstep(0., 1., abs(length(U)-r)-.02 )

                                uniform vec2 resolution;
                                void main() {
                                #ifdef xyz
                                    foo();
                                #endif
                                    gl_FragColor = circle(gl_FragCoord, iResolution.x);
                                    gl_FragColor = circle(dividedBy(gl_FragCoord, 1.0), iResolution.x);
                                }
                                """.trimIndent()
                        }

                        it("handles nested macro expansions") {
                            val glslFunction = glslCode.functions.only()

                            val glsl = glslFunction.toGlsl(Namespace("ns"), emptySet(), emptyMap())

                            expect(glsl.trim())
                                .toBe(
                                    "#line 6\n" +
                                            "void ns_main() {\n" +
                                            "\n" +
                                            "\n" +
                                            "\n" +
                                            "    gl_FragColor = smoothstep(0., 1., abs(length(gl_FragCoord)-resolution.x)-.02 );\n" +
                                            "    gl_FragColor = smoothstep(0., 1., abs(length((gl_FragCoord / 1.0))-resolution.x)-.02 );\n" +
                                            "}\n".trimIndent()
                                )
                        }
                    }
                }

                context("with overloaded functions") {
                    override(shaderText) {
                        """
                            vec3 mod289(vec3 x) { return x - floor(x * (1.0 / 289.0)) * 289.0; }
                            vec4 mod289(vec4 x) { return x - floor(x * (1.0 / 289.0)) * 289.0; }
                            void main() {}
                        """.trimIndent()
                    }

                    it("finds both functions") {
                        expect(glslCode.functions.map { it.prettify() })
                            .containsExactly(
                                "vec3 mod289(in vec3 x)",
                                "vec4 mod289(in vec4 x)",
                                "void main()"
                            )
                    }
                }

                context("with a comment before a function") {
                    override(shaderText) {
                        """
                            uniform float time; // @type time1
                            // @return time2
                            float main() { return time + sin(time); }
                        """.trimIndent()
                    }

                    it("attaches the comment to that function") {
                        expectStatements(
                            listOf(
                                GlslVar(
                                    "time",
                                    GlslType.Float,
                                    "uniform float time;", lineNumber = 1,
                                    comments = listOf(" @type time1"),
                                    isUniform = true
                                ),
                                GlslFunction(
                                    "main",
                                    GlslType.Void,
                                    params = emptyList(), // TODO: 1 seems wrong here, shouldn't it be 3?
                                    fullText = "float main() { return time + sin(time); }\n",
                                    lineNumber = 3,
                                    comments = listOf(" @return time2")
                                ),
                            ), { glslAnalyzer.findStatements(shaderText) }, true
                        )
                    }
                }
            }

            context("#detectDialect") {
                override(shaderText) { "void main() {}" }

                val dialect by value { glslAnalyzer.detectDialect(shaderText) }

                it("is generic") {
                    expect(dialect).toBe(GenericShaderDialect)
                }

                context("for shaders having a mainImage function") {
                    override(shaderText) { "void mainImage() {}" }

                    it("is ShaderToy") {
                        expect(dialect).toBe(ShaderToyShaderDialect)
                    }
                }
            }

            context("#validate") {
                val validationResult by value { glslAnalyzer.analyze(shaderText) }

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
                val importedShader by value { glslAnalyzer.import(shaderText) }
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
                val shader by value { glslAnalyzer.openShader(shaderText) }

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
                        expect(shader.entryPoint.name).toBe("main")
                    }

                    it("creates inputs for implicit uniforms") {
                        expect(shader.inputPorts.map { it.copy(glslArgSite = null) })
                            .containsExactly(
                                InputPort(
                                    "gl_FragCoord",
                                    ContentType.UvCoordinate,
                                    GlslType.Vec4,
                                    "Coordinates",
                                    isImplicit = true
                                ),
                                InputPort("time", ContentType.Time, GlslType.Float, "Time"),
                                InputPort("resolution", ContentType.Resolution, GlslType.Vec2, "Resolution"),
                                InputPort("blueness", ContentType.unknown(GlslType.Float), GlslType.Float, "Blueness")
                            )
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
                        expect(shader.entryPoint.name).toBe("mainImage")
                    }

                    it("creates inputs for implicit uniforms") {
                        expects(
                            listOf(
                                InputPort("blueness", ContentType.unknown(GlslType.Float), GlslType.Float, "Blueness"),
                                InputPort("fragCoord", ContentType.UvCoordinate, GlslType.Vec2, "U/V Coordinates"),
                                InputPort(
                                    "iResolution",
                                    ContentType.Resolution,
                                    GlslType.Vec3,
                                    "Resolution",
                                    isImplicit = true
                                ),
                                InputPort("iTime", ContentType.Time, GlslType.Float, "Time", isImplicit = true)
                            )
                        ) { shader.inputPorts.map { it.copy(glslArgSite = null) } }
                    }
                }

                context("with U/V translation shader") {
                    override(shaderText) { Shaders.cylindricalProjection.src }

                    it("identifies mainImage() as the entry point") {
                        expect(shader.entryPoint.name).toBe("main")
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
                        ) { shader.inputPorts.map { it.copy(glslArgSite = null) } }
                    }
                }

                context("with invalid shader") {
                    override(shaderText) { "" }

                    it("provides a fake entry point function") {
                        expect(shader.entryPoint.name)
                            .toEqual("invalid")
                    }
                }
            }

            context("const initializers") {
                override(shaderText) { "const vec3 baseColor = vec3(0.0,0.09,0.18);\nvoid main() {}" }
                val glslCode by value { glslAnalyzer.parse(shaderText) }

                it("handles const initializers") {
                    expect(glslCode.globalVars.only("global var"))
                        .toBe(
                            GlslVar(
                                "baseColor", GlslType.Vec3, "const vec3 baseColor = vec3(0.0,0.09,0.18);",
                                isConst = true,
                                lineNumber = 1
                            )
                        )
                }
            }
        }
    }
})

private fun GlslFunction.prettify() =
    "${returnType.glslLiteral} ${name}(${params.joinToString(", ") { it.prettify() }})"

private fun GlslParam.prettify(): String = "${
    when (isIn to isOut) {
        false to false -> ""
        true to false -> "in "
        false to true -> "out "
        true to true -> "inout "
        else -> error("huh?")
    }
}${type.glslLiteral} ${name}${
    if (comments.isNotEmpty()) " /* ${comments.joinToString(" ")} */" else ""
}"

