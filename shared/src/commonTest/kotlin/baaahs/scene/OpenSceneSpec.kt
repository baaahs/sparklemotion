package baaahs.scene

import baaahs.controllers.FakeController
import baaahs.controllers.FakeMappingManager
import baaahs.controllers.MutableFakeControllerConfig
import baaahs.describe
import baaahs.device.PixelArrayDevice
import baaahs.dmx.DmxTransportConfig
import baaahs.fixtures.FixtureMapping
import baaahs.fixtures.TransportConfig
import baaahs.gl.override
import baaahs.kotest.value
import baaahs.model.SurfaceDataForTest
import baaahs.sceneDataForTest
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly

class OpenSceneSpec : DescribeSpec({
    describe<OpenScene> {
        context("#relevantFixtureMappings") {
            val surface123 by value { SurfaceDataForTest("surface", expectedPixelCount = 123) }
            val transportConfig by value<TransportConfig?> { DmxTransportConfig(0) }
            val controllerFixtureMappingData by value<MutableFixtureMapping?> { null }
            val controllerFixtures by value { listOfNotNull(controllerFixtureMappingData) }
            val controllerConfig by value { MutableFakeControllerConfig("fake", null, null) }
            val legacyMappingData by value<FixtureMapping?> { null }
            val mappingManager by value {
                FakeMappingManager(mapOf(controllerConfig.likelyControllerId to listOfNotNull(legacyMappingData)))
            }
            val openScene by value {
                sceneDataForTest(surface123) {
                    controllers.put(controllerConfig.likelyControllerId, controllerConfig)
                    fixtureMappings.put(controllerConfig.likelyControllerId, controllerFixtures.toMutableList())
                }.open()
            }
            val controller by value { FakeController(controllerConfig.likelyControllerId.id) }

            val relevantMappings by value {
                openScene.relevantFixtureMappings(controller.controllerId, mappingManager, getAnonymousFixtureMappings = {
                    controller.getAnonymousFixtureMappings()
                })
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
                override(controllerFixtureMappingData) {
                    MutableFixtureMapping(surface123.edit(), PixelArrayDevice.MutableOptions(111, null, null, null), null)
                }

                it("returns the correct mappings") {
                    relevantMappings.shouldContainExactly(
                        FixtureMapping(openScene.model.findEntityByName("surface"), PixelArrayDevice.Options(111))
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