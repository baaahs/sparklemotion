package baaahs.gl.glsl

import baaahs.describe
import baaahs.gl.expectStatements
import baaahs.gl.glsl.GlslCode.*
import baaahs.gl.override
import baaahs.only
import baaahs.toBeSpecified
import baaahs.toEqual
import ch.tutteli.atrium.api.fluent.en_GB.containsExactly
import ch.tutteli.atrium.api.fluent.en_GB.isEmpty
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import org.spekframework.spek2.Spek

object GlslParserSpec : Spek({
    describe<GlslParser> {
        context("given some GLSL code") {
            val glslParser by value { GlslParser() }
            val shaderText by value<String> { toBeSpecified() }
            val fileName by value<String?> { null }

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
                    uniform struct FixtureInfo {
                        vec3 position;
                        vec3 rotation;
                    } leftEye;

                    void mainFunc( out vec4 fragColor, in vec2 fragCoord )
                    {
                        vec2 uv = fragCoord.xy / resolution.xy;
                        fragColor = vec4(uv.xy, 0., 1.);
                    }
                    
                    void anotherFunc(vec3 color[3]) {}
                    
                    #define N 4
                    void yetAnotherFunc(vec3 color[N]) {}
                    
                    void main() {
                        mainFunc(gl_FragColor, gl_FragCoord);
                    }
                    """.trimIndent()
                }
                val glslCode by value { glslParser.parse(shaderText, fileName) }

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
                                    "FixtureInfo",
                                    "position" to GlslType.Vec3,
                                    "rotation" to GlslType.Vec3
                                ),
                                "uniform struct FixtureInfo {\n" +
                                        "    vec3 position;\n" +
                                        "    vec3 rotation;\n" +
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
                                "anotherFunc",
                                GlslType.Void,
                                params = listOf(
                                    GlslParam("color[3]", GlslType.Vec3),
                                ),
                                fullText = "void anotherFunc(vec3 color[3]) {}",
                                lineNumber = 24
                            ),
                            GlslFunction(
                                "yetAnotherFunc",
                                GlslType.Void,
                                params = listOf(
                                    GlslParam("color[4]", GlslType.Vec3),
                                ),
                                fullText = "void yetAnotherFunc(vec3 color[4]) {}",
                                lineNumber = 26
                            ),
                            GlslFunction(
                                "main",
                                GlslType.Void,
                                params = emptyList(),
                                fullText = "void main() {\n" +
                                        "    mainFunc(gl_FragColor, gl_FragCoord);\n" +
                                        "}",
                                lineNumber = 29
                            )
                        ), { glslParser.findStatements(shaderText) }, true
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
                                    "FixtureInfo",
                                    "position" to GlslType.Vec3,
                                    "rotation" to GlslType.Vec3
                                ),
                                fullText = "uniform FixtureInfo leftEye;", lineNumber = 13,
                                comments = listOf(" @@AnotherClass key=value key2=value2")
                            )
                        )
                }

                it("finds the functions") {
                    expect(glslCode.functions.map { it.prettify() })
                        .containsExactly(
                            "void mainFunc(out vec4 fragColor, in vec2 fragCoord)",
                            "void anotherFunc(in vec3 color[3])",
                            "void yetAnotherFunc(in vec3 color[4])",
                            "void main()"
                        )
                }

                it("finds the structs") {
                    expect(glslCode.structs.map { "${it.lineNumber}: ${it.fullText}" })
                        .containsExactly(
                            "13: uniform struct FixtureInfo {\n    vec3 position;\n    vec3 rotation;\n} leftEye;"
                        )
                }

                context("with #ifdef's") {
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
                                #define iResolution resolution // ignore this comment
                                #define dividedBy(a,b) (a / b) /* ignore this comment too */
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

                            val glsl = glslFunction.toGlsl(null) { text ->
                                if (text == "main") Namespace("ns").qualify(text) else text
                            }

                            expect(glsl.trim())
                                .toBe(
                                    """
                                        #line 6
                                        void ns_main() {
                                        
                                        
                                        
                                            gl_FragColor = smoothstep(0., 1., abs(length(gl_FragCoord)-resolution.x)-.02 );
                                            gl_FragColor = smoothstep(0., 1., abs(length((gl_FragCoord / 1.0))-resolution.x)-.02 );
                                        }
                                    """.trimIndent()
                                )
                        }
                    }
                }

                context("with #if's") {
                    context("with a static expression which evaluates to false") {
                        override(shaderText) {
                            /**language=glsl*/
                            """
                                #if 0 > 1
                                uniform float shouldNotBeDefined;
                                #endif
                            """.trimIndent()
                        }

                        it("skips the line") {
                            expect(glslCode.globalVars.toList()).isEmpty()
                        }
                    }

                    context("with a static expression which evaluates to true") {
                        override(shaderText) {
                            /**language=glsl*/
                            """
                                #if 1 > 0
                                uniform float shouldBeDefined;
                                #endif
                            """.trimIndent()
                        }

                        it("includes the line") {
                            expect(glslCode.globalVars.toList()).containsExactly(
                                GlslVar(
                                    "shouldBeDefined", GlslType.Float,
                                    fullText = "uniform float shouldBeDefined;", isUniform = true, lineNumber = 2
                                )
                            )
                        }
                    }

                    context("with defined macros") {
                        override(shaderText) {
                            /**language=glsl*/
                            """
                                #define iResolution resolution // ignore this comment
                                #define dividedBy(a,b) (a / b) /* ignore this comment too */
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

                            val glsl = glslFunction.toGlsl(null) { text ->
                                if (text == "main") Namespace("ns").qualify(text) else text
                            }

                            expect(glsl.trim())
                                .toBe(
                                    """
                                        #line 6
                                        void ns_main() {
                                        
                                        
                                        
                                            gl_FragColor = smoothstep(0., 1., abs(length(gl_FragCoord)-resolution.x)-.02 );
                                            gl_FragColor = smoothstep(0., 1., abs(length((gl_FragCoord / 1.0))-resolution.x)-.02 );
                                        }
                                    """.trimIndent()
                                )
                        }
                    }
                }

                context("hash signs inside of comments") {
                    override(shaderText) {
                        /**language=glsl*/
                        """
                            // Title
                            
                            void main() {
                              // this #is ignored                             
                            }
                        """.trimIndent()
                    }

                    it("finds the global variables and performs substitutions") {
                        // Shouldn't throw "analysis error: directive #is ignored"
                        expect(glslCode.globalVars.toList()).isEmpty()
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

                context("with functions with const args") {
                    override(shaderText) {
                        """
                            float foo(const float x, const float y) { return 0.; }
                        """.trimIndent()
                    }

                    it("ignores the const qualifier") {
                        expect(glslCode.functions.map { it.prettify() })
                            .containsExactly(
                                "float foo(in float x, in float y)"
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
                            ), { glslParser.findStatements(shaderText) }, true
                        )
                    }
                }

                context("block comments") {
                    override(shaderText) {
                        """
                            /* Block comment! @return float */
                            float main() { return time + sin(time); }
                        """.trimIndent()
                    }

                    it("finds the comment") {
                        expect(glslCode.findFunction("main"))
                            .toEqual(
                                GlslFunction(
                                    "main", GlslType.Float, emptyList(),
                                    "float main() { return time + sin(time); }", 2,
                                    comments = listOf(" Block comment! @return float ")
                                )
                            )
                    }

                    context("multi-line") {
                        override(shaderText) {
                            """
                                /**
                                 * Block comment!
                                 * @return float
                                 */
                                float main() { return time + sin(time); }
                            """.trimIndent()
                        }

                        it("finds the comment") {
                            expect(glslCode.findFunction("main"))
                                .toEqual(
                                    GlslFunction(
                                        "main", GlslType.Float, emptyList(),
                                        "float main() { return time + sin(time); }", 5,
                                        comments = listOf("*\n * Block comment!\n * @return float\n ")
                                    )
                                )
                        }
                    }

                    context("on params") {
                        override(shaderText) {
                            """
                                float main(float time /* @type time */) { return time + sin(time); }
                            """.trimIndent()
                        }

                        it("finds the comment") {
                            expect(glslCode.findFunction("main"))
                                .toEqual(
                                    GlslFunction(
                                        "main", GlslType.Float, listOf(
                                            GlslParam(
                                                "time", GlslType.Float, true, false, 1,
                                                comments = listOf(" @type time ")
                                            )
                                        ),
                                        "float main(float time ) { return time + sin(time); }", 1
                                    )
                                )
                        }
                    }
                }

                context("function declarations without bodies") {
                    override(shaderText) {
                        """
                            // @param uv uv-coordinate
                            // @return uv-coordinate
                            vec4 channelA(vec2 uv);
                            
                            // @param uv uv-coordinate
                            // @return uv-coordinate
                            vec4 channelB(vec2 uv);

                            void main() {}
                        """.trimIndent()
                    }

                    it("are found and marked as abstract") {
                        expect(glslCode.functions.map { it.prettify() })
                            .containsExactly(
                                "vec4 channelA(in vec2 uv) [abstract]",
                                "vec4 channelB(in vec2 uv) [abstract]",
                                "void main()"
                            )
                    }
                }
            }

            context("const initializers") {
                override(shaderText) { "const vec3 baseColor = vec3(0.0,0.09,0.18);\nvoid main() {}" }
                val glslCode by value { glslParser.parse(shaderText, fileName) }

                it("handles const initializers") {
                    expect(glslCode.globalVars.only("global var"))
                        .toBe(
                            GlslVar(
                                "baseColor", GlslType.Vec3, "const vec3 baseColor = vec3(0.0,0.09,0.18);",
                                isConst = true,
                                initExpr = " = vec3(0.0,0.09,0.18)",
                                lineNumber = 1
                            )
                        )
                }
            }
        }
    }
})

private fun GlslFunction.prettify() =
    "${returnType.glslLiteral} ${name}" +
            "(${params.joinToString(", ") { it.prettify() }})" +
            if (isAbstract) " [abstract]" else ""

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