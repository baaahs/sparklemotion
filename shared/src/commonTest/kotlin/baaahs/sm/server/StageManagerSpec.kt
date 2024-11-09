package baaahs.sm.server

import baaahs.*
import baaahs.client.EventManager
import baaahs.fixtures.Fixture
import baaahs.fixtures.FixtureManager
import baaahs.fixtures.FixtureManagerImpl
import baaahs.fixtures.StubFixtureManager
import baaahs.gadgets.ColorPicker
import baaahs.gl.*
import baaahs.gl.render.RenderManager
import baaahs.glsl.Shaders
import baaahs.io.FakeRemoteFsBackend
import baaahs.io.FsClientSideSerializer
import baaahs.io.FsServerSideSerializer
import baaahs.kotest.value
import baaahs.plugin.core.feed.ColorPickerFeed
import baaahs.plugin.midi.MidiManager
import baaahs.scene.SceneMonitor
import baaahs.shaders.fakeFixture
import baaahs.show.*
import baaahs.show.Shader
import baaahs.show.live.ActivePatchSet
import baaahs.show.mutable.MutableLegacyTab
import baaahs.show.mutable.MutablePanel
import baaahs.show.mutable.MutableShow
import baaahs.show.mutable.ShowBuilder
import baaahs.shows.FakeGlContext
import baaahs.sim.FakeFs
import com.danielgergely.kgl.*
import ext.kotlinx_coroutines_test.TestCoroutineDispatcher
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.*
import io.kotest.matchers.collections.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonPrimitive

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
object StageManagerSpec : DescribeSpec({
    describe<StageManager> {
        val plugins by value { testPlugins() }
        val fakeFs by value { FakeFs() }
        val dispatcher by value { TestCoroutineDispatcher() }
        val pubSub by value { FakePubSub(dispatcher) }
        val fakeGlslContext by value { FakeGlContext() }
        val renderManager by value { RenderManager(fakeGlslContext) }
        val fixtureManager by value<FixtureManager> { FixtureManagerImpl(renderManager, plugins) }
        val gadgetManager by value { GadgetManager(pubSub.server, FakeClock(), dispatcher) }
        val eventManager by value { EventManager(MidiManager(emptyList()), ShowMonitor(), FakeClock()) }

        val stageManager by value {
            StageManager(
                testToolchain,
                renderManager,
                pubSub.server,
                fakeFs.rootFile,
                fixtureManager,
                FakeClock(),
                gadgetManager,
                ServerNotices(pubSub.server, dispatcher),
                SceneMonitor(),
                FsServerSideSerializer(),
                PinkyConfigStore(plugins, fakeFs.rootFile),
                ShowMonitor()
            )
        }

        describe("show management") {
            val shaderSrc by value {
                /**language=glsl*/
                "void main() { gl_FragColor = vec4(gl_FragCoord.xy, 0., 1.); }"
            }
            val shader2Src by value {
                /**language=glsl*/
                """
                    uniform float blue; // @@Slider
                    void main() { gl_FragColor = vec4(gl_FragCoord.xy, blue, 1.); }
                """.trimIndent()
            }

            val fixtures by value { listOf(fakeFixture(100)) }
            val panel by value { MutablePanel(Panel("Panel")) }
            val mutableShow by value {
                MutableShow("test show") {
                    editLayouts {
                        panels["panel"] = panel
                        editLayout("default") {
                            tabs.add(MutableLegacyTab("Tab"))
                        }
                    }

                    addPatch(
                        testToolchain.autoWire(Shaders.cylindricalProjection, Shaders.blue)
                            .acceptSuggestedLinkOptions()
                            .confirm()
                    )
                    addButtonGroup(
                        panel, "Backdrops"
                    ) {
                        addButton("backdrop 1") {
                            addPatch(
                                testToolchain.autoWire(Shader("backdrop1", shaderSrc))
                                    .acceptSuggestedLinkOptions()
                                    .confirm()
                            )
                        }
                        addButton("backdrop 2") {
                            addPatch(
                                testToolchain.autoWire(Shader("backdrop2", shader2Src))
                                    .acceptSuggestedLinkOptions()
                                    .confirm()
                            )
                        }
                    }
                }
            }
            val show by value { mutableShow.build(ShowBuilder()) }
            val showState by value { ShowState(mapOf("backdrop1" to mapOf("enabled" to JsonPrimitive(true)))) }
            val fakeProgram by value { fakeGlslContext.programs.only("program") }
            val addControls by value { {} }

            beforeEach {
                addControls()
                stageManager.switchTo(show, showState)
                fixtureManager.fixturesChanged(fixtures, emptyList())
                doRunBlocking { stageManager.renderAndSendNextFrame(true) }
            }

            context("port wiring") {
                it("wires up UV texture stuff") {
                    val pixelCoordsTextureUnit = fakeProgram.getUniform("ds_pixelLocation_texture") as Int
                    val textureConfig = fakeGlslContext.getTextureConfig(pixelCoordsTextureUnit, GL_TEXTURE_2D)

                    (textureConfig.width to textureConfig.height).shouldBe(100 to 1)
                    textureConfig.internalFormat.shouldBe(GlContext.GL_RGB32F)
                    textureConfig.format.shouldBe(GL_RGB)
                    textureConfig.type.shouldBe(GL_FLOAT)
                    textureConfig.params[GL_TEXTURE_MIN_FILTER].shouldBe(GL_NEAREST)
                    textureConfig.params[GL_TEXTURE_MAG_FILTER].shouldBe(GL_NEAREST)
                }

                context("for vec4 uniforms") {
                    override(shaderSrc) {
                        /**language=glsl*/
                        """
                    uniform vec4 color; // @@ColorPicker
                    void main() { gl_FragColor = color; }
                    """.trimIndent()
                    }

                    override(addControls) {
                        {
                            val colorPickerFeed = ColorPickerFeed("Color", Color.WHITE)
                            mutableShow.addControl(panel, colorPickerFeed.buildControl())
                        }
                    }

                    val colorPickerGadget by value {
                        stageManager.useGadget<ColorPicker>("color")
                    }

                    it("wires it up as a color picker") {
                        colorPickerGadget.title.shouldBe("Color")
                        colorPickerGadget.initialValue.shouldBe(Color.WHITE)
                    }

                    it("sets the uniform from the gadget's initial value") {
                        val colorUniform = fakeProgram.getUniform<List<Float>>("in_colorColorPicker")
                        colorUniform.shouldBe(arrayListOf(1f, 1f, 1f, 1f))
                    }

                    it("sets the uniform when the gadget value changes") {
                        colorPickerGadget.color = Color.YELLOW

                        doRunBlocking { stageManager.renderAndSendNextFrame(true) }
                        val colorUniform = fakeProgram.getUniform<List<Float>>("in_colorColorPicker")
                        colorUniform.shouldBe(arrayListOf(1f, 1f, 0f, 1f))
                    }
                }
            }

            context("patchset recalculation") {
                val activePatchSets by value { arrayListOf<ActivePatchSet>() }
                override(fixtureManager) {
                    object : StubFixtureManager() {
                        override fun activePatchSetChanged(activePatchSet: ActivePatchSet) {
                            activePatchSets.add(activePatchSet)
                        }

                        override fun hasActiveRenderPlan(): Boolean = true

                        override fun fixturesChanged(addedFixtures: Collection<Fixture>, removedFixtures: Collection<Fixture>) {
                            // Ignore.
                        }

                        override fun addFrameListener(frameListener: FrameListener) {
                        }

                        override fun removeFrameListener(frameListener: FrameListener) {
                        }

                        override fun maybeUpdateRenderPlans(): Boolean {
                            // Ignore.
                            return true
                        }

                        override fun sendFrame() = Unit // Ignore.
                    }
                }

                it("starts off with a single calc") {
                    activePatchSets.size.shouldBe(1)
                }

                context("when a new patch is requested by the user") {
                    val clientPub by value { pubSub.client("client") }
                    beforeEach {
                        activePatchSets.clear()
                        val backdrop1Channel = clientPub.subscribe(PubSub.Topic("/gadgets/backdrop1", GadgetDataSerializer)) {}
                        val backdrop2Channel = clientPub.subscribe(PubSub.Topic("/gadgets/backdrop2", GadgetDataSerializer)) {}
                        dispatcher.runCurrent()
                        backdrop1Channel.onChange(mapOf("enabled" to JsonPrimitive(false)))
                        backdrop2Channel.onChange(mapOf("enabled" to JsonPrimitive(true)))
                        dispatcher.runCurrent()
                        CoroutineScope(dispatcher).launch {
                            stageManager.renderAndSendNextFrame(true)
                        }
                        dispatcher.runCurrent()
                    }

                    it("delivers just one new patchset to FixtureManager") {
                        activePatchSets.size.shouldBe(1)
                    }

                    it("only delivers a new patchset when something has changed") {
                        CoroutineScope(dispatcher).launch {
                            stageManager.renderAndSendNextFrame(true)
                        }
                        dispatcher.runCurrent()
                        activePatchSets.size.shouldBe(1)
                    }

                    // TODO: make sure we don't recalculate e.g. when sliders move.
                }
            }
        }

        describe("client management") {
            val editingClient by value { pubSub.client("editingClient") }
            val fsClientSideSerializer by value {
                object : FsClientSideSerializer() {
                    override val backend = FakeRemoteFsBackend()
                }
            }
            var editingClientDocumentState: DocumentState<Show, ShowState>? = null
            val editingClientChannel by value {
                editingClient.subscribe(ShowState.createTopic(
                    plugins.serialModule, fsClientSideSerializer
                )) {
                    editingClient.log.add("update showEditorState: ${it?.document?.title}")
                    editingClientDocumentState = it
                }
            }

            val otherClient by value { pubSub.client("otherClient") }
            val otherClientChannel by value {
                otherClient.subscribe(ShowState.createTopic(
                    plugins.serialModule, fsClientSideSerializer
                )) {
                    println("otherClient heard from pubsub")
                    otherClient.log.add("update showEditorState: ${it?.document?.title}")
                }
            }

            val baseShow by value { SampleData.sampleShow }

            beforeEach {
                editingClientDocumentState = null
                editingClientChannel.let {}
                otherClientChannel.let {}
                stageManager.switchTo(baseShow, file = fakeFs.resolve("fake-file.sparkle"))
                pubSub.dispatcher.runCurrent()
            }

//        afterEachTest {
//            expect(emptyList()) { pubSub.testCoroutineContext.exceptions }
//        }

            it("a pubsub update is received on both clients") {
                editingClient.log
                    .shouldContainExactly("update showEditorState: Sample Show")
                otherClient.log
                    .shouldContainExactly("update showEditorState: Sample Show")
            }

            context("when a ShowEditorState change arrives from a client") {
                val editedShow by value { MutableShow(baseShow).apply { title = "Edited show" }.getShow() }

                beforeEach {
                    editingClient.log.clear()
                    otherClient.log.clear()

                    editingClientChannel.onChange(
                        DocumentState(
                            editedShow,
                            ShowState(emptyMap()),
                            isUnsaved = true,
                            file = editingClientDocumentState!!.file
                        )
                    )
                    dispatcher.runCurrent()
                }

                it("no additional pubsub updates are received by the editing client") {
                    editingClient.log
                        .isEmpty()
                }

                it("a pubsub update is received by the other client") {
                    otherClient.log
                        .shouldContainExactly("update showEditorState: Edited show")
                }
            }
        }
    }
})