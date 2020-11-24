package baaahs.gl.glsl

import baaahs.gl.expectStatements
import baaahs.gl.expects
import baaahs.gl.glsl.GlslCode.*
import baaahs.gl.override
import baaahs.gl.patch.ContentType
import baaahs.gl.shader.InputPort
import baaahs.gl.testPlugins
import baaahs.glsl.Shaders
import baaahs.only
import baaahs.toBeSpecified
import ch.tutteli.atrium.api.fluent.en_GB.containsExactly
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object GlslAnalyzerSpec : Spek({
    describe("ShaderFragment") {
        context("given some GLSL code") {
            val shaderText by value<String> { toBeSpecified() }
            val glslAnalyzer by value { GlslAnalyzer(testPlugins()) }
            val importedShader by value { glslAnalyzer.import(shaderText) }
            val glslCode by value { glslAnalyzer.analyze(importedShader.src) }

            context("#analyze") {
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
                    uniform struct MovingHead {
                        float pan;
                        float tilt;
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

                it("finds the title") {
                    expect(importedShader.title).toBe("This Shader's Name")
                }

                it("finds statements including line numbers") {
                    expectStatements(
                        listOf(
                            GlslOther(
                                "unknown",
                                "precision mediump float;",
                                lineNumber = 1,
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
                                "\n\n\n\nuniform vec2  resolution;",
                                isUniform = true,
                                lineNumber = 5,
                                comments = listOf(" @@HintClass", "   key=value", "   key2=value2")
                            ),
                            GlslVar(
                                "leftEye",
                                GlslType.from(
                                    "struct MovingHead {\n" +
                                            "    float pan;\n" +
                                            "    float tilt;\n" +
                                            "}"
                                ),
                                "uniform struct MovingHead {\n" +
                                        "    float pan;\n" +
                                        "    float tilt;\n" +
                                        "} leftEye;",
                                isUniform = true,
                                lineNumber = 12,
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
                                fullText = " \n\n\n\nuniform vec2  resolution;", isUniform = true, lineNumber = 5,
                                comments = listOf(" @@HintClass", "   key=value", "   key2=value2")
                            ), GlslVar(
                                "leftEye",
                                GlslType.from("struct MovingHead {\n    float pan;\n    float tilt;\n}"),
                                fullText = "uniform MovingHead leftEye;", lineNumber = 12,
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
                    expect(glslCode.structs.map { it.fullText })
                        .containsExactly(
                            "\nuniform struct MovingHead {\n    float pan;\n    float tilt;\n} leftEye;"
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
                                fullText = "\n\n\nuniform float shouldBeDefined;", isUniform = true, lineNumber = 5
                            ),
                            GlslVar(
                                "shouldBeThis", GlslType.Vec2,
                                fullText = "\n\n\n\n\nuniform vec2 shouldBeThis;", isUniform = true, lineNumber = 9
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

                            val glsl = glslFunction.toGlsl(GlslCode.Namespace("ns"), emptySet(), emptyMap())

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
                        """.trimIndent()
                    }

                    it("finds both functions") {
                        expect(glslCode.functions.map { it.prettify() })
                            .containsExactly(
                                "vec3 mod289(in vec3 x)",
                                "vec4 mod289(in vec4 x)"
                            )
                    }
                }

                context("with a comment before a function") {
                    override(shaderText) {
                        """
                            uniform float time; // @type time1
                            // @type time2
                            float mainMain() { return time + sin(time); }
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
                                    "mainMain",
                                    GlslType.Void,
                                    params = emptyList(), // TODO: 1 seems wrong here, shouldn't it be 3?
                                    fullText = "float mainMain() { return time + sin(time); }\n",
                                    lineNumber = 1,
                                    comments = listOf(" @type time2")
                                ),
                            ), { glslAnalyzer.findStatements(shaderText) }, true
                        )
                    }
                }
            }

            context("#asShader") {
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
                        expect(shader.inputPorts.map { it.copy(glslVar = null) })
                            .containsExactly(
                                InputPort("gl_FragCoord", GlslType.Vec4, "Coordinates", ContentType.UvCoordinateStream),
                                InputPort("time", GlslType.Float, "Time", ContentType.Time),
                                InputPort("resolution", GlslType.Vec2, "Resolution", ContentType.Resolution),
                                InputPort("blueness", GlslType.Float, "Blueness")
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
                                InputPort("blueness", GlslType.Float, "Blueness"),
                                InputPort("iResolution", GlslType.Vec3, "Resolution", ContentType.Resolution),
                                InputPort("iTime", GlslType.Float, "Time", ContentType.Time),
                                InputPort("sm_FragCoord", GlslType.Vec2, "Coordinates", ContentType.UvCoordinateStream)
                            )
                        ) { shader.inputPorts.map { it.copy(glslVar = null) } }
                    }
                }

                context("with U/V translation shader") {
                    override(shaderText) { Shaders.cylindricalProjection.src }

                    it("identifies mainImage() as the entry point") {
                        expect(shader.entryPoint.name).toBe("mainProjection")
                    }

                    it("creates inputs for implicit uniforms") {
                        expects(
                            listOf(
                                InputPort(
                                    "pixelCoordsTexture",
                                    GlslType.Sampler2D,
                                    "U/V Coordinates Texture",
                                    ContentType.PixelCoordinatesTexture
                                ),
                                InputPort("modelInfo", ContentType.ModelInfo.glslType, "Model Info", null)
                            )
                        ) { shader.inputPorts.map { it.copy(glslVar = null) } }
                    }
                }
            }

            context("const initializers") {
                override(shaderText) { "const vec3 baseColor = vec3(0.0,0.09,0.18);\n" }

                it("handles const initializers") {
                    expect(glslCode.statements.only("statement"))
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

