package baaahs

import baaahs.geom.Matrix4
import baaahs.glshaders.GlslProgram
import baaahs.glshaders.Patch
import baaahs.glsl.GlslRenderer
import baaahs.glsl.GlslRendererTest
import baaahs.mapper.MappingSession
import baaahs.mapper.Storage
import baaahs.model.Model
import baaahs.models.SheepModel
import baaahs.net.FragmentingUdpLink
import baaahs.net.Network
import baaahs.net.TestNetwork
import baaahs.proto.BrainHelloMessage
import baaahs.proto.Type
import baaahs.shaders.IGlslShader
import baaahs.show.SampleData
import baaahs.shows.FakeGlslContext
import baaahs.sim.FakeDmxUniverse
import baaahs.sim.FakeFs
import kotlinx.coroutines.InternalCoroutinesApi
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.expect

@InternalCoroutinesApi
class PinkyTest {
    private lateinit var fakeGlslContext: FakeGlslContext
    private lateinit var network: TestNetwork
    private lateinit var clientAddress: Network.Address
    private val clientPort = 1234

    private val panel17 = SheepModel.Panel("17")
    private val model = SheepModel().apply { panels = listOf(panel17); eyes = emptyList() } as Model<*>
    private lateinit var showRunner: ShowRunner
    private lateinit var pinky: Pinky
    private lateinit var pinkyLink: TestNetwork.Link
    private lateinit var fakeFs: FakeFs

    @BeforeTest
    fun setUp() {
        fakeGlslContext = FakeGlslContext()
        network = TestNetwork(1_000_000)
        clientAddress = TestNetwork.Address("client")
        fakeFs = FakeFs()
        pinky = Pinky(
            model,
            SampleData.sampleShow,
            network,
            FakeDmxUniverse(),
            StubBeatSource(),
            FakeClock(),
            fakeFs,
            PermissiveFirmwareDaddy(),
            StubSoundAnalyzer(),
            glslRenderer = GlslRenderer(fakeGlslContext, GlslRendererTest.UvTranslatorForTest)
        )
        showRunner = pinky.showRunner
        pinkyLink = network.links.only()
    }

    @Test
    fun whenUnmappedBrainUnknownToPinkyComesOnline_showShouldBeNotified() {
        pinky.receive(clientAddress, clientPort, BrainHelloMessage("brain1", null).toBytes())
        pinky.updateSurfaces()
        pinky.drawNextFrame()

        expect(1) { showRunner.renderSurfaces.size }

        val packetTypes = pinkyLink.packetsToSend.map { Type.get(it[FragmentingUdpLink.headerSize]) }
        expect(listOf(Type.BRAIN_PANEL_SHADE)) { packetTypes } // Should send no mapping packet.
    }

    @Test
    fun whenUnmappedBrainKnownToPinkyComesOnline_showShouldBeNotifiedAndBrainShouldReceiveMapping() {
        injectPanelMapping(BrainId("brain1"), panel17)

        pinky.receive(clientAddress, clientPort, BrainHelloMessage("brain1", null).toBytes())
        pinky.updateSurfaces()
        pinky.drawNextFrame()

        val surface = showRunner.renderSurfaces.keys.only()
        expect(true) { surface is IdentifiedSurface }
        expect(panel17.name) { (surface as IdentifiedSurface).name }

        val packetTypes = pinkyLink.packetsToSend.map { Type.get(it[FragmentingUdpLink.headerSize]) }
        expect(listOf(Type.BRAIN_MAPPING, Type.BRAIN_PANEL_SHADE)) { packetTypes } // Should send a mapping packet.
    }

    @Test
    fun whenPinkySideMappedBrainComesOnline_showShouldBeNotified() {
        injectPanelMapping(BrainId("brain1"), panel17)

        pinky.receive(clientAddress, clientPort, BrainHelloMessage("brain1", null).toBytes())
        pinky.updateSurfaces()
        pinky.drawNextFrame()

        expect(1) { showRunner.renderSurfaces.size }
        val packetTypes = pinkyLink.packetsToSend.map { Type.get(it[FragmentingUdpLink.headerSize]) }
        expect(listOf(Type.BRAIN_MAPPING, Type.BRAIN_PANEL_SHADE)) { packetTypes } // Should send a mapping packet.
    }

    @Test
    fun whenBrainSideMappedBrainComesOnline_showShouldBeNotified() {
        injectPanelMapping(BrainId("brain1"), panel17)

        pinky.receive(clientAddress, clientPort, BrainHelloMessage("brain1", panel17.name).toBytes())
        pinky.updateSurfaces()
        pinky.drawNextFrame()

        expect(1) { showRunner.renderSurfaces.size }
        val packetTypes = pinkyLink.packetsToSend.map { Type.get(it[FragmentingUdpLink.headerSize]) }
        expect(listOf(Type.BRAIN_PANEL_SHADE)) { packetTypes } // Should send no mapping packet.
    }

    @Test
    fun asBrainsComeOnlineAndAreMapped_showShouldBeNotified() {
        injectPanelMapping(BrainId("brain1"), panel17)

        pinky.receive(clientAddress, clientPort, BrainHelloMessage("brain1", null).toBytes())
        pinky.updateSurfaces()
        pinky.drawNextFrame()
        pinky.drawNextFrame()

        expect(1) { showRunner.renderSurfaces.size }
        expect(true) { showRunner.renderSurfaces.keys.only() is IdentifiedSurface }

        pinky.receive(clientAddress, clientPort, BrainHelloMessage("brain1", panel17.name).toBytes())
        pinky.updateSurfaces()
        pinky.drawNextFrame()
        pinky.drawNextFrame()
        expect(1) { showRunner.renderSurfaces.size }
        expect(true) { showRunner.renderSurfaces.keys.only() is IdentifiedSurface }
        expect(panel17.name) { (showRunner.renderSurfaces.keys.only() as IdentifiedSurface).name }
    }

    @Test
    fun whenBrainHelloRaceCondition_asBrainsComeOnline_showShouldBeNotified() {
        pinky.receive(clientAddress, clientPort, BrainHelloMessage("brain1", null).toBytes())
        pinky.updateSurfaces()
        pinky.drawNextFrame()
        pinky.drawNextFrame()
        val renderSurfaces = showRunner.renderSurfaces

        // Remap to 17L...
        pinky.receive(clientAddress, clientPort, BrainHelloMessage("brain1", panel17.name).toBytes())
        // ... but a packet also made it through identifying brain1 as unmapped.
        pinky.receive(clientAddress, clientPort, BrainHelloMessage("brain1", null).toBytes())
        pinky.updateSurfaces()
        pinky.drawNextFrame()
        pinky.drawNextFrame()

        // Pinky should have sent out another BrainMappingMessage message; todo: verify that!

        pinky.receive(clientAddress, clientPort, BrainHelloMessage("brain1", panel17.name).toBytes())
        pinky.updateSurfaces()
        pinky.drawNextFrame()
        pinky.drawNextFrame()

        expect(1) { renderSurfaces.size }
        expect(true) { (renderSurfaces.keys.only() as IdentifiedSurface).modelSurface == panel17 }

        pinky.receive(clientAddress, clientPort, BrainHelloMessage("brain1", panel17.name).toBytes())
        pinky.updateSurfaces()
        pinky.drawNextFrame()
        pinky.drawNextFrame()
        expect(1) { renderSurfaces.size }
        val surface = renderSurfaces.keys.only()
        expect(true) { surface is IdentifiedSurface }
        expect(panel17.name) { (surface as IdentifiedSurface).name }
    }

    private fun injectPanelMapping(brainId: BrainId, surface: Model.Surface) {
        val mappingSessionPath = Storage(fakeFs).saveSession(
            MappingSession(0.0, listOf(
                MappingSession.SurfaceData(brainId.uuid, surface.name,
                    emptyList(), null, null, null
                )
            ), Matrix4(emptyArray()), null, notes = "Simulated pixels")
        )
        fakeFs.renameFile(mappingSessionPath, fakeFs.resolve("mapping/${model.name}/$mappingSessionPath"))
    }
}

class StubBeatSource : BeatSource {
    override fun getBeatData(): BeatData = BeatData(0.0, 0, confidence = 0f)
}

class StubSoundAnalyzer : SoundAnalyzer {
    override val frequencies = floatArrayOf()

    override fun listen(analysisListener: SoundAnalyzer.AnalysisListener) {
    }

    override fun unlisten(analysisListener: SoundAnalyzer.AnalysisListener) {
    }
}

fun FakeGlslContext.fakeProgram() = GlslProgram(this, Patch(emptyMap(), emptyList())) { null }
fun FakeGlslContext.fakeShader() = FakeShader(fakeProgram())

class FakeShader(override val glslProgram: GlslProgram) : IGlslShader