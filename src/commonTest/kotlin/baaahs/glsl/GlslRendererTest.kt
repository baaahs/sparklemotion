package baaahs.glsl

import baaahs.Color
import baaahs.IdentifiedSurface
import baaahs.TestModelSurface
import baaahs.geom.Vector3F
import baaahs.shaders.GlslShader
import kotlinx.serialization.json.json
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.expect

@Ignore // TODO: requires `-XstartOnFirstThread` jvmargs or jvm will crash
class GlslRendererTest {
    @Test
    fun testSimpleRendering() {
        val renderer = GlslBase.manager.createRenderer(
            """
            uniform float time;
            void main() {
                gl_FragColor = vec4(gl_FragCoord.xy, 0.5, 1.);
            }
            """.trimIndent(), GlslShader.extraAdjustables
        )

        val glslSurface = renderer.addSurface(threePixelPanel(), UvTranslatorForTest)!!

        // TODO: yuck, let's not do this.
        glslSurface.uniforms.updateFrom(arrayOf(1f, 1f, 1f, 1f, 1f, 1f))

        renderer.draw()

        expect(listOf(
            Color(0f, .1f, .5f),
            Color(.2f, .3f, .5f),
            Color(.4f, .5f, .5f)
        )) { glslSurface.pixels.toList() }
    }

    @Test
    fun testRenderingWithUniform() {
        val adjustables = GlslShader.extraAdjustables +
                GlslShader.AdjustableValue("blue", "Slider", GlslShader.AdjustableValue.Type.FLOAT,
                    json { }
                )

        val renderer = GlslBase.manager.createRenderer(
            """
            uniform float time;
            uniform float blue;
            
            void main() {
                gl_FragColor = vec4(gl_FragCoord.xy, blue, 1.);
            }
            """.trimIndent(), adjustables
        )

        val glslSurface = renderer.addSurface(threePixelPanel(), UvTranslatorForTest)!!

        glslSurface.uniforms.updateFrom(arrayOf(1f, 1f, 1f, 1f, 1f, 1f, .1f))
        renderer.draw()
        expect(listOf(
            Color(0f, .1f, .1f),
            Color(.2f, .3f, .1f),
            Color(.4f, .5f, .1f)
        )) { glslSurface.pixels.toList() }

        glslSurface.uniforms.updateFrom(arrayOf(1f, 1f, 1f, 1f, 1f, 1f, .2f))
        renderer.draw()
        expect(listOf(
            Color(0f, .1f, .2f),
            Color(.2f, .3f, .2f),
            Color(.4f, .5f, .2f)
        )) { glslSurface.pixels.toList() }
    }

    private fun threePixelPanel(): IdentifiedSurface {
        return IdentifiedSurface(
            TestModelSurface("xyz"), 3, listOf(
                Vector3F(0f, .1f, 0f),
                Vector3F(.2f, .3f, 0f),
                Vector3F(.4f, .5f, 0f)
            )
        )
    }

    object UvTranslatorForTest : UvTranslator {
        override fun forPixels(pixelLocations: List<Vector3F?>) = object : UvTranslator.SurfaceUvTranslator {
            override val pixelCount = pixelLocations.count()
            override fun getUV(pixelIndex: Int): Pair<Float, Float> = pixelLocations[pixelIndex]!!.let { it.x to it.y }
        }
    }
}