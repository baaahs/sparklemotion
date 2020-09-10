package baaahs.show.live

import baaahs.gl.patch.AutoWirer
import baaahs.plugin.CorePlugin
import baaahs.plugin.Plugins
import baaahs.show.Layout
import baaahs.show.Layouts
import baaahs.show.ShaderType
import baaahs.show.mutable.MutableShow
import baaahs.show.mutable.ShowBuilder
import baaahs.shows.FakeGlContext
import baaahs.shows.FakeKgl
import baaahs.shows.FakeShowPlayer
import describe
import kotlinx.serialization.json.json
import org.spekframework.spek2.Spek
import kotlin.test.expect

object ControlDisplaySpec : Spek({
    describe<ControlDisplay> {
        val mutableShow by value {
            MutableShow("Show").editLayouts {
                copyFrom(Layouts(listOf("Panel 1", "Panel 2", "Panel 3"), mapOf("default" to Layout(json { }))))
            }
        }
        val show by value { mutableShow.build(ShowBuilder()) }

        val showPlayer by value { FakeShowPlayer(FakeGlContext(FakeKgl())) }
        val autoWirer by value { AutoWirer(Plugins.safe()) }
        val showOpener by value { ShowOpener(autoWirer.glslAnalyzer, show, showPlayer) }
        val openShow by value { showOpener.openShow() }
        val editMode by value { false }
        val editHandler by value { FakeEditHandler() }
        val dragNDrop by value { FakeDragNDrop() }
        val controlDisplay by value {
            ControlDisplay(openShow, editMode, editHandler, dragNDrop)
        }

        context("empty show") {
            it("renders all empty sections") {
                expect(
                    """
                    Panel 1:
                    Panel 2:
                    Panel 3:
                """.trimIndent()
                ) { openShow.fakeRender(controlDisplay) }
                expect(emptyList()) { controlDisplay.unplacedControls }
            }
        }

        context("a show with button groups") {
            beforeEachTest {
                val slider1 = CorePlugin.SliderDataSource("slider1", 0f, 0f, 1f, 1f)
                val slider2 = CorePlugin.SliderDataSource("slider2", 0f, 0f, 1f, 1f)

                mutableShow.addPatch(autoWirer.wireUp(fakeShader("Show Projection", ShaderType.Projection)))

                mutableShow.addButtonGroup("Panel 1", "Scenes") {
                    addButton("Scene 1") {
                        addPatch(autoWirer.wireUp(fakeShader("Scene 1 Shader")))

                        addButtonGroup("Panel 2", "Backdrops") {
                            addButton("Backdrop 1.1") {
                                addPatch(autoWirer.wireUp(fakeShader("Backdrop 1.1 Shader")))
                            }
                            addButton("Backdrop 1.2") {
                                addPatch(autoWirer.wireUp(fakeShader("Backdrop 1.2 Shader")))
                                addControl("Panel 3", slider2.buildControl())
                            }
                        }
                        addControl("Panel 3", slider1.buildControl())
                    }

                    addButton("Scene 2") {
                        addPatch(autoWirer.wireUp(fakeShader("Scene 2 Shader")))

                        addButtonGroup("Panel 2", "Backdrops") {
                            addButton("Backdrop 2.1") {
                                addPatch(autoWirer.wireUp(fakeShader("Backdrop 2.1 Shader")))
                                addControl("Panel 3", slider2.buildControl())
                            }
                            addButton("Backdrop 2.2") {
                                addPatch(autoWirer.wireUp(fakeShader("Backdrop 2.2 Shader")))
                                addControl("Panel 3", slider1.buildControl())
                            }
                        }
                    }
                }
            }

            val panel1 by value { openShow.controlLayout["Panel 1"] ?: emptyList() }
            val scenesButtonGroup by value { panel1.first() as OpenButtonGroupControl }

            it("renders controls appropriately") {
                expect(
                    """
                    Panel 1:
                      |0| scenesButtonGroup
                    Panel 2:
                      |0|
                      |1| backdropsButtonGroup
                    Panel 3:
                      |0|
                      |1| sliderSliderControl
                """.trimIndent()
                ) { openShow.fakeRender(controlDisplay) }
                expect(emptyList()) { controlDisplay.unplacedControls }
            }

            it("has the first item in the button group selected by default") {
                expect(listOf(true, false)) { scenesButtonGroup.buttons.map { it.isPressed } }

                expect(openShow.patches + scenesButtonGroup.buttons[0].patches) {
                    openShow.activeSet().getActivePatches()
                }
            }
        }
    }
})