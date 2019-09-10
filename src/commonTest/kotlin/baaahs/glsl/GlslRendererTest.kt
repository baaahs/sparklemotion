package baaahs.glsl

import baaahs.Color
import baaahs.IdentifiedSurface
import baaahs.SheepModel
import baaahs.geom.Vector3F
import baaahs.shaders.GlslShader
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.expect

class GlslRendererTest {
    @Ignore @Test // TODO: requires `-XstartOnFirstThread` jvmargs or jvm will crash
    fun testSimpleRendering() {
        val renderer = GlslBase.manager.createRenderer("""
uniform float time;
void main() {
    gl_FragColor = vec4(gl_FragCoord.xy, 0., 1.);
}
""".trimIndent(), GlslShader.extraAdjustables
        )

        val glslSurface = renderer.addSurface(
            IdentifiedSurface(
                SheepModel.Panel("xyz"), 3, listOf(
                    Vector3F(0f, .1f, 0f),
                    Vector3F(.2f, .3f, 0f),
                    Vector3F(.4f, .5f, 0f)
                )
            ), UvTranslatorForTest
        )
        glslSurface!!

        // TODO: this one renders nothing, why?
        renderer.draw()

        // TODO: yuck, let's not do this.
        glslSurface.uniforms.updateFrom(arrayOf(1f, 1f, 1f, 1f, 1f, 1f))

        renderer.draw()

        expect(listOf(
            Color(0f, .1f, 0f),
            Color(.2f, .3f, 0f),
            Color(.4f, .5f, 0f)
        )) { glslSurface.pixels.toList() }
    }

    object UvTranslatorForTest : UvTranslator {
        override fun forPixels(pixelLocations: List<Vector3F?>) = object : UvTranslator.SurfaceUvTranslator {
            override val pixelCount = pixelLocations.count()
            override fun getUV(pixelIndex: Int): Pair<Float, Float> = pixelLocations[pixelIndex]!!.let { it.x to it.y }
        }
    }
}