package baaahs

import baaahs.controller.ControllersManager
import baaahs.dmx.Dmx
import baaahs.dmx.DmxManager
import baaahs.fixtures.FixtureManager
import baaahs.geom.Matrix4
import baaahs.gl.RootToolchain
import baaahs.gl.override
import baaahs.gl.render.RenderManager
import baaahs.gl.testPlugins
import baaahs.libraries.ShaderLibraryManager
import baaahs.mapper.MappingSession
import baaahs.mapper.Storage
import baaahs.mapping.MappingManagerImpl
import baaahs.model.Model
import baaahs.models.SheepModel
import baaahs.net.FragmentingUdpSocket
import baaahs.net.Network
import baaahs.net.TestNetwork
import baaahs.plugin.beatlink.BeatData
import baaahs.plugin.beatlink.BeatSource
import baaahs.proto.BrainHelloMessage
import baaahs.proto.Ports
import baaahs.proto.Type
import baaahs.scene.SceneManager
import baaahs.show.SampleData
import baaahs.shows.FakeGlContext
import baaahs.sim.FakeDmxUniverse
import baaahs.sim.FakeFs
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
        val plugins by value { testPlugins() }
        val storage by value { Storage(fakeFs, plugins) }
        val link by value { network.link("pinky") }
        val pinky by value {
            val httpServer = link.startHttpServer(Ports.PINKY_UI_TCP)
            val pubSub = PubSub.Server(httpServer, CoroutineScope(ImmediateDispatcher))
            val renderManager = fakeGlslContext.runInContext { RenderManager(model) { fakeGlslContext } }
            val fakeDmxUniverse = FakeDmxUniverse()
            val toolchain = RootToolchain(plugins)
            val fixtureManager = FixtureManager(renderManager, plugins)
            val clock = FakeClock()
            val gadgetManager = GadgetManager(pubSub, clock, ImmediateDispatcher)
            val dmxManager = object : DmxManager {
                override fun allOff() = TODO("not implemented")

                override val dmxUniverse: Dmx.Universe get() = fakeDmxUniverse
            }

            val brainManager = BrainManager(
                PermissiveFirmwareDaddy(), link, Pinky.NetworkStats(), clock, pubSub, ImmediateDispatcher
            )
            val mappingManager = MappingManagerImpl(storage, model)
            val controllersManager = ControllersManager(listOf(brainManager), mappingManager, model, fixtureManager)
            val stageManager = StageManager(toolchain, renderManager, pubSub, storage, fixtureManager, clock, model,
                gadgetManager, controllersManager)
            val sceneManager = SceneManager(storage, controllersManager)
            Pinky(
                clock,
                PermissiveFirmwareDaddy(),
                plugins,
                storage,
                link,
                httpServer,
                pubSub,
                dmxManager,
                mappingManager,
                fixtureManager,
                ImmediateDispatcher,
                toolchain,
                stageManager,
                sceneManager,
                controllersManager,
                brainManager,
                ShaderLibraryManager(storage, pubSub),
                Pinky.NetworkStats(),
                PinkySettings()
            )
        }
        val pinkyLink by value { network.links.only() }
        val fixtureManager by value { pinky.fixtureManager }
        val renderTargets by value { fixtureManager.getRenderTargets_ForTestOnly() }

        val panelMappings by value { emptyList<Pair<BrainId, Model.Surface>>() }
        val pinkyUdpReceive: ((Network.Address, Int, ByteArray) -> Unit) by value {
            val pinkyUdp = link.udpListeners[Ports.PINKY] as FragmentingUdpSocket
            { fromAddress, port, bytes ->
                pinkyUdp.receiveBypassingFragmentation(fromAddress, port, bytes)
            }
        }

        beforeEachTest {
            pinky.switchTo(SampleData.sampleShow)

            doRunBlocking {
                panelMappings.forEach { (brainId, surface) ->
                    val surfaceData = MappingSession.SurfaceData(
                        BrainManager.controllerTypeName, brainId.uuid, surface.name, null, null, null
                    )
                    val mappingSessionPath = storage.saveSession(
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
                pinkyUdpReceive(clientAddress, clientPort, brainHelloMessage.toBytes())
                pinky.updateFixtures()
                doRunBlocking { pinky.renderAndSendNextFrame() }
            }

            describe("which are unmapped") {
                override(brainHelloMessage) { BrainHelloMessage("brain1", null) }

                it("should notify show of anonymous surface") {
                    val fixture = renderTargets.keys.only()
                    expect(fixture.modelEntity).toBe(null)
                }

                it("should send pixels but not not send mapping to the brain") {
                    val packetTypes = pinkyLink.packetsToSend.map { Type.get(it.data[FragmentingUdpSocket.headerSize]) }
                    expect(packetTypes)
                        .containsExactly(Type.BRAIN_PANEL_SHADE) // Should send no mapping packet.
                }
            }

            context("which are mapped to surfaces") {
                override(panelMappings) { listOf(BrainId("brain1") to panel17) }

                context("for which Pinky has a mapping") {
                    override(brainHelloMessage) { BrainHelloMessage("brain1", null) }

                    it("should notify show") {
                        val packetTypes = pinkyLink.packetsToSend.map { Type.get(it.data[FragmentingUdpSocket.headerSize]) }
                        expect(packetTypes).containsExactly(Type.BRAIN_PANEL_SHADE)
                    }

                    it("should send pixels to the brain") {
                        println(pinky)
                        val fixture = renderTargets.keys.only()
                        expect(fixture.modelEntity).toBe(panel17)
                    }

                    context("then when the brain re-sends its hello with its newfound mapping") {
                        it("should cause no changes") {
                            doRunBlocking { pinky.renderAndSendNextFrame() }

                            pinkyUdpReceive(clientAddress, clientPort, BrainHelloMessage("brain1", panel17.name).toBytes())
                            pinky.updateFixtures()
                            doRunBlocking { pinky.renderAndSendNextFrame() }
                            doRunBlocking { pinky.renderAndSendNextFrame() }
                            expect(renderTargets.size).toBe(1)
                            val fixture = renderTargets.keys.only()
                            expect(fixture.modelEntity).toBe(panel17)
                        }
                    }

                    context("in the case of a brain race condition") {
                        it("should notify show") {
                            doRunBlocking { pinky.renderAndSendNextFrame() }

                            // Remap to 17L...
                            pinkyUdpReceive(clientAddress, clientPort, BrainHelloMessage("brain1", panel17.name).toBytes())
                            // ... but a packet also made it through identifying brain1 as unmapped.
                            pinkyUdpReceive(clientAddress, clientPort, BrainHelloMessage("brain1", null).toBytes())
                            pinky.updateFixtures()
                            doRunBlocking { pinky.renderAndSendNextFrame() }
                            doRunBlocking { pinky.renderAndSendNextFrame() }

                            // Pinky should have sent out another BrainMappingMessage message; todo: verify that!

                            pinkyUdpReceive(clientAddress, clientPort, BrainHelloMessage("brain1", panel17.name).toBytes())
                            pinky.updateFixtures()
                            doRunBlocking { pinky.renderAndSendNextFrame() }
                            doRunBlocking { pinky.renderAndSendNextFrame() }

                            expect(renderTargets.size).toBe(1)
                            expect(renderTargets.keys.only().modelEntity).toBe(panel17)

                            pinkyUdpReceive(clientAddress, clientPort, BrainHelloMessage("brain1", panel17.name).toBytes())
                            pinky.updateFixtures()
                            doRunBlocking { pinky.renderAndSendNextFrame() }
                            doRunBlocking { pinky.renderAndSendNextFrame() }
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

private fun Pinky.updateFixtures() {
//    fixtureManager.maybeUpdateRenderPlans()
}

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
