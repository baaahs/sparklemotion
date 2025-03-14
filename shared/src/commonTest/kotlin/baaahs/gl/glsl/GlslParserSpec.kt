package baaahs.gl.glsl

import baaahs.describe
import baaahs.gl.expectStatements
import baaahs.gl.glsl.GlslCode.*
import baaahs.gl.override
import baaahs.kotest.value
import baaahs.only
import baaahs.toBeSpecified
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.*
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly

class GlslParserSpec : DescribeSpec({
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
                    
                    uniform vec4 palette[3]; // @@Palette
                    
                    void mainFunc( out vec4 fragColor, in vec2 fragCoord )
                    {
                        vec2 uv = fragCoord.xy / resolution.xy;
                        fragColor = vec4(uv.xy, 0., 1.);
                    }
                    
                    void anotherFunc(vec3 color[2], FixtureInfo fixtureInfo) {}
                    
                    #define N 4
                    lowp void yetAnotherFunc(mediump vec3 color[N]) {}
                    
                    void main() {
                        mainFunc(gl_FragColor, gl_FragCoord);
                    }
                    """.trimIndent()
                }
                val glslCode by value { glslParser.parse(shaderText, fileName) }

                it("finds statements including line numbers") {
                    val fixtureInfoStruct = GlslType.Struct(
                        "FixtureInfo",
                        "position" to GlslType.Vec3,
                        "rotation" to GlslType.Vec3
                    )
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
                                fixtureInfoStruct,
                                "uniform struct FixtureInfo {\n" +
                                        "    vec3 position;\n" +
                                        "    vec3 rotation;\n" +
                                        "} leftEye;",
                                isUniform = true,
                                lineNumber = 13,
                                comments = listOf("@@AnotherClass key=value key2=value2")
                            ),
                            GlslVar(
                                "palette",
                                GlslType.Vec4.arrayOf(2),
                                "uniform vec4 palette[3];",
                                isUniform = true,
                                lineNumber = 18,
                                comments = listOf("@@Palette")
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
                                lineNumber = 20
                            ),
                            GlslFunction(
                                "anotherFunc",
                                GlslType.Void,
                                params = listOf(
                                    GlslParam("color[3]", GlslType.Vec4),
                                    GlslParam("fixtureInfo", fixtureInfoStruct),
                                ),
                                fullText = "void anotherFunc(vec3 color[2], FixtureInfo fixtureInfo) {}",
                                lineNumber = 26
                            ),
                            GlslFunction(
                                "yetAnotherFunc",
                                GlslType.Void,
                                params = listOf(
                                    GlslParam("color[4]", GlslType.Vec3.arrayOf(4)),
                                ),
                                fullText = "lowp void yetAnotherFunc(mediump vec3 color[4]) {}",
                                lineNumber = 28
                            ),
                            GlslFunction(
                                "main",
                                GlslType.Void,
                                params = emptyList(),
                                fullText = "void main() {\n" +
                                        "    mainFunc(gl_FragColor, gl_FragCoord);\n" +
                                        "}",
                                lineNumber = 31
                            )
                        ), { glslParser.findStatements(shaderText) }, true
                    )
                }

                it("finds the global variables") {
                    glslCode.globalVars.toList()
                        .shouldContainExactly(
                            GlslVar(
                                "time", GlslType.Float,
                                fullText = "uniform float time;", isUniform = true, lineNumber = 5,
                                comments = listOf(" trailing comment")
                            ),
                            GlslVar(
                                "resolution", GlslType.Vec2,
                                fullText = "uniform vec2  resolution;", isUniform = true, lineNumber = 10,
                                comments = listOf(" @@HintClass", "   key=value", "   key2=value2")
                            ),
                            GlslVar(
                                "palette", GlslType.Vec4.arrayOf(3),
                                fullText = "uniform vec4 palette[3];", isUniform = true, lineNumber = 18,
                                comments = listOf(" @@Palette")
                            ),
                            GlslVar(
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
                    glslCode.functions.map { it.prettify() }
                        .shouldContainExactly(
                            "void mainFunc(out vec4 fragColor, in vec2 fragCoord)",
                            "void anotherFunc(in vec3 color[2], in FixtureInfo fixtureInfo)",
                            "void yetAnotherFunc(in vec3 color[4])",
                            "void main()"
                        )
                }

                it("finds the structs") {
                    glslCode.structs.map { "${it.lineNumber}: ${it.fullText}" }
                        .shouldContainExactly(
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
                        glslCode.globalVars.toList().shouldContainExactly(
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
                        glslCode.functions.map { it.prettify() }.shouldContainExactly(
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

                            val glsl = glslFunction.toGlsl(null, TokenRewriter { text ->
                                if (text == "main") Namespace("ns").qualify(text) else text
                            })

                            glsl.trim().shouldBe(
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

                context("with #if/#elif's") {
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
                            glslCode.globalVars.toList().shouldBeEmpty()
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
                            glslCode.globalVars.map { it.name }.shouldContainExactly("shouldBeDefined")
                        }
                    }

                    context("with a static expression including an elif") {
                        override(shaderText) {
                            /**language=glsl*/
                            """
                                #if 0 > 1
                                uniform float zeroIsGreaterThanOne;
                                #elif 1 > 0
                                uniform float oneIsGreaterThanZero;
                                #else
                                uniform float neitherIsTrue;
                                #endif
                            """.trimIndent()
                        }

                        it("includes the line") {
                            glslCode.globalVars.map { it.name }.shouldContainExactly("oneIsGreaterThanZero")
                        }
                    }

                    context("with a static expression falling through to an else") {
                        override(shaderText) {
                            /**language=glsl*/
                            """
                                #if 0 == 1
                                uniform float zeroIsOne;
                                #elif 1 == 0
                                uniform float oneIsZero;
                                #else
                                uniform float elseClause;
                                #endif
                            """.trimIndent()
                        }

                        it("includes the line") {
                            glslCode.globalVars.map { it.name }.shouldContainExactly("elseClause")
                        }
                    }

                    context("with expressions including a macro expansion") {
                        override(shaderText) {
                            /**language=glsl*/
                            """
                                #define VALUE 1
                                #if VALUE == 0
                                uniform float valueIsZero;
                                #elif VALUE == 1
                                uniform float valueIsOne;
                                #elif VALUE == 2
                                uniform float valueIsTwo;
                                #else
                                uniform float valueIsUnknown;
                                #endif
                            """.trimIndent()
                        }

                        it("includes the correct line") {
                            glslCode.globalVars.map { it.name }.shouldContainExactly("valueIsOne")
                        }
                    }

                    context("with an invalid expression") {
                        override(shaderText) {
                            /**language=glsl*/
                            """
                                #if FOO
                                uniform float valueIsUnknown;
                                #endif
                            """.trimIndent()
                        }

                        it("generates an analysis error") {
                            shouldThrow<AnalysisException> { glslCode }
                                .message shouldBe("Shader analysis error: Could not resolve variable 'FOO'")
                        }
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

                        val glsl = glslFunction.toGlsl(null, TokenRewriter { text ->
                            if (text == "main") Namespace("ns").qualify(text) else text
                        })

                        glsl.trim().shouldBe(
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

                // TODO: Test that #elif/#else inside unfollowed branches are ignored.
                xcontext("with nested ifs") {}

                context("with unbalanced #endif") {
                    override(shaderText) { "#endif" }

                    it("generates an analysis error") {
                        shouldThrow<AnalysisException> { glslCode }
                            .message.shouldBe("Shader analysis error: #endif outside of #if")
                    }
                }

                context("with unbalanced #else") {
                    override(shaderText) { "#else" }

                    it("generates an analysis error") {
                        shouldThrow<AnalysisException> { glslCode }
                            .message.shouldBe("Shader analysis error: #else outside of #if/#endif")
                    }
                }

                context("with unbalanced #elif") {
                    override(shaderText) { "#elif FOO" }

                    it("generates an analysis error") {
                        shouldThrow<AnalysisException> { glslCode }
                            .message.shouldBe("Shader analysis error: #elif outside of #if/#endif")
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
                        glslCode.globalVars.toList().shouldBeEmpty()
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
                        glslCode.functions.map { it.prettify() }
                            .shouldContainExactly(
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
                        glslCode.functions.map { it.prettify() }
                            .shouldContainExactly(
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
                        glslCode.findFunction("main")
                            .shouldBe(
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
                            glslCode.findFunction("main")
                                .shouldBe(
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
                            glslCode.findFunction("main")
                                .shouldBe(
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
                        glslCode.functions.map { it.prettify() }
                            .shouldContainExactly(
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
                    glslCode.globalVars.only("global var").shouldBe(
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