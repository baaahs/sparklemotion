package baaahs

import baaahs.ShowRunner.FixtureReceiver
import baaahs.fixtures.Fixture
import baaahs.fixtures.FixtureManager
import baaahs.fixtures.IdentifiedFixture
import baaahs.gadgets.Slider
import baaahs.gl.render.FixtureRenderPlan
import baaahs.gl.render.RenderEngine
import baaahs.mapper.Storage
import baaahs.model.ModelInfo
import baaahs.models.SheepModel
import baaahs.net.TestNetwork
import baaahs.plugin.Plugins
import baaahs.show.SampleData
import baaahs.shows.FakeGlContext
import baaahs.sim.FakeDmxUniverse
import baaahs.sim.FakeFs
import ext.TestCoroutineContext
import kotlinx.coroutines.InternalCoroutinesApi
import kotlin.test.BeforeTest
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.expect

@InternalCoroutinesApi
class ShowRunnerTest {
    private lateinit var testCoroutineContext: TestCoroutineContext
    private val network = TestNetwork(0)
    private val serverNetwork = network.link("test")
    private lateinit var server: PubSub.Server

    private val fs = FakeFs()

    private lateinit var fixtureRenderPlans: Map<Fixture, FixtureRenderPlan>
    private val surface1Messages = mutableListOf<Pixels>()
    private val surface1Receiver =
        FakeFixtureReceiver(IdentifiedFixture(SheepModel.Panel("surface 1"), 1)) { buffer -> surface1Messages.add(buffer) }
    private val surface2Messages = mutableListOf<Pixels>()
    private val surface2Receiver =
        FakeFixtureReceiver(IdentifiedFixture(SheepModel.Panel("surface 2"), 1)) { buffer -> surface2Messages.add(buffer) }
    private lateinit var fakeGlslContext: FakeGlContext
    private lateinit var dmxUniverse: FakeDmxUniverse
    private val dmxEvents = mutableListOf<String>()
    private val sheepModel = SheepModel().apply { panels = emptyList() }
    private lateinit var stageManager: StageManager
    private lateinit var fixtureManager: FixtureManager

    @BeforeTest
    fun setUp() {
        testCoroutineContext = TestCoroutineContext("network")
        server = PubSub.listen(serverNetwork.startHttpServer(1234), testCoroutineContext)
        fakeGlslContext = FakeGlContext()
        dmxUniverse = FakeDmxUniverse()
        dmxUniverse.reader(1, 1) { dmxEvents.add("dmx frame sent") }
        val renderEngine = RenderEngine(fakeGlslContext, ModelInfo.Empty)
        fixtureManager = FixtureManager(renderEngine)
        val movingHeadManager = MovingHeadManager(fs, server, emptyList())
        stageManager = StageManager(
            Plugins.safe(), renderEngine, server, Storage(fs, Plugins.safe()), fixtureManager,
            dmxUniverse, movingHeadManager, FakeClock(), sheepModel, testCoroutineContext
        )
        stageManager.switchTo(SampleData.sampleShow)
        fixtureRenderPlans = fixtureManager.getFixtureRenderPlans_ForTestOnly()
        surface1Messages.clear()
        surface2Messages.clear()
        dmxEvents.clear()
    }

    @Test @Ignore // TODO
    fun whenNoKnownSurfaces_shouldStillCreateShow() {
        stageManager.renderAndSendNextFrame()
        expect(1) { fixtureRenderPlans.size }
        expect(0) { surface1Messages.size }
    }

    @Test
    fun shouldRenderShow() {
        fixtureManager.fixturesChanged(listOf(surface1Receiver, surface2Receiver), emptyList())
        stageManager.renderAndSendNextFrame()
        expect(2) { fixtureRenderPlans.size }
        expect(1) { surface1Messages.size }
        expect(1) { surface2Messages.size }

        stageManager.renderAndSendNextFrame()
        expect(2) { fixtureRenderPlans.size }
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
        stageManager.renderAndSendNextFrame(false) // No surfaces so no show created, nothing rendered.
        expect(0) { fixtureRenderPlans.size }

        fixtureManager.fixturesChanged(listOf(surface1Receiver), emptyList())
        stageManager.renderAndSendNextFrame(false) // Create a new show with one surface but don't render to it yet.
        expect(1) { fixtureRenderPlans.size }
        expect(0) { surface1Messages.size }

        stageManager.renderAndSendNextFrame(false) // Render and include that surface.
        expect(1) { fixtureRenderPlans.size }
        expect(1) { surface1Messages.size }

        fixtureManager.fixturesChanged(listOf(surface2Receiver), emptyList())
        stageManager.renderAndSendNextFrame(false) // Add another surface, but still only render to the first.
        expect(2) { fixtureRenderPlans.size }
        expect(2) { surface1Messages.size }
        expect(0) { surface2Messages.size }

        stageManager.renderAndSendNextFrame(false) // Render to both.
        expect(2) { fixtureRenderPlans.size }
        expect(3) { surface1Messages.size }
        expect(1) { surface2Messages.size }

        fixtureManager.fixturesChanged(emptyList(), listOf(surface1Receiver))
        stageManager.renderAndSendNextFrame(false) // Remove the first surface, but still render to both.
        expect(1) { fixtureRenderPlans.size }
        expect(4) { surface1Messages.size }
        expect(2) { surface2Messages.size }

        stageManager.renderAndSendNextFrame(false) // Render again to the remaining surface.
        expect(1) { fixtureRenderPlans.size }
        expect(4) { surface1Messages.size }
        expect(3) { surface2Messages.size }
    }

    @Test
    fun inNoProcrastinationMode_whenSurfacesAreAddedOrRemoved_shouldUpdateShowAfterNextFrame() {
        stageManager.renderAndSendNextFrame() // No surfaces so no show created, nothing rendered.
        expect(0) { fixtureRenderPlans.size }

        fixtureManager.fixturesChanged(listOf(surface1Receiver), emptyList())
        stageManager.renderAndSendNextFrame() // Create a new show with one surface and render to it.
        expect(1) { fixtureRenderPlans.size }
        expect(1) { surface1Messages.size }

        stageManager.renderAndSendNextFrame() // Render again.
        expect(1) { fixtureRenderPlans.size }
        expect(2) { surface1Messages.size }

        fixtureManager.fixturesChanged(listOf(surface2Receiver), emptyList())
        stageManager.renderAndSendNextFrame() // Add another surface, render to both.
        expect(2) { fixtureRenderPlans.size }
        expect(3) { surface1Messages.size }
        expect(1) { surface2Messages.size }

        stageManager.renderAndSendNextFrame() // Render again to both.
        expect(2) { fixtureRenderPlans.size }
        expect(4) { surface1Messages.size }
        expect(2) { surface2Messages.size }

        fixtureManager.fixturesChanged(emptyList(), listOf(surface1Receiver))
        stageManager.renderAndSendNextFrame() // Remove the first surface and render to only the second.
        expect(1) { fixtureRenderPlans.size }
        expect(4) { surface1Messages.size }
        expect(3) { surface2Messages.size }

        stageManager.renderAndSendNextFrame() // Render another frame on the remaining surface.
        expect(1) { fixtureRenderPlans.size }
        expect(4) { surface1Messages.size }
        expect(4) { surface2Messages.size }
    }

    @Test @Ignore // all shows support surface changes.
    fun forShowsThatDontSupportSurfaceChanges_whenSurfacesAreAddedOrRemoved_shouldRecreateShowAfterNextFrame() {
//        renderSurfaces.supportsSurfaceChange = false

        fixtureManager.fixturesChanged(listOf(surface1Receiver), emptyList())
        stageManager.renderAndSendNextFrame() // Render a frame.
        expect(1) { fixtureRenderPlans.size }
        expect(1) { surface1Messages.size }

        fixtureManager.fixturesChanged(listOf(surface2Receiver), emptyList())
        stageManager.renderAndSendNextFrame() // Prior show renders a frame, new show is created with two surfaces.
        expect(2) { fixtureRenderPlans.size }
        expect(2) { surface1Messages.size }
        expect(0) { surface2Messages.size }

        stageManager.renderAndSendNextFrame() // Render a frame with the new show.
        expect(2) { fixtureRenderPlans.size }
        expect(3) { surface1Messages.size }
        expect(1) { surface2Messages.size }

        fixtureManager.fixturesChanged(emptyList(), listOf(surface1Receiver))
        stageManager.renderAndSendNextFrame() // Render another frame on both surfaces, then recreate the show with new surfaces.
        expect(3) { fixtureRenderPlans.size }
        expect(4) { surface1Messages.size }
        expect(2) { surface2Messages.size }

        stageManager.renderAndSendNextFrame() // Render another frame on the remaining surface.
        expect(3) { fixtureRenderPlans.size }
        expect(4) { surface1Messages.size }
        expect(3) { surface2Messages.size }
    }

    @Test
    fun forShowsThatDontSupportSurfaceChanges_whenShowIsRecreated_gadgetSettingsAreRestored() {
//        renderSurfaces.supportsSurfaceChange = false

        fixtureManager.fixturesChanged(listOf(surface1Receiver), emptyList())
        stageManager.renderAndSendNextFrame() // Create show and request gadgets.
        expect(1) { fixtureRenderPlans.size }

        val originalSlider = stageManager.useGadget<Slider>("brightnessSliderControl")
        expect(1.0f) { originalSlider.value }
        originalSlider.value = 0.5f

        fixtureManager.fixturesChanged(listOf(surface2Receiver), emptyList())
        stageManager.renderAndSendNextFrame() // Recreate show and restore gadget state.
        expect(2) { fixtureRenderPlans.size }

        val recreatedSlider = stageManager.useGadget<Slider>("brightnessSliderControl")
        expect(0.5f) { recreatedSlider.value }
    }

    @Test
    fun forShowsThatDontSupportSurfaceChanges_whenShowIsRecreated_publishedActiveGadgetsAreUnchanged() {
//        renderSurfaces.supportsSurfaceChange = false

        fixtureManager.fixturesChanged(listOf(surface1Receiver), emptyList())
        stageManager.renderAndSendNextFrame() // Create show and request gadgets.
        expect(1) { fixtureRenderPlans.size }

        expect(0) { serverNetwork.packetsToSend.size }

        val originalSlider = stageManager.useGadget<Slider>("brightnessSliderControl")
        expect(1.0f) { originalSlider.value }
        originalSlider.value = 0.5f

        fixtureManager.fixturesChanged(listOf(surface2Receiver), emptyList())
        stageManager.renderAndSendNextFrame() // Recreate show and restore gadget state.
        expect(2) { fixtureRenderPlans.size }

        val recreatedSlider = stageManager.useGadget<Slider>("brightnessSliderControl")
        expect(0.5f) { recreatedSlider.value }
    }

    @Test
    fun shouldUpdateDmxAfterEveryFrame() {
        expect(emptyList<String>()) { dmxEvents }

        stageManager.renderAndSendNextFrame()

        expect(listOf("dmx frame sent")) { dmxEvents }
    }

    @Ignore
    @Test
    fun shouldSyncGadgetsProperly() {
        TODO("write me")
    }

    @Test
    fun whenSurfaceIsReAddedAndNewBufferIsRegistered_shouldHaveForgottenAboutOldOne() {
        fixtureManager.fixturesChanged(listOf(surface1Receiver), emptyList())
        stageManager.renderAndSendNextFrame() // Creates show and registers a buffer for surface1.

        fixtureManager.fixturesChanged(listOf(), listOf(surface1Receiver))
        stageManager.renderAndSendNextFrame() // Removes old buffer for surface1.

        fixtureManager.fixturesChanged(listOf(surface1Receiver), emptyList())
        stageManager.renderAndSendNextFrame() // Creates new buffer for surface1.

        stageManager.renderAndSendNextFrame() // Renders frame, expect no exceptions due to too many buffers.
    }
}

class FakeFixtureReceiver(override val fixture: Fixture, val sendFn: (Pixels) -> Unit) : FixtureReceiver {
    override fun send(pixels: Pixels) = sendFn(pixels)
}
