package baaahs

import baaahs.glshaders.GlslProgram
import baaahs.glshaders.Patch
import baaahs.glsl.GlslRenderer
import baaahs.glsl.GlslRendererTest
import baaahs.net.FragmentingUdpLink
import baaahs.net.Network
import baaahs.net.TestNetwork
import baaahs.proto.BrainHelloMessage
import baaahs.proto.Type
import baaahs.shaders.IGlslShader
import baaahs.shows.FakeGlslContext
import baaahs.sim.FakeDmxUniverse
import baaahs.sim.FakeFs
import kotlinx.coroutines.InternalCoroutinesApi
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.expect

@InternalCoroutinesApi
class PinkyTest {
    private lateinit var network: TestNetwork
    private lateinit var clientAddress: Network.Address
    private val clientPort = 1234

    private val panel17 = SheepModel.Panel("17")
    private val model = SheepModel().apply { panels = listOf(panel17); eyes = emptyList() } as Model<*>
    private lateinit var testShow1: TestShow1
    private lateinit var pinky: Pinky
    private lateinit var pinkyLink: TestNetwork.Link

    @BeforeTest
    fun setUp() {
        network = TestNetwork(1_000_000)
        clientAddress = TestNetwork.Address("client")
        testShow1 = TestShow1()
        pinky = Pinky(
            model,
            listOf(testShow1),
            network,
            FakeDmxUniverse(),
            StubBeatSource(),
            FakeClock(),
            FakeFs(),
            PermissiveFirmwareDaddy(),
            StubPinkyDisplay(),
            StubSoundAnalyzer(),
            glslRenderer = GlslRenderer(FakeGlslContext(), GlslRendererTest.UvTranslatorForTest)
        )
        pinkyLink = network.links.only()
    }

    @Test
    fun whenUnmappedBrainUnknownToPinkyComesOnline_showShouldBeNotified() {
        pinky.receive(clientAddress, clientPort, BrainHelloMessage("brain1", null).toBytes())
        pinky.updateSurfaces()
        pinky.drawNextFrame()

        val show = testShow1.createdShows.only()
        expect(1) { show.shaderBuffers.size }

        val packetTypes = pinkyLink.packetsToSend.map { Type.get(it[FragmentingUdpLink.headerSize]) }
        expect(listOf(Type.BRAIN_PANEL_SHADE)) { packetTypes } // Should send no mapping packet.
    }

    @Test
    fun whenUnmappedBrainKnownToPinkyComesOnline_showShouldBeNotifiedAndBrainShouldReceiveMapping() {
        pinky.providePanelMapping_CHEAT(BrainId("brain1"), panel17)

        pinky.receive(clientAddress, clientPort, BrainHelloMessage("brain1", null).toBytes())
        pinky.updateSurfaces()
        pinky.drawNextFrame()

        val show = testShow1.createdShows.only()
        expect(1) { show.shaderBuffers.size }
        expect(true) { show.shaderBuffers.keys.only() is IdentifiedSurface }
        expect(panel17.name) { (show.shaderBuffers.keys.only() as IdentifiedSurface).name }

        val packetTypes = pinkyLink.packetsToSend.map { Type.get(it[FragmentingUdpLink.headerSize]) }
        expect(listOf(Type.BRAIN_MAPPING, Type.BRAIN_PANEL_SHADE)) { packetTypes } // Should send a mapping packet.
    }

    @Test
    fun whenPinkySideMappedBrainComesOnline_showShouldBeNotified() {
        pinky.providePanelMapping_CHEAT(BrainId("brain1"), panel17)

        pinky.receive(clientAddress, clientPort, BrainHelloMessage("brain1", null).toBytes())
        pinky.updateSurfaces()
        pinky.drawNextFrame()

        val show = testShow1.createdShows.only()
        expect(1) { show.shaderBuffers.size }
        val packetTypes = pinkyLink.packetsToSend.map { Type.get(it[FragmentingUdpLink.headerSize]) }
        expect(listOf(Type.BRAIN_MAPPING, Type.BRAIN_PANEL_SHADE)) { packetTypes } // Should send a mapping packet.
    }

    @Test
    fun whenBrainSideMappedBrainComesOnline_showShouldBeNotified() {
        pinky.providePanelMapping_CHEAT(BrainId("brain1"), panel17)

        pinky.receive(clientAddress, clientPort, BrainHelloMessage("brain1", panel17.name).toBytes())
        pinky.updateSurfaces()
        pinky.drawNextFrame()

        val show = testShow1.createdShows.only()
        expect(1) { show.shaderBuffers.size }
        val packetTypes = pinkyLink.packetsToSend.map { Type.get(it[FragmentingUdpLink.headerSize]) }
        expect(listOf(Type.BRAIN_PANEL_SHADE)) { packetTypes } // Should send no mapping packet.
    }

    @Test
    fun asBrainsComeOnlineAndAreMapped_showShouldBeNotified() {
        pinky.providePanelMapping_CHEAT(BrainId("brain1"), panel17)

        pinky.receive(clientAddress, clientPort, BrainHelloMessage("brain1", null).toBytes())
        pinky.updateSurfaces()
        pinky.drawNextFrame()
        pinky.drawNextFrame()

        val show = testShow1.createdShows.only()
        expect(1) { show.shaderBuffers.size }
        expect(true) { show.shaderBuffers.keys.only() is IdentifiedSurface }

        pinky.receive(clientAddress, clientPort, BrainHelloMessage("brain1", panel17.name).toBytes())
        pinky.updateSurfaces()
        pinky.drawNextFrame()
        pinky.drawNextFrame()
        expect(1) { show.shaderBuffers.size }
        val surface = show.shaderBuffers.keys.only()
        expect(true) { surface is IdentifiedSurface }
        expect(panel17.name) { (surface as IdentifiedSurface).name }
    }

    @Test
    fun whenBrainHelloRaceCondition_asBrainsComeOnline_showShouldBeNotified() {
        pinky.receive(clientAddress, clientPort, BrainHelloMessage("brain1", null).toBytes())
        pinky.updateSurfaces()
        pinky.drawNextFrame()
        pinky.drawNextFrame()
        val show = testShow1.createdShows.only()

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

        expect(1) { show.shaderBuffers.size }
        expect(true) { (show.shaderBuffers.keys.only() as IdentifiedSurface).modelSurface == panel17 }

        pinky.receive(clientAddress, clientPort, BrainHelloMessage("brain1", panel17.name).toBytes())
        pinky.updateSurfaces()
        pinky.drawNextFrame()
        pinky.drawNextFrame()
        expect(1) { show.shaderBuffers.size }
        val surface = show.shaderBuffers.keys.only()
        expect(true) { surface is IdentifiedSurface }
        expect(panel17.name) { (surface as IdentifiedSurface).name }
    }

    class TestShow1(var supportsSurfaceChange: Boolean = true) : Show("TestShow1") {
        val createdShows = mutableListOf<ShowRenderer>()
        val solidShader = FakeShader()

        override fun createRenderer(model: Model<*>, showContext: ShowContext): Renderer {
            return ShowRenderer(showContext).also { createdShows.add(it) }
        }

        inner class ShowRenderer(private val showContext: ShowContext) : Renderer {
            val shaderBuffers =
                showContext.allSurfaces.associateWith { showContext.getShaderBuffer(it, solidShader) }.toMutableMap()

            override fun nextFrame() {
                shaderBuffers.values.forEach {
//                TODO    it.color = Color.WHITE
                }
            }

            override fun surfacesChanged(newSurfaces: List<Surface>, removedSurfaces: List<Surface>) {
                if (!supportsSurfaceChange) {
                    super.surfacesChanged(newSurfaces, removedSurfaces)
                } else {
                    removedSurfaces.forEach { shaderBuffers.remove(it) }
                    newSurfaces.forEach { shaderBuffers[it] = showContext.getShaderBuffer(it, solidShader) }
                }
            }
        }
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

class FakeShader(
    override val glslProgram: GlslProgram =
        GlslProgram(FakeGlslContext(), Patch(emptyMap(), emptyList()))
            .apply { bind { uniformPortRef -> null } }
) : IGlslShader {
}