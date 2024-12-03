package baaahs.dmx

import baaahs.*
import baaahs.controller.ControllerId
import baaahs.controllers.FakeMappingManager
import baaahs.gl.override
import baaahs.kotest.value
import baaahs.model.MovingHeadData
import baaahs.scene.MutableDirectDmxControllerConfig
import baaahs.scene.MutableFixtureMapping
import baaahs.sim.FakeDmxUniverse
import baaahs.sim.SimDmxDevice
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.*
import io.kotest.matchers.collections.shouldHaveSize

@Suppress("unused")
class DirectDmxControllerSpec : DescribeSpec({
    describe<DirectDmxController> {
        val testAdapter by value { TestMovingHeadAdapter(10) }
        val entity1 by value { MovingHeadData("mover1", adapter = testAdapter, baseDmxChannel = -1) }
        val entity2 by value { MovingHeadData("mover2", adapter = testAdapter, baseDmxChannel = -1) }
        val controllerConfig by value { DirectDmxControllerConfig() }
        val dmxDevice by value { SimDmxDevice(FakeDmxUniverse()) }
        val controllerId by value { ControllerId(controllerConfig.controllerType, dmxDevice.id) }
        val transportConfig1 by value { MutableDmxTransportConfig(null, null, null) }
        val transportConfig2 by value { MutableDmxTransportConfig(null, null, null) }
        val scene by value { sceneDataForTest(entity1, entity2) {
            controllers[controllerId] = MutableDirectDmxControllerConfig(dmxDevice.id, null, null)
            fixtureMappings[controllerId] = mutableListOf(
                MutableFixtureMapping(this.model.entities[0], null, transportConfig1),
                MutableFixtureMapping(this.model.entities[1], null, transportConfig2),
            )
        } }
        val openScene by value { scene.open() }
//        val openControllerConfig by value { openScene.controllers.getBang(controllerId, "controller") }
//        val mappingFixtureOptions by value<FixtureOptions> { MovingHeadDevice.Options(testAdapter) }
//        val mappingTransportConfig by value<TransportConfig?> { DmxTransportConfig() }
//        val fixtureMapping by value { openScene.fixtureMappings.getBang(controllerId, "fixture mapping") }
        val controller by value { DirectDmxController(dmxDevice) }
        val fixtures by value {
            openScene.resolveFixtures(controller, FakeMappingManager())
        }
        val fixture1 by value { fixtures[0] }
        val fixture2 by value { fixtures[1] }

        it("should create both fixtures") {
            fixtures.shouldHaveSize(2)
        }

        context("when start channel is not specified") {
            it("starts at 0") {
                val transport1 = fixture1.transport as DirectDmxController.DirectDmxTransport
                transport1.startChannel.shouldBe(0)
                transport1.endChannel.shouldBe(9)
                val transport2 = fixture2.transport as DirectDmxController.DirectDmxTransport
                transport2.startChannel.shouldBe(10)
                transport2.endChannel.shouldBe(19)
            }
        }

        context("when start channel is specified") {
            override(transportConfig1) {
                MutableDmxTransportConfig(2, null, null)
            }

            it("starts there") {
                val transport1 = fixture1.transport as DirectDmxController.DirectDmxTransport
                transport1.startChannel.shouldBe(2)
                transport1.endChannel.shouldBe(11)
                val transport2 = fixture2.transport as DirectDmxController.DirectDmxTransport
                transport2.startChannel.shouldBe(12)
                transport2.endChannel.shouldBe(21)
            }
        }
    }
})