package baaahs.driverack

import baaahs.TestRig
import baaahs.describe
import baaahs.toEqual
import ch.tutteli.atrium.api.verbs.expect
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.serialization.builtins.serializer
import org.spekframework.spek2.Spek

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
object DriveRackSpec : Spek({
    describe<DriveRack> {
        val testRig by value { TestRig() }

        val sliderA by value { RackMap.Entry("sliderA", .5f, Float.serializer()) }
        val rackMap by value { RackMap(sliderA) }
        val initialBuses by value { setOf("A") }
        val serverManager by value { DriveRackManager(testRig.server, testRig.dispatcher, isServer = true, testRig.dispatcher) }
        val client1Manager by value { DriveRackManager(testRig.client1, testRig.dispatcher, isServer = false, testRig.dispatcher) }
        val client2Manager by value { DriveRackManager(testRig.client2, testRig.dispatcher, isServer = false, testRig.dispatcher) }

        beforeEachTest { serverManager.start() }

//        it("starts with no pubsub events") {
//            expect(spyPubSub.events).isEmpty()
//        }

        context("when a rack map is published on the server") {
            val serverDriveRack by value { serverManager.publish(rackMap, initialBuses) }
            val serverBusA by value { serverDriveRack.getBus("A") }
            val serverBusASliderA by value { serverBusA.channel(sliderA) }

            it("values may be read and written locally") {
                expect(serverBusASliderA.value).toEqual(.5f)

                serverBusASliderA.value = .25f
                expect(serverBusASliderA.value).toEqual(.25f)
            }

            context("and a client subscribes") {
                val client1DriveRack by value { client1Manager.subscribe(serverDriveRack.id, rackMap) }
                val client1BusA by value { client1DriveRack.getBus("A") }
                val client1BusASliderA by value { client1BusA.channel(sliderA) }

                context("before they sync") {
                    it("client values may be read and written locally") {
                        expect(client1BusASliderA.value).toEqual(.5f)

                        client1BusASliderA.value = .25f
                        expect(client1BusASliderA.value).toEqual(.25f)
                    }
                }

                context("when they sync") {
                    beforeEachTest {
                        serverBusASliderA.run {}
                        client1BusASliderA.run {}
                        testRig.dispatcher.runCurrent()
                    }

                    it("changes on server are visible on client") {
                        serverBusASliderA.value = .25f
                        testRig.dispatcher.runCurrent()
                        expect(client1BusASliderA.value).toEqual(.25f)
                    }

                    it("changes on client are visible on server") {
                        client1BusASliderA.value = .25f
                        testRig.dispatcher.runCurrent()
                        expect(serverBusASliderA.value).toEqual(.25f)
                    }
                }
            }
        }

        context("when a bus is created") {
//            val busA by value { serverManager.createBus("A") }
//            beforeEachTest { busA.run { } }
//
//            it("can be retrieved by name") {
//                expect(serverManager.getBus("A")).isSameAs(busA)
//            }
//
//            it("registers channels with pubsub") {
//                expect(spyPubSub.events).containsExactly(
//                    "openChannel(/driverack/bus-A/sliderA) := 0.5"
//                )
//            }

//            context("with a named channel") {
//                val sliderA by value { busA.channel<Float>("sliderA") }
//                val sliderAEvents by value { arrayListOf<String>() }
//
//                beforeEachTest {
//                    sliderA.addObserver {
//                        sliderAEvents.add("sliderA changed: ${it.value}")
//                    }
//                }
//
//                it("can obtain a value") {
//                    expect(sliderA.value).toEqual(.5f)
//                }
//
//                context("when the value is changed locally") {
//                    beforeEachTest {
//                        spyPubSub.clearEvents()
//                        sliderA.value = .75f
//                    }
//
//                    it("its new value is returned") {
//                        expect(sliderA.value).toEqual(.75f)
//                    }
//
//                    it("it notifies pubsub of the change") {
//                        expect(spyPubSub.events).contains(
//                            "/driverack/bus-A/sliderA.onChange(0.75)"
//                        )
//                    }
//
//                    it("notifies its listeners") {
//                        expect(sliderAEvents).containsExactly("sliderA changed: 0.75")
//                    }
//                }
//
//                context("when the value is set to the same value locally") {
//                    beforeEachTest {
//                        spyPubSub.clearEvents()
//                        sliderA.value = .5f
//                    }
//
//                    it("it doesn't notify pubsub of a change") {
//                        expect(spyPubSub.events).isEmpty()
//                    }
//
//                    it("doesn't notify its listeners") {
//                        expect(sliderAEvents).isEmpty()
//                    }
//                }
//
//                context("when the value is changed elsewhere") {
//                    beforeEachTest {
//                        spyPubSub.clearEvents()
//
//                        spyPubSub.receive("/driverack/bus-A/sliderA", .25f)
//                    }
//
//                    it("its new value is returned") {
//                        expect(sliderA.value).toEqual(.25f)
//                    }
//
//                    it("notifies its listeners") {
//                        expect(sliderAEvents).containsExactly("sliderA changed: 0.25")
//                    }
//
//                    it("it doesn't re-notify pubsub of the change") {
//                        expect(spyPubSub.events).isEmpty()
//                    }
//                }
//            }

//            context("duplicating an existing bus") {
//                beforeEachTest {
//                    val busASliderA = busA.channel<Float>("sliderA")
//                    busASliderA.value = .25f
//                }
//
//                val busB by value { serverManager.createBus("B", busA) }
//                val busBSliderA by value { busB.channel<Float>("sliderA") }
//                val busBSliderAEvents by value { arrayListOf<String>() }
//
//                beforeEachTest {
//                    busB.run {}
//                    busBSliderA.addObserver {
//                        busBSliderAEvents.add("sliderA changed: ${it.value}")
//                    }
//                }
//
//                it("should initialize values from the existing bus") {
//                    expect(busBSliderA.value).toEqual(0.25f)
//                }
//
//                it("registers channels with pubsub") {
//                    expect(spyPubSub.events).containsExactly(
//                        "openChannel(/driverack/bus-A/sliderA) := 0.5",
//                        "/driverack/bus-A/sliderA.onChange(0.25)",
//                        "openChannel(/driverack/bus-B/sliderA) := 0.25"
//                    )
//                }
//
//                context("when the value is changed locally") {
//                    beforeEachTest {
//                        spyPubSub.clearEvents()
//                        busBSliderA.value = .75f
//                    }
//
//                    it("its new value is returned") {
//                        expect(busBSliderA.value).toEqual(.75f)
//                    }
//
//                    it("it notifies pubsub of the change") {
//                        expect(spyPubSub.events).contains(
//                            "/driverack/bus-B/sliderA.onChange(0.75)"
//                        )
//                    }
//
//                    it("notifies its listeners") {
//                        expect(busBSliderAEvents).containsExactly("sliderA changed: 0.75")
//                    }
//
//                }
//
//                context("when the value is changed elsewhere") {
//                    beforeEachTest {
//                        spyPubSub.clearEvents()
//
//                        spyPubSub.receive("/driverack/bus-B/sliderA", .25f)
//                    }
//
//                    it("its new value is returned") {
//                        expect(busBSliderA.value).toEqual(.25f)
//                    }
//
//                    it("notifies its listeners") {
//                        expect(busBSliderAEvents).containsExactly("sliderA changed: 0.25")
//                    }
//
//                    it("it doesn't re-notify pubsub of the change") {
//                        expect(spyPubSub.events).isEmpty()
//                    }
//                }
//            }
        }

//        context("when an initial bus is specified") {
//            override(initialBuses) { setOf("A") }
//            val busA by value { serverManager.createBus("A") }
//            val sliderA by value { busA.channel<Float>("sliderA") }
//            val sliderAEvents by value { arrayListOf<String>() }
//
//            beforeEachTest {
//                sliderA.addObserver {
//                    sliderAEvents.add("sliderA changed: ${it.value}")
//                }
//            }
//
//            it("can be retrieved by name") {
//                expect(serverManager.getBus("A").id).toEqual("A")
//            }
//
//            it("registers channels with pubsub") {
//                expect(spyPubSub.events).containsExactly(
//                    "openChannel(/driverack/bus-A/sliderA) := 0.5"
//                )
//            }
//
//            it("notifies listeners of changes") {
//                sliderA.value = .25f
//                expect(sliderAEvents).containsExactly("sliderA changed: 0.25")
//            }
//
//            context("when another bus is created") {
//                val busB by value { serverManager.createBus("B") }
//                beforeEachTest { busB.run { } }
//
//                it("still notifies listeners on other buses of changes") {
//                    sliderA.value = .25f
//                    expect(sliderAEvents).containsExactly("sliderA changed: 0.25")
//                }
//            }
//        }

//        context("when the rack map is changed") {
//            override(initialBuses) { setOf("A") }
//
//            context("and a channel is added") {
//                beforeEachTest {
//                    serverManager.rackMap = RackMap(
//                        rackMap.entries + RackMap.Entry("sliderB", .25f, Float.serializer())
//                    )
//                }
//
//                it("can be retrieved by name") {
//                    expect(serverManager.getBus("A").id).toEqual("A")
//                }
//
//                it("registers channels with pubsub") {
//                    expect(spyPubSub.events).containsExactly(
//                        "openChannel(/driverack/bus-A/sliderA) := 0.5",
//                        "openChannel(/driverack/bus-A/sliderB) := 0.25"
//                    )
//                }
//            }
//
//            context("and a channel's type is changed") {
//                beforeEachTest {
//                    serverManager.rackMap = RackMap(
//                        RackMap.Entry("sliderA", ".25", String.serializer())
//                    )
//                }
//
//                it("re-registers the channel with pubsub") {
//                    expect(spyPubSub.events).containsExactly(
//                        "openChannel(/driverack/bus-A/sliderA) := 0.5",
//                        "/driverack/bus-A/sliderA.close()",
//                        "openChannel(/driverack/bus-A/sliderA) := .25"
//                    )
//                }
//            }
//
//            context("and a channel is removed") {
//                beforeEachTest {
//                    serverManager.rackMap = RackMap(
//                        RackMap.Entry("sliderB", .25f, Float.serializer())
//                    )
//                }
//
//                it("registers channels with pubsub") {
//                    expect(spyPubSub.events).containsExactly(
//                        "openChannel(/driverack/bus-A/sliderA) := 0.5",
//                        "/driverack/bus-A/sliderA.close()",
//                        "openChannel(/driverack/bus-A/sliderB) := 0.25"
//                    )
//                }
//            }
//        }
    }
})
