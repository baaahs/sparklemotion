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

fun expectStatements(expected: List<GlslStatement>, actual: () -> List<GlslStatement>, checkLineNumbers: Boolean = false) {
    assertEquals(
        expected.map { it.esc(checkLineNumbers) }.joinToString("\n"),
        actual().map { it.esc(checkLineNumbers) }.joinToString("\n"))
}

object GlslAnalyzerSpec : Spek({
    describe("ShaderFragment") {
        context("given a full GLSL program") {
            val shaderText by value {
/**language=glsl*/
"""// This Shader's Name
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
            val fragment by value { GlslAnalyzer().analyze(shaderText) }

            it("finds the title") {
                expect("This Shader's Name") { fragment.title }
            }

            it("finds statements including line numbers") {
                expectStatements(listOf(
                    GlslStatement("uniform float time;", listOf("This Shader's Name", "Other stuff."), lineNumber = 1),
                    GlslStatement("uniform vec2  resolution;", lineNumber = 5),
                    GlslStatement("void mainFunc( out vec4 fragColor, in vec2 fragCoord )\n" +
                            "{\n" +
                            "    vec2 uv = fragCoord.xy / iResolution.xy;\n" +
                            "    fragColor = vec4(uv.xy, 0., 1.);\n" +
                            "}", lineNumber = 6),
                    GlslStatement("void main() {\n" +
                            "    mainFunc(gl_FragColor, gl_FragCoord);\n" +
                            "}", lineNumber = 12)
                ), { GlslAnalyzer().findStatements(shaderText) }, true)
            }

            it("finds the uniforms") {
                expect(listOf(
                    ShaderFragment.GlslUniform("float", "time"),
                    ShaderFragment.GlslUniform("vec2", "resolution")
                )) { fragment.uniforms }
            }

            it("finds the functions") {
                expect(listOf(
                    ShaderFragment.GlslFunction("void", "mainFunc", " out vec4 fragColor, in vec2 fragCoord ",
                    "{\n" +
                            "    vec2 uv = fragCoord.xy / iResolution.xy;\n" +
                            "    fragColor = vec4(uv.xy, 0., 1.);\n" +
                            "}"),

                    ShaderFragment.GlslFunction("void", "main", "",
                    "{\n" +
                            "    mainFunc(gl_FragColor, gl_FragCoord);\n" +
                            "}")
                    )) { fragment.functions }
            }

            it("finds the entry point function") {
                expect(ShaderFragment.GlslFunction("void", "main", "",
                    "{\n" +
                            "    mainFunc(gl_FragColor, gl_FragCoord);\n" +
                            "}")
                ) { fragment.entryPoint }
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

                it("finds the uniforms and performs substitutions") {
                    expect(listOf(
                        ShaderFragment.GlslUniform("float", "shouldBeDefined"),
                        ShaderFragment.GlslUniform("vec2", "shouldBeThis")
                    )) { fragment.uniforms }
                }

                it("finds the functions and performs substitutions") {
                    expect(listOf(
                        ShaderFragment.GlslFunction("void", "mainFunc", "out vec4 fragColor, in vec2 fragCoord",
                            "{ fragColor = vec4(uv.xy, 3.14159, 1.); }"
                        ),

                        ShaderFragment.GlslFunction("void", "main", "",
                            "{ mainFunc(gl_FragColor, gl_FragCoord); }"
                        )
                    )) { fragment.functions }
                }
            }
        }
    }
})