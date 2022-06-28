package baaahs.plugin.webcam

import baaahs.describe
import baaahs.gl.RootToolchain
import baaahs.gl.autoWire
import baaahs.gl.openShader
import baaahs.gl.patch.AutoWirer
import baaahs.gl.patch.ContentType
import baaahs.gl.shader.type.PaintShader
import baaahs.plugin.ClientPlugins
import baaahs.plugin.Plugins
import baaahs.plugin.core.CorePlugin
import baaahs.show.Shader
import baaahs.toEqual
import ch.tutteli.atrium.api.verbs.expect
import com.danielgergely.kgl.TextureResource
import org.spekframework.spek2.Spek

object VideoInPluginSpec : Spek({
    describe<VideoInPlugin> {
        val videoProvider by value {
            object : VideoProvider {
                override fun getTextureResource(): TextureResource = TODO("not implemented")
            }
        }
        val plugins by value {
            ClientPlugins(
                listOf(CorePlugin.openSafe(Plugins.dummyContext)) + VideoInPlugin(videoProvider),
                Plugins.dummyContext
            )
        }
        val autoWirer by value { AutoWirer(plugins) }
        val toolchain by value { RootToolchain(plugins, autoWirer = autoWirer) }
        val shader by value {
            /**language=glsl*/
            """
                // @param uv uv-coordinate
                // @@baaahs.VideoIn:VideoIn
                vec4 videoIn(vec2 uv);

                // @return color
                // @param uvIn uv-coordinate
                vec4 main(vec2 uvIn) {
                    return videoIn(vec2(uvIn.x, 1. - uvIn.y));
                }
            """.trimIndent()

        }
        val openShader by value { toolchain.openShader(Shader("video", shader)) }
        val glsl by value {
            toolchain.autoWire(openShader).acceptSuggestedLinkOptions().confirm()
                .openForPreview(toolchain, ContentType.Color)!!
                .toGlsl().trim()
        }

        it("should not appear to be a FilterShader") {
            expect(openShader.shaderType).toEqual(PaintShader)
        }

        it("should generate sensible GLSL") {
            expect(glsl).toEqual(
                """
                    #ifdef GL_ES
                    precision mediump float;
                    #endif

                    // SparkleMotion-generated GLSL

                    layout(location = 0) out vec4 sm_result;

                    // Data source: Video In
                    uniform sampler2D ds_videoIn_texture;

                    // Shader: video; namespace: p0
                    // video

                    vec4 p0_videoi_result = vec4(0., 0., 0., 1.);

                    #line 3 0
                    vec4 p0_video_videoIn(vec2 uv) {
                        return texture(ds_videoIn_texture, uv);
                    }

                    #line 7 0
                    vec4 p0_video_main(vec2 uvIn) {
                        return p0_video_videoIn(vec2(uvIn.x, 1. - uvIn.y));
                    }


                    #line 10001
                    void main() {
                        // Invoke video
                        p0_videoi_result = p0_video_main(vec2(0.));

                        sm_result = p0_videoi_result;
                    }
                """.trimIndent()
            )
        }

//        context("shader preview") {
//            val builder by value {
//                PreviewShaderBuilder(
//                    openShader, toolchain, fakeModel(), CoroutineScope(ImmediateDispatcher)
//                )
//            }
//
//            it("generates a good preview") {
//                builder.linkedPatch!!.rootNode
//                val x = builder
//                println("builder = $x")
//            }
//        }
    }
})