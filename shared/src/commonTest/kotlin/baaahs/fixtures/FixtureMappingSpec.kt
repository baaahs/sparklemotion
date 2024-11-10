package baaahs.fixtures

import baaahs.controller.Controller
import baaahs.controllers.FakeController
import baaahs.describe
import baaahs.device.EnumeratedPixelLocations
import baaahs.device.PixelArrayDevice
import baaahs.dmx.DmxTransportConfig
import baaahs.geom.Vector3F
import baaahs.gl.override
import baaahs.kotest.value
import baaahs.model.LightBar
import baaahs.model.Model
import baaahs.modelForTest
import baaahs.testModelSurface
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.*
import io.kotest.matchers.properties.shouldHaveValue
import io.kotest.matchers.types.shouldBeTypeOf

@Suppress("unused")
class FixtureMappingSpec : DescribeSpec({
    describe<FixtureMapping> {
        context("buildFixture") {
            val entity by value<Model.Entity?> { testModelSurface("surface", expectedPixelCount = null) }
            val model by value { modelForTest(listOfNotNull(entity)) }
            val mappingFixtureOptions by value<FixtureOptions> { PixelArrayDevice.Options() }
            val mappingTransportConfig by value<TransportConfig?> { DmxTransportConfig(777) }
            val mapping by value { FixtureMapping(entity, mappingFixtureOptions, mappingTransportConfig) }
            val controllerDefaultFixtureOptions by value<FixtureOptions?> { null }
            val controllerDefaultTransportConfig by value<TransportConfig?> { null }
            val controller by value<Controller> {
                FakeController("fake", controllerDefaultFixtureOptions, controllerDefaultTransportConfig)
            }
            val fixture by value { mapping.buildFixture(controller, model) }

            context("with a mapped entity") {
                it("creates a fixture for that entity") {
                    fixture::modelEntity.shouldHaveValue(entity)
                    fixture::componentCount.shouldHaveValue(1)
                    fixture.fixtureConfig.shouldBeTypeOf<PixelArrayDevice.Config> {
                        it::gammaCorrection.shouldHaveValue(1f)
                        it::pixelLocations.shouldHaveValue(EnumeratedPixelLocations())
                    }

                    fixture.transport.config.shouldBeTypeOf<DmxTransportConfig> {
                        it::startChannel.shouldHaveValue(777)
                    }
                }

                context("whose model entity specifies a surface fixture config") {
                    override(entity) { testModelSurface("surface", expectedPixelCount = 123) }

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
                        LightBar("light bar", startVertex = Vector3F.origin, endVertex = Vector3F.unit3d)
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
                    override(controllerDefaultTransportConfig) { DmxTransportConfig(555) }

                    it("the mapping's config takes precedence") {
                        fixture.transport.config.shouldBeTypeOf<DmxTransportConfig> {
                            it::startChannel shouldHaveValue 777
                        }
                    }

                    context("and the fixture mapping doesn't override it") {
                        override(mappingTransportConfig) { null }

                        it("the default config is used") {
                            fixture.transport.config.shouldBeTypeOf<DmxTransportConfig> {
                                it::startChannel shouldHaveValue 555
                            }
                        }
                    }
                }
            }
        }
    }
})