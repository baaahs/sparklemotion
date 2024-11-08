package baaahs.show.live

import baaahs.control.OpenButtonGroupControl
import baaahs.control.OpenSliderControl
import baaahs.describe
import baaahs.gadgets.Slider
import baaahs.getBang
import baaahs.gl.glsl.GlslType
import baaahs.gl.testToolchain
import baaahs.kotest.value
import baaahs.only
import baaahs.plugin.core.feed.TimeFeed
import baaahs.show.*
import baaahs.show.mutable.*
import baaahs.shows.FakeShowPlayer
import baaahs.toEqual
import ch.tutteli.atrium.api.fluent.en_GB.containsExactly
import ch.tutteli.atrium.api.fluent.en_GB.isEmpty
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import io.kotest.core.spec.style.DescribeSpec
import kotlin.collections.set

object OpenShowSpec : DescribeSpec({
    describe<OpenShow> {
        val mutableShow by value {
            MutableShow("Show") {
                editLayouts {
                    editLayout("default") {
                        tabs.add(MutableLegacyTab("Tab"))
                    }
                }
            }
        }
        val show by value { mutableShow.build(ShowBuilder()) }

        val showPlayer by value { FakeShowPlayer() }
        val showOpener by value { ShowOpener(testToolchain, show, showPlayer) }
        val openShow by value { showOpener.openShow() }

        beforeEach {
            mutableShow.editLayouts {
                copyFrom(
                    MutableLayouts(
                        Layouts(
                            listOf("Panel 1", "Panel 2", "Panel 3").associateWith { Panel(it) },
                            mapOf(
                                "default" to Layout(
                                    null,
                                    listOf(LegacyTab("Tab", emptyList(), emptyList(), emptyList()))
                                )
                            )
                        ), mutableShow
                    )
                )
            }
        }

        context("empty show") {
            it("creates an empty OpenShow") {
                expect(openShow.title).toBe("Show")

                expect(openShow.buildActivePatchSet().activePatches).isEmpty()
            }
        }

        context("resource allocation") {
            beforeEach {
                mutableShow.addPatch(
                    testToolchain.wireUp(Shader("Shader", "uniform float time;\nvoid main() { ... }"))
                )
            }

            it("opens feeds") {
                openShow.run {}
                val timeFeed = showPlayer.feeds.getBang(TimeFeed(), "feed key")
                expect(timeFeed.inUse()).toBe(true)
            }

            context("when released") {
                it("closes feeds") {
                    openShow.onRelease()
                    val timeFeed = showPlayer.feeds.getBang(TimeFeed(), "feed key")
                    expect(timeFeed.inUse()).toBe(false)
                }
            }
        }

        context("a show with button groups") {
            beforeEach {
                mutableShow.addPatch(testToolchain.wireUp(fakeShader("Show Shader")))

                mutableShow.addButtonGroup(mutableShow.findPanel("Panel 1"), "Scenes") {
                    addButton("First Scene") {
                        addPatch(testToolchain.wireUp(fakeShader("First Scene Shader")))
                    }

                    addButton("Second Scene") {
                        addPatch(testToolchain.wireUp(fakeShader("Second Scene Shader")))
                    }
                }
            }

            val panel1 by value { openShow.controlLayout[openShow.getPanel("panel1")] ?: emptyList() }
            val scenesButtonGroup by value { panel1.first() as OpenButtonGroupControl }

            it("creates an OpenShow") {
                expect(scenesButtonGroup.title)
                    .toBe("Scenes")
                expect(scenesButtonGroup.buttons.map { it.title })
                    .containsExactly("First Scene", "Second Scene")
            }

            it("has the first item in the button group selected by default") {
                expect(scenesButtonGroup.buttons.map { it.isPressed })
                    .containsExactly(true, false)

                expect(openShow.buildActivePatchSet().activePatches)
                    .toBe(openShow.patches + scenesButtonGroup.buttons[0].patches)
            }
        }

        context("when a feed has no corresponding placed control") {
            beforeEach {
                mutableShow.addPatch(
                    testToolchain.wireUp(
                        Shader("Shader", "uniform float slider; // @@Slider\nvoid main() { ... }")
                    )
                )
            }

            it("an implicit control is created") {
                expect(openShow.allControls.size).toEqual(1)
                val implicitSlider = openShow.allControls.only("control")
                implicitSlider as OpenSliderControl
                expect(implicitSlider.slider)
                    .toEqual(Slider("Slider"))
                expect(implicitSlider.controlledFeed)
                    .toEqual(show.feeds.values.only("feed"))

                expect(openShow.implicitControls).containsExactly(implicitSlider)
            }
        }

        context("when a patch has weird incoming links") {
            beforeEach {
                mutableShow.addPatch(
                    testToolchain.wireUp(
                        Shader(
                            "Weird Shader",
                            "uniform float time;\nvoid main() { gl_FragColor = gl_FragCoord + time; }"
                        )
                    ).apply {
                        incomingLinks["nonsense"] =
                            MutableConstPort("invalid", GlslType.Companion.from("?huh?"))
                    }
                )
            }

            it("ignores links to unknown ports") {
                expect(openShow.patches.only().incomingLinks.keys)
                    .toBe(setOf("gl_FragCoord", "time"))
            }
        }
    }
})