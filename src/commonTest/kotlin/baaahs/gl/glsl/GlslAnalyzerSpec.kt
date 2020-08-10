package baaahs.gl.glsl

import baaahs.gl.expectStatements
import baaahs.gl.glsl.GlslAnalyzer.GlslStatement
import baaahs.gl.override
import baaahs.gl.patch.ContentType
import baaahs.gl.shader.InputPort
import baaahs.glsl.Shaders
import baaahs.only
import baaahs.plugin.PluginRef
import baaahs.toBeSpecified
import kotlinx.serialization.json.json
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.expect

object GlslAnalyzerSpec : Spek({
    describe("ShaderFragment") {
        context("given some GLSL code") {
            val shaderText by value<String> { toBeSpecified() }
            val glslAnalyzer by value { GlslAnalyzer() }
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
                    
                    struct MovingHead {
                        float pan;
                        float tilt;
                    };
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
                    expect("This Shader's Name") { importedShader.title }
                }

                it("finds statements including line numbers") {
                    expectStatements(
                        listOf(
                            GlslStatement(
                                "precision mediump float;",
                                listOf("This Shader's Name", "Other stuff."),
                                lineNumber = 1
                            ),
                            GlslStatement(
                                "uniform float time;", lineNumber = 5,
                                comments = listOf(" trailing comment")
                            ),
                            GlslStatement(
                                "\n\n\n\nuniform vec2  resolution;", lineNumber = 5,
                                comments = listOf(" @@HintClass", "   key=value", "   key2=value2")
                            ),
                            GlslStatement(
                                "struct MovingHead {\n" +
                                        "    float pan;\n" +
                                        "    float tilt;\n" +
                                        "};", lineNumber = 12
                            ),
                            GlslStatement(
                                "void mainFunc( out vec4 fragColor, in vec2 fragCoord )\n" +
                                        "{\n" +
                                        "    vec2 uv = fragCoord.xy / resolution.xy;\n" +
                                        "    fragColor = vec4(uv.xy, 0., 1.);\n" +
                                        "}", lineNumber = 16
                            ),
                            GlslStatement(
                                "void main() {\n" +
                                        "    mainFunc(gl_FragColor, gl_FragCoord);\n" +
                                        "}", lineNumber = 22
                            )
                        ), { glslAnalyzer.findStatements(shaderText) }, true
                    )
                }

                it("finds the global variables") {
                    expect(
                        listOf(
                            GlslCode.GlslVar(
                                "float", "time",
                                fullText = "uniform float time;", isUniform = true, lineNumber = 5,
                                comments = listOf(" trailing comment")
                            ),
                            GlslCode.GlslVar(
                                "vec2", "resolution",
                                fullText = " \n\n\n\nuniform vec2  resolution;", isUniform = true, lineNumber = 5,
                                comments = listOf(" @@HintClass", "   key=value", "   key2=value2")
                            )
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

                it("finds the structs") {
                    expect(
                        listOf(
                            "struct MovingHead {\n    float pan;\n    float tilt;\n};"
                        )
                    ) { glslCode.structs.map { it.fullText } }
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
                        expect(
                            listOf(
                                GlslCode.GlslVar(
                                    "float", "shouldBeDefined",
                                    fullText = "\n\n\nuniform float shouldBeDefined;", isUniform = true, lineNumber = 5
                                ),
                                GlslCode.GlslVar(
                                    "vec2", "shouldBeThis",
                                    fullText = "\n\n\n\n\nuniform vec2 shouldBeThis;", isUniform = true, lineNumber = 9
                                )
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
                                #define Circle(U,r) smoothstep(0., 1., abs(length(U)-r)-.02 )

                                uniform vec2 resolution;
                                void main() {
                                #ifdef xyz
                                    foo();
                                #endif
                                    gl_FragColor = Circle(gl_FragCoord, iResolution.x);
                                }
                                """.trimIndent()
                        }

                        it("handles and replaces directives with empty lines") {
                            val glslFunction = glslCode.functions.only()

                            val glsl = glslFunction.toGlsl(GlslCode.Namespace("ns"), emptySet(), emptyMap())

                            expect(
                                "#line 5\n" +
                                        "void ns_main() {\n" +
                                        "\n" +
                                        "\n" +
                                        "\n" +
                                        "    gl_FragColor = smoothstep(0., 1., abs(length(gl_FragCoord)-resolution.x)-.02 );\n" +
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
                        expect("main") { shader.entryPoint.name }
                    }

                    it("creates inputs for implicit uniforms") {
                        expect(
                            listOf(
                                InputPort(
                                    "time",
                                    "float",
                                    "Time",
                                    ContentType.Time
                                ),
                                InputPort(
                                    "resolution",
                                    "vec2",
                                    "Resolution",
                                    ContentType.Resolution
                                ),
                                InputPort("blueness", "float", "Blueness"),
                                InputPort(
                                    "gl_FragCoord",
                                    "vec4",
                                    "Coordinates",
                                    ContentType.UvCoordinate
                                )
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
                                InputPort("blueness", "float", "Blueness"),
                                InputPort(
                                    "iResolution",
                                    "vec3",
                                    "Resolution",
                                    ContentType.Resolution
                                ),
                                InputPort(
                                    "iTime",
                                    "float",
                                    "Time",
                                    ContentType.Time
                                ),
                                InputPort(
                                    "sm_FragCoord",
                                    "vec2",
                                    "Coordinates",
                                    ContentType.UvCoordinate
                                )
                            )
                        ) { shader.inputPorts.map { it.copy(glslVar = null) } }
                    }
                }

                context("with U/V translation shader") {
                    override(shaderText) { Shaders.cylindricalProjection.src }

                    it("identifies mainImage() as the entry point") {
                        expect("mainProjection") { shader.entryPoint.name }
                    }

                    it("creates inputs for implicit uniforms") {
                        expect(
                            listOf(
                                InputPort(
                                    "pixelCoordsTexture",
                                    "sampler2D",
                                    "U/V Coordinates Texture",
                                    ContentType.PixelCoordinatesTexture
                                ),
                                InputPort(
                                    "modelInfo",
                                    "ModelInfo",
                                    "Model Info",
                                    null
                                )
                            )
                        ) { shader.inputPorts.map { it.copy(glslVar = null) } }
                    }
                }
            }
        }
    }

    describe("GlslVar") {
        it("handles const initializers") {
            expect(
                GlslCode.GlslVar(
                    "vec3", "baseColor", "const vec3 baseColor = vec3(0.0,0.09,0.18);\n",
                    isConst = true
                )
            ) {
                GlslStatement("const vec3 baseColor = vec3(0.0,0.09,0.18);\n").asVarOrNull()
            }
        }

        it("doen't match functions") {
            expect(null) {
                GlslStatement("vec3 baseColor() { abc = vec3(0.0,0.09,0.18); }\n").asVarOrNull()
            }
        }

        context("with comments") {
            val hintClassStr by value { "whatever.package.Plugin:Thing" }
            val glslVar by value {
                GlslCode.GlslVar(
                    "float", "varName", isUniform = true,
                    comments = listOf(" @@$hintClassStr", "  key=value", "  key2=value2")
                )
            }

            it("parses hints") {
                expect(PluginRef("whatever.package.Plugin", "Thing")) { glslVar.hint!!.pluginRef }
                expect(json { "key" to "value"; "key2" to "value2" }) { glslVar.hint!!.config }
            }

            context("when package is unspecified") {
                override(hintClassStr) { "Thing" }

                it("defaults to baaahs.Core") {
                    expect(PluginRef("baaahs.Core", "Thing")) { glslVar.hint!!.pluginRef }
                }
            }

            context("when package is partially specified") {
                override(hintClassStr) { "FooPlugin:Thing" }

                it("defaults to baaahs.Core") {
                    expect(PluginRef("baaahs.FooPlugin", "Thing")) { glslVar.hint!!.pluginRef }
                }
            }
        }

        it("englishizes camel case names") {
            expect("A Man A Plan AAARGH Panama I Say") {
                GlslStatement("vec3 aManAPlanAAARGHPanamaISay;\n").asVarOrNull()!!.displayName()
            }
        }
    }
})