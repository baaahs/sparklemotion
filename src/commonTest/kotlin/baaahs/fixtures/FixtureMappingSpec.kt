package baaahs.fixtures

import baaahs.controller.Controller
import baaahs.controllers.FakeController
import baaahs.describe
import baaahs.device.EnumeratedPixelLocations
import baaahs.device.PixelArrayDevice
import baaahs.dmx.DmxTransportConfig
import baaahs.gl.override
import baaahs.model.Model
import baaahs.modelForTest
import baaahs.testModelSurface
import baaahs.toEqual
import ch.tutteli.atrium.api.fluent.en_GB.feature
import ch.tutteli.atrium.api.fluent.en_GB.isA
import ch.tutteli.atrium.api.verbs.expect
import org.spekframework.spek2.Spek

object FixtureMappingSpec : Spek({
    describe<FixtureMapping> {
        context("buildFixture") {
            val entity by value<Model.Entity?> { testModelSurface("surface", expectedPixelCount = null) }
            val model by value { modelForTest(listOfNotNull(entity)) }
            val mappingFixtureOptions by value<FixtureOptions> { PixelArrayDevice.Options() }
            val mappingTransportConfig by value<TransportConfig?> { DmxTransportConfig(777) }
            val mapping by value {
                FixtureMapping(entity, mappingFixtureOptions, mappingTransportConfig)
            }
            val controllerDefaultFixtureOptions by value<FixtureOptions?> { null }
            val controllerDefaultTransportConfig by value<TransportConfig?> { null }
            val controller by value<Controller> {
                FakeController("fake", controllerDefaultFixtureOptions, controllerDefaultTransportConfig)
            }
            val fixture by value { mapping.buildFixture(controller, model) }

            context("with a mapped entity") {
                it("creates a fixture for that entity") {
                    expect(fixture) {
                        feature { f(it::modelEntity) }.toEqual(entity)
                        feature { f(it::componentCount) }.toEqual(1)
                        feature { f(it::fixtureConfig) }.isA<PixelArrayDevice.Config> {
                            feature { f(it::gammaCorrection) }.toEqual(1f)
                            feature { f(it::pixelLocations) }.toEqual(EnumeratedPixelLocations())
                        }
                    }

                    expect(fixture.transport.config).isA<DmxTransportConfig> {
                        feature { f(it::startChannel) }.toEqual(777)
                    }
                }

                context("whose model entity specifies a fixture config") {
                    override(entity) { testModelSurface("surface", expectedPixelCount = 123) }

                    it("creates a fixture with that config") {
                        expect(fixture.componentCount) { toEqual(123) }
                    }

                    context("but explicit fixture mapping overrides it") {
                        override(mappingFixtureOptions) { PixelArrayDevice.Options(pixelCount = 321) }

                        it("the mapping's config takes precedence") {
                            expect(fixture.componentCount) { toEqual(321) }
                        }
                    }

                    context("whose controller's default fixture config specifies a fixture config") {
                        override(controllerDefaultFixtureOptions) { PixelArrayDevice.Options(456) }

                        it("the model entity's config takes precedence") {
                            expect(fixture.componentCount) { toEqual(123) }
                        }

                        context("but explicit fixture mapping overrides it") {
                            override(mappingFixtureOptions) { PixelArrayDevice.Options(pixelCount = 321) }

                            it("the mapping's config takes precedence") {
                                expect(fixture.componentCount) { toEqual(321) }
                            }
                        }
                    }
                }

                context("whose controller's default fixture config specifies a pixel count") {
                    override(controllerDefaultFixtureOptions) { PixelArrayDevice.Options(456) }

                    it("creates a fixture with that config") {
                        expect(fixture.componentCount) { toEqual(456) }
                    }

                    context("but explicit fixture mapping overrides it") {
                        override(mappingFixtureOptions) { PixelArrayDevice.Options(pixelCount = 321) }

                        it("the mapping's config takes precedence") {
                            expect(fixture.componentCount) { toEqual(321) }
                        }
                    }
                }

                context("whose controller has a default transport config") {
                    override(controllerDefaultTransportConfig) { DmxTransportConfig(555) }

                    it("the mapping's config takes precedence") {
                        expect(fixture.transport.config).isA<DmxTransportConfig> {
                            feature { f(it::startChannel) }.toEqual(777)
                        }
                    }

                    context("and the fixture mapping doesn't override it") {
                        override(mappingTransportConfig) { null }

                        it("the default config is used") {
                            expect(fixture.transport.config).isA<DmxTransportConfig> {
                                feature { f(it::startChannel) }.toEqual(555)
                            }
                        }
                    }
                }
            }
        }
    }
})