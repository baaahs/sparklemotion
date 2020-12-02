package baaahs.show.live

import baaahs.describe
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.AutoWirer
import baaahs.gl.testPlugins
import baaahs.only
import baaahs.show.Layout
import baaahs.show.Layouts
import baaahs.show.mutable.MutableConstPort
import baaahs.show.mutable.MutableShow
import baaahs.show.mutable.ShowBuilder
import baaahs.shows.FakeShowPlayer
import ch.tutteli.atrium.api.fluent.en_GB.containsExactly
import ch.tutteli.atrium.api.fluent.en_GB.isEmpty
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import kotlinx.serialization.json.buildJsonObject
import org.spekframework.spek2.Spek
import kotlin.collections.set

object OpenShowSpec : Spek({
    describe<OpenShow> {
        val mutableShow by value { MutableShow("Show") }
        val show by value { mutableShow.build(ShowBuilder()) }

        val showPlayer by value { FakeShowPlayer() }
        val autoWirer by value { AutoWirer(testPlugins()) }
        val showOpener by value { ShowOpener(autoWirer.glslAnalyzer, show, showPlayer) }
        val openShow by value { showOpener.openShow() }

        beforeEachTest {
            mutableShow.editLayouts {
                copyFrom(
                    Layouts(
                        listOf("Panel 1", "Panel 2", "Panel 3"),
                        mapOf("default" to Layout(buildJsonObject { }))
                    )
                )
            }
        }

        context("empty show") {
            it("creates an empty OpenShow") {
                expect(openShow.title).toBe("Show")

                expect(openShow.activePatchSet().activePatches).isEmpty()
            }
        }

        context("a show with button groups") {
            beforeEachTest {
                mutableShow.addPatch(autoWirer.wireUp(fakeShader("Show Shader")))

                mutableShow.addButtonGroup("Panel 1", "Scenes") {
                    addButton("First Scene") {
                        addPatch(autoWirer.wireUp(fakeShader("First Scene Shader")))
                    }

                    addButton("Second Scene") {
                        addPatch(autoWirer.wireUp(fakeShader("Second Scene Shader")))
                    }
                }
            }

            val panel1 by value { openShow.controlLayout["Panel 1"] ?: emptyList() }
            val scenesButtonGroup by value { panel1.first() as OpenButtonGroupControl }

            it("creates an OpenShow") {
                expect(scenesButtonGroup.title)
                    .toBe("Scenes")
                expect(scenesButtonGroup.buttons.map { it.title })
                    .containsExactly("First Scene","Second Scene")
            }

            it("has the first item in the button group selected by default") {
                expect(scenesButtonGroup.buttons.map { it.isPressed })
                    .containsExactly(true,false)

                expect(openShow.activePatchSet().activePatches)
                    .toBe(openShow.patches + scenesButtonGroup.buttons[0].patches)
            }
        }

        context("when a shader instance has weird incoming links") {
            beforeEachTest {
                mutableShow.addPatch(
                    autoWirer.wireUp(fakeShader("Weird Shader")).apply {
                        mutableShaderInstances.only().incomingLinks["nonsense"] =
                            MutableConstPort("invalid", GlslType.Companion.from("?huh?"))
                    }
                )
            }

            it("ignores links to unknown ports") {
                expect(openShow.patches.only().shaderInstances.only().incomingLinks.keys)
                    .toBe(setOf("gl_FragCoord", "time"))
            }
        }
    }
})