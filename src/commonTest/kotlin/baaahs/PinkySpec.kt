package baaahs

import baaahs.geom.Matrix4
import baaahs.gl.override
import baaahs.gl.render.ModelRenderer
import baaahs.mapper.MappingSession
import baaahs.mapper.Storage
import baaahs.model.Model
import baaahs.model.ModelInfo
import baaahs.models.SheepModel
import baaahs.net.FragmentingUdpLink
import baaahs.net.TestNetwork
import baaahs.plugin.Plugins
import baaahs.proto.BrainHelloMessage
import baaahs.proto.Type
import baaahs.show.SampleData
import baaahs.shows.FakeGlContext
import baaahs.sim.FakeDmxUniverse
import baaahs.sim.FakeFs
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Runnable
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.coroutines.CoroutineContext
import kotlin.test.expect

@InternalCoroutinesApi
object PinkySpec : Spek({
    describe("Pinky") {
        val fakeGlslContext by value { FakeGlContext() }
        val network by value { TestNetwork(1_000_000) }
        val clientAddress by value { TestNetwork.Address("client") }
        val clientPort = 1234

        val panel17 = SheepModel.Panel("17")
        val model = SheepModel().apply { panels = listOf(panel17); eyes = emptyList() } as Model<*>

        val fakeFs by value { FakeFs() }
        val pinky by value {
            Pinky(
                model,
                network,
                FakeDmxUniverse(),
                StubBeatSource(),
                FakeClock(),
                fakeFs,
                PermissiveFirmwareDaddy(),
                StubSoundAnalyzer(),
                modelRenderer = ModelRenderer(fakeGlslContext, ModelInfo.Empty),
                pinkyMainDispatcher = object : CoroutineDispatcher() {
                    override fun dispatch(context: CoroutineContext, block: Runnable) {
                        block.run()
                    }
                }
            )
        }
        val pinkyLink by value { network.links.only() }
        val surfaceManager by value { pinky.surfaceManager }
        val renderSurfaces by value { surfaceManager.getRenderSurfaces_ForTestOnly() }

        val panelMappings by value { emptyList<Pair<BrainId, Model.Surface>>() }

        beforeEachTest {
            pinky.switchTo(SampleData.sampleShow)

            doRunBlocking {
                panelMappings.forEach { (brainId, surface) ->
                    val surfaceData = MappingSession.SurfaceData(
                        brainId.uuid, surface.name, emptyList(), null, null, null
                    )
                    val mappingSessionPath = Storage(fakeFs, Plugins.safe()).saveSession(
                        MappingSession(
                            0.0, listOf(surfaceData),
                            Matrix4(emptyArray()), null, notes = "Simulated pixels"
                        )
                    )
                    fakeFs.renameFile(mappingSessionPath, fakeFs.resolve("mapping/${model.name}/$mappingSessionPath"))
                }

                pinky.launchStartupJobs()
            }
        }

        describe("brains reporting to Pinky") {
            val brainHelloMessage by value { nuffin<BrainHelloMessage>() }

            beforeEachTest {
                pinky.receive(clientAddress, clientPort, brainHelloMessage.toBytes())
                pinky.updateSurfaces()
                pinky.renderAndSendNextFrame()
            }

            describe("which are unmapped") {
                override(brainHelloMessage) { BrainHelloMessage("brain1", null) }

                it("should notify show of anonymous surface") {
                    val surface = renderSurfaces.keys.only()
                    expect(true) { surface is AnonymousSurface }
                }

                it("should send pixels but not not send mapping to the brain") {
                    val packetTypes = pinkyLink.packetsToSend.map { Type.get(it[FragmentingUdpLink.headerSize]) }
                    expect(listOf(Type.BRAIN_PANEL_SHADE)) { packetTypes } // Should send no mapping packet.
                }
            }

            context("which are mapped to surfaces") {
                override(panelMappings) { listOf(BrainId("brain1") to panel17) }

                context("for which the brain provides its mapping") {
                    override(brainHelloMessage) { BrainHelloMessage("brain1", panel17.name) }

                    it("should notify show") {
                        val surface = renderSurfaces.keys.only()
                        expect(true) { surface is IdentifiedSurface }
                        expect(panel17.name) { (surface as IdentifiedSurface).name }
                    }

                    it("should send pixels but not mapping to the brain") {
                        val packetTypes = pinkyLink.packetsToSend.map { Type.get(it[FragmentingUdpLink.headerSize]) }
                        expect(listOf(Type.BRAIN_PANEL_SHADE)) { packetTypes } // Should send no mapping packet.
                    }
                }

                context("for which Pinky has a mapping") {
                    override(brainHelloMessage) { BrainHelloMessage("brain1", null) }

                    it("should notify show") {
                        val packetTypes = pinkyLink.packetsToSend.map { Type.get(it[FragmentingUdpLink.headerSize]) }
                        expect(
                            listOf(Type.BRAIN_MAPPING, Type.BRAIN_PANEL_SHADE)
                        ) { packetTypes } // Should send a mapping packet.
                    }

                    it("should send mapping and pixels to the brain") {
                        val surface = renderSurfaces.keys.only()
                        expect(true) { surface is IdentifiedSurface }
                        expect(panel17.name) { (surface as IdentifiedSurface).name }
                    }

                    context("then when the brain re-sends its hello with its newfound mapping") {
                        it("should cause no changes") {
                            pinky.renderAndSendNextFrame()

                            pinky.receive(clientAddress, clientPort, BrainHelloMessage("brain1", panel17.name).toBytes())
                            pinky.updateSurfaces()
                            pinky.renderAndSendNextFrame()
                            pinky.renderAndSendNextFrame()
                            expect(1) { renderSurfaces.size }
                            expect(true) { renderSurfaces.keys.only() is IdentifiedSurface }
                            expect(panel17.name) { (renderSurfaces.keys.only() as IdentifiedSurface).name }
                        }
                    }

                    context("in the case of a brain race condition") {
                        it("should notify show") {
                            pinky.renderAndSendNextFrame()

                            // Remap to 17L...
                            pinky.receive(clientAddress, clientPort, BrainHelloMessage("brain1", panel17.name).toBytes())
                            // ... but a packet also made it through identifying brain1 as unmapped.
                            pinky.receive(clientAddress, clientPort, BrainHelloMessage("brain1", null).toBytes())
                            pinky.updateSurfaces()
                            pinky.renderAndSendNextFrame()
                            pinky.renderAndSendNextFrame()

                            // Pinky should have sent out another BrainMappingMessage message; todo: verify that!

                            pinky.receive(clientAddress, clientPort, BrainHelloMessage("brain1", panel17.name).toBytes())
                            pinky.updateSurfaces()
                            pinky.renderAndSendNextFrame()
                            pinky.renderAndSendNextFrame()

                            expect(1) { renderSurfaces.size }
                            expect(true) { (renderSurfaces.keys.only() as IdentifiedSurface).modelSurface == panel17 }

                            pinky.receive(clientAddress, clientPort, BrainHelloMessage("brain1", panel17.name).toBytes())
                            pinky.updateSurfaces()
                            pinky.renderAndSendNextFrame()
                            pinky.renderAndSendNextFrame()
                            expect(1) { renderSurfaces.size }
                            val surface = renderSurfaces.keys.only()
                            expect(true) { surface is IdentifiedSurface }
                            expect(panel17.name) { (surface as IdentifiedSurface).name }
                        }
                    }
                }
            }



        }
    }
})

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
