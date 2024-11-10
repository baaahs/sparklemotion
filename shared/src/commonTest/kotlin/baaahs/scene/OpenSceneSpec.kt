package baaahs.scene

import baaahs.controllers.FakeController
import baaahs.controllers.FakeControllerManager
import baaahs.controllers.FakeMappingManager
import baaahs.describe
import baaahs.device.PixelArrayDevice
import baaahs.dmx.DmxTransportConfig
import baaahs.fixtures.FixtureMapping
import baaahs.fixtures.TransportConfig
import baaahs.gl.override
import baaahs.kotest.value
import baaahs.model.Model
import baaahs.modelForTest
import baaahs.testModelSurface
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly

@Suppress("MoveLambdaOutsideParentheses")
class OpenSceneSpec : DescribeSpec({
    describe<OpenScene> {
        context("#relevantFixtureMappings") {
            val surface123 by value<Model.Entity?> { testModelSurface("surface", expectedPixelCount = 123) }
            val model by value { modelForTest(listOfNotNull(surface123)) }
            val transportConfig by value<TransportConfig?> { DmxTransportConfig(0) }
            val controller by value { FakeController("fake", null, null) }
            val controllerFixtureMappingData by value<FixtureMappingData?> { null }
            val controllerFixtures by value { listOfNotNull(controllerFixtureMappingData) }
            val controllerConfig by value<ControllerConfig> {
                FakeControllerManager.Config(controllers = listOf(controller), fixtures = controllerFixtures)
            }
            val legacyMappingData by value<FixtureMapping?> { null }
            val mappingManager by value {
                FakeMappingManager(mapOf(controller.controllerId to listOfNotNull(legacyMappingData)))
            }
            val openScene by value { OpenScene(model, mapOf(controller.controllerId to controllerConfig)) }

            val relevantMappings by value {
                openScene.relevantFixtureMappings(controller, mappingManager)
            }

            context("with no mappings anywhere") {
                it("returns no mappings") {
                    relevantMappings.shouldBeEmpty()
                }
            }

            // no, because there are no mappings, so the config doesn't show up anywhere
//            context("when the controller has a default fixture config") {
//                override(controller) { FakeController("fake", PixelArrayDevice.Config(111), null) }
//
//                it("returns the default") {
//                    expect(relevantMappings).shouldContainExactly(
//                        FixtureMapping(null, PixelArrayDevice, PixelArrayDevice.Config(111), transportConfig)
//                    )
//                }
//            }

            context("with fixture mappings configured for the controller") {
                override(controllerFixtureMappingData) { FixtureMappingData(fixtureOptions = PixelArrayDevice.Options(111)) }

                it("returns the correct mappings") {
                    relevantMappings.shouldContainExactly(
                        FixtureMapping(null, PixelArrayDevice.Options(111))
                    )
                }
            }

            context("with legacy mapping data") {
                override(legacyMappingData) {
                    FixtureMapping(null, PixelArrayDevice.Options(222), transportConfig)
                }

                it("returns the correct mapping") {
                    relevantMappings.shouldContainExactly(
                        FixtureMapping(null, PixelArrayDevice.Options(222), transportConfig)
                    )
                }
            }

            context("when the controller has anonymous mapping") {
                override(controller) {
                    FakeController(
                        "fake", null,
                        anonymousFixtureMapping = FixtureMapping(null, PixelArrayDevice.Options(333))
                    )
                }

                it("returns it") {
                    relevantMappings.shouldContainExactly(
                        FixtureMapping(null, PixelArrayDevice.Options(333))
                    )
                }
            }
        }
    }

})