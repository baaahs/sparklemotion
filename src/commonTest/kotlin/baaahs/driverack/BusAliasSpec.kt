package baaahs.driverack

import baaahs.SpyPubSub
import baaahs.describe
import baaahs.toEqual
import baaahs.ui.addObserver
import baaahs.useBetterSpekReporter
import ch.tutteli.atrium.api.fluent.en_GB.containsExactly
import ch.tutteli.atrium.api.fluent.en_GB.isEmpty
import ch.tutteli.atrium.api.verbs.expect
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.builtins.serializer
import org.spekframework.spek2.Spek
import kotlin.coroutines.EmptyCoroutineContext

object BusAliasSpec : Spek({
    useBetterSpekReporter()

    describe<BusAlias> {
        val rackMap by value { RackMap(RackMap.Entry("sliderA", .5f, Float.serializer())) }
        val spyPubSub by value { SpyPubSub() }
        val driveRack by value {
            DriveRack(spyPubSub, EmptyCoroutineContext + Dispatchers.Default, initialRackMap = rackMap)
        }
        val busA by value { driveRack.createBus("A") }
        val busB by value { driveRack.createBus("B") }
        val busAlias by value { BusAlias(busA) }
        val busASlider by value { busA.channel<Float>("sliderA") }
        val busBSlider by value { busB.channel<Float>("sliderA") }
        val busAliasSlider by value { busAlias.channel<Float>("sliderA") }
        val busASliderListener by value { arrayListOf<String>() }
        val busBSliderListener by value { arrayListOf<String>() }
        val busAliasSliderListener by value { arrayListOf<String>() }

        beforeEachTest {
            busASlider.addObserver { busASliderListener.add("changed to ${it.value}") }
            busBSlider.addObserver { busBSliderListener.add("changed to ${it.value}") }
            busAliasSlider.addObserver { busAliasSliderListener.add("changed to ${it.value}") }
        }

        context("when a value is locally changed") {
            beforeEachTest {
                busAliasSlider.value = .25f
            }

            it("propagates the change to the aliased bus") {
                expect(busASlider.value).toEqual(.25f)
            }

            it("notifies listeners on the aliased bus") {
                expect(busASliderListener).containsExactly("changed to 0.25")
                expect(busBSliderListener).isEmpty()
            }

            it("notifies pubsub") {
                expect(spyPubSub.events).containsExactly(
                    "openChannel(/driverack/bus-A/sliderA) := 0.5",
                    "openChannel(/driverack/bus-B/sliderA) := 0.5",
                    "/driverack/bus-A/sliderA.onChange(0.25)"
                )
            }
        }

        context("switching buses") {
            fun switchToBusB() { busAlias.bus = busB }

            context("when no value has changed on the other bus") {
                beforeEachTest {
                    switchToBusB()
                }

                it("doesn't notify listeners") {
                    expect(busAliasSliderListener).isEmpty()
                }
            }


            context("when a value has changed on the other bus") {
                beforeEachTest {
                    busB.channel<Float>("sliderA").value = .75f

                    switchToBusB()
                }

                it("returns the new value") {
                    expect(busAliasSlider.value).toEqual(.75f)
                }

                it("notifies listeners") {
                    expect(busAliasSliderListener).containsExactly("changed to 0.75")
                }
            }

            context("when a value is locally changed") {
                beforeEachTest {
                    switchToBusB()

                    busAliasSlider.value = .25f
                }

                it("propagates the change to the aliased bus") {
                    expect(busBSlider.value).toEqual(.25f)
                }

                it("notifies listeners on the aliased bus") {
                    expect(busASliderListener).isEmpty()
                    expect(busBSliderListener).containsExactly("changed to 0.25")
                }
            }
        }
    }
})