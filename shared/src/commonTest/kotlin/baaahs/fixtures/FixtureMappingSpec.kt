package baaahs.fixtures

import baaahs.*
import baaahs.controller.Controller
import baaahs.controller.ControllerId
import baaahs.controllers.FakeController
import baaahs.controllers.FakeMappingManager
import baaahs.controllers.FakeTransportConfig
import baaahs.device.EnumeratedPixelLocations
import baaahs.device.PixelArrayDevice
import baaahs.dmx.DirectDmxController
import baaahs.geom.Vector3F
import baaahs.gl.override
import baaahs.kotest.value
import baaahs.model.EntityData
import baaahs.model.LightBar
import baaahs.model.LightBarData
import baaahs.scene.FixtureMappingData
import baaahs.scene.MutableDirectDmxControllerConfig
import baaahs.scene.MutableEntity
import baaahs.scene.MutableFixtureMapping
import baaahs.sim.FakeDmxUniverse
import baaahs.sim.SimDmxDevice
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.properties.shouldHaveValue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf

@Suppress("unused")
class FixtureMappingSpec : DescribeSpec({
    describe<FixtureMapping> {
        context("buildFixture") {
            val entity by value<EntityData?> { testModelSurfaceData("surface", expectedPixelCount = null) }
            val controllerId by value { ControllerId(FakeController.type, "fake") }
            val mappingFixtureOptions by value<FixtureOptions> { PixelArrayDevice.Options() }
            val mappingTransportConfig by value<TransportConfig?> { FakeTransportConfig(777) }
            val mapping by value { MutableFixtureMapping(entity?.edit(), mappingFixtureOptions.edit(), mappingTransportConfig?.edit()) }
            val scene by value { sceneDataForTest(listOfNotNull(entity)) {
                controllers[controllerId] = MutableDirectDmxControllerConfig("uart1", null, null)
                fixtureMappings[controllerId] = mutableListOf(mapping)
            } }
            val openScene by value { scene.open() }
            val controllerDefaultFixtureOptions by value<FixtureOptions?> { null }
            val controllerDefaultTransportConfig by value<TransportConfig?> { null }
            val controller by value<Controller> {
                FakeController("fake", controllerDefaultFixtureOptions, controllerDefaultTransportConfig)
            }
            val fixtures by value { openScene.resolveFixtures(controller, FakeMappingManager()) }
            val fixture by value { fixtures.only("fixture") }

            context("with a mapped entity") {
                it("creates a fixture for that entity") {
                    fixture.modelEntity!!.title.shouldBe(entity!!.title)
                    fixture::componentCount.shouldHaveValue(1)
                    fixture.fixtureConfig.shouldBeTypeOf<PixelArrayDevice.Config> {
                        it::gammaCorrection.shouldHaveValue(1f)
                        it::pixelLocations.shouldHaveValue(EnumeratedPixelLocations())
                    }

                    fixture.transport.config.shouldBeTypeOf<FakeTransportConfig> {
                        it::startChannel.shouldHaveValue(777)
                    }
                }

                context("whose model entity specifies a surface fixture config") {
                    override(entity) { testModelSurfaceData("surface", expectedPixelCount = 123) }

                    it("creates a fixture with that config") {
                        fixture.componentCount shouldBe 123
                    }

                    context("but explicit fixture mapping overrides it") {
                        override(mappingFixtureOptions) { PixelArrayDevice.Options(pixelCount = 321) }

                        it("the mapping's config takes precedence") {
                            fixture.componentCount shouldBe 321
                        }
                    }

                    context("whose controller's default fixture config specifies a fixture config") {
                        override(controllerDefaultFixtureOptions) { PixelArrayDevice.Options(456) }

                        it("the model entity's config takes precedence") {
                            fixture.componentCount shouldBe 123
                        }

                        context("but explicit fixture mapping overrides it") {
                            override(mappingFixtureOptions) { PixelArrayDevice.Options(pixelCount = 321) }

                            it("the mapping's config takes precedence") {
                                fixture.componentCount shouldBe 321
                            }
                        }
                    }
                }

                context("whose model entity is a pixel array") {
                    override(entity) {
                        LightBarData("light bar", startVertex = Vector3F.origin, endVertex = Vector3F.unit3d)
                    }
                    override(mappingFixtureOptions) { PixelArrayDevice.Options(pixelCount = 3) }

                    it("creates a fixture with that config") {
                        fixture.componentCount shouldBe 3
                        fixture.fixtureConfig
                            .shouldBeTypeOf<PixelArrayDevice.Config> {
                                it.pixelLocations shouldBe                                     EnumeratedPixelLocations(
                                    Vector3F.origin,
                                    Vector3F(.5f, .5f, .5f),
                                    Vector3F.unit3d
                                )
                            }
                    }
                }

                context("whose controller's default fixture config specifies a pixel count") {
                    override(controllerDefaultFixtureOptions) { PixelArrayDevice.Options(456) }

                    it("creates a fixture with that config") {
                        fixture.componentCount shouldBe 456
                    }

                    context("but explicit fixture mapping overrides it") {
                        override(mappingFixtureOptions) { PixelArrayDevice.Options(pixelCount = 321) }

                        it("the mapping's config takes precedence") {
                            fixture.componentCount shouldBe 321
                        }
                    }
                }

                context("whose controller has a default transport config") {
                    override(controllerDefaultTransportConfig) { FakeTransportConfig(555) }

                    it("the mapping's config takes precedence") {
                        fixture.transport.config.shouldBeTypeOf<FakeTransportConfig> {
                            it::startChannel shouldHaveValue 777
                        }
                    }

                    context("and the fixture mapping doesn't override it") {
                        override(mappingTransportConfig) { null }

                        it("the default config is used") {
                            fixture.transport.config.shouldBeTypeOf<FakeTransportConfig> {
                                it::startChannel shouldHaveValue 555
                            }
                        }
                    }
                }
            }
        }
    }
})