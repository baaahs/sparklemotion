package baaahs

import baaahs.SheepModel.Panel
import baaahs.ShowRunner.SurfaceReceiver
import baaahs.gadgets.Slider
import baaahs.shaders.SolidShader
import baaahs.sim.FakeDmxUniverse
import baaahs.sim.FakeNetwork
import ext.TestCoroutineContext
import kotlinx.coroutines.InternalCoroutinesApi
import kotlin.test.*

@InternalCoroutinesApi
class ShowRunnerTest {
    val testCoroutineContext = TestCoroutineContext("network")
    val network = FakeNetwork(0, coroutineContext = testCoroutineContext)

    val serverNetwork = network.link()
    val server = PubSub.listen(serverNetwork, 1234).apply { install(gadgetModule) }

    val gadgetProvider = GadgetProvider(server)
    lateinit var showRunner: ShowRunner

    val testShow1 = TestShow1()
    val surface1Messages = mutableListOf<Shader.Buffer>()
    val surface1Receiver = SurfaceReceiver(Panel("surface 1")) { buffer -> surface1Messages.add(buffer) }
    val surface2Messages = mutableListOf<Shader.Buffer>()
    val surface2Receiver = SurfaceReceiver(Panel("surface 2")) { buffer -> surface2Messages.add(buffer) }
    lateinit var dmxUniverse: FakeDmxUniverse
    val dmxEvents = mutableListOf<String>()

    @BeforeTest
    fun setUp() {
        dmxUniverse = FakeDmxUniverse()
        dmxUniverse.reader(1, 1) { dmxEvents.add("dmx frame sent") }
        showRunner = ShowRunner(SheepModel(), testShow1, gadgetProvider, listOf(), FakeBeatProvider, dmxUniverse)
        surface1Messages.clear()
        surface2Messages.clear()
        dmxEvents.clear()
    }

    @Test
    fun whenNoKnownSurfaces_shouldNotCreateShow() {
        showRunner.nextFrame()
        expect(0) { testShow1.createdShows.size }
        expect(0) { surface1Messages.size }
    }

    @Test
    fun shouldRenderShow() {
        showRunner.surfacesChanged(listOf(surface1Receiver, surface2Receiver), emptyList())
        showRunner.nextFrame() // First time through nothing is rendered but a show is created.
        expect(1) { testShow1.createdShows.size }
        expect(0) { surface1Messages.size }

        showRunner.nextFrame() // Render to our surface.
        expect(1) { testShow1.createdShows.size }
        expect(1) { surface1Messages.size }

        val buffer1 = surface1Messages[0]
        expect(buffer1.shader) { testShow1.solidShader }
        expect((buffer1 as SolidShader.Buffer).color) { Color.WHITE }

        val buffer2 = surface1Messages[0]
        expect(buffer2.shader) { testShow1.solidShader }
        expect((buffer2 as SolidShader.Buffer).color) { Color.WHITE }
    }

    @Test
    fun forShowsThatSupportSurfaceChanges_whenSurfacesAreAddedOrRemoved_shouldUpdateShow() {
        showRunner.nextFrame() // No surfaces so no show created, nothing rendered.

        showRunner.surfacesChanged(listOf(surface1Receiver), emptyList())
        showRunner.nextFrame() // Create a new show with one surface, but we don't render anything the first time.
        expect(1) { testShow1.createdShows.size }
        expect(0) { surface1Messages.size }

        showRunner.nextFrame() // Render to our surface.
        expect(1) { testShow1.createdShows.size }
        expect(1) { surface1Messages.size }

        showRunner.surfacesChanged(listOf(surface2Receiver), emptyList())
        showRunner.nextFrame() // Render a frame, then inform show of the new surface.
        expect(1) { testShow1.createdShows.size }
        expect(2) { surface1Messages.size }
        expect(0) { surface2Messages.size }

        showRunner.nextFrame() // Render a frame on both surfaces.
        expect(1) { testShow1.createdShows.size }
        expect(3) { surface1Messages.size }
        expect(1) { surface2Messages.size }

        showRunner.surfacesChanged(emptyList(), listOf(surface1Receiver))
        showRunner.nextFrame() // Render another frame on both surfaces, then update the show's surfaces.
        expect(1) { testShow1.createdShows.size }
        expect(4) { surface1Messages.size }
        expect(2) { surface2Messages.size }

        showRunner.nextFrame() // Render another frame on the remaining surface.
        expect(1) { testShow1.createdShows.size }
        expect(4) { surface1Messages.size }
        expect(3) { surface2Messages.size }
    }

    @Test
    fun forShowsThatDontSupportSurfaceChanges_whenSurfacesAreAddedOrRemoved_shouldRecreateShowAfterNextFrame() {
        testShow1.supportsSurfaceChange = false

        showRunner.surfacesChanged(listOf(surface1Receiver), emptyList())
        showRunner.nextFrame() // First time through we don't actually render anything.
        expect(1) { testShow1.createdShows.size }
        expect(0) { surface1Messages.size }

        showRunner.nextFrame() // Render a frame.
        expect(1) { testShow1.createdShows.size }
        expect(1) { surface1Messages.size }

        showRunner.surfacesChanged(listOf(surface2Receiver), emptyList())
        showRunner.nextFrame() // Prior show renders a frame, new show is created with two surfaces.
        expect(2) { testShow1.createdShows.size }
        expect(2) { surface1Messages.size }
        expect(0) { surface2Messages.size }

        showRunner.nextFrame() // Render a frame with the new show.
        expect(2) { testShow1.createdShows.size }
        expect(3) { surface1Messages.size }
        expect(1) { surface2Messages.size }

        showRunner.surfacesChanged(emptyList(), listOf(surface1Receiver))
        showRunner.nextFrame() // Render another frame on both surfaces, then recreate the show with new surfaces.
        expect(3) { testShow1.createdShows.size }
        expect(4) { surface1Messages.size }
        expect(2) { surface2Messages.size }

        showRunner.nextFrame() // Render another frame on the remaining surface.
        expect(3) { testShow1.createdShows.size }
        expect(4) { surface1Messages.size }
        expect(3) { surface2Messages.size }
    }

    @Test
    fun forShowsThatDontSupportSurfaceChanges_whenShowIsRecreated_gadgetSettingsAreRestored() {
        testShow1.supportsSurfaceChange = false

        showRunner.surfacesChanged(listOf(surface1Receiver), emptyList())
        showRunner.nextFrame() // Create show and request gadgets.
        expect(testShow1.createdShows.size) { 1 }

        val originalSlider = gadgetProvider.findGadget("slider")!! as Slider
        expect(originalSlider.value) { 1.0f }
        originalSlider.value = 0.5f

        showRunner.surfacesChanged(listOf(surface2Receiver), emptyList())
        showRunner.nextFrame() // Recreate show and restore gadget data.
        expect(testShow1.createdShows.size) { 2 }

        val recreatedSlider = gadgetProvider.findGadget("slider")!! as Slider
        expect(recreatedSlider.value) { 0.5f }
    }

    @Test
    fun shouldUpdateDmxAfterEveryFrame() {
        expect(dmxEvents) { emptyList<String>() }

        showRunner.nextFrame()
        showRunner.send()
        expect(dmxEvents) { listOf("dmx frame sent") }
    }

    @Ignore
    @Test
    fun shouldSyncGadgetsProperly() {
        TODO("write me")
    }

    @Test
    fun whenShowLeavesHangingBuffersForASurface_shouldReportError() {
        testShow1.onShowCreate = {
            showRunner.allSurfaces.forEach { surface ->
                showRunner.getShaderBuffer(surface, SolidShader())
            }
        }

        showRunner.surfacesChanged(listOf(surface1Receiver), emptyList())
        showRunner.nextFrame() // Creates show but doesn't render a frame yet.
        val e = assertFailsWith(IllegalStateException::class) {
            showRunner.nextFrame()
        }
        assertTrue { e.message!!.startsWith("Too many shader buffers for Panel surface 1") }
    }

    @Test
    fun whenSurfaceIsReAddedAndNewBufferIsRegistered_shouldHaveForgottenAboutOldOne() {
        testShow1.supportsSurfaceChange = true

        showRunner.surfacesChanged(listOf(surface1Receiver), emptyList())
        showRunner.nextFrame() // Creates show and registers a buffer for surface1.

        showRunner.surfacesChanged(listOf(), listOf(surface1Receiver))
        showRunner.nextFrame() // Removes old buffer for surface1.

        showRunner.surfacesChanged(listOf(surface1Receiver), emptyList())
        showRunner.nextFrame() // Creates new buffer for surface1.

        showRunner.nextFrame() // Renders frame, expect no exceptions due to too many buffers.
    }

    class TestShow1(
        var supportsSurfaceChange: Boolean = true,
        var onShowCreate: () -> Unit = {},
        var onNextFrame: () -> Unit = {},
        var onSurfacesChanged: (List<Surface>, List<Surface>) -> Unit = { _, _ -> }
    ) : Show.MetaData("TestShow1") {
        val createdShows = mutableListOf<Show>()
        val solidShader = SolidShader()

        override fun createShow(sheepModel: SheepModel, showRunner: ShowRunner): Show {
            return ShowRenderer(showRunner).also { createdShows.add(it) }
        }

        inner class ShowRenderer(private val showRunner: ShowRunner) : Show {
            val slider = showRunner.getGadget("slider", Slider("slider"))
            val shaderBuffers =
                showRunner.allSurfaces.associateWith { showRunner.getShaderBuffer(it, solidShader) }.toMutableMap()

            init {
                onShowCreate()
            }

            override fun nextFrame() {
                shaderBuffers.values.forEach { it.color = Color.WHITE }
                onNextFrame()
            }

            override fun surfacesChanged(newSurfaces: List<Surface>, removedSurfaces: List<Surface>) {
                if (!supportsSurfaceChange) {
                    super.surfacesChanged(newSurfaces, removedSurfaces)
                } else {
                    removedSurfaces.forEach { shaderBuffers.remove(it) }
                    newSurfaces.forEach { shaderBuffers[it] = showRunner.getShaderBuffer(it, solidShader) }
                }

                onSurfacesChanged(newSurfaces, removedSurfaces)
            }
        }
    }

    object FakeBeatProvider : Pinky.BeatProvider {
        override var bpm = 120f
        override val beat = 0f
    }
}