package baaahs.gl.render

import baaahs.Color
import baaahs.device.PixelArrayDevice
import baaahs.doRunBlocking
import baaahs.fixtures.Fixture
import baaahs.fixtures.NullTransport
import baaahs.fixtures.PixelArrayFixture
import baaahs.gadgets.Slider
import baaahs.geom.Vector3F
import baaahs.gl.*
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.patch.ContentType
import baaahs.plugin.core.datasource.ColorPickerDataSource
import baaahs.plugin.core.datasource.SliderDataSource
import baaahs.shows.FakeShowPlayer
import baaahs.testModelSurface
import kotlin.math.abs
import kotlin.random.Random
import kotlin.test.*

class RenderEngineTest {
    // assumeTrue() doesn't work in js runners; instead, bail manually.
    // TODO: Do something better.
//    @BeforeTest
//    fun verifyGlslAvailable() = assumeTrue(GlslBase.manager.available)

    private lateinit var glContext: GlContext
    private lateinit var renderEngine: ModelRenderEngine
    private lateinit var fakeShowPlayer: FakeShowPlayer

    @BeforeTest
    fun setUp() {
        if (glslAvailable()) {
            glContext = GlBase.manager.createContext()
            renderEngine = ModelRenderEngine(glContext, PixelArrayDevice,)
            fakeShowPlayer = FakeShowPlayer()
        }
    }

    @AfterTest
    fun tearDown() {
        if (glslAvailable()) {
            glContext.release()
            renderEngine.release()
        }
    }

    // TODO: Need to swap out uvShader for simpler test version to get this passing.

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
        val renderTarget = renderEngine.addFixture(fakeSurface())
        renderEngine.setRenderPlan(renderPlanFor(glslProgram, renderTarget))

        drawAndFinish()

        expectColor(
            Color(0f, .1f, .5f),
            Color(.2f, .3f, .5f),
            Color(.4f, .5f, .5f)
        ) { renderTarget.colors.toList() }
    }

    @Test
    fun testRenderingWithUniform() {
        if (!glslAvailable()) return

        val program =
            /**language=glsl*/
            """
            uniform float time;
            uniform float blue; // @@Slider
            
            void main() {
                gl_FragColor = vec4(gl_FragCoord.xy, blue, 1.);
            }
            """.trimIndent()

        val glslProgram = compileAndBind(program)
        val renderTarget = renderEngine.addFixture(fakeSurface())
        renderEngine.setRenderPlan(renderPlanFor(glslProgram, renderTarget))

        fakeShowPlayer.getGadget<Slider>("blueSlider").position = .1f
        drawAndFinish()

        expectColor(
            Color(0f, .1f, .1f),
            Color(.2f, .3f, .1f),
            Color(.4f, .5f, .1f)
        ) { renderTarget.colors.toList() }

        fakeShowPlayer.getGadget<Slider>("blueSlider").position = .2f
        drawAndFinish()

        expectColor(
            Color(0f, .1f, .2f),
            Color(.2f, .3f, .2f),
            Color(.4f, .5f, .2f)
        ) { renderTarget.colors.toList() }
    }

    @Test
    fun testRenderingWithMultipleFixtures() {
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

        val renderTarget1 = renderEngine.addFixture(fakeSurface("s1"))
        val renderTarget2 = renderEngine.addFixture(fakeSurface("s2", 2))
        val renderTarget3 = renderEngine.addFixture(fakeSurface("s3"))
        renderEngine.setRenderPlan(renderPlanFor(glslProgram, renderTarget1, renderTarget2, renderTarget3))

        drawAndFinish()

        expectColor(
            Color(0f, .1f, .5f),
            Color(.2f, .3f, .5f),
            Color(.4f, .5f, .5f)
        ) { renderTarget1.colors.toList() }

        expectColor(
            Color(0f, .1f, .5f),
            Color(.2f, .3f, .5f)
        ) { renderTarget2.colors.toList() }

        expectColor(
            Color(0f, .1f, .5f),
            Color(.2f, .3f, .5f),
            Color(.4f, .5f, .5f)
        ) { renderTarget3.colors.toList() }
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

        val renderTarget1 = renderEngine.addFixture(fakeSurface("s1"))
        val renderTarget2 = renderEngine.addFixture(fakeSurface("s2"))
        renderEngine.setRenderPlan(renderPlanFor(glslProgram, renderTarget1, renderTarget2))

        // TODO: yuck, let's not do this [first part]
//        renderTarget1.uniforms.updateFrom(arrayOf(1f, 1f, 1f, 1f, 1f, 1f, .2f))
//        renderTarget2.uniforms.updateFrom(arrayOf(1f, 1f, 1f, 1f, 1f, 1f, .3f))

        drawAndFinish()

        expectColor(
            Color(0f, .1f, .2f),
            Color(.2f, .3f, .2f),
            Color(.4f, .503f, .2f)
        ) { renderTarget1.colors.toList() }

        // Interpolation between vertex 0 and the surface's center.
        expectColor(
            Color(.6f, .6f, .3f),
            Color(.651f, .651f, .3f),
            Color(.7f, .7f, .3f)
        ) { renderTarget2.colors.toList() }
    }

    @Test
    fun mapFixturePanelsToRects_shouldWrapAsNecessary() {

        // ....
        // xxx.
        expect(listOf(
            Quad.Rect(1f, 0f, 2f, 3f)
        )) { ModelRenderEngine.mapFixtureComponentsToRects(4, 4, createSurface("A", 3)) }

        // ...x
        // xxxx
        // xx..
        expect(listOf(
            Quad.Rect(0f, 3f, 1f, 4f),
            Quad.Rect(1f, 0f, 2f, 4f),
            Quad.Rect(2f, 0f, 3f, 2f)
        )) { ModelRenderEngine.mapFixtureComponentsToRects(3, 4, createSurface("A", 7)) }
    }

    private fun fakeSurface(name: String = "xyz", pixelCount: Int = 3): Fixture {
        return PixelArrayFixture(
            testModelSurface(name),
            pixelCount,
            transport = NullTransport,
            /**
             * e.g.:
             *  Vector3F(0f, .1f, 0f),
             *  Vector3F(.2f, .3f, 0f),
             *  Vector3F(.4f, .5f, 0f)
             */
            pixelLocations = (0 until pixelCount).map { i ->
                val offset = i * .2f
                Vector3F(0f + offset, .1f + offset, 0f)
            }
        )
    }

    private fun createSurface(name: String, pixelCount: Int): Fixture {
        return PixelArrayFixture(
            testModelSurface(name), pixelCount,
            transport = NullTransport,
            pixelLocations = (0 until pixelCount)
                .map { Vector3F(Random.nextFloat(), Random.nextFloat(), Random.nextFloat()) }
        )
    }

    private fun compileAndBind(program: String): GlslProgram {
        val shader = testToolchain.import(program)
        val linkedPatch = testToolchain
            .autoWire(directXyProjection, shader)
            .acceptSuggestedLinkOptions()
            .confirm()
            .openForPreview(testToolchain, ContentType.Color)!!
        return renderEngine.compile(linkedPatch) { id, dataSource ->
            when (dataSource) {
                is ColorPickerDataSource -> {
                    fakeShowPlayer.registerGadget(id, dataSource.createGadget(), dataSource)
                }
                is SliderDataSource -> {
                    fakeShowPlayer.registerGadget(id, dataSource.createGadget(), dataSource)
                }
            }
            dataSource.open(fakeShowPlayer, id)
        }
    }

    private fun drawAndFinish() {
        doRunBlocking {
            renderEngine.draw()
            renderEngine.finish()
        }
    }

    // More forgiving color equality checking, allows each channel to be off by one.
    fun expectColor(vararg expected: Color, actualFn: () -> List<Color>) {
        val actual = actualFn()
        val nearlyEqual = expected.zip(actual) { exp, act ->
            val diff = exp - act
            (diff.redI <= 1 && diff.greenI <= 1 && diff.blueI <= 1)
        }.all { it }
        if (!nearlyEqual) {
            expect(expected.toList()) { actual }
        }
    }

    operator fun Color.minus(other: Color) =
        Color(abs(redI - other.redI), abs(greenI - other.greenI), abs(blueI - other.blueI), abs(alphaI - other.alphaI))
}