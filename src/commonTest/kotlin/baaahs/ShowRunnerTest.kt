package baaahs

import baaahs.controller.Controller
import baaahs.fixtures.*
import baaahs.gadgets.Slider
import baaahs.gl.render.RenderManager
import baaahs.gl.render.RenderTarget
import baaahs.gl.testPlugins
import baaahs.gl.testToolchain
import baaahs.io.ByteArrayWriter
import baaahs.mapper.Storage
import baaahs.midi.NullMidiDevices
import baaahs.net.TestNetwork
import baaahs.scene.SceneMonitor
import baaahs.show.SampleData
import baaahs.shows.FakeGlContext
import baaahs.sim.FakeDmxUniverse
import baaahs.sim.FakeFs
import baaahs.sm.server.EventManager
import baaahs.sm.server.GadgetManager
import baaahs.sm.server.ServerNotices
import baaahs.sm.server.StageManager
import ch.tutteli.atrium.api.fluent.en_GB.containsExactly
import ch.tutteli.atrium.api.fluent.en_GB.isEmpty
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import ext.kotlinx_coroutines_test.TestCoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlin.test.BeforeTest
import kotlin.test.Ignore
import kotlin.test.Test

@InternalCoroutinesApi
class ShowRunnerTest {
    private val network = TestNetwork(0)
    private val serverNetwork = network.link("test")
    private lateinit var server: PubSub.Server

    private val fs = FakeFs()

    private lateinit var renderTargets: Map<Fixture, RenderTarget>
    private val surface1Messages = mutableListOf<String>()
    private val surface1Fixture =
        PixelArrayFixture(testModelSurface("surface 1"), 1,
            transport = FakeTransport { surface1Messages.add("frame") })
    private val surface2Messages = mutableListOf<String>()
    private val surface2Fixture =
        PixelArrayFixture(testModelSurface("surface 2"), 1,
            transport = FakeTransport { surface2Messages.add("frame") })
    private lateinit var fakeGlslContext: FakeGlContext
    private lateinit var dmxUniverse: FakeDmxUniverse
    private val dmxEvents = mutableListOf<String>()
    private lateinit var stageManager: StageManager
    private lateinit var fixtureManager: FixtureManagerImpl

    @BeforeTest
    fun setUp() {
        val dispatcher = TestCoroutineDispatcher()
        server = PubSub.listen(serverNetwork.startHttpServer(1234), CoroutineScope(dispatcher))
        fakeGlslContext = FakeGlContext()
        dmxUniverse = FakeDmxUniverse()
        dmxUniverse.listen(1, 1) { dmxEvents.add("dmx frame sent") }
        val renderManager = RenderManager(fakeGlslContext)
        val plugins = testPlugins()
        fixtureManager = FixtureManagerImpl(renderManager, plugins)
        stageManager = StageManager(
            testToolchain, renderManager, server, Storage(fs, plugins), fixtureManager,
            FakeClock(), GadgetManager(server, FakeClock(), dispatcher),
            ServerNotices(server, dispatcher), SceneMonitor(), EventManager(NullMidiDevices(), server, plugins)
        )
        stageManager.switchTo(SampleData.sampleLegacyShow)
        stageManager.switchToScene(testSceneData())
        renderTargets = fixtureManager.getRenderTargets_ForTestOnly()
        surface1Messages.clear()
        surface2Messages.clear()
        dmxEvents.clear()
    }

    @Test
    @Ignore // TODO
    fun whenNoKnownSurfaces_shouldStillCreateShow() = doRunBlocking {
        stageManager.renderAndSendNextFrame(true)
        expect(renderTargets.size).toBe(1)
        expect(surface1Messages.size).toBe(0)
    }

    @Test
    fun shouldRenderShow() = doRunBlocking {
        fixtureManager.fixturesChanged(listOf(surface1Fixture, surface2Fixture), emptyList())
        stageManager.renderAndSendNextFrame(true)
        expect(renderTargets.size).toBe(2)
        expect(surface1Messages.size).toBe(1)
        expect(surface2Messages.size).toBe(1)

        stageManager.renderAndSendNextFrame(true)
        expect(renderTargets.size).toBe(2)
        expect(surface1Messages.size).toBe(2)
        expect(surface2Messages.size).toBe(2)

        val buffer1 = surface1Messages[0]
//        expect(testShow1.fakeShader) { buffer1.shader }
//        expect(Color.WHITE) { buffer1.color }

        val buffer2 = surface1Messages[0]
//        expect(testShow1.fakeShader) { buffer2.shader }
//        expect(Color.WHITE) { (buffer2).color }
    }

    @Test
    fun inProcrastinationMode_whenSurfacesAreAddedOrRemoved_shouldUpdateShowAfterNextFrame() = doRunBlocking {
        stageManager.renderAndSendNextFrame(false) // No surfaces so no show created, nothing rendered.
        expect(renderTargets.size).toBe(0)

        fixtureManager.fixturesChanged(listOf(surface1Fixture), emptyList())
        stageManager.renderAndSendNextFrame(false) // Create a new show with one surface but don't render to it yet.
        expect(renderTargets.size).toBe(1)
        expect(surface1Messages.size).toBe(0)

        stageManager.renderAndSendNextFrame(false) // Render and include that surface.
        expect(renderTargets.size).toBe(1)
        expect(surface1Messages.size).toBe(1)

        fixtureManager.fixturesChanged(listOf(surface2Fixture), emptyList())
        stageManager.renderAndSendNextFrame(false) // Add another surface, but still only render to the first.
        expect(renderTargets.size).toBe(2)
        expect(surface1Messages.size).toBe(2)
        expect(surface2Messages.size).toBe(0)

        stageManager.renderAndSendNextFrame(false) // Render to both.
        expect(renderTargets.size).toBe(2)
        expect(surface1Messages.size).toBe(3)
        expect(surface2Messages.size).toBe(1)

        fixtureManager.fixturesChanged(emptyList(), listOf(surface1Fixture))
        stageManager.renderAndSendNextFrame(false) // Remove the first surface, but still render to both.
        expect(renderTargets.size).toBe(1)
        expect(surface1Messages.size).toBe(4)
        expect(surface2Messages.size).toBe(2)

        stageManager.renderAndSendNextFrame(false) // Render again to the remaining surface.
        expect(renderTargets.size).toBe(1)
        expect(surface1Messages.size).toBe(4)
        expect(surface2Messages.size).toBe(3)
    }

    @Test
    fun inNoProcrastinationMode_whenSurfacesAreAddedOrRemoved_shouldUpdateShowAfterNextFrame() = doRunBlocking {
        stageManager.renderAndSendNextFrame(true) // No surfaces so no show created, nothing rendered.
        expect(renderTargets.size).toBe(0)

        fixtureManager.fixturesChanged(listOf(surface1Fixture), emptyList())
        stageManager.renderAndSendNextFrame(true) // Create a new show with one surface and render to it.
        expect(renderTargets.size).toBe(1)
        expect(surface1Messages.size).toBe(1)

        stageManager.renderAndSendNextFrame(true) // Render again.
        expect(renderTargets.size).toBe(1)
        expect(surface1Messages.size).toBe(2)

        fixtureManager.fixturesChanged(listOf(surface2Fixture), emptyList())
        stageManager.renderAndSendNextFrame(true) // Add another surface, render to both.
        expect(renderTargets.size).toBe(2)
        expect(surface1Messages.size).toBe(3)
        expect(surface2Messages.size).toBe(1)

        stageManager.renderAndSendNextFrame(true) // Render again to both.
        expect(renderTargets.size).toBe(2)
        expect(surface1Messages.size).toBe(4)
        expect(surface2Messages.size).toBe(2)

        fixtureManager.fixturesChanged(emptyList(), listOf(surface1Fixture))
        stageManager.renderAndSendNextFrame(true) // Remove the first surface and render to only the second.
        expect(renderTargets.size).toBe(1)
        expect(surface1Messages.size).toBe(4)
        expect(surface2Messages.size).toBe(3)

        stageManager.renderAndSendNextFrame(true) // Render another frame on the remaining surface.
        expect(renderTargets.size).toBe(1)
        expect(surface1Messages.size).toBe(4)
        expect(surface2Messages.size).toBe(4)
    }

    @Test @Ignore // all shows support surface changes.
    fun forShowsThatDontSupportSurfaceChanges_whenSurfacesAreAddedOrRemoved_shouldRecreateShowAfterNextFrame() = doRunBlocking {
//        renderSurfaces.supportsSurfaceChange = false

        fixtureManager.fixturesChanged(listOf(surface1Fixture), emptyList())
        stageManager.renderAndSendNextFrame(true) // Render a frame.
        expect(renderTargets.size).toBe(1)
        expect(surface1Messages.size).toBe(1)

        fixtureManager.fixturesChanged(listOf(surface2Fixture), emptyList())
        stageManager.renderAndSendNextFrame(true) // Prior show renders a frame, new show is created with two surfaces.
        expect(renderTargets.size).toBe(2)
        expect(surface1Messages.size).toBe(2)
        expect(surface2Messages.size).toBe(0)

        stageManager.renderAndSendNextFrame(true) // Render a frame with the new show.
        expect(renderTargets.size).toBe(2)
        expect(surface1Messages.size).toBe(3)
        expect(surface2Messages.size).toBe(1)

        fixtureManager.fixturesChanged(emptyList(), listOf(surface1Fixture))
        stageManager.renderAndSendNextFrame(true) // Render another frame on both surfaces, then recreate the show with new surfaces.
        expect(renderTargets.size).toBe(3)
        expect(surface1Messages.size).toBe(4)
        expect(surface2Messages.size).toBe(2)

        stageManager.renderAndSendNextFrame(true) // Render another frame on the remaining surface.
        expect(renderTargets.size).toBe(3)
        expect(surface1Messages.size).toBe(4)
        expect(surface2Messages.size).toBe(3)
    }

    @Test
    fun forShowsThatDontSupportSurfaceChanges_whenShowIsRecreated_gadgetSettingsAreRestored() = doRunBlocking {
//        renderSurfaces.supportsSurfaceChange = false

        fixtureManager.fixturesChanged(listOf(surface1Fixture), emptyList())
        stageManager.renderAndSendNextFrame(true) // Create show and request gadgets.
        expect(renderTargets.size).toBe(1)

        val originalSlider = stageManager.useGadget<Slider>("brightness")
        expect(originalSlider.position).toBe(1.0f)
        originalSlider.position = 0.5f

        fixtureManager.fixturesChanged(listOf(surface2Fixture), emptyList())
        stageManager.renderAndSendNextFrame(true) // Recreate show and restore gadget state.
        expect(renderTargets.size).toBe(2)

        val recreatedSlider = stageManager.useGadget<Slider>("brightness")
        expect(recreatedSlider.position).toBe(0.5f)
    }

    @Test
    fun forShowsThatDontSupportSurfaceChanges_whenShowIsRecreated_publishedActiveGadgetsAreUnchanged() = doRunBlocking {
//        renderSurfaces.supportsSurfaceChange = false

        fixtureManager.fixturesChanged(listOf(surface1Fixture), emptyList())
        stageManager.renderAndSendNextFrame(true) // Create show and request gadgets.
        expect(renderTargets.size).toBe(1)

        expect(serverNetwork.packetsToSend.size).toBe(0)

        val originalSlider = stageManager.useGadget<Slider>("brightness")
        expect(originalSlider.position).toBe(1.0f)
        originalSlider.position = 0.5f

        fixtureManager.fixturesChanged(listOf(surface2Fixture), emptyList())
        stageManager.renderAndSendNextFrame(true) // Recreate show and restore gadget state.
        expect(renderTargets.size).toBe(2)

        val recreatedSlider = stageManager.useGadget<Slider>("brightness")
        expect(recreatedSlider.position).toBe(0.5f)
    }

    @Test
    fun shouldUpdateDmxAfterEveryFrame() = doRunBlocking {
        expect(dmxEvents).isEmpty()

        stageManager.renderAndSendNextFrame(true)

        expect(dmxEvents).containsExactly("dmx frame sent")
    }

    @Ignore
    @Test
    fun shouldSyncGadgetsProperly() {
        TODO("write me")
    }

    @Test
    fun whenSurfaceIsReAddedAndNewBufferIsRegistered_shouldHaveForgottenAboutOldOne() = doRunBlocking {
        fixtureManager.fixturesChanged(listOf(surface1Fixture), emptyList())
        stageManager.renderAndSendNextFrame(true) // Creates show and registers a buffer for surface1.

        fixtureManager.fixturesChanged(emptyList(), listOf(surface1Fixture))
        stageManager.renderAndSendNextFrame(true) // Removes old buffer for surface1.

        fixtureManager.fixturesChanged(listOf(surface1Fixture), emptyList())
        stageManager.renderAndSendNextFrame(true) // Creates new buffer for surface1.

        stageManager.renderAndSendNextFrame(true) // Renders frame, expect no exceptions due to too many buffers.
    }

    class FakeTransport(
        override val name: String = "Fake Transport",
        private val fn: (byteArray: ByteArray) -> Unit
    ) : Transport {
        override val controller: Controller
            get() = TODO("not implemented")
        override val config: TransportConfig?
            get() = null

        override fun deliverBytes(byteArray: ByteArray) {
            fn(byteArray)
        }

        override fun deliverComponents(
            componentCount: Int,
            bytesPerComponent: Int,
            fn: (componentIndex: Int, buf: ByteArrayWriter) -> Unit
        ) {
            val buf = ByteArrayWriter(componentCount * bytesPerComponent)
            for (componentIndex in 0 until componentCount) {
                buf.offset = componentIndex * bytesPerComponent
            }
            fn(buf.toBytes())
        }
    }
}