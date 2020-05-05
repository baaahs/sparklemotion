package baaahs.glshaders

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.expect

object AutoWirerSpec : Spek({

    describe("AutoWirer") {
        describe(".autoWire") {
            val shaderText by value {
                /**language=glsl*/
                """
                // This Shader's Name
                // Other stuff.
                
                uniform float time;
                uniform vec2  resolution;
                uniform float blueness;
                int someGlobalVar;
                const int someConstVar = 123;
                
                int anotherFunc(int i) { return i; }
                
                void main( void ) {
                    vec2 uv = gl_FragCoord.xy / resolution.xy;
                    someGlobalVar = anotherFunc(someConstVar);
                    gl_FragColor = vec4(uv.xy, blueness, 1.);
                }
                """.trimIndent()
            }
            val shader by value { GlslAnalyzer().asShader(shaderText) as ColorShader }
            val shaders by value { mapOf("color" to shader) }
            val patch by value { AutoWirer().autoWire(shaders) }

            it("creates a reasonable guess patch") {
                expect(
                    listOf(
                        GlslProgram.Time
                                linkTo Patch.ShaderPortRef("color", "time"),
                        GlslProgram.Resolution
                                linkTo Patch.ShaderPortRef("color", "resolution"),
                        GlslProgram.InputPortRef("float", "blueness", "baaahs.Core:Slider")
                                linkTo Patch.ShaderPortRef("color", "blueness"),
                        GlslProgram.GlFragCoord
                                linkTo Patch.ShaderPortRef("color", "gl_FragCoord")
                    )
                ) { patch.links }
            }

            context("with a UV projection shader") {
                val uvShader by value { GlslAnalyzer().asShader(
                    /**language=glsl*/
                    """
                        uniform sampler2D sm_uvCoordsTexture;
                        
                        vec2 mainUvFromRaster(vec2 rasterCoord) {
                            int rasterX = int(rasterCoord.x);
                            int rasterY = int(rasterCoord.y);
                            
                            vec2 uvCoord = vec2(
                                texelFetch(sm_uvCoordsTexture, ivec2(rasterX * 2, rasterY), 0).r,    // u
                                texelFetch(sm_uvCoordsTexture, ivec2(rasterX * 2 + 1, rasterY), 0).r // v
                            );
                            return uvCoord;
                        }
                        """.trimIndent()) }

                override(shaders) {
                    mapOf("color" to shader, "uv" to uvShader)
                }

                it("creates a reasonable guess patch") {
                    expects(
                        listOf(
                            GlslProgram.Time
                                    linkTo Patch.ShaderPortRef("color", "time"),
                            GlslProgram.Resolution
                                    linkTo Patch.ShaderPortRef("color", "resolution"),
                            GlslProgram.InputPortRef("float", "blueness", "baaahs.Core:Slider")
                                    linkTo Patch.ShaderPortRef("color", "blueness"),
                            Patch.ShaderOut("uv")
                                    linkTo Patch.ShaderPortRef("color", "gl_FragCoord"),
                            Patch.UniformPortRef("sampler2D", "sm_uvCoordsTexture", "baaahs.Core:uvCoords")
                                    linkTo Patch.ShaderPortRef("uv", "sm_uvCoordsTexture")
                        )
                    ) { patch.links }
                }
            }
        }
    }
})