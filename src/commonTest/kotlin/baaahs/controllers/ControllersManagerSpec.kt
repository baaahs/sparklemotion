package baaahs.controllers

import baaahs.*
import baaahs.controller.*
import baaahs.device.MovingHeadDevice
import baaahs.device.PixelArrayDevice
import baaahs.dmx.Shenzarpy
import baaahs.fixtures.*
import baaahs.geom.Vector3F
import baaahs.gl.override
import baaahs.glsl.LinearSurfacePixelStrategy
import baaahs.io.ByteArrayWriter
import baaahs.mapping.MappingManager
import baaahs.model.Model
import baaahs.model.ModelManager
import baaahs.model.MovingHead
import baaahs.scene.*
import baaahs.ui.Observable
import baaahs.util.Time
import ch.tutteli.atrium.api.fluent.en_GB.isEmpty
import ch.tutteli.atrium.api.fluent.en_GB.isSameAs
import ch.tutteli.atrium.api.fluent.en_GB.size
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import org.spekframework.spek2.Spek
import kotlin.random.Random

@OptIn(InternalCoroutinesApi::class, ExperimentalCoroutinesApi::class)
object ControllersManagerSpec : Spek({
    describe<ControllersManager> {
        val modelEntity by value<Model.Entity> {
            val quad1x1 = listOf(
                Vector3F(1f, 1f, 1f), Vector3F(2f, 2f, 1f),
                Vector3F(1f, 2f, 2f), Vector3F(2f, 1f, 2f)
            )
            testModelSurface("panel", 2, vertices = quad1x1)
        }
        val model by value<Model?> { fakeModel(modelEntity) }
        val fakeController by value { FakeController("c1") }
        val mappingResults by value {
            mapOf(fakeController.controllerId to listOf(FixtureMapping(modelEntity, null, null)))
        }
        val fakeControllerConfig by value {
            FakeControllerManager.Config(
                fakeController.controllerId.controllerType, fakeController.controllerId.id,
                listOf(fakeController), emptyList()
            )
        }
        val scene by value {
            model?.let { OpenScene(it, controllers = mapOf(fakeController.controllerId to fakeControllerConfig) ) }
        }
        val mappingManager by value { FakeMappingManager(mappingResults, false) }
        val fakeControllerMgr by value { FakeControllerManager() }
        val controllerManagers by value { listOf(fakeControllerMgr) }
        val fixtureListener by value { FakeFixtureListener() }
        val sceneMonitor by value { SceneMonitor(scene) }
        val controllersManager by value {
            ControllersManager(controllerManagers, mappingManager, sceneMonitor, listOf(fixtureListener))
        }

        context("when model and mapping data haven't loaded yet") {
            override(sceneMonitor) { SceneMonitor() }
            beforeEachTest { controllersManager.start() }

            it("starts controllers when start() is called") {
                expect(fakeControllerMgr.hasStarted).toBe(true)
            }

            it("waits for mapping data before processing controllers from controller managers") {
                expect(fixtureListener.changes).isEmpty()

                sceneMonitor.onChange(scene) // Load model.
                expect(fixtureListener.changes).isEmpty()

                mappingManager.dataHasLoaded = true
                expect(fixtureListener.changes).size.toEqual(1)
            }

            it("waits for model before processing controllers from controller managers") {
                expect(fixtureListener.changes).isEmpty()

                mappingManager.dataHasLoaded = true
                expect(fixtureListener.changes).isEmpty()

                sceneMonitor.onChange(scene) // Load model.
                expect(fixtureListener.changes).size.toEqual(1)
            }

            it("only starts controller managers once") {
                sceneMonitor.onChange(scene) // Load model.
                mappingManager.dataHasLoaded = true
                mappingManager.notifyChanged()
                mappingManager.notifyChanged()
                expect(fakeControllerMgr.hasStarted).toBe(true)
            }
        }

        context("when mapping data has loaded") {
            beforeEachTest {
                mappingManager.dataHasLoaded = true
                controllersManager.start()
                sceneMonitor.onChange(scene) // Load model.
            }

            it("calls start() on controller managers") {
                expect(fakeControllerMgr.hasStarted).toBe(true)
            }
        }

        context("when controllers are reported") {
            beforeEachTest {
                mappingManager.dataHasLoaded = true
                sceneMonitor.onChange(scene) // Load model.
                controllersManager.start()
            }

            val change by value { fixtureListener.changes.only("fixture change") }
            val addedFixture by value { change.added.only("added fixture") }

            context("with no mapping results") {
                value(mappingResults) { mapOf(fakeController.controllerId to emptyList()) }

                it("ignores the controller") {
                    expect(fixtureListener.changes).isEmpty()
                }

                context("and the controller specifies an anonymous fixture") {
                    value(fakeController) {
                        FakeController(
                            "c1",
                            anonymousFixtureMapping = FixtureMapping(
                                null, 3, null,
                                PixelArrayDevice.Config(
                                    pixelFormat = PixelArrayDevice.PixelFormat.RGB8,
                                    pixelArrangement = LinearSurfacePixelStrategy(Random(1))
                                )
                            )
                        )
                    }

                    it("adds the anonymous fixture") {
                        expect(addedFixture.modelEntity).toBe(null)
                        expect(addedFixture.deviceType).toBe(modelEntity.deviceType)
                        expect(addedFixture.transport).isSameAs(fakeController.transport)
                    }

                    it("generates pixel positions within the model bounds") {
                        expect(addedFixture.pixelCount).toBe(3)
                        expect(addedFixture.pixelLocations)
                            .toBe(LinearSurfacePixelStrategy(Random(1)).forUnknownEntity(3, model!!))
                    }
                }
            }

            context("with a mapping result pointing to an entity") {
                value(mappingResults) {
                    mapOf(fakeController.controllerId to listOf(
                        FixtureMapping(modelEntity, 3, null,
                        PixelArrayDevice.Config(3, PixelArrayDevice.PixelFormat.RGB8,
                            pixelArrangement = LinearSurfacePixelStrategy(Random(1))))
                    )
                    )
                }

                it("finds model entity mapping for the controller and creates a fixture") {
                    expect(addedFixture.modelEntity).toBe(modelEntity)
                    expect(addedFixture.deviceType).toBe(modelEntity.deviceType)
                    expect(addedFixture.transport).isSameAs(fakeController.transport)
                }

                it("generates pixel positions within the entity bounds") {
                    expect(addedFixture.pixelCount).toBe(3)
                    expect(addedFixture.pixelLocations)
                        .toBe(LinearSurfacePixelStrategy(Random(1)).forKnownEntity(3, modelEntity, model!!))
                }

                context("with pixel location data") {
                    value(mappingResults) {
                        mapOf(fakeController.controllerId to listOf(
                            FixtureMapping(modelEntity, 3, listOf(
                            Vector3F(1f, 1f, 1f),
                            Vector3F(2f, 2f, 3f),
                            Vector3F(3f, 2f, 3f),
                        ))
                        ))
                    }

                    it("uses the pixel data") {
                        expect(addedFixture.pixelCount).toBe(3)
                        expect(addedFixture.pixelLocations)
                            .toBe(listOf(
                                Vector3F(1f, 1f, 1f),
                                Vector3F(2f, 2f, 3f),
                                Vector3F(3f, 2f, 3f),
                            ))
                    }
                }
            }

            context("when controller specifies its pixel count") {
                value(fakeController) {
                    FakeController("c1", FixtureMapping(null, 59, null))
                }

                it("finds model entity mapping for the controller and creates a fixture") {
                    expect(addedFixture.modelEntity).toBe(modelEntity)
                    expect(addedFixture.pixelCount).toBe(59)
                    expect(addedFixture.pixelLocations).toBe(emptyList())
                    expect(addedFixture.deviceType).toBe(modelEntity.deviceType)
                    expect(addedFixture.transport).isSameAs(fakeController.transport)
                }
            }

            context("when the device type specifies defaultPixelCount") {
                value(modelEntity) { MovingHead("mover", baseDmxChannel = 1, adapter = Shenzarpy) }

                it("creates an appropriate fixture") {
                    expect(addedFixture.modelEntity).toBe(modelEntity)
                    expect(addedFixture.pixelCount).toBe(1)
                    expect(addedFixture.pixelLocations).toBe(emptyList())
                    expect(addedFixture.deviceType).toBe(MovingHeadDevice)
                    expect(addedFixture.transport).isSameAs(fakeController.transport)
                }
            }

            context("when the scene is closed") {
                beforeEachTest {
                    sceneMonitor.onChange(null)
                }

                override(change) { fixtureListener.changes.getOrNull(1) ?: error("no second change") }
                val removedFixture by value { change.removed.only("removed fixture") }

                it("removes the previously added fixture and controller") {
                    expect(removedFixture.modelEntity).toBe(modelEntity)
                    expect(removedFixture.pixelCount).toBe(0)
                    expect(removedFixture.pixelLocations).toBe(emptyList())
                    expect(removedFixture.deviceType).toBe(modelEntity.deviceType)
                    expect(removedFixture.transport).isSameAs(fakeController.transport)

                    expect(fakeControllerMgr.controllers).isEmpty()
                }
            }
        }
    }
})

class FakeMappingManager(
    data: Map<ControllerId, List<FixtureMapping>> = mutableMapOf(),
    dataHasLoaded: Boolean = true
) : Observable(), MappingManager {
    val data = data.toMutableMap()
    override var dataHasLoaded: Boolean = dataHasLoaded
        set(value) {
            field = value
            notifyChanged()
        }

    override suspend fun start(): Unit = TODO("not implemented")

    override fun findMappings(controllerId: ControllerId): List<FixtureMapping> {
        return data[controllerId] ?: emptyList()
    }

    override fun getAllControllerMappings(): Map<ControllerId, List<FixtureMapping>> {
        TODO("not implemented")
    }
}

class FakeControllerManager(
    startingControllers: List<FakeController> = emptyList()
) : BaseControllerManager("FAKE") {
    var hasStarted: Boolean = false
    val controllers = startingControllers.toMutableList()

    override fun start() {
        if (hasStarted) error("Already started!")
        hasStarted = true
        controllers.forEach { notifyListeners { onAdd(it) } }
    }

    override fun onConfigChange(controllerConfigs: Map<ControllerId, ControllerConfig>) {
        if (hasStarted) {
            controllers.forEach { notifyListeners { onRemove(it) } }
        }

        controllers.clear()

        controllers.addAll(controllerConfigs.values.flatMap { config -> (config as Config).controllers })
        if (hasStarted) {
            controllers.forEach { notifyListeners { onAdd(it) } }
        }
    }

    override fun stop() {
        TODO("not implemented")
    }

    class Config(
        override val controllerType: String,
        override val title: String,
        val controllers: List<FakeController>,
        override val fixtures: List<FixtureMappingData>
    ) : ControllerConfig {
        override fun edit(): MutableControllerConfig = TODO("not implemented")
    }
}

class FakeController(
    val name: String,
    override val defaultFixtureMapping: FixtureMapping? = null,
    private val anonymousFixtureMapping: FixtureMapping? = null
) : Controller {
    override val state: ControllerState = object : ControllerState() {
        override val title: String get() = TODO("not implemented")
        override val address: String get() = TODO("not implemented")
        override val onlineSince: Time get() = TODO("not implemented")
    }
    val transport = FakeTransport()
    override val controllerId: ControllerId = ControllerId(type, name)
    override fun createTransport(
        entity: Model.Entity?, fixtureConfig: FixtureConfig, transportConfig: TransportConfig?, pixelCount: Int
    ): Transport = transport

    override fun getAnonymousFixtureMappings(): List<FixtureMapping> = listOfNotNull(anonymousFixtureMapping)

    inner class FakeTransport : Transport {
        override val name: String get() = this@FakeController.name
        override val controller: Controller
            get() = this@FakeController
        override fun deliverBytes(byteArray: ByteArray) {}

        override fun deliverComponents(
            componentCount: Int,
            bytesPerComponent: Int,
            fn: (componentIndex: Int, buf: ByteArrayWriter) -> Unit
        ) {}
    }

    companion object {
        val type = "FAKE"
    }
}

class FakeModelManager(val model: Model) : Observable(), ModelManager {
    override fun findEntity(name: String): Model.Entity? {
        TODO("not implemented")
    }
}

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