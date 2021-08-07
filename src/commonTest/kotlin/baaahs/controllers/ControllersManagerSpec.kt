package baaahs.controllers

import baaahs.TestModelSurface
import baaahs.controller.Controller
import baaahs.controller.ControllerListener
import baaahs.controller.ControllerManager
import baaahs.controller.ControllersManager
import baaahs.describe
import baaahs.device.PixelArrayDevice
import baaahs.fakeModel
import baaahs.fixtures.Fixture
import baaahs.fixtures.FixtureConfig
import baaahs.fixtures.FixtureListener
import baaahs.fixtures.Transport
import baaahs.geom.Vector3F
import baaahs.glsl.LinearSurfacePixelStrategy
import baaahs.mapper.ControllerId
import baaahs.mapper.FixtureMapping
import baaahs.mapper.TransportConfig
import baaahs.mapping.MappingManager
import baaahs.model.Model
import baaahs.model.ModelManager
import baaahs.only
import baaahs.scene.ControllerConfig
import baaahs.ui.Observable
import ch.tutteli.atrium.api.fluent.en_GB.isEmpty
import ch.tutteli.atrium.api.fluent.en_GB.isSameAs
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import org.spekframework.spek2.Spek
import kotlin.random.Random

object ControllersManagerSpec : Spek({
    describe<ControllersManager> {
        val mappingManager by value { FakeMappingManager() }
        val panel by value {
            val quad1x1 = listOf(
                Vector3F(1f, 1f, 1f), Vector3F(2f, 2f, 1f),
                Vector3F(1f, 2f, 2f), Vector3F(2f, 1f, 2f)
            )
            TestModelSurface("panel", 2, vertices = quad1x1)
        }
        val model by value { fakeModel(panel) }
        val modelManager by value { FakeModelManager(model) }
        val fakeControllerMgr by value { FakeControllerManager() }
        val controllerManagers by value { listOf(fakeControllerMgr) }
        val fixtureListener by value { FakeFixtureListener() }
        val controllersManager by value {
            ControllersManager(controllerManagers, mappingManager, model, fixtureListener)
        }
        val fakeController by value { FakeController("c1") }

        context("when mapping data hasn't loaded yet") {
            beforeEachTest { controllersManager.start() }

            it("waits for mapping data before calling start() on controller managers") {
                expect(fakeControllerMgr.hasStarted).toBe(false)

                mappingManager.notifyChanged()
                expect(fakeControllerMgr.hasStarted).toBe(false)

                mappingManager.dataHasLoaded = true
                mappingManager.notifyChanged()
                expect(fakeControllerMgr.hasStarted).toBe(true)
            }

            it("only starts controller managers once") {
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
            }

            it("calls start() on controller managers") {
                expect(fakeControllerMgr.hasStarted).toBe(true)
            }
        }

        context("when controllers are reported") {
            val mappingResults by value {
                listOf(FixtureMapping(panel, null, null))
            }

            beforeEachTest {
                fakeControllerMgr.myControllers.add(fakeController)
                mappingManager.data[fakeController.controllerId] = mappingResults
                mappingManager.dataHasLoaded = true
                controllersManager.start()
            }

            val change by value { fixtureListener.changes.only("fixture changes") }
            val addedFixture by value { change.added.only("added fixture") }

            context("with no mapping results") {
                value(mappingResults) { listOf<FixtureMapping>() }

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
                        expect(addedFixture.deviceType).toBe(panel.deviceType)
                        expect(addedFixture.transport).isSameAs(fakeController.transport)
                    }

                    it("generates pixel positions within the model bounds") {
                        expect(addedFixture.pixelCount).toBe(3)
                        expect(addedFixture.pixelLocations)
                            .toBe(LinearSurfacePixelStrategy(Random(1)).forUnknownEntity(3, model))
                    }
                }
            }

            context("with a mapping result pointing to an entity") {
                value(mappingResults) {
                    listOf(FixtureMapping(panel, 3, null,
                        PixelArrayDevice.Config(3, PixelArrayDevice.PixelFormat.RGB8,
                            pixelArrangement = LinearSurfacePixelStrategy(Random(1)))))
                }

                it("finds model entity mapping for the controller and creates a fixture") {
                    expect(addedFixture.modelEntity).toBe(panel)
                    expect(addedFixture.deviceType).toBe(panel.deviceType)
                    expect(addedFixture.transport).isSameAs(fakeController.transport)
                }

                it("generates pixel positions within the entity bounds") {
                    expect(addedFixture.pixelCount).toBe(3)
                    expect(addedFixture.pixelLocations)
                        .toBe(LinearSurfacePixelStrategy(Random(1)).forKnownEntity(3, panel, model))
                }

                context("with pixel location data") {
                    value(mappingResults) { listOf(FixtureMapping(panel, 3, listOf(
                        Vector3F(1f, 1f, 1f),
                        Vector3F(2f, 2f, 3f),
                        Vector3F(3f, 2f, 3f),
                    ))) }

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
                    val change = fixtureListener.changes.only("fixture changes")
                    val addedFixture = change.added.only("added fixture")
                    expect(addedFixture.modelEntity).toBe(panel)
                    expect(addedFixture.pixelCount).toBe(59)
                    expect(addedFixture.pixelLocations).toBe(emptyList())
                    expect(addedFixture.deviceType).toBe(panel.deviceType)
                    expect(addedFixture.transport).isSameAs(fakeController.transport)
                }
            }
        }
    }
})

class FakeMappingManager : Observable(), MappingManager {
    val data = mutableMapOf<ControllerId, List<FixtureMapping>>()

    override var dataHasLoaded: Boolean = false

    override suspend fun start(): Unit = TODO("not implemented")

    override fun findMappings(controllerId: ControllerId): List<FixtureMapping> {
        return data[controllerId] ?: emptyList()
    }

    override fun getAllControllerMappings(): Map<ControllerId, List<FixtureMapping>> {
        TODO("not implemented")
    }
}

class FakeControllerManager : ControllerManager {
    var hasStarted: Boolean = false
    var controllerListener: ControllerListener? = null
    val myControllers = mutableListOf<Controller>()

    override val controllerType: String
        get() = "FAKE"

    override fun start(controllerListener: ControllerListener) {
        if (hasStarted) error("Already started!")

        hasStarted = true
        this.controllerListener = controllerListener

        myControllers.forEach { controllerListener.onAdd(it) }
    }

    override fun onConfigChange(controllerConfigs: Map<String, ControllerConfig>) {
        TODO("not implemented")
    }

    override fun stop() {
        TODO("not implemented")
    }

    override fun logStatus() {
        TODO("not implemented")
    }
}

class FakeController(
    val name: String,
    override val fixtureMapping: FixtureMapping? = null,
    private val anonymousFixtureMapping: FixtureMapping? = null
) : Controller {
    val transport = FakeTransport()
    override val controllerId: ControllerId = ControllerId("FAKE", name)
    override fun createTransport(
        entity: Model.Entity?, fixtureConfig: FixtureConfig, transportConfig: TransportConfig?, pixelCount: Int
    ): Transport = transport

    override fun getAnonymousFixtureMappings(): List<FixtureMapping> = listOfNotNull(anonymousFixtureMapping)

    inner class FakeTransport : Transport {
        override val name: String get() = this@FakeController.name
        override fun deliverBytes(byteArray: ByteArray) {}
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

    class Changes(val added: Collection<Fixture>, val removed: Collection<Fixture>)
}