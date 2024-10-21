package baaahs.sm.brain

import baaahs.*
import baaahs.controller.ControllerId
import baaahs.controller.ControllersManager
import baaahs.controller.SpyFixtureListener
import baaahs.controller.entity
import baaahs.controllers.FakeMappingManager
import baaahs.device.PixelArrayDevice
import baaahs.device.PixelFormat
import baaahs.gl.override
import baaahs.net.TestNetwork
import baaahs.scene.ControllerConfig
import baaahs.scene.FixtureMappingData
import baaahs.scene.OpenScene
import baaahs.scene.SceneMonitor
import baaahs.sm.brain.proto.BrainHelloMessage
import ch.tutteli.atrium.api.fluent.en_GB.containsExactly
import ch.tutteli.atrium.api.fluent.en_GB.feature
import ch.tutteli.atrium.api.fluent.en_GB.isEmpty
import ch.tutteli.atrium.api.verbs.expect
import org.spekframework.spek2.Spek

object BrainManagerSpec : Spek({
    describe<BrainManager> {
        val link by value { TestNetwork().link("brainlink") }
        val model by value { modelForTest(entity("surface1")) }
        val controllerConfigs by value { mapOf<ControllerId, ControllerConfig>() }
        val brain1Id by value { ControllerId(BrainManager.controllerTypeName, "brain1") }
        val scene by value { OpenScene(model, controllerConfigs) }
        val brainManager by value {
            BrainManager(
                PermissiveFirmwareDaddy(),
                link,
                Pinky.NetworkStats(),
                FakeClock(),
                ImmediateDispatcher
            )
        }
        val listener by value { SpyFixtureListener() }
        val brain1Fixtures by value { emptyList<FixtureMappingData>() }
        val mappingManager by value { FakeMappingManager(emptyMap()) }
        val controllersManager by value {
            ControllersManager(listOf(brainManager), mappingManager, SceneMonitor(scene), listOf(listener))
        }

        beforeEachTest {
            controllersManager.start()
            brainManager.onConfigChange(controllerConfigs)
        }

        context("with no declared controllers") {
            it("no notifications are sent") {
                expect(listener.added).isEmpty()
            }
        }

        context("with config for a controller") {
            override(controllerConfigs) {
                mapOf(
                    brain1Id to BrainControllerConfig(
                        "Brain Controller",
                        fixtures = brain1Fixtures,
                        defaultFixtureOptions = PixelArrayDevice.Options(123)
                    )
                )
            }

            val surface1Mapping by value { fixtureMappingData("surface1") }
            override(brain1Fixtures) { listOf(surface1Mapping) }

            context("without any word from the actual controller") {
                it("no notifications are sent") {
                    expect(listener.added).isEmpty()
                }
            }

            context("when a brain dials in") {
                beforeEachTest {
                    brainManager.foundBrain(link.myAddress, BrainHelloMessage("brain1", null))
                }

                it("notifies listener of controller") {
                    expect(listener.added.map { it.name }).containsExactly("surface1@Brain:brain1")
                }

                it("applies that config") {
                    val fixture = listener.added.only("fixture")
                    expect(fixture) {
                        feature { f(it::componentCount) }.toEqual(123)
                    }
                }
            }
        }
    }
})

private fun fixtureMappingData(
    entityName: String,
    pixelCount: Int? = null
) =
    FixtureMappingData(
        entityName,
        PixelArrayDevice.Options(pixelCount, PixelFormat.RGB8)
    )
