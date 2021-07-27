package baaahs.controllers

import baaahs.TestModelSurface
import baaahs.controller.*
import baaahs.describe
import baaahs.fakeModel
import baaahs.fixtures.*
import baaahs.mapper.ControllerId
import baaahs.mapper.FixtureMapping
import baaahs.model.Model
import baaahs.only
import baaahs.ui.Observable
import ch.tutteli.atrium.api.fluent.en_GB.isEmpty
import ch.tutteli.atrium.api.fluent.en_GB.isSameAs
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import org.spekframework.spek2.Spek

object ControllersManagerSpec : Spek({
    describe<ControllersManager> {
        val mappingManager by value { FakeMappingManager() }
        val panel by value { TestModelSurface("panel") }
        val modelManager by value { FakeModelManager(fakeModel(panel)) }
        val fakeControllerMgr by value { FakeControllerManager() }
        val controllerManagers by value { listOf(fakeControllerMgr) }
        val fixtureListener by value { FakeFixtureListener() }
        val controllersManager by value {
            ControllersManager(controllerManagers, mappingManager, modelManager, fixtureListener)
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

            context("with no mapping results") {
                value(mappingResults) { listOf() }

                it("ignores the controller") {
                    expect(fixtureListener.changes).isEmpty()
                }

                context("and the controller specifies an anonymous fixture") {
                    value(fakeController) {
                        FakeController(
                            "c1",
                            anonymousFixtureMapping = FixtureMapping(
                                null, 59, null, PixelArrayDevice.Config()
                            )
                        )
                    }

                    it("adds the anonymous fixture") {
                        val change = fixtureListener.changes.only("fixture changes")
                        val addedFixture = change.added.only("added fixture")
                        expect(addedFixture.modelEntity).toBe(null)
                        expect(addedFixture.pixelCount).toBe(59)
                        expect(addedFixture.pixelLocations).toBe(emptyList())
                        expect(addedFixture.deviceType).toBe(panel.deviceType)
                        expect(addedFixture.transport).isSameAs(fakeController.transport)
                    }
                }
            }

            context("with a mapping result pointing to an entity") {
                value(mappingResults) { listOf(FixtureMapping(panel, null, null)) }

                it("finds model entity mapping for the controller and creates a fixture") {
                    val change = fixtureListener.changes.only("fixture changes")
                    val addedFixture = change.added.only("added fixture")
                    expect(addedFixture.modelEntity).toBe(panel)
                    expect(addedFixture.pixelCount).toBe(0)
                    expect(addedFixture.pixelLocations).toBe(emptyList())
                    expect(addedFixture.deviceType).toBe(panel.deviceType)
                    expect(addedFixture.transport).isSameAs(fakeController.transport)
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

    override fun findMappings(controllerId: ControllerId): List<FixtureMapping> {
        return data[controllerId] ?: emptyList()
    }
}

class FakeControllerManager : ControllerManager {
    var hasStarted: Boolean = false
    var controllerListener: ControllerListener? = null
    val myControllers = mutableListOf<Controller>()

    override fun start(controllerListener: ControllerListener) {
        if (hasStarted) error("Already started!")

        hasStarted = true
        this.controllerListener = controllerListener

        if (myControllers.isNotEmpty()) {
            controllerListener.onChange(myControllers, emptyList())
        }
    }

    override fun stop() {
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
        entity: Model.Entity?, fixtureConfig: FixtureConfig, deviceOffset: Int, pixelCount: Int
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