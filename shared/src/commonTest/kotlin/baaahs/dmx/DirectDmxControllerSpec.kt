package baaahs.dmx

import baaahs.*
import baaahs.controller.Controller
import baaahs.device.MovingHeadDevice
import baaahs.fixtures.FixtureMapping
import baaahs.fixtures.FixtureOptions
import baaahs.fixtures.TransportConfig
import baaahs.model.Model
import baaahs.model.MovingHead
import baaahs.sim.FakeDmxUniverse
import baaahs.sim.SimDmxDevice
import ch.tutteli.atrium.api.verbs.expect
import org.spekframework.spek2.Spek

@Suppress("unused")
object DirectDmxControllerSpec : Spek({
    describe<DirectDmxController> {
        val testAdapter by value { TestMovingHeadAdapter(10) }
        val entity by value<Model.Entity?> { MovingHead("mover", adapter = testAdapter, baseDmxChannel = -1) }
        val model by value { modelForTest(listOfNotNull(entity)) }
        val mappingFixtureOptions by value<FixtureOptions> { MovingHeadDevice.Options(testAdapter) }
        val mappingTransportConfig by value<TransportConfig?> { DmxTransportConfig() }
        val mapping by value {
            FixtureMapping(entity, mappingFixtureOptions, mappingTransportConfig)
        }
        val controller by value<Controller> {
            DirectDmxController(SimDmxDevice(FakeDmxUniverse()), FakeClock())
        }
        val fixture by value { mapping.buildFixture(controller, model) }

        context("when start channel is specified") {
            it("starts there") {
                controller.beforeFixtureResolution()
                val transport = fixture.transport as DirectDmxController.DirectDmxTransport
                expect(transport.startChannel).toEqual(0)
                expect(transport.endChannel).toEqual(9)
            }
        }
    }
})