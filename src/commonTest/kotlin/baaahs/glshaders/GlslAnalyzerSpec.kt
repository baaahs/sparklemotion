package baaahs.glshaders

import baaahs.glshaders.GlslAnalyzer.GlslStatement
import baaahs.testing.value
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals
import kotlin.test.expect

fun String.esc() = replace("\n", "\\n")

fun GlslStatement.esc(): String =
    if (comments.isEmpty()) {
        text.trim().esc()
    } else {
        "${text.trim().esc()} // ${comments.joinToString(" ") { it.trim().esc() }}"
    }

fun expectStatements(expected: List<GlslStatement>, actual: () -> List<GlslStatement>) {
    assertEquals(expected.map { it.esc() }.joinToString("\n"), actual().map { it.esc() }.joinToString("\n"))
}

object GlslAnalyzerSpec : Spek({
    describe("ShaderFragment") {
        context("given a full GLSL program") {
            val shaderText by value {
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
            val fragment by value { GlslAnalyzer().analyze(shaderText) }

            it("finds the title") {
                expect("This Shader's Name") { fragment.title }
            }

            it("finds statements") {
                expectStatements(listOf(
                    GlslStatement("uniform float time;", listOf("This Shader's Name", "Other stuff.")),
                    GlslStatement("uniform vec2  resolution;"),
                    GlslStatement("void mainFunc( out vec4 fragColor, in vec2 fragCoord )\n" +
                            "{\n" +
                            "    vec2 uv = fragCoord.xy / iResolution.xy;\n" +
                            "    fragColor = vec4(uv.xy, 0., 1.);\n" +
                            "}"),
                    GlslStatement("void main() {\n" +
                            "    mainFunc(gl_FragColor, gl_FragCoord);\n" +
                            "}")
                )) { GlslAnalyzer().findStatements(shaderText) }
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
                val shaderText by value {
                    /**language=glsl*/
                    """
#ifdef NOT_DEFINED
uniform float shouldNotBeDefined;
#define NOT_DEFINED_A
#define DEF_VAL shouldNotBeThis
#else
uniform float shouldBeDefined;
#define NOT_DEFINED_B
#define DEF_VAL shouldBeThis
#endif

uniform float DEF_VAL;
#ifdef NOT_DEFINED_A
void this_is_super_busted() {
#endif
#ifndef NOT_DEFINED_B
}
#endif

#ifdef NOT_DEFINED_B
void mainFunc(out vec4 fragColor, in vec2 fragCoord) { fragCood = vec4(uv.xy, 0., 1.); }
#endif
void main() { mainFunc(gl_FragColor, gl_FragCoord); }
""".trimIndent()
                }

                it("finds the uniforms") {
                    expect(listOf(
                        ShaderFragment.GlslUniform("float", "shouldBeDefined")
                    )) { fragment.uniforms }
                }

                it("finds the functions") {
                    expect(listOf(
                        ShaderFragment.GlslFunction("void", "mainFunc", " out vec4 fragColor, in vec2 fragCoord ",
                            "{ fragColor = vec4(uv.xy, 0., 1.); }"),

                        ShaderFragment.GlslFunction("void", "main", "",
                            "{ mainFunc(gl_FragColor, gl_FragCoord); }")
                    )) { fragment.functions }
                }
            }
        }
    }
})