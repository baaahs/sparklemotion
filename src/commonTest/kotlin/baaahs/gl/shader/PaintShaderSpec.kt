package baaahs.gl.shader

import baaahs.describe
import baaahs.gl.expects
import baaahs.gl.glsl.GlslAnalyzer
import baaahs.gl.glsl.GlslCode
import baaahs.gl.glsl.GlslType
import baaahs.gl.override
import baaahs.gl.patch.ContentType
import baaahs.gl.testPlugins
import baaahs.show.ShaderType
import baaahs.toBeSpecified
import baaahs.toEqual
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

@Suppress("unused")
object PaintShaderSpec : Spek({
    describe<PaintShader> {
        val shaderText by value { toBeSpecified<String>() }
        val analyzer by value { GlslAnalyzer(testPlugins()) }
        val shaderAnalysis by value { analyzer.validate(shaderText) }
        val openShader by value { analyzer.openShader(shaderText) }
        val shaderType by value { PaintShader }

        context("when return type is color") {
            override(shaderText) {
                """
                    // @return color
                    // @param uvIn uv-coordinate
                    vec4 main(vec4 uvIn) { ... }
                """.trimIndent()
            }

            it("#match returns Match") {
                expect(shaderType.matches(shaderAnalysis))
                    .toEqual(ShaderType.MatchLevel.Match)
            }
        }

        context("when using gl_FragColor and gl_FragCoord") {
            override(shaderText) {
                """
                    void main() { gl_FragColor = gl_FragCoord; }
                """.trimIndent()
            }

            it("#match returns Match") {
                expect(shaderType.matches(shaderAnalysis))
                    .toEqual(ShaderType.MatchLevel.Match)
            }
        }

        context("when return type isn't color") {
            override(shaderText) {
                """
                    vec4 main(vec4 foo) { ... }
                """.trimIndent()
            }

            it("#match returns NoMatch") {
                expect(shaderType.matches(shaderAnalysis))
                    .toEqual(ShaderType.MatchLevel.NoMatch)
            }
        }

        context("#template") {
            override(shaderText) { shaderType.newShaderFromTemplate().src }

            it("generates a paint shader") {
                expect(openShader.shaderType)
                    .toEqual(shaderType)
            }
        }
    }

    // TODO: This should be in GenericShaderPrototype probably;.
    describe("PaintShader") {
        val shaderText by value<String> { toBeSpecified() }
        val shader by value { GlslAnalyzer(testPlugins()).openShader(shaderText) }
        val namespace by value { GlslCode.Namespace("p0") }

        context("generic shaders") {
            override(shaderText) {
                /**language=glsl*/
                """
                    // This Shader's Name
                    // Other stuff.
                    
                    uniform float time;
                    uniform vec2  resolution;
                    uniform vec2  mouse;
                    uniform float blueness;
                    int someGlobalVar;
                    const int someConstVar = 123;
                    
                    float identity(float value) { return value; }

                    void main( void ) {
                        vec2 uv = gl_FragCoord.xy / resolution.xy;
                        gl_FragColor = vec4(uv.xy, identity(blueness), 1.);
                    }
                """.trimIndent()
            }

            it("finds magic uniforms") {
                expects(
                    listOf(
                        InputPort(
                            "gl_FragCoord",
                            ContentType.UvCoordinate,
                            GlslType.Vec4,
                            "Coordinates",
                            isImplicit = true
                        ),
                        InputPort("time", ContentType.Time, GlslType.Float, "Time"),
                        InputPort("resolution", ContentType.Resolution, GlslType.Vec2, "Resolution"),
                        InputPort("mouse", ContentType.Mouse, GlslType.Vec2, "Mouse"),
                        InputPort("blueness", ContentType.unknown(GlslType.Float), GlslType.Float, "Blueness")
                    )
                ) { shader.inputPorts.map { it.copy(glslArgSite = null) } }
            }

            it("generates function declarations") {
                expect(
                    shader.toGlsl(
                        namespace,
                        mapOf(
                            "resolution" to "in_resolution",
                            "blueness" to "aquamarinity",
                            "identity" to "p0_identity",
                            "gl_FragColor" to "sm_result"
                        )
                    ).trim()
                )
                    .toBe(
                        """
                        #line 8
                        int p0_someGlobalVar;
                        
                        #line 9
                        const int p0_someConstVar = 123;
                        
                        #line 11
                        float p0_identity(float value) { return value; }
                        
                        #line 13
                        void p0_main( void ) {
                            vec2 uv = gl_FragCoord.xy / in_resolution.xy;
                            sm_result = vec4(uv.xy, p0_identity(aquamarinity), 1.);
                        }
                    """.trimIndent()
                    )
            }

            it("generates invocation GLSL") {
                expect(shader.invocationGlsl(namespace, "resultVar"))
                    .toBe("p0_main()")
            }

            context("using entry point parameters") {
                override(shaderText) {
                    /**language=glsl*/
                        """
                            uniform float time;
                            // @param uv uv-coordinate
                            void main(vec2 uv) { gl_FragColor = vec4(uv.xy, 0., 1.); }
                        """.trimIndent()
                }

                it("identifies uniform and param input ports and excludes gl_FragCoord") {
                    expects(
                        listOf(
                            InputPort(
                                "time", ContentType.Time, GlslType.Float, "Time",
                                glslArgSite = GlslCode.GlslVar(
                                    "time", GlslType.Float, "uniform float time;", isUniform = true, lineNumber = 1)
                            ),
                            InputPort(
                                "uv", ContentType.UvCoordinate, GlslType.Vec2, "Uv",
                                glslArgSite = GlslCode.GlslParam("uv", GlslType.Vec2, isIn = true, lineNumber = 3)
                            )
                        )
                    ) { shader.inputPorts }
                }

                it("generates invocation GLSL") {
                    expect(shader.invocationGlsl(namespace, "resultVar", mapOf(
                        "uv" to "uvArg"
                    )))
                        .toBe("p0_main(uvArg)")
                }

                context("with a type annotation") {
                    override(shaderText) {
                        /**language=glsl*/
                        """
                            uniform float time;
                            void main(
                                vec2 uv // @type uv-coordinate
                            ) {
                                gl_FragColor = vec4(uv.xy, 0., 1.);
                            }
                        """.trimIndent()
                    }

                    it("identifies the param's content type properly") {
                        expects(
                            listOf(
                                InputPort(
                                    "time", ContentType.Time, GlslType.Float, "Time",
                                    glslArgSite = GlslCode.GlslVar(
                                        "time", GlslType.Float, "uniform float time;", isUniform = true, lineNumber = 1)
                                ),
                                InputPort(
                                    "uv", ContentType.UvCoordinate, GlslType.Vec2, "Uv",
                                    glslArgSite = GlslCode.GlslParam("uv", GlslType.Vec2, isIn = true, lineNumber = 2,
                                    comments = listOf(" @type uv-coordinate"))
                                )
                            )
                        ) { shader.inputPorts }
                    }
                }
            }
        }

        context("ShaderToy shaders") {
            override(shaderText) {
                /**language=glsl*/
                """
                    // This Shader's Name
                    // Other stuff.
                    
                    uniform float blueness;
                    int someGlobalVar;
                    const int someConstVar = 123;

                    float identity(float value) { return value; }

                    void mainImage( out vec4 fragColor, in vec2 fragCoord ) {
                        vec2 uv = fragCoord.xy / iResolution.xy * iTime;
                        fragColor = vec4(uv.xy / iMouse, identity(blueness), 1.);
                    }
                """.trimIndent()
            }

            describe("#inputPorts") {
                it("finds magic uniforms") {
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
                            InputPort("iTime", ContentType.Time, GlslType.Float, "Time", isImplicit = true),
                            InputPort("iMouse", ContentType.Mouse, GlslType.Vec4, "Mouse", isImplicit = true)
                        )
                    ) { shader.inputPorts.map { it.copy(glslArgSite = null) } }
                }
            }

            it("generates function declarations") {
                expect(
                    shader.toGlsl(
                        namespace, mapOf(
                            "iResolution" to "in_resolution",
                            "iMouse" to "in_mouse",
                            "iTime" to "in_time",
                            "blueness" to "aquamarinity",
                            "identity" to "p0_identity",
                            "fragCoord" to "gl_FragCoord.xy"
                        )
                    ).trim()
                )
                    .toBe(
                        """
                        #line 5
                        int p0_someGlobalVar;
                        
                        #line 6
                        const int p0_someConstVar = 123;
                        
                        #line 8
                        float p0_identity(float value) { return value; }
                        
                        #line 10
                        void p0_mainImage( out vec4 fragColor, in vec2 fragCoord ) {
                            vec2 uv = fragCoord.xy / in_resolution.xy * in_time;
                            fragColor = vec4(uv.xy / in_mouse, p0_identity(aquamarinity), 1.);
                        }
                    """.trimIndent()
                    )
            }

            it("generates invocation GLSL") {
                expect(shader.invocationGlsl(namespace, "resultVar", mapOf("fragCoord" to "gl_FragCoord.xy")))
                    .toBe("p0_mainImage(resultVar, gl_FragCoord.xy)")
            }
        }
    }
})
