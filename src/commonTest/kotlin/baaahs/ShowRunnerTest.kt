package baaahs

import baaahs.ShowRunner.SurfaceReceiver
import baaahs.gadgets.Slider
import baaahs.glsl.GlslRenderer
import baaahs.glsl.GlslRendererTest
import baaahs.glsl.RenderSurface
import baaahs.model.Model
import baaahs.models.SheepModel
import baaahs.net.TestNetwork
import baaahs.show.SampleData
import baaahs.shows.FakeGlslContext
import baaahs.sim.FakeDmxUniverse
import baaahs.sim.FakeFs
import kotlinx.coroutines.InternalCoroutinesApi
import kotlin.test.BeforeTest
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.expect

@InternalCoroutinesApi
class ShowRunnerTest {
    private val network = TestNetwork(0)
    private val serverNetwork = network.link("test")
    private val server = PubSub.listen(serverNetwork.startHttpServer(1234)).apply { install(gadgetModule) }

    private val movingHeadManager = MovingHeadManager(FakeFs(), server, emptyList())
    private lateinit var showRunner: ShowRunner

    private lateinit var renderSurfaces: Map<Surface, RenderSurface>
    private val surface1Messages = mutableListOf<Pixels>()
    private val surface1Receiver =
        FakeSurfaceReceiver(IdentifiedSurface(SheepModel.Panel("surface 1"), 1)) { buffer -> surface1Messages.add(buffer) }
    private val surface2Messages = mutableListOf<Pixels>()
    private val surface2Receiver =
        FakeSurfaceReceiver(IdentifiedSurface(SheepModel.Panel("surface 2"), 1)) { buffer -> surface2Messages.add(buffer) }
    private lateinit var fakeGlslContext: FakeGlslContext
    private lateinit var dmxUniverse: FakeDmxUniverse
    private val dmxEvents = mutableListOf<String>()
    private val sheepModel = SheepModel()
    private lateinit var showManager: ShowManager

    @BeforeTest
    fun setUp() {
        fakeGlslContext = FakeGlslContext()
        dmxUniverse = FakeDmxUniverse()
        dmxUniverse.reader(1, 1) { dmxEvents.add("dmx frame sent") }
        val show = SampleData.sampleShow
        showManager = ShowManager(show, server, fakeGlslContext)
        showRunner = ShowRunner(
            sheepModel,
            show.scenes[0].patchSets[0],
            show,
            showManager,
            StubBeatSource(),
            dmxUniverse,
            movingHeadManager,
            FakeClock(),
            GlslRenderer(fakeGlslContext, GlslRendererTest.UvTranslatorForTest),
            PubSub.Server(serverNetwork.startHttpServer(0))
        )
        renderSurfaces = showRunner.renderSurfaces
        surface1Messages.clear()
        surface2Messages.clear()
        dmxEvents.clear()
    }

    @Test @Ignore // TODO
    fun whenNoKnownSurfaces_shouldStillCreateShow() {
        showRunner.nextFrame()
        expect(1) { renderSurfaces.size }
        expect(0) { surface1Messages.size }
    }

    @Test
    fun shouldRenderShow() {
        showRunner.surfacesChanged(listOf(surface1Receiver, surface2Receiver), emptyList())
        showRunner.nextFrame()
        expect(2) { renderSurfaces.size }
        expect(1) { surface1Messages.size }
        expect(1) { surface2Messages.size }

        showRunner.nextFrame()
        expect(2) { renderSurfaces.size }
        expect(2) { surface1Messages.size }
        expect(2) { surface2Messages.size }

        val buffer1 = surface1Messages[0]
//        expect(testShow1.fakeShader) { buffer1.shader }
//        expect(Color.WHITE) { buffer1.color }

        val buffer2 = surface1Messages[0]
//        expect(testShow1.fakeShader) { buffer2.shader }
//        expect(Color.WHITE) { (buffer2).color }
    }

    @Test
    fun inProcrastinationMode_whenSurfacesAreAddedOrRemoved_shouldUpdateShowAfterNextFrame() {
        showRunner.nextFrame(false) // No surfaces so no show created, nothing rendered.
        expect(0) { renderSurfaces.size }

        showRunner.surfacesChanged(listOf(surface1Receiver), emptyList())
        showRunner.nextFrame(false) // Create a new show with one surface but don't render to it yet.
        expect(1) { renderSurfaces.size }
        expect(0) { surface1Messages.size }

        showRunner.nextFrame(false) // Render and include that surface.
        expect(1) { renderSurfaces.size }
        expect(1) { surface1Messages.size }

        showRunner.surfacesChanged(listOf(surface2Receiver), emptyList())
        showRunner.nextFrame(false) // Add another surface, but still only render to the first.
        expect(2) { renderSurfaces.size }
        expect(2) { surface1Messages.size }
        expect(0) { surface2Messages.size }

        showRunner.nextFrame(false) // Render to both.
        expect(2) { renderSurfaces.size }
        expect(3) { surface1Messages.size }
        expect(1) { surface2Messages.size }

        showRunner.surfacesChanged(emptyList(), listOf(surface1Receiver))
        showRunner.nextFrame(false) // Remove the first surface, but still render to both.
        expect(1) { renderSurfaces.size }
        expect(4) { surface1Messages.size }
        expect(2) { surface2Messages.size }

        showRunner.nextFrame(false) // Render again to the remaining surface.
        expect(1) { renderSurfaces.size }
        expect(4) { surface1Messages.size }
        expect(3) { surface2Messages.size }
    }

    @Test
    fun inNoProcrastinationMode_whenSurfacesAreAddedOrRemoved_shouldUpdateShowAfterNextFrame() {
        showRunner.nextFrame() // No surfaces so no show created, nothing rendered.
        expect(0) { renderSurfaces.size }

        showRunner.surfacesChanged(listOf(surface1Receiver), emptyList())
        showRunner.nextFrame() // Create a new show with one surface and render to it.
        expect(1) { renderSurfaces.size }
        expect(1) { surface1Messages.size }

        showRunner.nextFrame() // Render again.
        expect(1) { renderSurfaces.size }
        expect(2) { surface1Messages.size }

        showRunner.surfacesChanged(listOf(surface2Receiver), emptyList())
        showRunner.nextFrame() // Add another surface, render to both.
        expect(2) { renderSurfaces.size }
        expect(3) { surface1Messages.size }
        expect(1) { surface2Messages.size }

        showRunner.nextFrame() // Render again to both.
        expect(2) { renderSurfaces.size }
        expect(4) { surface1Messages.size }
        expect(2) { surface2Messages.size }

        showRunner.surfacesChanged(emptyList(), listOf(surface1Receiver))
        showRunner.nextFrame() // Remove the first surface and render to only the second.
        expect(1) { renderSurfaces.size }
        expect(4) { surface1Messages.size }
        expect(3) { surface2Messages.size }

        showRunner.nextFrame() // Render another frame on the remaining surface.
        expect(1) { renderSurfaces.size }
        expect(4) { surface1Messages.size }
        expect(4) { surface2Messages.size }
    }

    @Test @Ignore // all shows support surface changes.
    fun forShowsThatDontSupportSurfaceChanges_whenSurfacesAreAddedOrRemoved_shouldRecreateShowAfterNextFrame() {
//        renderSurfaces.supportsSurfaceChange = false

        showRunner.surfacesChanged(listOf(surface1Receiver), emptyList())
        showRunner.nextFrame() // Render a frame.
        expect(1) { renderSurfaces.size }
        expect(1) { surface1Messages.size }

        showRunner.surfacesChanged(listOf(surface2Receiver), emptyList())
        showRunner.nextFrame() // Prior show renders a frame, new show is created with two surfaces.
        expect(2) { renderSurfaces.size }
        expect(2) { surface1Messages.size }
        expect(0) { surface2Messages.size }

        showRunner.nextFrame() // Render a frame with the new show.
        expect(2) { renderSurfaces.size }
        expect(3) { surface1Messages.size }
        expect(1) { surface2Messages.size }

        showRunner.surfacesChanged(emptyList(), listOf(surface1Receiver))
        showRunner.nextFrame() // Render another frame on both surfaces, then recreate the show with new surfaces.
        expect(3) { renderSurfaces.size }
        expect(4) { surface1Messages.size }
        expect(2) { surface2Messages.size }

        showRunner.nextFrame() // Render another frame on the remaining surface.
        expect(3) { renderSurfaces.size }
        expect(4) { surface1Messages.size }
        expect(3) { surface2Messages.size }
    }

    @Test
    fun forShowsThatDontSupportSurfaceChanges_whenShowIsRecreated_gadgetSettingsAreRestored() {
//        renderSurfaces.supportsSurfaceChange = false

        showRunner.surfacesChanged(listOf(surface1Receiver), emptyList())
        showRunner.nextFrame() // Create show and request gadgets.
        expect(1) { renderSurfaces.size }

        val originalSlider = showManager.useGadget<Slider>("brightnessSlider")
        expect(1.0f) { originalSlider.value }
        originalSlider.value = 0.5f

        showRunner.surfacesChanged(listOf(surface2Receiver), emptyList())
        showRunner.nextFrame() // Recreate show and restore gadget state.
        expect(2) { renderSurfaces.size }

        val recreatedSlider = showManager.useGadget<Slider>("brightnessSlider")
        expect(0.5f) { recreatedSlider.value }
    }

    @Test
    fun forShowsThatDontSupportSurfaceChanges_whenShowIsRecreated_publishedActiveGadgetsAreUnchanged() {
//        renderSurfaces.supportsSurfaceChange = false

        showRunner.surfacesChanged(listOf(surface1Receiver), emptyList())
        showRunner.nextFrame() // Create show and request gadgets.
        expect(1) { renderSurfaces.size }

        expect(0) { serverNetwork.packetsToSend.size }

        val originalSlider = showManager.useGadget<Slider>("brightnessSlider")
        expect(1.0f) { originalSlider.value }
        originalSlider.value = 0.5f

        showRunner.surfacesChanged(listOf(surface2Receiver), emptyList())
        showRunner.nextFrame() // Recreate show and restore gadget state.
        expect(2) { renderSurfaces.size }

        val recreatedSlider = showManager.useGadget<Slider>("brightnessSlider")
        expect(0.5f) { recreatedSlider.value }
    }

    @Test
    fun shouldUpdateDmxAfterEveryFrame() {
        expect(emptyList<String>()) { dmxEvents }

        showRunner.nextFrame()

        expect(listOf("dmx frame sent")) { dmxEvents }
    }

    @Ignore
    @Test
    fun shouldSyncGadgetsProperly() {
        TODO("write me")
    }

    @Test
    fun whenSurfaceIsReAddedAndNewBufferIsRegistered_shouldHaveForgottenAboutOldOne() {
        showRunner.surfacesChanged(listOf(surface1Receiver), emptyList())
        showRunner.nextFrame() // Creates show and registers a buffer for surface1.

        showRunner.surfacesChanged(listOf(), listOf(surface1Receiver))
        showRunner.nextFrame() // Removes old buffer for surface1.

        showRunner.surfacesChanged(listOf(surface1Receiver), emptyList())
        showRunner.nextFrame() // Creates new buffer for surface1.

        showRunner.nextFrame() // Renders frame, expect no exceptions due to too many buffers.
    }

//    @Test
//    fun whenShowAttemptsToObtainShaderBufferDuringNextFrame_shouldThrowException() {
//        showRunner.surfacesChanged(listOf(surface1Receiver), emptyList())
//        showRunner.nextFrame()
//
//        renderSurfaces.onNextFrame = {
//            // It's illegal to request a new ShaderBuffer during #nextFrame().
////            showRunner.getShaderBuffer(surface1Receiver.surface, fakeGlslContext.fakeShader())
//        }
//
//        val e = assertFailsWith(IllegalStateException::class) { showRunner.nextFrame() }
//        assertTrue { e.message!!.startsWith("Shaders can't be obtained during #nextFrame()") }
//    }

//    @Test
//    fun whenShowAttemptsToObtainMovingHeadDuringNextFrame_shouldThrowException() {
//        showRunner.surfacesChanged(listOf(surface1Receiver), emptyList())
//        showRunner.nextFrame()
//
//        renderSurfaces.onNextFrame = {
//            // It's illegal to request a new ShaderBuffer during #nextFrame().
//            showRunner.getMovingHeadBuffer(
//                MovingHead(
//                    "leftEye",
//                    Vector3F(-163.738f, 204.361f, 439.302f)
//                )
//            )
//        }
//
//        val e = assertFailsWith(IllegalStateException::class) { showRunner.nextFrame() }
//        expect("Moving heads can't be obtained during #nextFrame()") { e.message!! }
//    }

//    @Test
//    fun whenShowAttemptsToObtainGadgetDuringNextFrame_shouldThrowException() {
//        showRunner.surfacesChanged(listOf(surface1Receiver), emptyList())
//        showRunner.nextFrame()
//
//        renderSurfaces.onNextFrame = {
//            // It's illegal to request a new Gadget during #nextFrame().
//            showRunner.getGadget("another-gadget", Slider("Another Gadget"))
//        }
//
//        val e = assertFailsWith(IllegalStateException::class) { showRunner.nextFrame() }
//        assertTrue { e.message!!.startsWith("Gadgets can't be obtained during #nextFrame()") }
//    }

//    @Test
//    fun whenShowThrowsExceptionDuringNextFrame_shouldPerformHousekeepingImmediatelyOnNextFrame() {
//        showRunner.surfacesChanged(listOf(surface1Receiver), emptyList())
//        showRunner.nextFrame()
//
//        renderSurfaces.onNextFrame = {
//            throw Exception("fake exception from show")
//        }
//
//        val e = assertFailsWith(Exception::class) { showRunner.nextFrame() }
//        assertTrue { e.message!!.startsWith("fake exception from show") }
//
//        // When a show throws an exception, Pinky will switch to a safe show (e.g. SolidColorShow);
//        // we should use it to render the next frame.
////        showRunner.nextPatchSet = TestShow1()
//        showRunner.nextFrame()
//    }

    inner class TestShow1(
        var supportsSurfaceChange: Boolean = true,
        var onCreateShow: () -> Unit = {},
        var onNextFrame: () -> Unit = {},
        var onSurfacesChanged: (List<Surface>, List<Surface>) -> Unit = { _, _ -> }
    ) : Show("TestShow1") {
        val createdShows = mutableListOf<ShowRenderer>()
        val fakeShader = fakeGlslContext.fakeShader()

        override fun createRenderer(model: Model<*>, showContext: ShowContext): Renderer {
            return ShowRenderer(showContext).also { createdShows.add(it) }
        }

        inner class ShowRenderer(private val showContext: ShowContext) : Renderer {
            val slider = showContext.getGadget("slider", Slider("slider"))
//            val shaderBuffers =
//                showContext.allSurfaces.associateWith { showContext.getShaderBuffer(it, fakeShader) }.toMutableMap()

            init {
                onCreateShow()
            }

            override fun nextFrame() {
//                shaderBuffers.values.forEach {
////                TODO    it.color = Color.WHITE
//                }
//                onNextFrame()
            }

            override fun surfacesChanged(newSurfaces: List<Surface>, removedSurfaces: List<Surface>) {
                if (!supportsSurfaceChange) {
                    super.surfacesChanged(newSurfaces, removedSurfaces)
                } else {
//                    removedSurfaces.forEach { shaderBuffers.remove(it) }
//                    newSurfaces.forEach { shaderBuffers[it] = showContext.getShaderBuffer(it, fakeShader) }
                }

                onSurfacesChanged(newSurfaces, removedSurfaces)
            }
        }
    }
}

class FakeSurfaceReceiver(override val surface: Surface, val sendFn: (Pixels) -> Unit) : SurfaceReceiver {
    override fun send(pixels: Pixels) = sendFn(pixels)
}
