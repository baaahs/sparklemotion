package baaahs.glshaders

import baaahs.glshaders.GlslAnalyzer.GlslStatement
import baaahs.testing.override
import baaahs.testing.value
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
                    
                    uniform float time;
                    uniform vec2  resolution;
                    
                    void mainFunc( out vec4 fragColor, in vec2 fragCoord )
                    {
                        vec2 uv = fragCoord.xy / iResolution.xy;
                        fragColor = vec4(uv.xy, 0., 1.);
                    }
                    
                    void main() {
                        mainFunc(gl_FragColor, gl_FragCoord);
                    }
                    """.trimIndent()
                }
                val glslCode by value { GlslAnalyzer().analyze(shaderText).stripSource() }

                it("finds the title") {
                    expect("This Shader's Name") { glslCode.title }
                }

                it("finds statements including line numbers") {
                    expectStatements(
                        listOf(
                            GlslStatement(
                                "uniform float time;",
                                listOf("This Shader's Name", "Other stuff."),
                                lineNumber = 1
                            ),
                            GlslStatement("uniform vec2  resolution;", lineNumber = 5),
                            GlslStatement(
                                "void mainFunc( out vec4 fragColor, in vec2 fragCoord )\n" +
                                        "{\n" +
                                        "    vec2 uv = fragCoord.xy / iResolution.xy;\n" +
                                        "    fragColor = vec4(uv.xy, 0., 1.);\n" +
                                        "}", lineNumber = 7
                            ),
                            GlslStatement(
                                "void main() {\n" +
                                        "    mainFunc(gl_FragColor, gl_FragCoord);\n" +
                                        "}", lineNumber = 13
                            )
                        ), { GlslAnalyzer().findStatements(shaderText) }, true
                    )
                }

                it("finds the global variables") {
                    expect(
                        listOf(
                            GlslCode.GlslVar("float", "time", isUniform = true),
                            GlslCode.GlslVar("vec2", "resolution", isUniform = true)
                        )
                    ) { glslCode.globalVars }
                }

                it("finds the functions") {
                    expect(
                        listOf(
                            GlslCode.GlslFunction(
                                "void", "mainFunc", " out vec4 fragColor, in vec2 fragCoord ",
                                "{\n" +
                                        "    vec2 uv = fragCoord.xy / iResolution.xy;\n" +
                                        "    fragColor = vec4(uv.xy, 0., 1.);\n" +
                                        "}"
                            ),

                            GlslCode.GlslFunction(
                                "void", "main", "",
                                "{\n" +
                                        "    mainFunc(gl_FragColor, gl_FragCoord);\n" +
                                        "}"
                            )
                        )
                    ) { glslCode.functions }
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
                                GlslCode.GlslVar("float", "shouldBeDefined", isUniform = true),
                                GlslCode.GlslVar("vec2", "shouldBeThis", isUniform = true)
                            )
                        ) { glslCode.globalVars }
                    }

                    it("finds the functions and performs substitutions") {
                        expect(
                            listOf(
                                GlslCode.GlslFunction(
                                    "void", "mainFunc", "out vec4 fragColor, in vec2 fragCoord",
                                    "{ fragColor = vec4(uv.xy, 3.14159, 1.); }"
                                ),

                                GlslCode.GlslFunction(
                                    "void", "main", "",
                                    "{ mainFunc(gl_FragColor, gl_FragCoord); }"
                                )
                            )
                        ) { glslCode.functions }
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
                        expect(
                            GlslCode.GlslFunction(
                                "void", "main", " void ",
                                "{\n" +
                                        "    vec2 uv = gl_FragCoord.xy / resolution.xy;\n" +
                                        "    gl_FragColor = vec4(uv.xy, 0., 1.);\n" +
                                        "}"
                            )
                        ) { shader.entryPoint.stripSource() }
                    }

                    it("creates inputs for implicit uniforms") {
                        expect(
                            setOf(
                                InputPort("vec4", "gl_FragCoord", "Coordinates", GlslCode.ContentType.UvCoordinate),
                                InputPort("float", "time", "Time", GlslCode.ContentType.Time),
                                InputPort("vec2", "resolution", "Resolution", GlslCode.ContentType.Resolution),
                                InputPort("float", "blueness", null, GlslCode.ContentType.Unknown)
                            )
                        ) { shader.inputPorts.toSet() }
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

                    it("creates an entry point function calling mainImage()") {
                        expect(
                            GlslCode.GlslFunction(
                                "void", "sm_main", "",
                                "{\n    mainImage(sm_FragColor, sm_FragCoord);\n}"
                            )
                        ) { shader.entryPoint }
                    }

                    it("creates inputs for implicit uniforms") {
                        expect(
                            setOf(
                                InputPort("vec2", "sm_FragCoord", "Coordinates", GlslCode.ContentType.UvCoordinate),
                                InputPort("float", "iTime", "Time", GlslCode.ContentType.Time),
                                InputPort("vec2", "iResolution", "Resolution", GlslCode.ContentType.Resolution),
                                InputPort("float", "blueness", null, GlslCode.ContentType.Unknown)
                            )
                        ) { shader.inputPorts.toSet() }
                    }
                }
            }
        }
    }
})