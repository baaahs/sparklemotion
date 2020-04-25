package baaahs.glshaders

import baaahs.glshaders.GlslAnalyzer.GlslStatement
import baaahs.only
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals
import kotlin.test.expect

fun String.esc() = replace("\n", "\\n")

fun GlslStatement.esc(lineNumbers: Boolean): String {
    val buf = StringBuilder()
    buf.append(text.trim().esc())
    if (comments.isNotEmpty())
        buf.append(" // ${comments.joinToString(" ") { it.trim().esc() }}")
    if (lineNumbers)
        buf.append(" # $lineNumber")
    return buf.toString()
}

fun expectStatements(
    expected: List<GlslStatement>,
    actual: () -> List<GlslStatement>,
    checkLineNumbers: Boolean = false
) {
    assertEquals(
        expected.map { it.esc(checkLineNumbers) }.joinToString("\n"),
        actual().map { it.esc(checkLineNumbers) }.joinToString("\n")
    )
}

object GlslAnalyzerSpec : Spek({
    describe("ShaderFragment") {
        context("given some GLSL code") {
            val shaderText by value<String> { TODO() }

            context("#analyze") {
                override(shaderText) {
                    /**language=glsl*/
                    """
                    // This Shader's Name
                    // Other stuff.
                    
                    precision mediump float;
                    uniform float time;
                    uniform vec2  resolution;
                    
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
                val glslCode by value { GlslAnalyzer().analyze(shaderText) }

                it("finds the title") {
                    expect("This Shader's Name") { glslCode.title }
                }

                it("finds statements including line numbers") {
                    expectStatements(
                        listOf(
                            GlslStatement(
                                "precision mediump float;",
                                listOf("This Shader's Name", "Other stuff."),
                                lineNumber = 1
                            ),
                            GlslStatement("uniform float time;", lineNumber = 5),
                            GlslStatement("uniform vec2  resolution;", lineNumber = 6),
                            GlslStatement(
                                "void mainFunc( out vec4 fragColor, in vec2 fragCoord )\n" +
                                        "{\n" +
                                        "    vec2 uv = fragCoord.xy / resolution.xy;\n" +
                                        "    fragColor = vec4(uv.xy, 0., 1.);\n" +
                                        "}", lineNumber = 8
                            ),
                            GlslStatement(
                                "void main() {\n" +
                                        "    mainFunc(gl_FragColor, gl_FragCoord);\n" +
                                        "}", lineNumber = 14
                            )
                        ), { GlslAnalyzer().findStatements(shaderText) }, true
                    )
                }

                it("finds the global variables") {
                    expect(
                        listOf(
                            GlslCode.GlslVar("float", "time",
                                fullText = "uniform float time;", isUniform = true, lineNumber = 5),
                            GlslCode.GlslVar("vec2", "resolution",
                                fullText = "uniform vec2  resolution;", isUniform = true, lineNumber = 6)
                        )
                    ) { glslCode.globalVars.toList() }
                }

                it("finds the functions") {
                    expect(
                        listOf(
                            "void mainFunc( out vec4 fragColor, in vec2 fragCoord )",
                            "void main()"
                        )
                    ) { glslCode.functions.map { "${it.returnType} ${it.name}(${it.params})" } }
                }

                context("with #ifdefs") {
                    override(shaderText) {
                        /**language=glsl*/
                        """
                        // Shader Name
                        
                        #ifdef NOT_DEFINED
                        uniform float shouldNotBeDefined;
                        #define NOT_DEFINED_A
                        #define DEF_VAL shouldNotBeThis
                        #else
                        uniform float shouldBeDefined;
                        #define NOT_DEFINED_B
                        #define DEF_VAL shouldBeThis
                        #endif
                        #define PI 3.14159
                        
                        uniform vec2 DEF_VAL;
                        #ifdef NOT_DEFINED_A
                        void this_is_super_busted() {
                        #endif
                        #ifndef NOT_DEFINED_B
                        }
                        #endif
                        
                        #ifdef NOT_DEFINED_B
                        void mainFunc(out vec4 fragColor, in vec2 fragCoord) { fragColor = vec4(uv.xy, PI, 1.); }
                        #endif
                        #undef PI
                        void main() { mainFunc(gl_FragColor, gl_FragCoord); }
                        """.trimIndent()
                    }

                    it("finds the global variables and performs substitutions") {
                        expect(
                            listOf(
                                GlslCode.GlslVar("float", "shouldBeDefined",
                                    fullText = "\n\n\nuniform float shouldBeDefined;", isUniform = true, lineNumber = 5),
                                GlslCode.GlslVar("vec2", "shouldBeThis",
                                    fullText = "\n\n\n\n\nuniform vec2 shouldBeThis;", isUniform = true, lineNumber = 9)
                            )
                        ) { glslCode.globalVars.toList() }
                    }

                    it("finds the functions and performs substitutions") {
                        expect(
                            listOf(
                                "void mainFunc(out vec4 fragColor, in vec2 fragCoord)",
                                "void main()"
                            )
                        ) { glslCode.functions.map { "${it.returnType} ${it.name}(${it.params})" } }
                    }

                    context("with defines") {
                        override(shaderText) {
                            /**language=glsl*/
                            """
                                #define iResolution resolution
                                uniform vec2 resolution;
                                void main() {
                                #ifdef xyz
                                    foo();
                                #endif
                                    gl_FragColor = iResolution.x;
                                }
                                """.trimIndent()
                        }

                        it("handles and replaces directives with empty lines") {
                            val glslFunction = GlslAnalyzer().analyze(shaderText).functions.only()

                            val glsl = glslFunction.toGlsl(GlslCode.Namespace("ns"), emptySet(), emptyMap())

                            expect("#line 3\n" +
                                    "void ns_main() {\n" +
                                    "\n" +
                                    "\n" +
                                    "\n" +
                                    "    gl_FragColor = resolution.x;\n" +
                                    "}\n".trimIndent()
                            ) { glsl.trim() }
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
                        expect(
                            listOf(
                                "vec3 mod289(vec3 x)",
                                "vec4 mod289(vec4 x)"
                            )
                        ) { glslCode.functions.map { "${it.returnType} ${it.name}(${it.params})" } }
                    }
                }
            }

            context("#asShader") {
                val shader by value { GlslAnalyzer().asShader(shaderText) }

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
                        expect("main") { shader.entryPoint.name }
                    }

                    it("creates inputs for implicit uniforms") {
                        expect(
                            listOf(
                                InputPort("float", "time", "Time", GlslCode.ContentType.Time),
                                InputPort("vec2", "resolution", "Resolution", GlslCode.ContentType.Resolution),
                                InputPort("float", "blueness", "Blueness", GlslCode.ContentType.Unknown),
                                InputPort("vec4", "gl_FragCoord", "Coordinates", GlslCode.ContentType.UvCoordinate)
                            )
                        ) { shader.inputPorts.map { it.copy(glslVar = null) } }
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
                        expect("mainImage") { shader.entryPoint.name }
                    }

                    it("creates inputs for implicit uniforms") {
                        expect(
                            listOf(
                                InputPort("float", "blueness", "Blueness", GlslCode.ContentType.Unknown),
                                InputPort("vec3", "iResolution", "Resolution", GlslCode.ContentType.Resolution),
                                InputPort("float", "iTime", "Time", GlslCode.ContentType.Time),
                                InputPort("vec2", "sm_FragCoord", "Coordinates", GlslCode.ContentType.UvCoordinate)
                            )
                        ) { shader.inputPorts.map { it.copy(glslVar = null) } }
                    }
                }
            }
        }
    }
})