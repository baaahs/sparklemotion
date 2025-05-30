package baaahs

import baaahs.app.settings.FeatureFlags
import baaahs.app.settings.ObservableProvider
import baaahs.client.EventManager
import baaahs.controller.ControllersManager
import baaahs.controller.generify
import baaahs.dmx.Dmx
import baaahs.dmx.DmxManager
import baaahs.fixtures.FixtureManagerImpl
import baaahs.geom.Matrix4F
import baaahs.geom.identity
import baaahs.gl.RootToolchain
import baaahs.gl.override
import baaahs.gl.render.RenderManager
import baaahs.gl.testPlugins
import baaahs.io.FsServerSideSerializer
import baaahs.io.ResourcesFs
import baaahs.kotest.value
import baaahs.libraries.ShaderLibraryManager
import baaahs.mapper.MappingSession
import baaahs.mapper.MappingStore
import baaahs.mapper.PinkyMapperHandlers
import baaahs.mapping.MappingManagerImpl
import baaahs.model.Model
import baaahs.net.FragmentingUdpSocket
import baaahs.net.Network
import baaahs.net.TestNetwork
import baaahs.plugin.midi.MidiManager
import baaahs.scene.OpenScene
import baaahs.scene.SceneMonitor
import baaahs.show.SampleData
import baaahs.show.ShowMonitor
import baaahs.shows.FakeGlContext
import baaahs.sim.FakeDmxUniverse
import baaahs.sim.FakeFs
import baaahs.sm.brain.BrainId
import baaahs.sm.brain.BrainManager
import baaahs.sm.brain.PermissiveFirmwareDaddy
import baaahs.sm.brain.proto.BrainHelloMessage
import baaahs.sm.brain.proto.Ports
import baaahs.sm.brain.proto.Type
import baaahs.sm.server.GadgetManager
import baaahs.sm.server.PinkyConfigStore
import baaahs.sm.server.ServerNotices
import baaahs.sm.server.StageManager
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch

@Suppress("unused")
@InternalCoroutinesApi
class PinkySpec : DescribeSpec({
    describe("Pinky").config(coroutineTestScope = true) {
        val fakeGlslContext by value { FakeGlContext() }
        val network by value { TestNetwork(1_000_000) }
        val clientAddress by value { TestNetwork.Address("client") }
        val clientPort = 1234

        val panel17 by value { testModelSurface("17") }
        val model by value { modelForTest(listOf(panel17)) }

        val fakeFs by value { FakeFs() }
        val plugins by value { testPlugins() }
        val mappingStore by value { MappingStore(fakeFs.rootFile, plugins, FakeClock()) }
        val link by value { network.link("pinky") }
        val renderManager by value { fakeGlslContext.runInContext { RenderManager(fakeGlslContext) } }
        val fixtureManager by value { FixtureManagerImpl(renderManager, plugins) }
        val toolchain by value { RootToolchain(plugins) }
        val httpServer by value { link.createHttpServer(Ports.PINKY_UI_TCP) }
        val coroutineScope by value { CoroutineScope(ImmediateDispatcher) }
        val pubSub by value { PubSub.Server(httpServer, coroutineScope) }
        val clock by value { FakeClock() }
        val gadgetManager by value { GadgetManager(pubSub, clock, CoroutineScope(ImmediateDispatcher)) }
        val brainManager by value {
            BrainManager(PermissiveFirmwareDaddy(), link, Pinky.NetworkStats(), clock, CoroutineScope(ImmediateDispatcher))
        }
        val serverNotices by value { ServerNotices(pubSub, ImmediateDispatcher) }
        val sceneMonitor by value { SceneMonitor(OpenScene(model)) }
        val showMonitor by value { ShowMonitor() }
        val eventManager by value { EventManager(MidiManager(emptyList()), showMonitor, FakeClock()) }
        val stageManager by value {
            StageManager(
                toolchain, renderManager, pubSub, fakeFs.rootFile, fixtureManager, clock,
                gadgetManager, serverNotices, sceneMonitor, FsServerSideSerializer(),
                PinkyConfigStore(plugins, fakeFs.rootFile), showMonitor, ObservableProvider(FeatureFlags.JVM)
            )
        }
        val mappingManager by value { MappingManagerImpl(mappingStore, sceneMonitor, coroutineScope) }
        val controllersManager by value {
            ControllersManager(listOf(generify(brainManager)), mappingManager, sceneMonitor, listOf(fixtureManager), pubSub, plugins)
        }

        val renderAndSendFrame by value {
            { doRunBlocking { stageManager.renderAndSendNextFrame(true) } }
        }
        
        val pinky by value {
            val fakeDmxUniverse = FakeDmxUniverse()
            val dmxManager = object : DmxManager {
                override fun allOff() = error("not implemented")

                override val dmxUniverse: Dmx.Universe get() = fakeDmxUniverse
            }

            Pinky(
                clock, PermissiveFirmwareDaddy(), plugins, fakeFs.rootFile, link, httpServer, pubSub,
                dmxManager, mappingManager, fixtureManager, CoroutineScope(ImmediateDispatcher), toolchain,
                stageManager, controllersManager, brainManager,
                ShaderLibraryManager(plugins, fakeFs, FsServerSideSerializer(), pubSub, toolchain),
                Pinky.NetworkStats(), PinkySettings(), serverNotices, PinkyMapperHandlers(mappingStore),
                PinkyConfigStore(plugins, fakeFs.rootFile), eventManager,
                ObservableProvider(FeatureFlags.JVM), ResourcesFs()
            )
        }
        val pinkyLink by value { network.links.only() }
        val renderTargets by value { fixtureManager.getRenderTargets_ForTestOnly() }

        val panelMappings by value { emptyList<Pair<BrainId, Model.Surface>>() }
        val pinkyUdpReceive: ((Network.Address, Int, ByteArray) -> Unit) by value {
            val pinkyUdp = link.udpListeners[Ports.PINKY] as FragmentingUdpSocket
            { fromAddress, port, bytes ->
                CoroutineScope(ImmediateDispatcher).launch {
                    pinkyUdp.receiveBypassingFragmentation(fromAddress, port, bytes)
                }
            }
        }

        beforeEach {
            pinky.switchTo(SampleData.sampleShow)

            val startup = CoroutineScope(testCoroutineScheduler).launch {
                panelMappings.forEach { (brainId, surface) ->
                    val surfaceData = MappingSession.SurfaceData(
                        BrainManager.controllerTypeName, brainId.uuid, surface.name, null, null
                    )
                    val mappingSessionPath = mappingStore.saveSession(
                        MappingSession(
                            clock.now(), listOf(surfaceData),
                            Matrix4F.identity, null, savedAt = clock.now(),
                            notes = "Simulated pixels"
                        )
                    )
                    fakeFs.renameFile(mappingSessionPath, fakeFs.resolve("mapping/${model.name}/$mappingSessionPath"))
                }

                with(pinky) { launch { launchStartupJobs() } }
            }

            testCoroutineScheduler.runCurrent()
            startup.join()
        }

        describe("brains reporting to Pinky") {
            val brainHelloMessage by value { nuffin<BrainHelloMessage>() }

            beforeEach {
                pinkyUdpReceive(clientAddress, clientPort, brainHelloMessage.toBytes())
                pinky.updateFixtures()
                renderAndSendFrame()
            }

            describe("which are unmapped") {
                override(brainHelloMessage) { BrainHelloMessage("brain1", null) }

                it("should notify show of anonymous surface") {
                    val fixture = renderTargets.keys.only()
                    fixture.modelEntity.shouldBe(null)
                }

                it("should send pixels but not not send mapping to the brain") {
                    val packetTypes = pinkyLink.packetsToSend.map { Type.get(it.data[FragmentingUdpSocket.headerSize]) }
                    packetTypes
                        .shouldContainExactly(Type.BRAIN_PANEL_SHADE) // Should send no mapping packet.
                }
            }

            context("which are mapped to surfaces") {
                override(panelMappings) { listOf(BrainId("brain1") to panel17) }

                context("for which Pinky has a mapping") {
                    override(brainHelloMessage) {
                        BrainHelloMessage("brain1", null)
                    }

                    it("should send pixels to the brain") {
                        val packetTypes = pinkyLink.packetsToSend.map { Type.get(it.data[FragmentingUdpSocket.headerSize]) }
                        packetTypes.shouldContainExactly(Type.BRAIN_PANEL_SHADE)
                    }

                    context("then when the brain re-sends its hello with its newfound mapping") {
                        it("should cause no changes") {
                            renderAndSendFrame()

                            pinkyUdpReceive(clientAddress, clientPort, BrainHelloMessage("brain1", panel17.title).toBytes())
                            pinky.updateFixtures()
                            renderAndSendFrame()
                            renderAndSendFrame()
                            renderTargets.size.shouldBe(1)
                            val fixture = renderTargets.keys.only()
                            fixture.modelEntity.shouldBe(panel17)
                        }
                    }

                    context("in the case of a brain race condition") {
                        it("should notify show") {
                            renderAndSendFrame()

                            // Remap to 17L...
                            pinkyUdpReceive(clientAddress, clientPort, BrainHelloMessage("brain1", panel17.title).toBytes())
                            // ... but a packet also made it through identifying brain1 as unmapped.
                            pinkyUdpReceive(clientAddress, clientPort, BrainHelloMessage("brain1", null).toBytes())
                            pinky.updateFixtures()
                            renderAndSendFrame()
                            renderAndSendFrame()

                            // Pinky should have sent out another BrainMappingMessage message; todo: verify that!

                            pinkyUdpReceive(clientAddress, clientPort, BrainHelloMessage("brain1", panel17.title).toBytes())
                            pinky.updateFixtures()
                            renderAndSendFrame()
                            renderAndSendFrame()

                            renderTargets.size.shouldBe(1)
                            renderTargets.keys.only().modelEntity.shouldBe(panel17)

                            pinkyUdpReceive(clientAddress, clientPort, BrainHelloMessage("brain1", panel17.title).toBytes())
                            pinky.updateFixtures()
                            renderAndSendFrame()
                            renderAndSendFrame()
                            renderTargets.size.shouldBe(1)
                            val fixture = renderTargets.keys.only()
                            fixture.modelEntity.shouldBe(panel17)
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