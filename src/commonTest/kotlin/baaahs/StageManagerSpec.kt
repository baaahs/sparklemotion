package baaahs

import baaahs.fixtures.FixtureManager
import baaahs.gl.render.RenderManager
import baaahs.gl.testPlugins
import baaahs.gl.testToolchain
import baaahs.io.FakeRemoteFsBackend
import baaahs.io.FsClientSideSerializer
import baaahs.mapper.Storage
import baaahs.models.SheepModel
import baaahs.show.SampleData
import baaahs.show.mutable.MutableShow
import baaahs.shows.FakeGlContext
import baaahs.sim.FakeFs
import ch.tutteli.atrium.api.fluent.en_GB.containsExactly
import ch.tutteli.atrium.api.fluent.en_GB.isEmpty
import ch.tutteli.atrium.api.verbs.expect
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Runnable
import org.spekframework.spek2.Spek
import kotlin.coroutines.CoroutineContext

@InternalCoroutinesApi
object StageManagerSpec : Spek({
    describe<StageManager> {
        val panel17 by value { SheepModel.Panel("17") }
        val model by value { ModelForTest(listOf(panel17)) }

        val plugins by value { testPlugins() }
        val fakeFs by value { FakeFs() }
        val pubSub by value { TestPubSub() }
        val fakeGlslContext by value { FakeGlContext() }
        val renderManager by value { RenderManager(TestModel) { fakeGlslContext } }
        val baseShow by value { SampleData.sampleShow }

        val stageManager by value {
            StageManager(
                testToolchain,
                renderManager,
                pubSub.server,
                Storage(fakeFs, plugins),
                FixtureManager(renderManager),
                FakeClock(),
                model,
                object : CoroutineDispatcher() {
                    override fun dispatch(context: CoroutineContext, block: Runnable) {
                        block.run()
                    }
                }
            )
        }
        val editingClient by value { pubSub.client("editingClient") }
        val fsClientSideSerializer by value {
            object : FsClientSideSerializer() {
                override val backend = FakeRemoteFsBackend()
            }
        }
        var editingClientShowEditorState: ShowEditorState? = null
        val editingClientChannel by value {
            editingClient.subscribe(ShowEditorState.createTopic(plugins, fsClientSideSerializer)) {
                editingClient.log.add("update showEditorState: ${it?.show?.title}")
                editingClientShowEditorState = it
            }
        }

        val otherClient by value { pubSub.client("otherClient") }
        val otherClientChannel by value {
            otherClient.subscribe(ShowEditorState.createTopic(plugins, fsClientSideSerializer)) {
                println("otherClient heard from pubsub")
                otherClient.log.add("update showEditorState: ${it?.show?.title}")
            }
        }

        beforeEachTest {
            editingClientShowEditorState = null
            editingClientChannel.let {}
            otherClientChannel.let {}
            stageManager.switchTo(baseShow, file = fakeFs.resolve("fake-file.sparkle"))
            pubSub.testCoroutineContext.runAll()
        }

//        afterEachTest {
//            expect(emptyList()) { pubSub.testCoroutineContext.exceptions }
//        }

        it("a pubsub update is received on both clients") {
            expect(editingClient.log)
                .containsExactly("update showEditorState: Sample Show")
            expect(otherClient.log)
                .containsExactly("update showEditorState: Sample Show")
        }

        context("when a ShowEditorState change arrives from a client") {
            val editedShow by value { MutableShow(baseShow).apply { title = "Edited show" }.getShow() }

            beforeEachTest {
                editingClient.log.clear()
                otherClient.log.clear()

                editingClientChannel.onChange(
                    ShowEditorState(
                        editedShow,
                        ShowState(emptyMap()),
                        isUnsaved = true,
                        file = editingClientShowEditorState!!.file
                    )
                )
                pubSub.testCoroutineContext.runAll()
            }

            it("no additional pubsub updates are received by the editing client") {
                expect(editingClient.log)
                    .isEmpty()
            }

            it("a pubsub update is received by the other client") {
                expect(otherClient.log)
                    .containsExactly("update showEditorState: Edited show")
            }
        }
    }
})
