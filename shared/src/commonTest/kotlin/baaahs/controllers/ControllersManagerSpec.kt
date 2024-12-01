package baaahs.controllers

import baaahs.controller.ControllersManager
import baaahs.describe
import baaahs.device.EnumeratedPixelLocations
import baaahs.device.MovingHeadDevice
import baaahs.device.PixelArrayDevice
import baaahs.device.PixelFormat
import baaahs.dmx.Shenzarpy
import baaahs.fixtures.Fixture
import baaahs.fixtures.FixtureListener
import baaahs.fixtures.FixtureMapping
import baaahs.geom.Vector3F
import baaahs.gl.override
import baaahs.gl.render.FixtureTypeForTest
import baaahs.glsl.LinearSurfacePixelStrategy
import baaahs.kotest.value
import baaahs.model.FakeModelEntityData
import baaahs.model.MovingHead
import baaahs.only
import baaahs.scene.SceneMonitor
import baaahs.sceneDataForTest
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.properties.shouldHaveValue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.matchers.types.shouldBeTypeOf
import kotlin.random.Random

@Suppress("unused")
class ControllersManagerSpec : DescribeSpec({
    describe<ControllersManager> {
        val modelFixtureType by value { FixtureTypeForTest() }
        val modelEntity by value { FakeModelEntityData("panel", modelFixtureType) }
        val fakeControllerConfig by value { MutableFakeControllerConfig("controller1", null, null) }
        val fakeControllerId by value { fakeControllerConfig.likelyControllerId }
        val scene by value { sceneDataForTest(modelEntity) {
            controllers.putAll(mapOf(fakeControllerId to fakeControllerConfig))
        } }
        val openScene by value { scene.open() }
        val model by value { openScene.model }
        val legacyMappings by value {
            mapOf(fakeControllerId to listOf(
                FixtureMapping(
                    model.findEntityByName(modelEntity.title),
                    modelEntity.fixtureType.emptyOptions
                ))
            )
        }
        val mappingManager by value { FakeMappingManager(legacyMappings, false) }
        val fakeControllerMgr by value { FakeControllerManager() }
        val controllerManagers by value { listOf(fakeControllerMgr) }
        val fixtureListener by value { FakeFixtureListener() }
        val changes by value { fixtureListener.changes }
        val sceneMonitor by value { SceneMonitor(openScene) }
        val controllersManager by value {
            ControllersManager(controllerManagers, mappingManager, sceneMonitor, listOf(fixtureListener))
        }
        val fakeController by value { fakeControllerMgr.controllers.only("controller") }

        xcontext("when model and mapping data haven't loaded yet") {
            override(sceneMonitor) { SceneMonitor() }
            beforeEach { controllersManager.start() }

            it("starts controllers when start() is called") {
                fakeControllerMgr.hasStarted.shouldBeTrue()
            }

            it("waits for mapping data before processing controllers from controller managers") {
                fixtureListener.changes.shouldBeEmpty()

                sceneMonitor.onChange(openScene) // Load model.
                fixtureListener.changes.shouldBeEmpty()

                mappingManager.dataHasLoaded = true
                fixtureListener.changes.size.shouldBe(1)
            }

            it("waits for model before processing controllers from controller managers") {
                fixtureListener.changes.shouldBeEmpty()

                mappingManager.dataHasLoaded = true
                fixtureListener.changes.shouldBeEmpty()

                sceneMonitor.onChange(openScene) // Load model.
                fixtureListener.changes.size.shouldBe(1)
            }

            it("only starts controller managers once") {
                sceneMonitor.onChange(openScene) // Load model.
                mappingManager.dataHasLoaded = true
                mappingManager.notifyChanged()
                mappingManager.notifyChanged()
                fakeControllerMgr.hasStarted.shouldBeTrue()
            }
        }

        xcontext("when mapping data has loaded") {
            beforeEach {
                mappingManager.dataHasLoaded = true
                controllersManager.start()
                sceneMonitor.onChange(openScene) // Load model.
            }

            it("calls start() on controller managers") {
                fakeControllerMgr.hasStarted.shouldBeTrue()
            }
        }

        context("when controllers are reported") {
            beforeEach {
                mappingManager.dataHasLoaded = true
                sceneMonitor.onChange(openScene) // Load model.
                controllersManager.start()
            }

            val firstChange by value { changes.only("fixture change") }
            val addedFixture by value { firstChange.added.only("added fixture") }

            context("with no mapping results") {
                value(legacyMappings) { mapOf(fakeControllerId to emptyList()) }

                xit("ignores the controller") {
                    fixtureListener.changes.shouldBeEmpty()
                }

                context("and the controller specifies an anonymous fixture") {
                    override(fakeControllerConfig) {
                        MutableFakeControllerConfig(
                            "controller1", null, null,
                            anonymousFixtureMapping = FixtureMapping(
                                null, PixelArrayDevice.Options(
                                    pixelCount = 3,
                                    pixelFormat = PixelFormat.RGB8,
                                    pixelArrangement = LinearSurfacePixelStrategy(Random(1))
                                )
                            )
                        )
                    }

                    val expectedPixelLocations by value {
                        val locations = LinearSurfacePixelStrategy(Random(1)).forUnknownEntity(3, model!!)
                        EnumeratedPixelLocations(locations)
                    }

                    it("adds the anonymous fixture") {
                        addedFixture.modelEntity.shouldBe(null)
                        addedFixture.fixtureType.shouldBe(PixelArrayDevice)
                        addedFixture.transport.shouldBeSameInstanceAs(fakeController.transport)
                    }

                    xit("generates pixel positions within the model bounds") {
                        addedFixture::componentCount.shouldHaveValue(3)
                        addedFixture.fixtureConfig.shouldBeTypeOf<PixelArrayDevice.Config> {
                            it.pixelLocations.shouldBe(expectedPixelLocations)
                        }
                    }
                }
            }

            xcontext("with a mapping result pointing to an entity") {
                value(legacyMappings) {
                    mapOf(
                        fakeControllerId to listOf(
                            FixtureMapping(
                                openScene.model.findEntityByName(modelEntity.title),
                                PixelArrayDevice.Options(
                                    3, PixelFormat.RGB8,
                                    pixelArrangement = LinearSurfacePixelStrategy(Random(1))
                                )
                            )
                        )
                    )
                }

                it("finds model entity mapping for the controller and creates a fixture") {
                    addedFixture.modelEntity.shouldBe(modelEntity)
                    addedFixture.fixtureType.shouldBe(PixelArrayDevice)
                    addedFixture.transport.shouldBeSameInstanceAs(fakeController.transport)
                }

                it("generates pixel positions within the entity bounds") {
                    addedFixture
                    addedFixture::componentCount.shouldHaveValue(3)
                    addedFixture.fixtureConfig.shouldBeTypeOf<PixelArrayDevice.Config> {
                        it.pixelLocations.shouldBe(
                            EnumeratedPixelLocations(
                                Vector3F.origin,
                                Vector3F.origin,
                                Vector3F.origin
                            )
                        )
                    }
                }

                context("with pixel location data") {
                    value(legacyMappings) {
                        mapOf(
                            fakeControllerId to listOf(
                                FixtureMapping(
                                    openScene.model.findEntityByName(modelEntity.title),
                                    PixelArrayDevice.Options(
                                        3,
                                        pixelLocations = listOf(
                                            Vector3F(1f, 1f, 1f),
                                            Vector3F(2f, 2f, 3f),
                                            Vector3F(3f, 2f, 3f)
                                        )
                                    )
                                )
                            )
                        )
                    }

                    it("uses the pixel data") {
                        addedFixture::componentCount.shouldHaveValue(3)
                        addedFixture.fixtureConfig
                            .shouldBeTypeOf<PixelArrayDevice.Config> {
                                it.pixelLocations.shouldBe(
                                    EnumeratedPixelLocations(
                                        Vector3F(1f, 1f, 1f),
                                        Vector3F(2f, 2f, 3f),
                                        Vector3F(3f, 2f, 3f),
                                    )
                                )
                            }
                    }
                }
            }

            xcontext("when controller provides a default fixture config") {
                value(fakeController) {
                    FakeController("controller1", modelFixtureType.Options(59, 3))
                }

                it("finds model entity mapping for the controller and creates a fixture with options from the model") {
                    addedFixture.modelEntity.shouldBe(modelEntity)
                    addedFixture.componentCount.shouldBe(59)
                    addedFixture.fixtureConfig.shouldBeTypeOf<FixtureTypeForTest.Config> {
                        it.pixelLocations.shouldBe(EnumeratedPixelLocations())
                    }
                    addedFixture.fixtureType.shouldBe(modelEntity.fixtureType)
                    addedFixture.transport.shouldBeSameInstanceAs(fakeController.transport)
                }

                context("and the fixture type also provides a default fixture config") {
                    override(modelFixtureType) {
                        FixtureTypeForTest().apply {
                            defaultOptions = Options(4321, bytesPerComponent = 3)
                        }
                    }

                    it("finds model entity mapping for the controller and creates a fixture with the model's ") {
                        addedFixture.modelEntity.shouldBe(modelEntity)
                        addedFixture.componentCount.shouldBe(59)
                        addedFixture.fixtureConfig.shouldBeTypeOf<FixtureTypeForTest.Config> {
                            it.pixelLocations.shouldBe(EnumeratedPixelLocations())
                        }
                        addedFixture.fixtureType.shouldBe(modelEntity.fixtureType)
                        addedFixture.transport.shouldBeSameInstanceAs(fakeController.transport)
                    }
                }

                context("when controller provides a default fixture config for a different fixture type") {
                    value(fakeController) { FakeController("controller1", PixelArrayDevice.Options(4321)) }

                    it("ignores it, because we use the most specific fixture type to filter out others") {
                        addedFixture.modelEntity.shouldBe(modelEntity)
                        addedFixture.componentCount.shouldBe(1)
                        addedFixture.fixtureConfig.shouldBeTypeOf<FixtureTypeForTest.Config> {
                            it.pixelLocations.shouldBe(EnumeratedPixelLocations())
                        }
                        addedFixture.fixtureType.shouldBe(modelEntity.fixtureType)
                        addedFixture.transport.shouldBeSameInstanceAs(fakeController.transport)
                    }
                }
            }

            xcontext("when the fixture type specifies defaultPixelCount") {
                value(modelEntity) { MovingHead("mover", baseDmxChannel = 1, adapter = Shenzarpy) }

                it("creates an appropriate fixture") {
                    addedFixture.modelEntity.shouldBe(modelEntity)
                    addedFixture.componentCount.shouldBe(1)
                    addedFixture.fixtureType.shouldBe(MovingHeadDevice)
                    addedFixture.fixtureConfig.shouldBeTypeOf<MovingHeadDevice.Config>()
                    addedFixture.transport.shouldBeSameInstanceAs(fakeController.transport)
                }
            }

            xcontext("when the scene is closed") {
                beforeEach {
                    sceneMonitor.onChange(null)
                }

                val secondChange by value { fixtureListener.changes.getOrNull(1) ?: error("no second change") }
                val removedFixture by value { secondChange.removed.only("removed fixture") }

                it("removes the previously added fixture and controller") {
                    val previouslyAddedFixture = changes.first().added.only("added fixture")
                    removedFixture.shouldBeSameInstanceAs(previouslyAddedFixture)

                    fakeControllerMgr.controllers.shouldBeEmpty()
                }
            }
        }
    }
})

class FakeFixtureListener : FixtureListener {
    val changes = mutableListOf<Changes>()

    override fun fixturesChanged(addedFixtures: Collection<Fixture>, removedFixtures: Collection<Fixture>) {
        changes.add(Changes(addedFixtures, removedFixtures))
    }

    data class Changes(
        val added: Collection<Fixture>,
        val removed: Collection<Fixture>,
        val stack: Exception = Exception()
    )
}