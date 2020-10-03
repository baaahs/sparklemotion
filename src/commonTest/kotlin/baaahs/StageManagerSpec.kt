package baaahs

import baaahs.fixtures.FixtureManager
import baaahs.gl.render.ModelRenderer
import baaahs.io.FakeRemoteFsBackend
import baaahs.io.FsClientSideSerializer
import baaahs.mapper.Storage
import baaahs.model.Model
import baaahs.model.ModelInfo
import baaahs.models.SheepModel
import baaahs.plugin.Plugins
import baaahs.show.SampleData
import baaahs.show.mutable.MutableShow
import baaahs.shows.FakeGlContext
import baaahs.sim.FakeDmxUniverse
import baaahs.sim.FakeFs
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Runnable
import org.spekframework.spek2.Spek
import kotlin.coroutines.CoroutineContext
import kotlin.test.expect

@InternalCoroutinesApi
object StageManagerSpec : Spek({
    describe<StageManager> {
        val panel17 = SheepModel.Panel("17")
        val model = SheepModel().apply { panels = listOf(panel17) } as Model<*>

        val plugins by value { Plugins.safe() }
        val fakeFs by value { FakeFs() }
        val pubSub by value { TestPubSub() }
        val fakeGlslContext by value { FakeGlContext() }
        val modelRenderer by value { ModelRenderer(fakeGlslContext, ModelInfo.Empty) }
        val baseShow by value { SampleData.sampleShow }

        val stageManager by value {
            StageManager(
                plugins,
                modelRenderer,
                pubSub.server,
                Storage(fakeFs, plugins),
                FixtureManager(modelRenderer),
                FakeDmxUniverse(),
                MovingHeadManager(fakeFs, pubSub.server, emptyList()),
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
            expect(listOf("update showEditorState: Sample Show")) { editingClient.log }
            expect(listOf("update showEditorState: Sample Show")) { otherClient.log }
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
                expect(emptyList()) { editingClient.log }
            }

            it("a pubsub update is received by the other client") {
                expect(listOf("update showEditorState: Edited show")) { otherClient.log }
            }
        }
    }
})
