package baaahs.gl.render

import baaahs.Color
import baaahs.device.MovingHeadDevice
import baaahs.device.PixelArrayDevice
import baaahs.dmx.Shenzarpy
import baaahs.doRunBlocking
import baaahs.fixtures.*
import baaahs.gl.GlBase
import baaahs.gl.autoWire
import baaahs.gl.patch.ContentType
import baaahs.gl.patch.LinkedProgram
import baaahs.gl.testToolchain
import baaahs.model.MovingHead
import baaahs.show.mutable.MutablePatchSet
import baaahs.shows.FakeShowPlayer
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import kotlin.math.abs
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.expect

@Tags(Tag("glsl"))
class RenderManagerTest {
    // assumeTrue() doesn't work in js runners; instead, bail manually.
    // TODO: Do something better.
//    @BeforeTest
//    fun verifyGlslAvailable() = assumeTrue(GlslBase.manager.available)


    private lateinit var renderManager: RenderManager
    private lateinit var fakeShowPlayer: FakeShowPlayer

    @BeforeTest
    fun setUp() {
        if (glslAvailable()) {
            val context = GlBase.manager.createContext(true)
            renderManager = RenderManager(context)
            fakeShowPlayer = FakeShowPlayer()
        } else error("GLSL not available!")
    }

    @AfterTest
    fun tearDown() {
        if (glslAvailable()) {
            renderManager.release()
        }
    }

    @Test
    fun testRenderingMultipleFixtureTypes() {
        if (!glslAvailable()) return

        val program1 =
            /**language=glsl*/
            "void main() { gl_FragColor = vec4(0.5, 0.25, 0.75, 1.); }"

        val program2 =
            /**language=glsl*/
            """
                struct MovingHeadParams {
                    float pan;
                    float tilt;
                    float colorWheel;
                    float dimmer;
                };
                
                // @param params moving-head-params
                void main(out MovingHeadParams params) {
                    params.pan = .25;
                    params.tilt = .5;
                    params.colorWheel = .75;
                    params.dimmer = .8;
                }
            """.trimIndent()

        val renderTarget1 = renderManager.addFixture(pixelArrayFixture(null, 2, transport = NullTransport))
        val linkedProgram1 = resolve(program1, ContentType.Color)
        val glslProgram1 = renderTarget1.renderEngine.compile(linkedProgram1) { _, _ -> error("none") }
            .bind()

        val movingHead = MovingHead("mover", adapter = Shenzarpy, baseDmxChannel = 1)
        val renderTarget2 = renderManager.addFixture(
            movingHeadFixture(
                movingHead,
                2,
                transport = NullTransport,
                adapter = Shenzarpy
            )
        )
        val linkedProgram2 = resolve(program2, MovingHeadDevice.resultContentType)
        val glslProgram2 = renderTarget2.renderEngine.compile(linkedProgram2) { _, _ -> error("none") }
            .bind()

        renderManager.setRenderPlan(
            RenderPlan(
                mapOf(
                    PixelArrayDevice to
                            FixtureTypeRenderPlan(listOf(ProgramRenderPlan(glslProgram1, listOf(renderTarget1)))),
                    MovingHeadDevice to
                            FixtureTypeRenderPlan(listOf(ProgramRenderPlan(glslProgram2, listOf(renderTarget2)))),
                )
            )
        )

        drawAndFinish()

        expectColor(
            Color.from(0.5f, 0.25f, 0.75f),
            Color.from(0.5f, 0.25f, 0.75f),
        ) { renderTarget1.colors.toList() }

        with (renderTarget2.movingHeadParams.movingHeadParams) {
            expect(pan) { .25f }
            expect(tilt) { .5f }
            expect(colorWheel) { .75f }
            expect(dimmer) { .8f }
        }
    }


    private fun resolve(program: String, contentType: ContentType): LinkedProgram {
        val shader = testToolchain.import(program)
        return testToolchain
            .autoWire(shader)
            .acceptSuggestedLinkOptions()
            .confirm().let { MutablePatchSet(it) }
            .openForPreview(testToolchain, contentType)
            ?: error("Found no patch generating ${contentType.description}")
    }

    private fun drawAndFinish() {
        doRunBlocking {
            renderManager.draw()
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