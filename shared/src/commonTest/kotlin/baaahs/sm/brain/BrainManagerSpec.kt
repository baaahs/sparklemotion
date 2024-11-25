package baaahs.sm.brain

import baaahs.*
import baaahs.controller.ControllerId
import baaahs.controller.ControllersManager
import baaahs.controller.SpyFixtureListener
import baaahs.controllers.FakeMappingManager
import baaahs.device.PixelArrayDevice
import baaahs.device.PixelFormat
import baaahs.gl.override
import baaahs.kotest.value
import baaahs.net.TestNetwork
import baaahs.scene.MutableBrainControllerConfig
import baaahs.scene.MutableControllerConfig
import baaahs.scene.MutableFixtureMapping
import baaahs.scene.SceneMonitor
import baaahs.sm.brain.proto.BrainHelloMessage
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.properties.shouldHaveValue
import kotlinx.coroutines.CoroutineScope

class BrainManagerSpec : DescribeSpec({
    describe<BrainManager> {
        val link by value { TestNetwork().link("brainlink") }
        val surface1 by value { entityDataForTest("surface1") }
        val controllerConfigs by value { mapOf<ControllerId, MutableControllerConfig>() }
        val brain1Id by value { ControllerId(BrainManager.controllerTypeName, "brain1") }
        val scene by value { sceneDataForTest(surface1) {
            controllers.putAll(controllerConfigs)
        } }
        val openScene by value { scene.open() }
        val brainManager by value {
            BrainManager(
                PermissiveFirmwareDaddy(),
                link,
                Pinky.NetworkStats(),
                FakeClock(),
                CoroutineScope(ImmediateDispatcher)
            )
        }
        val listener by value { SpyFixtureListener() }
        val brain1Fixtures by value { emptyList<MutableFixtureMapping>() }
        val mappingManager by value { FakeMappingManager(emptyMap()) }
        val controllersManager by value {
            ControllersManager(listOf(brainManager), mappingManager, SceneMonitor(openScene), listOf(listener))
        }

        beforeEach {
            controllersManager.start()
            brainManager.onConfigChange(openScene.controllers)
        }

        xcontext("with no declared controllers") {
            it("no notifications are sent") {
                listener.added.shouldBeEmpty()
            }
        }

        context("with config for a controller") {
            override(controllerConfigs) {
                mapOf(
                    brain1Id to MutableBrainControllerConfig(
                        "Brain 1",
                        null,
                        fixtures = brain1Fixtures.toMutableList(),
                        defaultFixtureOptions = PixelArrayDevice.MutableOptions(123, null, null, null),
                        null
                    )
                )
            }

            val surface1Mapping by value {
                MutableFixtureMapping(
                    surface1.edit(),
                    PixelArrayDevice.MutableOptions(null, PixelFormat.RGB8, null, null),
                    null
                )
            }
            override(brain1Fixtures) { listOf(surface1Mapping) }

            xcontext("without any word from the actual controller") {
                it("no notifications are sent") {
                    listener.added.shouldBeEmpty()
                }
            }

            context("when a brain dials in") {
                beforeEach {
                    brainManager.foundBrain(link.myAddress, BrainHelloMessage("brain1", null))
                }

                it("notifies listener of controller") {
                    listener.added.map { it.name }.shouldContainExactly("surface1@Brain:brain1")
                }

                xit("applies that config") {
                    val fixture = listener.added.only("fixture")
                    fixture::componentCount shouldHaveValue 123
                }
            }
        }
    }
})

