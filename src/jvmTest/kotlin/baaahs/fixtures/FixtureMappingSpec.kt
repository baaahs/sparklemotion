package baaahs.fixtures

import baaahs.controller.Controller
import baaahs.controllers.FakeController
import baaahs.describe
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
            val mappingFixtureConfig by value<FixtureConfig?> { PixelArrayDevice.Config() }
            val mappingTransportConfig by value<TransportConfig?> { DmxTransportConfig(0, 0) }
            val mapping by value {
                FixtureMapping(entity, PixelArrayDevice, mappingFixtureConfig, mappingTransportConfig)
            }
            val controller by value<Controller> {
                FakeController("fake", null, null)
            }
            val fixture by value { mapping.buildFixture(controller, model) }

            context("with a mapped entity") {
                it("creates a fixture for that entity") {
                    expect(fixture).isA<PixelArrayFixture>() {
                        feature({ f(it::modelEntity) }) { toEqual(entity) }
                        feature({ f(it::pixelCount) }) { toEqual(1) }
                        feature({ f(it::componentCount) }) { toEqual(1) }
                        feature({ f(it::gammaCorrection) }) { toEqual(1f) }
                        feature({ f(it::pixelLocations) }) { toEqual(emptyList()) }
                    }
                }

                context("which specifies a pixel count") {
                    override(entity) { testModelSurface("surface", expectedPixelCount = 123) }

                    it("creates a fixture with that many pixels") {
                        expect(fixture.componentCount) { toEqual(123) }
                    }

                    context("but explicit controller mapping overrides it") {
                        override(mappingFixtureConfig) { PixelArrayDevice.Config(pixelCount = 321) }

                        it("creates a fixture with that many pixels") {
                            expect(fixture.componentCount) { toEqual(321) }
                        }
                    }
                }
            }

        }
    }
})