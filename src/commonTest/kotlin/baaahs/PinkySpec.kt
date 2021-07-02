package baaahs

import baaahs.dmx.DmxManager
import baaahs.geom.Matrix4
import baaahs.gl.override
import baaahs.gl.render.RenderManager
import baaahs.gl.testPlugins
import baaahs.mapper.MappingSession
import baaahs.mapper.Storage
import baaahs.model.Model
import baaahs.models.SheepModel
import baaahs.net.FragmentingUdpSocket
import baaahs.net.TestNetwork
import baaahs.plugin.beatlink.BeatData
import baaahs.plugin.beatlink.BeatSource
import baaahs.proto.BrainHelloMessage
import baaahs.proto.Ports
import baaahs.proto.Type
import baaahs.show.SampleData
import baaahs.shows.FakeGlContext
import baaahs.sim.FakeDmxUniverse
import baaahs.sim.FakeFs
import baaahs.sim.SimDmxDriver
import baaahs.ui.Observable
import ch.tutteli.atrium.api.fluent.en_GB.containsExactly
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.InternalCoroutinesApi
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

@Suppress("unused")
@InternalCoroutinesApi
object PinkySpec : Spek({
    describe("Pinky") {
        val fakeGlslContext by value { FakeGlContext() }
        val network by value { TestNetwork(1_000_000) }
        val clientAddress by value { TestNetwork.Address("client") }
        val clientPort = 1234

        val panel17 = SheepModel.Panel("17")
        val model = ModelForTest(listOf(panel17))

        val fakeFs by value { FakeFs() }
        val pinky by value {
            val link = network.link("pinky")
            val httpServer = link.startHttpServer(Ports.PINKY_UI_TCP)
            val pubSub = PubSub.Server(httpServer, CoroutineScope(ImmediateDispatcher))

            val fakeDmxUniverse = FakeDmxUniverse()
            Pinky(
                model,
                network,
                FakeClock(),
                fakeFs,
                PermissiveFirmwareDaddy(),
                renderManager = fakeGlslContext.runInContext { RenderManager(model) { fakeGlslContext } },
                plugins = testPlugins(),
                pinkyMainDispatcher = ImmediateDispatcher,
                link = link,
                httpServer = httpServer,
                pubSub = pubSub,
                dmxManager = DmxManager(SimDmxDriver(fakeDmxUniverse), pubSub, fakeDmxUniverse)
            )
        }
        val pinkyLink by value { network.links.only() }
        val fixtureManager by value { pinky.fixtureManager }
        val renderTargets by value { fixtureManager.getRenderTargets_ForTestOnly() }

        val panelMappings by value { emptyList<Pair<BrainId, Model.Surface>>() }

        beforeEachTest {
            pinky.switchTo(SampleData.sampleShow)

            doRunBlocking {
                panelMappings.forEach { (brainId, surface) ->
                    val surfaceData = MappingSession.SurfaceData(
                        BrainManager.controllerTypeName, brainId.uuid, surface.name, emptyList(), null, null, null
                    )
                    val mappingSessionPath = Storage(fakeFs, testPlugins()).saveSession(
                        MappingSession(
                            0.0, listOf(surfaceData),
                            Matrix4(doubleArrayOf()), null, notes = "Simulated pixels"
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
                pinky.updateFixtures()
                pinky.renderAndSendNextFrame()
            }

            describe("which are unmapped") {
                override(brainHelloMessage) { BrainHelloMessage("brain1", null) }

                it("should notify show of anonymous surface") {
                    val fixture = renderTargets.keys.only()
                    expect(fixture.modelEntity).toBe(null)
                }

                it("should send pixels but not not send mapping to the brain") {
                    val packetTypes = pinkyLink.packetsToSend.map { Type.get(it[FragmentingUdpSocket.headerSize]) }
                    expect(packetTypes)
                        .containsExactly(Type.BRAIN_PANEL_SHADE) // Should send no mapping packet.
                }
            }

            context("which are mapped to surfaces") {
                override(panelMappings) { listOf(BrainId("brain1") to panel17) }

                context("for which the brain provides its mapping") {
                    override(brainHelloMessage) { BrainHelloMessage("brain1", panel17.name) }

                    it("should notify show") {
                        val fixture = renderTargets.keys.only()
                        expect(fixture.modelEntity).toBe(panel17)
                        expect(fixture.name).toBe(panel17.name)
                    }

                    it("should send pixels but not mapping to the brain") {
                        val packetTypes = pinkyLink.packetsToSend.map { Type.get(it[FragmentingUdpSocket.headerSize]) }
                        expect(packetTypes)
                            .containsExactly(Type.BRAIN_PANEL_SHADE) // Should send no mapping packet.
                    }
                }

                context("for which Pinky has a mapping") {
                    override(brainHelloMessage) { BrainHelloMessage("brain1", null) }

                    it("should notify show") {
                        val packetTypes = pinkyLink.packetsToSend.map { Type.get(it[FragmentingUdpSocket.headerSize]) }
                        expect(packetTypes)
                            .containsExactly(Type.BRAIN_MAPPING,Type.BRAIN_PANEL_SHADE) // Should send a mapping packet.
                    }

                    it("should send mapping and pixels to the brain") {
                        val fixture = renderTargets.keys.only()
                        expect(fixture.modelEntity).toBe(panel17)
                    }

                    context("then when the brain re-sends its hello with its newfound mapping") {
                        it("should cause no changes") {
                            pinky.renderAndSendNextFrame()

                            pinky.receive(clientAddress, clientPort, BrainHelloMessage("brain1", panel17.name).toBytes())
                            pinky.updateFixtures()
                            pinky.renderAndSendNextFrame()
                            pinky.renderAndSendNextFrame()
                            expect(renderTargets.size).toBe(1)
                            val fixture = renderTargets.keys.only()
                            expect(fixture.modelEntity).toBe(panel17)
                        }
                    }

                    context("in the case of a brain race condition") {
                        it("should notify show") {
                            pinky.renderAndSendNextFrame()

                            // Remap to 17L...
                            pinky.receive(clientAddress, clientPort, BrainHelloMessage("brain1", panel17.name).toBytes())
                            // ... but a packet also made it through identifying brain1 as unmapped.
                            pinky.receive(clientAddress, clientPort, BrainHelloMessage("brain1", null).toBytes())
                            pinky.updateFixtures()
                            pinky.renderAndSendNextFrame()
                            pinky.renderAndSendNextFrame()

                            // Pinky should have sent out another BrainMappingMessage message; todo: verify that!

                            pinky.receive(clientAddress, clientPort, BrainHelloMessage("brain1", panel17.name).toBytes())
                            pinky.updateFixtures()
                            pinky.renderAndSendNextFrame()
                            pinky.renderAndSendNextFrame()

                            expect(renderTargets.size).toBe(1)
                            expect(renderTargets.keys.only().modelEntity).toBe(panel17)

                            pinky.receive(clientAddress, clientPort, BrainHelloMessage("brain1", panel17.name).toBytes())
                            pinky.updateFixtures()
                            pinky.renderAndSendNextFrame()
                            pinky.renderAndSendNextFrame()
                            expect(renderTargets.size).toBe(1)
                            val fixture = renderTargets.keys.only()
                            expect(fixture.modelEntity).toBe(panel17)
                        }
                    }
                }
            }



        }
    }
})

class StubBeatSource : Observable(), BeatSource {
    override fun getBeatData(): BeatData = BeatData(0.0, 0, confidence = 0f)
}

class StubSoundAnalyzer : SoundAnalyzer {
    override val frequencies = floatArrayOf()

    override fun listen(analysisListener: SoundAnalyzer.AnalysisListener) {
    }

    override fun unlisten(analysisListener: SoundAnalyzer.AnalysisListener) {
    }
}
