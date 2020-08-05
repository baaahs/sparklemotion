package baaahs.glshaders

import baaahs.glsl.Shaders
import baaahs.show.Shader
import baaahs.show.ShaderChannel
import baaahs.show.ShaderType
import baaahs.show.Surfaces
import baaahs.show.live.ShowOpener
import baaahs.show.mutable.MutablePatch
import baaahs.show.mutable.MutableShow
import baaahs.show.mutable.ShowBuilder
import baaahs.shows.FakeGlslContext
import baaahs.shows.FakeShowPlayer
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.expect

object PatchLayeringSpec : Spek({
    describe("Layering of patch links") {
        val autoWirer by value { AutoWirer(Plugins.safe()) }

        fun autoWire(vararg shaders: Shader): MutablePatch {
            return autoWirer.autoWire(*shaders).acceptSymbolicChannelLinks().resolve()
        }

        val uvShader = Shaders.cylindricalUvMapper.shader
        val blackShader by value {
            Shader("Black Shader", ShaderType.Color,
                "void main() {\n  gl_FragColor = vec4(0.);\n}")
        }
        val orangeShader by value {
            Shader("Orange Shader", ShaderType.Color,
                "void main() {\n  gl_FragColor = vec4(1., .5, 0., gl_FragCoord.x);\n}")
        }
        val brightnessFilter by value {
            Shader("Brightness Filter", ShaderType.Filter,
                "uniform float brightness; // @@Slider min=0 max=1 default=1\nvec4 filterImage(vec4 colorIn) {\n  return colorIn * brightness;\n}")
        }
        val saturationFilter by value {
            Shader("Saturation Filter", ShaderType.Filter,
                "vec4 filterImage(vec4 colorIn) { return colorIn; }")
        }
        val mutableShow by value { MutableShow("test show") }
        val show by value {
            val show = mutableShow.build(ShowBuilder())
            ShowOpener(show, FakeShowPlayer(FakeGlslContext())).openShow()
        }

        context("with a show, scene, and patchset patch") {
            beforeEachTest {
                mutableShow.apply {
                    addPatch(autoWire(uvShader, blackShader))

                    addScene("scene") {
                        addPatch(autoWire(brightnessFilter))

                        addPatchSet("patchset") {
                            addPatch(autoWire(orangeShader))
                        }
                    }
                }
            }

            it("merges layered patches into a single patch") {
                val portDiagrams =
                    autoWirer.merge(show, show.scenes[0], show.scenes[0].patchSets[0])
                val portDiagram = portDiagrams[Surfaces.AllSurfaces]!!
                val linkedPatch = portDiagram.resolvePatch(ShaderChannel.Main, ContentType.Color)!!
                expect(
                    /** language=glsl */
                    """
                        #ifdef GL_ES
                        precision mediump float;
                        #endif

                        // SparkleMotion-generated GLSL

                        layout(location = 0) out vec4 sm_result;

                        struct ModelInfo {
                            vec3 center;
                            vec3 extents;
                        };
                        uniform float in_brightnessSlider;
                        uniform ModelInfo in_modelInfo;
                        uniform sampler2D in_pixelCoordsTexture;

                        // Shader: Cylindrical Projection; namespace: p0
                        // Cylindrical Projection

                        vec2 p0i_result;

                        #line 12
                        const float p0_PI = 3.141592654;

                        #line 14
                        vec2 p0_project(vec3 pixelLocation) {
                            vec3 pixelOffset = pixelLocation - in_modelInfo.center;
                            vec3 normalDelta = normalize(pixelOffset);
                            float theta = atan(abs(normalDelta.z), normalDelta.x); // theta in range [-π,π]
                            if (theta < 0.0) theta += (2.0f * p0_PI);                 // theta in range [0,2π)
                            float u = theta / (2.0f * p0_PI);                         // u in range [0,1)
                            float v = (pixelOffset.y + in_modelInfo.extents.y / 2.0f) / in_modelInfo.extents.y;
                            return vec2(u, v);
                        }

                        #line 24
                        vec2 p0_mainUvFromRaster(vec2 rasterCoord) {
                            int rasterX = int(rasterCoord.x);
                            int rasterY = int(rasterCoord.y);
                            
                            vec3 pixelCoord = texelFetch(in_pixelCoordsTexture, ivec2(rasterX, rasterY), 0).xyz;
                            return p0_project(pixelCoord);
                        }

                        // Shader: Orange Shader; namespace: p1
                        // Orange Shader

                        vec4 p1_gl_FragColor;

                        #line 1
                        void p1_main() {
                          p1_gl_FragColor = vec4(1., .5, 0., p0i_result.x);
                        }

                        // Shader: Brightness Filter; namespace: p2
                        // Brightness Filter

                        vec4 p2i_result;
                        
                        #line 1
                         vec4 p2_filterImage(vec4 colorIn) {
                          return colorIn * in_brightnessSlider;
                        }
                        
                        
                        #line -1
                        void main() {
                          p0i_result = p0_mainUvFromRaster(gl_FragCoord.xy); // Cylindrical Projection
                          p1_main(); // Orange Shader
                          p2i_result = p2_filterImage(p1_gl_FragColor); // Brightness Filter
                          sm_result = p2i_result;
                        }
                        
                    """.trimIndent()
                ) { linkedPatch.toGlsl() }
            }
        }
    }
})
