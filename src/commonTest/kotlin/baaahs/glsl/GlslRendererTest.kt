package baaahs.glsl

import baaahs.*
import baaahs.geom.Vector3F
import baaahs.glshaders.AutoWirer
import baaahs.io.ByteArrayWriter
import baaahs.shaders.GlslShader
import kotlin.math.abs
import kotlin.random.Random
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.expect

class GlslRendererTest {
    // assumeTrue() doesn't work in js runners; instead, bail manually.
    // TODO: Do something better.
//    @BeforeTest
//    fun verifyGlslAvailable() = assumeTrue(GlslBase.manager.available)

    fun glslAvailable(): Boolean {
        val available = GlslBase.manager.available
        if (!available) {
            println("WARNING: OpenGL not available, skipping test!")
        }
        return available
    }

    @BeforeTest
    fun resetPlugins() {
        GlslBase.plugins.clear()
    }

    @Test
    fun testSimpleRendering() {
        if (!glslAvailable()) return

        val program =
            /**language=glsl*/
            """
            uniform float time;
            void main() {
                gl_FragColor = vec4(gl_FragCoord.xy, 0.5, 1.);
            }
            """.trimIndent()

        val glslProgram = AutoWirer().autoWire(program).compile()
        val renderer = GlslRenderer(GlslShader.renderContext, glslProgram, UvTranslatorForTest)

        val glslSurface = renderer.addSurface(surfaceWithThreePixels())!!

        // TODO: yuck, let's not do this.
        glslSurface.uniforms.updateFrom(arrayOf(1f, 1f, 1f, 1f, 1f, 1f))

        renderer.draw()

        expectColor(listOf(
            Color(0f, .1f, .5f),
            Color(.2f, .3f, .5f),
            Color(.4f, .5f, .5f)
        )) { glslSurface.pixels.toList() }
    }

    @Test
    fun testRenderingWithUniform() {
        if (!glslAvailable()) return

        val program =
            /**language=glsl*/
            """
            uniform float time;
            
            // SPARKLEMOTION GADGET: Slider {}
            uniform float blue;
            
            void main() {
                gl_FragColor = vec4(gl_FragCoord.xy, blue, 1.);
            }
            """.trimIndent()

        val glslProgram = AutoWirer().autoWire(program).compile()
        val renderer = GlslRenderer(GlslShader.renderContext, glslProgram, UvTranslatorForTest)

        val glslSurface = renderer.addSurface(surfaceWithThreePixels())!!

        glslSurface.uniforms.updateFrom(arrayOf(1f, 1f, 1f, 1f, 1f, 1f, .1f))
        renderer.draw()
        expectColor(listOf(
            Color(0f, .1f, .1f),
            Color(.2f, .3f, .1f),
            Color(.4f, .5f, .1f)
        )) { glslSurface.pixels.toList() }

        glslSurface.uniforms.updateFrom(arrayOf(1f, 1f, 1f, 1f, 1f, 1f, .2f))
        renderer.draw()
        expectColor(listOf(
            Color(0f, .1f, .2f),
            Color(.2f, .3f, .2f),
            Color(.4f, .5f, .2f)
        )) { glslSurface.pixels.toList() }
    }

    @Test
    fun testRenderingWithUnmappedPixels() {
        if (!glslAvailable()) return

        val program =
            /**language=glsl*/
            """
            uniform float time;
            void main() {
                gl_FragColor = vec4(gl_FragCoord.xy, 0.5, 1.);
            }
            """.trimIndent()

        val glslProgram = AutoWirer().autoWire(program).compile()
        val renderer = GlslRenderer(GlslShader.renderContext, glslProgram, UvTranslatorForTest)

        val glslSurface1 = renderer.addSurface(surfaceWithThreePixels())!!
        val glslSurface2 = renderer.addSurface(identifiedSurfaceWithThreeUnmappedPixels())!!
        val glslSurface3 = renderer.addSurface(anonymousSurfaceWithThreeUnmappedPixels())!!

        // TODO: yuck, let's not do this.
        listOf(glslSurface1, glslSurface2, glslSurface3).forEach {
            it.uniforms.updateFrom(arrayOf(1f, 1f, 1f, 1f, 1f, 1f))
        }

        renderer.draw()

        expectColor(listOf(
            Color(0f, .1f, .5f),
            Color(.2f, .3f, .5f),
            Color(.4f, .5f, .5f)
        )) { glslSurface1.pixels.toList() }

        // Interpolation between vertex 0 and the surface's center.
        expectColor(listOf(
            Color(.6f, .6f, .5f),
            Color(.651f, .651f, .5f),
            Color(.7f, .7f, .5f)
        )) { glslSurface2.pixels.toList() }

        // TODO: this is wrong (and flaky); it depends on LinearModelSpaceUvTranslator picking a random
        //       x,y,x coord in [0..100], which is usually > 1.
//        expect(listOf(
//            Color(1f, 1f, .5f),
//            Color(1f, 1f, .5f),
//            Color(1f, 1f, .5f)
//        )) { glslSurface3.pixels.toList() }
    }

    // More forgiving color equality checking, allows each channel to be off by one.
    fun expectColor(expected: List<Color>, actualFn: () -> List<Color>) {
        val actual = actualFn()
        val nearlyEqual = expected.zip(actual) { exp, act ->
            val diff = exp - act
            (diff.redI <= 1 || diff.greenI <= 1 || diff.blueI <= 1)
        }.all { it }
        if (!nearlyEqual) {
            kotlin.test.expect(expected, actualFn)
        }
    }

    operator fun Color.minus(other: Color) =
        Color(abs(redI - other.redI), abs(greenI - other.greenI), abs(blueI - other.blueI), abs(alphaI - other.alphaI))

    @Test
    fun testRenderingSurfacesWithDifferentBufferValues() {
        if (!glslAvailable()) return

        val program =
            /**language=glsl*/
            """
            // SPARKLEMOTION GADGET: Slider {name: "Blue", initialValue: 1.0, minValue: 0.0, maxValue: 1.0}
            uniform float blue;

            uniform float time;
            void main() {
                gl_FragColor = vec4(gl_FragCoord.xy, blue, 1.);
            }
            """.trimIndent()

        val glslProgram = AutoWirer().autoWire(program).compile()
        val renderer = GlslRenderer(GlslShader.renderContext, glslProgram, UvTranslatorForTest)

        val glslSurface1 = renderer.addSurface(surfaceWithThreePixels())!!
        val glslSurface2 = renderer.addSurface(identifiedSurfaceWithThreeUnmappedPixels())!!

        // TODO: yuck, let's not do this [first part]
        glslSurface1.uniforms.updateFrom(arrayOf(1f, 1f, 1f, 1f, 1f, 1f, .2f))
        glslSurface2.uniforms.updateFrom(arrayOf(1f, 1f, 1f, 1f, 1f, 1f, .3f))

        renderer.draw()

        expectColor(listOf(
            Color(0f, .1f, .2f),
            Color(.2f, .3f, .2f),
            Color(.4f, .503f, .2f)
        )) { glslSurface1.pixels.toList() }

        // Interpolation between vertex 0 and the surface's center.
        expectColor(listOf(
            Color(.6f, .6f, .3f),
            Color(.651f, .651f, .3f),
            Color(.7f, .7f, .3f)
        )) { glslSurface2.pixels.toList() }
    }

    @Test
    fun mapSurfacesToRects_shouldWrapAsNecessary() {

        // ....
        // xxx.
        expect(listOf(
            Quad.Rect(1f, 0f, 2f, 3f)
        )) { GlslRenderer.mapSurfaceToRects(4, 4, createSurface("A", 3)) }

        // ...x
        // xxxx
        // xx..
        expect(listOf(
            Quad.Rect(0f, 3f, 1f, 4f),
            Quad.Rect(1f, 0f, 2f, 4f),
            Quad.Rect(2f, 0f, 3f, 2f)
        )) { GlslRenderer.mapSurfaceToRects(3, 4, createSurface("A", 7)) }
    }

    private fun surfaceWithThreePixels(): IdentifiedSurface {
        return IdentifiedSurface(
            TestModelSurface("xyz"), 3, listOf(
                Vector3F(0f, .1f, 0f),
                Vector3F(.2f, .3f, 0f),
                Vector3F(.4f, .5f, 0f)
            )
        )
    }

    private fun identifiedSurfaceWithThreeUnmappedPixels(): IdentifiedSurface {
        return IdentifiedSurface(
            TestModelSurface("zyx", vertices = listOf(
                Vector3F(.6f, .6f, 0f),
                Vector3F(.8f, .8f, 0f),
                Vector3F(.6f, .8f, 0f),
                Vector3F(.8f, .6f, 0f)
            )), 3, null)
    }

    private fun anonymousSurfaceWithThreeUnmappedPixels(): AnonymousSurface {
        return AnonymousSurface(BrainId("some-brain"), 3)
    }

    private fun createSurface(name: String, pixelCount: Int): Surface {
        return IdentifiedSurface(
            TestModelSurface(name), pixelCount,
            (0 until pixelCount).map { Vector3F(Random.nextFloat(), Random.nextFloat(), Random.nextFloat()) }
        )
    }

    object UvTranslatorForTest : UvTranslator(Id.PANEL_SPACE_UV_TRANSLATOR) {
        override fun serializeConfig(writer: ByteArrayWriter) = TODO("not implemented")

        override fun forPixels(pixelLocations: List<Vector3F?>) = object : SurfaceUvTranslator {
            override val pixelCount = pixelLocations.count()
            override fun getUV(pixelIndex: Int): Pair<Float, Float> = pixelLocations[pixelIndex]!!.let { it.x to it.y }
        }
    }
}