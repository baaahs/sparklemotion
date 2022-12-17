package baaahs.plugin.beatlink

import baaahs.FakeClock
import baaahs.TestRig
import baaahs.describe
import baaahs.gl.shader.InputPort
import baaahs.plugin.ClientPlugins
import baaahs.plugin.PluginContext
import baaahs.plugin.PluginRef
import baaahs.plugin.ServerPlugins
import baaahs.shows.FakeGlContext
import baaahs.shows.FakeGlslProgram
import baaahs.shows.FakeShowPlayer
import baaahs.sm.server.PinkyArgs
import baaahs.toEqual
import ch.tutteli.atrium.api.verbs.expect
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import org.spekframework.spek2.Spek

@OptIn(InternalCoroutinesApi::class, ExperimentalCoroutinesApi::class)
object BeatLinkDataFlowSpec : Spek({
    describe<BeatLinkPlugin> {
        context("data flow") {
            val testRig by value { TestRig() }
            val clock by value { FakeClock(2.0) }
            val fakeBeatSource by value { FakeBeatSource(BeatData(1.0, 500)) }
            val serverContext by value { PluginContext(clock, testRig.server) }
            val serverPlugin by value {
                BeatLinkPlugin.openForServer(serverContext, TestArgs(beatSource = fakeBeatSource))
            }
            val serverPlugins by value { ServerPlugins(listOf(serverPlugin), serverContext, PinkyArgs.defaults) }
            val clientContext by value { PluginContext(clock, testRig.client1) }
            val clientPlugin by value { BeatLinkPlugin.openForClient(clientContext) }
            val clientPlugins by value { ClientPlugins(listOf(clientPlugin), clientContext) }
            val inputPort by value {
                val pluginRef = PluginRef.from("baaahs.BeatLink:BeatInfo",)
                InputPort("beatLink", BeatLinkPlugin.beatInfoContentType, pluginRef = pluginRef)
            }
            val fakeGlslProgram by value { FakeGlslProgram() }

            beforeEachTest {
                testRig.server.run {}
                testRig.serverConnections.run {}
                testRig.client1.run {}
                serverPlugin.run {}
                clientPlugin.run {}
                fakeBeatSource.notifyChanged()
                testRig.dispatcher.advanceUntilIdle()
            }

            it("flows from beat source to server feed") {
                val serverDataSource = serverPlugins.resolveFeed(inputPort)
                val programFeed = serverDataSource.open(FakeShowPlayer(), "beatInfo")
                    .bind(FakeGlContext())
                    .bind(fakeGlslProgram)
                programFeed.setOnProgram()

                val beatUniform = fakeGlslProgram.uniforms["in_beatInfo.beat"]!!
                expect(beatUniform.value).toEqual(2f)

                fakeBeatSource.setBeatData(BeatData(1.0, 400))
                testRig.dispatcher.advanceUntilIdle()
                programFeed.setOnProgram()
                expect(beatUniform.value).toEqual(2.5f)
            }

            it("flows from beat source to client feed") {
                val clientDataSource = clientPlugins.resolveFeed(inputPort)
                val programFeed = clientDataSource.open(FakeShowPlayer(), "beatInfo")
                    .bind(FakeGlContext())
                    .bind(fakeGlslProgram)
                programFeed.setOnProgram()

                val beatUniform = fakeGlslProgram.uniforms["in_beatInfo.beat"]!!
                expect(beatUniform.value).toEqual(2f)

                fakeBeatSource.setBeatData(BeatData(1.0, 400))
                testRig.dispatcher.advanceUntilIdle()
                programFeed.setOnProgram()
                expect(beatUniform.value).toEqual(2.5f)
            }
        }
    }
})

class TestArgs(
    override val enableBeatLink: Boolean = true,
    override val beatSource: BeatSource? = null
) : BeatLinkPlugin.Args