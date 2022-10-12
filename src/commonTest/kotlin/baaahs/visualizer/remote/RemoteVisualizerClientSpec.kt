package baaahs.visualizer.remote

import baaahs.*
import baaahs.client.Notifier
import baaahs.client.document.FakeFileDialog
import baaahs.client.document.SceneManager
import baaahs.device.PixelFormat
import baaahs.fixtures.FakeFixtureManager
import baaahs.fixtures.NullTransport
import baaahs.fixtures.PixelArrayFixture
import baaahs.geom.Vector3F
import baaahs.gl.testPlugins
import baaahs.io.PubSubRemoteFsClientBackend
import baaahs.model.FakeModelEntity
import baaahs.model.ModelUnit
import baaahs.scene.OpenScene
import baaahs.scene.SceneMonitor
import baaahs.sim.FakeNetwork
import baaahs.sim.SimulationEnv
import baaahs.sm.brain.proto.Ports
import baaahs.visualizer.FakeItemVisualizer
import baaahs.visualizer.FakeVisualizer
import ch.tutteli.atrium.api.fluent.en_GB.containsExactly
import ch.tutteli.atrium.api.verbs.expect
import ext.kotlinx_coroutines_test.TestCoroutineScope
import kotlinx.coroutines.InternalCoroutinesApi
import org.spekframework.spek2.Spek

@Suppress("unused")
@OptIn(InternalCoroutinesApi::class)
object RemoteVisualizerClientSpec : Spek({
    describe<RemoteVisualizerClient> {
        val pubSubRig by value { TestRig() }
        val dispatcher by value { pubSubRig.dispatcher }
        val coroutineScope by value { TestCoroutineScope(dispatcher) }
        val network by value { FakeNetwork(coroutineScope = coroutineScope) }
        val serverLink by value { network.link("server") }
        val clientLink by value { network.link("server") }
        val fakeFixtureManager by value { FakeFixtureManager() }
        val plugins by value { testPlugins() }
        val entity by value { FakeModelEntity("fake entity") }
        val sceneMonitor by value { SceneMonitor(OpenScene(modelForTest(entity))) }
        val sceneManager by value {
            SceneManager(
                pubSubRig.client1, PubSubRemoteFsClientBackend(pubSubRig.client1),
                plugins, Notifier(pubSubRig.client1), FakeFileDialog(), sceneMonitor
            )
        }
        val server by value {
            RemoteVisualizerServer(fakeFixtureManager, testPlugins()).also { server ->
                serverLink.startHttpServer(Ports.PINKY_UI_TCP)
                    .listenWebSocket("/ws/visualizer") { server }
            }
        }
        val fakeVisualizer by value { FakeVisualizer(ModelUnit.Centimeters) }
        val client by value {
            RemoteVisualizerClient(
                clientLink, serverLink.myAddress,
                fakeVisualizer, sceneManager, sceneMonitor,
                SimulationEnv { }, testPlugins()
            )
        }

        beforeEachTest {
            server.run {}
            client.run {}
            dispatcher.advanceUntilIdle()

            fakeFixtureManager.sendFixtureInfo(
                PixelArrayFixture(entity, 2, transport = NullTransport,
                    pixelLocations = listOf(Vector3F.origin, Vector3F.unit3d)
                )
            )
            dispatcher.advanceUntilIdle()
        }

        afterEachTest { coroutineScope.cleanupTestCoroutines() }

        it("should populate the visualizer with model entities") {
            expect(fakeVisualizer.itemVisualizers).containsExactly(
                FakeItemVisualizer(entity)
            )
        }

        context("receiving pixel array fixture data") {
            it("updates the visualizer with remote config data") {
                val visualizer = fakeVisualizer.itemVisualizers.only() as FakeItemVisualizer
                expect(visualizer.remoteConfig.pixelCount).toEqual(2)
                expect(visualizer.remoteConfig.pixelLocations).containsExactly(Vector3F.origin, Vector3F.unit3d)
            }
        }

        context("receiving pixel array frame data") {
            beforeEachTest {
                fakeFixtureManager.sendFrameData(entity) { out ->
                    out.writeInt(2)
                    PixelFormat.RGB8.writeColor(Color.MAGENTA, out)
                    PixelFormat.RGB8.writeColor(Color.GREEN, out)
                }
                dispatcher.advanceUntilIdle()
            }

            it("updates the visualizer with pixel colors") {
                val visualizer = fakeVisualizer.itemVisualizers.only() as FakeItemVisualizer
                expect(visualizer.pixelColors).containsExactly(Color.MAGENTA, Color.GREEN)
            }
        }
    }
})