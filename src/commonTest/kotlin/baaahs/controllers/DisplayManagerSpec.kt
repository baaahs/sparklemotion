package baaahs.controllers

import baaahs.FakeClock
import baaahs.SpyPubSub
import baaahs.controller.DisplayManager
import baaahs.controller.DisplayProvider
import baaahs.describe
import baaahs.gl.Display
import baaahs.gl.Displays
import baaahs.gl.Mode
import baaahs.scene.SceneMonitor
import baaahs.ui.IObservable
import baaahs.ui.Observable
import ch.tutteli.atrium.api.fluent.en_GB.contains
import ch.tutteli.atrium.api.verbs.expect
import org.spekframework.spek2.Spek

@Suppress("unused")
object DisplayManagerSpec : Spek({
    describe<DisplayManager> {
        val displays by value { Displays() }
        val defaultMode by value { Mode(320, 240) }
        val fakeDisplayProvider by value {
            FakeDisplayProvider(
                "FAKE", listOf(
                    Display("FAKE", "Fake Display 1", listOf(defaultMode), defaultMode, true)
                )
            )
        }
        val displayProviders by value { listOf(fakeDisplayProvider) }
        val fakeFixtureListener by value { FakeFixtureListener() }
        val displayManager by value {
            DisplayManager(displays, displayProviders, SceneMonitor(), listOf(fakeFixtureListener), FakeClock(), SpyPubSub())
        }

        beforeEachTest {
            displayManager.start()
        }

        it("initializes properly") {
           expect(fakeFixtureListener.changes).contains(
               FakeFixtureListener.Changes(listOf(), emptyList())
           )
        }

        context("with a projector") {
            it("initializes properly") {
                expect(fakeFixtureListener.changes).contains(
                    FakeFixtureListener.Changes(listOf(), emptyList())
                )
            }
        }
    }
})

class FakeDisplayProvider(
    override val id: String,
    override val displays: List<Display> = emptyList()
) : DisplayProvider, IObservable by Observable()