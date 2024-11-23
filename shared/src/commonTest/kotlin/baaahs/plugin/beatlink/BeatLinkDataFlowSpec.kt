package baaahs.plugin.beatlink

import baaahs.FakeClock
import baaahs.TestRig
import baaahs.describe
import baaahs.gl.shader.InputPort
import baaahs.kotest.value
import baaahs.plugin.ClientPlugins
import baaahs.plugin.PluginContext
import baaahs.plugin.PluginRef
import baaahs.plugin.ServerPlugins
import baaahs.shows.FakeGlContext
import baaahs.shows.FakeGlslProgram
import baaahs.shows.FakeShowPlayer
import baaahs.sm.server.PinkyArgs
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

@OptIn(InternalCoroutinesApi::class, ExperimentalCoroutinesApi::class)
class BeatLinkDataFlowSpec : DescribeSpec({
    describe<BeatLinkPlugin> {
        context("data flow") {
            val testRig by value { TestRig() }
            val clock by value { FakeClock(2.0) }
            val fakeBeatSource by value { FakeBeatSource() }
            val serverContext by value { PluginContext(clock, testRig.server) }
            val serverPlugin by value {
                BeatLinkPlugin.openForServer(serverContext, fakeBeatSource)
            }
            val serverPlugins by value { ServerPlugins(listOf(serverPlugin), serverContext, PinkyArgs.defaults) }
            val clientContext by value { PluginContext(clock, testRig.client1) }
            val clientPlugin by value { BeatLinkPlugin.openForClient(clientContext) }
            val clientPlugins by value { ClientPlugins(listOf(clientPlugin), clientContext) }
            val inputPort by value {
                val pluginRef = PluginRef.from("baaahs.BeatLink:BeatInfo",)
                InputPort("beatLink", BeatInfoFeed.contentType, pluginRef = pluginRef)
            }
            val fakeGlslProgram by value { FakeGlslProgram() }

            beforeEach {
                testRig.server.run {}
                testRig.serverConnections.run {}
                testRig.client1.run {}
                serverPlugin.run {}
                clientPlugin.run {}
                fakeBeatSource.setBeatData(BeatData(1.0, 500))
                testRig.fakePinkyDispatcher.advanceUntilIdle()
            }

            it("flows from beat source to server feed") {
                val serverFeed = serverPlugins.resolveFeed(inputPort)
                val programFeed = serverFeed.open(FakeShowPlayer(), "beatInfo")
                    .bind(FakeGlContext())
                    .bind(fakeGlslProgram)
                programFeed.setOnProgram()

                val beatUniform = fakeGlslProgram.uniforms["in_beatInfo.beat"]!!
                beatUniform.value.shouldBe(2f)

                fakeBeatSource.setBeatData(BeatData(1.0, 400))
                testRig.fakePinkyDispatcher.advanceUntilIdle()
                programFeed.setOnProgram()
                beatUniform.value.shouldBe(2.5f)
            }

            it("flows from beat source to client feed") {
                val clientFeed = clientPlugins.resolveFeed(inputPort)
                val programFeed = clientFeed.open(FakeShowPlayer(), "beatInfo")
                    .bind(FakeGlContext())
                    .bind(fakeGlslProgram)
                programFeed.setOnProgram()

                val beatUniform = fakeGlslProgram.uniforms["in_beatInfo.beat"]!!
                beatUniform.value.shouldBe(2f)

                fakeBeatSource.setBeatData(BeatData(1.0, 400))
                testRig.fakePinkyDispatcher.advanceUntilIdle()
                programFeed.setOnProgram()
                beatUniform.value.shouldBe(2.5f)
            }
        }
    }
})