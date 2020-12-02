package baaahs.gl.render

import baaahs.Color
import baaahs.TestModel
import baaahs.TestModelSurface
import baaahs.fixtures.ColorResultType
import baaahs.fixtures.Fixture
import baaahs.fixtures.NullTransport
import baaahs.fixtures.PixelArrayDevice
import baaahs.gadgets.Slider
import baaahs.geom.Vector3F
import baaahs.gl.GlBase
import baaahs.gl.GlContext
import baaahs.gl.glsl.GlslAnalyzer
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.patch.AutoWirer
import baaahs.gl.renderPlanFor
import baaahs.gl.testPlugins
import baaahs.plugin.CorePlugin
import baaahs.show.Shader
import baaahs.show.ShaderType
import baaahs.shows.FakeShowPlayer
import kotlin.math.abs
import kotlin.random.Random
import kotlin.test.*

class RenderEngineTest {
    // assumeTrue() doesn't work in js runners; instead, bail manually.
    // TODO: Do something better.
//    @BeforeTest
//    fun verifyGlslAvailable() = assumeTrue(GlslBase.manager.available)

    fun glslAvailable(): Boolean {
        val available = GlBase.manager.available
        if (!available) {
            println("WARNING: OpenGL not available, skipping test!")
        }
        return available
    }

    private lateinit var glContext: GlContext
    private lateinit var renderEngine: ModelRenderEngine
    private lateinit var fakeShowPlayer: FakeShowPlayer

    @BeforeTest
    fun setUp() {
        if (glslAvailable()) {
            glContext = GlBase.manager.createContext()
            renderEngine = ModelRenderEngine(glContext, TestModel, PixelArrayDevice)
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

        renderEngine.draw()

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

        fakeShowPlayer.getGadget<Slider>("blueSlider").value = .1f
        renderEngine.draw()

        expectColor(
            Color(0f, .1f, .1f),
            Color(.2f, .3f, .1f),
            Color(.4f, .5f, .1f)
        ) { renderTarget.colors.toList() }

        fakeShowPlayer.getGadget<Slider>("blueSlider").value = .2f
        renderEngine.draw()

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

        renderEngine.draw()

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

        renderEngine.draw()

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
        )) { ModelRenderEngine.mapFixturePixelsToRects(4, 4, createSurface("A", 3)) }

        // ...x
        // xxxx
        // xx..
        expect(listOf(
            Quad.Rect(0f, 3f, 1f, 4f),
            Quad.Rect(1f, 0f, 2f, 4f),
            Quad.Rect(2f, 0f, 3f, 2f)
        )) { ModelRenderEngine.mapFixturePixelsToRects(3, 4, createSurface("A", 7)) }
    }

    private fun fakeSurface(name: String = "xyz", pixelCount: Int = 3): Fixture {
        return Fixture(
            TestModelSurface(name),
            pixelCount,
            /**
             * e.g.:
             *  Vector3F(0f, .1f, 0f),
             *  Vector3F(.2f, .3f, 0f),
             *  Vector3F(.4f, .5f, 0f)
             */
            (0 until pixelCount).map { i ->
                val offset = i * .2f
                Vector3F(0f + offset, .1f + offset, 0f)
            },
            PixelArrayDevice,
            transport = NullTransport
        )
    }

    private fun createSurface(name: String, pixelCount: Int): Fixture {
        return Fixture(
            TestModelSurface(name), pixelCount,
            (0 until pixelCount).map { Vector3F(Random.nextFloat(), Random.nextFloat(), Random.nextFloat()) },
            PixelArrayDevice,
            transport = NullTransport
        )
    }

    private fun compileAndBind(program: String): GlslProgram {
        val autoWirer = AutoWirer(testPlugins())
        val shader = GlslAnalyzer(testPlugins()).import(program)
        val linkedPatch = autoWirer
            .autoWire(directXyProjection, shader)
            .acceptSuggestedLinkOptions()
            .confirm()
            .openForPreview(autoWirer)!!
        return renderEngine.compile(linkedPatch) { id, dataSource ->
            if (dataSource is CorePlugin.GadgetDataSource<*>) {
                fakeShowPlayer.registerGadget(id, dataSource.createGadget(), dataSource)
            }
            dataSource.createFeed(fakeShowPlayer, id)
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

private val directXyProjection = Shader("Direct XY Projection", ShaderType.Projection,
    /**language=glsl*/
    """
        // Direct XY Projection
        // !SparkleMotion:internal

        uniform sampler2D pixelCoordsTexture;

        struct ModelInfo {
            vec3 center;
            vec3 extents;
        };
        uniform ModelInfo modelInfo;

        vec2 project(vec3 pixelLocation) {
            return vec2(pixelLocation.x, pixelLocation.y);
        }

        vec2 mainProjection(vec2 rasterCoord) {
            int rasterX = int(rasterCoord.x);
            int rasterY = int(rasterCoord.y);
            
            vec3 pixelCoord = texelFetch(pixelCoordsTexture, ivec2(rasterX, rasterY), 0).xyz;
            return project(pixelCoord);
        }
    """.trimIndent()
)

val RenderTarget.colors: ColorResultType.ColorResultView
    get() = PixelArrayDevice.getColorResults(this.resultViews)