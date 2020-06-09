package baaahs.glsl

import baaahs.*
import baaahs.gadgets.Slider
import baaahs.geom.Vector3F
import baaahs.glshaders.AutoWirer
import baaahs.glshaders.CorePlugin
import baaahs.glshaders.GlslProgram
import baaahs.glshaders.Plugins
import baaahs.io.ByteArrayWriter
import baaahs.shows.FakeShowResources
import kotlin.math.abs
import kotlin.random.Random
import kotlin.test.*

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

    private lateinit var glslContext: GlslContext
    private lateinit var glslRenderer: GlslRenderer
    private lateinit var fakeShowResources: FakeShowResources

    @BeforeTest
    fun setUp() {
        if (glslAvailable()) {
            glslContext = GlslBase.manager.createContext()
            glslRenderer = GlslRenderer(glslContext, UvTranslatorForTest)
            fakeShowResources = FakeShowResources(glslContext)
        }
    }

    @AfterTest
    fun tearDown() {
        if (glslAvailable()) {
            glslContext.release()
            glslRenderer.release()
        }
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

        val glslProgram = compileAndBind(program)
        val renderSurface = glslRenderer.addSurface(surfaceWithThreePixels()).apply { this.program = glslProgram }

        glslRenderer.draw()

        expectColor(listOf(
            Color(0f, .1f, .5f),
            Color(.2f, .3f, .5f),
            Color(.4f, .5f, .5f)
        )) { renderSurface.pixels.toList() }
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

        val glslProgram = compileAndBind(program)
        val renderSurface = glslRenderer.addSurface(surfaceWithThreePixels()).apply { this.program = glslProgram }

        fakeShowResources.getGadget<Slider>("glsl_in_blue").value = .1f
        fakeShowResources.drawFrame()

        expectColor(listOf(
            Color(0f, .1f, .1f),
            Color(.2f, .3f, .1f),
            Color(.4f, .5f, .1f)
        )) { renderSurface.pixels.toList() }

        fakeShowResources.getGadget<Slider>("glsl_in_blue").value = .2f
        fakeShowResources.drawFrame()

        expectColor(listOf(
            Color(0f, .1f, .2f),
            Color(.2f, .3f, .2f),
            Color(.4f, .5f, .2f)
        )) { renderSurface.pixels.toList() }
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

        val glslProgram = compileAndBind(program)

        val renderSurface1 = glslRenderer.addSurface(surfaceWithThreePixels()).apply { this.program = glslProgram }
        val renderSurface2 = glslRenderer.addSurface(identifiedSurfaceWithThreeUnmappedPixels()).apply { this.program = glslProgram }
        val renderSurface3 = glslRenderer.addSurface(anonymousSurfaceWithThreeUnmappedPixels()).apply { this.program = glslProgram }

        glslRenderer.draw()

        expectColor(listOf(
            Color(0f, .1f, .5f),
            Color(.2f, .3f, .5f),
            Color(.4f, .5f, .5f)
        )) { renderSurface1.pixels.toList() }

        // Interpolation between vertex 0 and the surface's center.
        expectColor(listOf(
            Color(.6f, .6f, .5f),
            Color(.651f, .651f, .5f),
            Color(.7f, .7f, .5f)
        )) { renderSurface2.pixels.toList() }

        // TODO: this is wrong (and flaky); it depends on LinearModelSpaceUvTranslator picking a random
        //       x,y,x coord in [0..100], which is usually > 1.
//        expect(listOf(
//            Color(1f, 1f, .5f),
//            Color(1f, 1f, .5f),
//            Color(1f, 1f, .5f)
//        )) { renderSurface3.pixels.toList() }
    }

    @Ignore @Test // TODO: Per-surface uniform control TBD
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

        val glslProgram = compileAndBind(program)

        val renderSurface1 = glslRenderer.addSurface(surfaceWithThreePixels()).apply { this.program = glslProgram }
        val renderSurface2 = glslRenderer.addSurface(identifiedSurfaceWithThreeUnmappedPixels()).apply { this.program = glslProgram }

        // TODO: yuck, let's not do this [first part]
//        renderSurface1.uniforms.updateFrom(arrayOf(1f, 1f, 1f, 1f, 1f, 1f, .2f))
//        renderSurface2.uniforms.updateFrom(arrayOf(1f, 1f, 1f, 1f, 1f, 1f, .3f))

        glslRenderer.draw()

        expectColor(listOf(
            Color(0f, .1f, .2f),
            Color(.2f, .3f, .2f),
            Color(.4f, .503f, .2f)
        )) { renderSurface1.pixels.toList() }

        // Interpolation between vertex 0 and the surface's center.
        expectColor(listOf(
            Color(.6f, .6f, .3f),
            Color(.651f, .651f, .3f),
            Color(.7f, .7f, .3f)
        )) { renderSurface2.pixels.toList() }
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

    private fun compileAndBind(program: String): GlslProgram {
        val corePlugin = CorePlugin()
        val plugins = Plugins(mapOf(corePlugin.packageName to corePlugin))

        return AutoWirer(Plugins.safe()).autoWire(program).compile(glslContext) { uniformPortRef ->
            plugins.findDataSource(uniformPortRef)?.create(fakeShowResources)
        }
    }

    // More forgiving color equality checking, allows each channel to be off by one.
    fun expectColor(expected: List<Color>, actualFn: () -> List<Color>) {
        val actual = actualFn()
        val nearlyEqual = expected.zip(actual) { exp, act ->
            val diff = exp - act
            (diff.redI <= 1 && diff.greenI <= 1 && diff.blueI <= 1)
        }.all { it }
        if (!nearlyEqual) {
            kotlin.test.expect(expected, actualFn)
        }
    }

    operator fun Color.minus(other: Color) =
        Color(abs(redI - other.redI), abs(greenI - other.greenI), abs(blueI - other.blueI), abs(alphaI - other.alphaI))

    object UvTranslatorForTest : UvTranslator(Id.PANEL_SPACE_UV_TRANSLATOR) {
        override fun serializeConfig(writer: ByteArrayWriter) = TODO("not implemented")

        override fun forPixels(pixelLocations: List<Vector3F?>) = object : SurfaceUvTranslator {
            override val pixelCount = pixelLocations.count()
            override fun getUV(pixelIndex: Int): Pair<Float, Float> = pixelLocations[pixelIndex]!!.let { it.x to it.y }
        }
    }
}