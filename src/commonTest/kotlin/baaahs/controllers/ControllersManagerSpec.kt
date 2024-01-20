package baaahs.controllers

import baaahs.controller.*
import baaahs.describe
import baaahs.device.EnumeratedPixelLocations
import baaahs.device.MovingHeadDevice
import baaahs.device.PixelArrayDevice
import baaahs.device.PixelFormat
import baaahs.dmx.DmxTransport
import baaahs.dmx.DmxTransportConfig
import baaahs.dmx.Shenzarpy
import baaahs.fakeModel
import baaahs.fixtures.*
import baaahs.geom.Vector3F
import baaahs.gl.override
import baaahs.gl.render.FixtureTypeForTest
import baaahs.glsl.LinearSurfacePixelStrategy
import baaahs.io.ByteArrayWriter
import baaahs.mapping.MappingManager
import baaahs.model.FakeModelEntity
import baaahs.model.Model
import baaahs.model.MovingHead
import baaahs.only
import baaahs.scene.*
import baaahs.toEqual
import baaahs.ui.Observable
import ch.tutteli.atrium.api.fluent.en_GB.*
import ch.tutteli.atrium.api.verbs.expect
import kotlinx.datetime.Instant
import org.spekframework.spek2.Spek
import kotlin.random.Random

@Suppress("unused")
object ControllersManagerSpec : Spek({
    describe<ControllersManager> {
        val modelFixtureType by value { FixtureTypeForTest() }
        val modelEntity by value<Model.Entity> { FakeModelEntity("panel", modelFixtureType) }
        val model by value<Model?> { fakeModel(modelEntity) }
        val fakeController by value { FakeController("c1") }
        val legacyMappings by value {
            mapOf(
                fakeController.controllerId to
                        listOf(FixtureMapping(modelEntity, modelEntity.fixtureType.emptyOptions))
            )
        }
        val fakeControllerConfig by value {
            FakeControllerManager.Config(
                fakeController.controllerId.controllerType, fakeController.controllerId.id,
                listOf(fakeController), emptyList()
            )
        }
        val scene by value {
            model?.let { OpenScene(it, controllers = mapOf(fakeController.controllerId to fakeControllerConfig)) }
        }
        val mappingManager by value { FakeMappingManager(legacyMappings, false) }
        val fakeControllerMgr by value { FakeControllerManager() }
        val controllerManagers by value { listOf(fakeControllerMgr) }
        val fixtureListener by value { FakeFixtureListener() }
        val changes by value { fixtureListener.changes }
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

            val firstChange by value { changes.only("fixture change") }
            val addedFixture by value { firstChange.added.only("added fixture") }

            context("with no mapping results") {
                value(legacyMappings) { mapOf(fakeController.controllerId to emptyList()) }

                it("ignores the controller") {
                    expect(fixtureListener.changes).isEmpty()
                }

                context("and the controller specifies an anonymous fixture") {
                    value(fakeController) {
                        FakeController(
                            "c1",
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
                        expect(addedFixture.modelEntity).toBe(null)
                        expect(addedFixture.fixtureType).toBe(PixelArrayDevice)
                        expect(addedFixture.transport).isSameAs(fakeController.transport)
                    }

                    it("generates pixel positions within the model bounds") {
                        expect(addedFixture) {
                            feature(Fixture::componentCount).toBe(3)
                            feature(Fixture::fixtureConfig)
                                .isA<PixelArrayDevice.Config> {
                                    feature(PixelArrayDevice.Config::pixelLocations)
                                        .toBe(expectedPixelLocations)
                                }
                        }
                    }
                }
            }

            context("with a mapping result pointing to an entity") {
                value(legacyMappings) {
                    mapOf(
                        fakeController.controllerId to listOf(
                            FixtureMapping(
                                modelEntity, PixelArrayDevice.Options(
                                    3, PixelFormat.RGB8,
                                    pixelArrangement = LinearSurfacePixelStrategy(Random(1))
                                )
                            )
                        )
                    )
                }

                it("finds model entity mapping for the controller and creates a fixture") {
                    expect(addedFixture.modelEntity).toBe(modelEntity)
                    expect(addedFixture.fixtureType).toBe(PixelArrayDevice)
                    expect(addedFixture.transport).isSameAs(fakeController.transport)
                }

                it("generates pixel positions within the entity bounds") {
                    expect(addedFixture) {
                        feature(Fixture::componentCount).toBe(3)
                        feature(Fixture::fixtureConfig)
                            .isA<PixelArrayDevice.Config> {
                                feature(PixelArrayDevice.Config::pixelLocations)
                                    .toBe(EnumeratedPixelLocations(Vector3F.origin, Vector3F.origin, Vector3F.origin))
                            }
                    }
                }

                context("with pixel location data") {
                    value(legacyMappings) {
                        mapOf(
                            fakeController.controllerId to listOf(
                                FixtureMapping(
                                    modelEntity, PixelArrayDevice.Options(
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
                        expect(addedFixture) {
                            feature(Fixture::componentCount).toBe(3)
                            feature(Fixture::fixtureConfig)
                                .isA<PixelArrayDevice.Config> {
                                    feature(PixelArrayDevice.Config::pixelLocations)
                                        .toBe(
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
            }

            context("when controller provides a default fixture config") {
                value(fakeController) {
                    FakeController("c1", modelFixtureType.Options(59, 3))
                }

                it("finds model entity mapping for the controller and creates a fixture with options from the model") {
                    expect(addedFixture) {
                        feature(Fixture::modelEntity).toBe(modelEntity)
                        feature(Fixture::componentCount).toBe(59)
                        feature(Fixture::fixtureConfig)
                            .isA<FixtureTypeForTest.Config> {
                                feature(FixtureTypeForTest.Config::pixelLocations)
                                    .toBe(EnumeratedPixelLocations())
                            }
                        feature(Fixture::fixtureType).toBe(modelEntity.fixtureType)
                        feature(Fixture::transport).isSameAs(fakeController.transport)
                    }
                }

                context("and the fixture type also provides a default fixture config") {
                    override(modelFixtureType) {
                        FixtureTypeForTest().apply {
                            defaultOptions = Options(4321, bytesPerComponent = 3)
                        }
                    }

                    it("finds model entity mapping for the controller and creates a fixture with the model's ") {
                        expect(addedFixture) {
                            feature(Fixture::modelEntity).toBe(modelEntity)
                            feature(Fixture::componentCount).toBe(59)
                            feature(Fixture::fixtureConfig)
                                .isA<FixtureTypeForTest.Config> {
                                    feature(FixtureTypeForTest.Config::pixelLocations)
                                        .toBe(EnumeratedPixelLocations())
                                }
                            feature(Fixture::fixtureType).toBe(modelEntity.fixtureType)
                            feature(Fixture::transport).isSameAs(fakeController.transport)
                        }
                    }
                }

                context("when controller provides a default fixture config for a different fixture type") {
                    value(fakeController) { FakeController("c1", PixelArrayDevice.Options(4321)) }

                    it("ignores it, because we use the most specific fixture type to filter out others") {
                        expect(addedFixture) {
                            feature(Fixture::modelEntity).toBe(modelEntity)
                            feature(Fixture::componentCount).toBe(1)
                            feature(Fixture::fixtureConfig)
                                .isA<FixtureTypeForTest.Config> {
                                    feature(FixtureTypeForTest.Config::pixelLocations)
                                        .toBe(EnumeratedPixelLocations())
                                }
                            feature(Fixture::fixtureType).toBe(modelEntity.fixtureType)
                            feature(Fixture::transport).isSameAs(fakeController.transport)
                        }
                    }
                }
            }

            context("when the fixture type specifies defaultPixelCount") {
                value(modelEntity) { MovingHead("mover", baseDmxChannel = 1, adapter = Shenzarpy) }

                it("creates an appropriate fixture") {
                    expect(addedFixture.modelEntity).toBe(modelEntity)
                    expect(addedFixture.componentCount).toBe(1)
                    expect(addedFixture.fixtureType).toBe(MovingHeadDevice)
                    expect(addedFixture.fixtureConfig).isA<MovingHeadDevice.Config>()
                    expect(addedFixture.transport).isSameAs(fakeController.transport)
                }
            }

            context("when the scene is closed") {
                beforeEachTest {
                    sceneMonitor.onChange(null)
                }

                val secondChange by value { fixtureListener.changes.getOrNull(1) ?: error("no second change") }
                val removedFixture by value { secondChange.removed.only("removed fixture") }

                it("removes the previously added fixture and controller") {
                    val previouslyAddedFixture = changes.first().added.only("added fixture")
                    expect(removedFixture).isSameAs(previouslyAddedFixture)

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
        override val controllerType: String = "FAKE",
        override val title: String = "fake controller",
        val controllers: List<FakeController> = emptyList(),
        override val fixtures: List<FixtureMappingData> = emptyList(),
        override val defaultFixtureOptions: FixtureOptions? = null,
        override val defaultTransportConfig: TransportConfig? = null
    ) : ControllerConfig {
        override val emptyTransportConfig: TransportConfig
            get() = DmxTransportConfig()

        override fun edit(): MutableControllerConfig = TODO("not implemented")
        override fun createFixturePreview(
            fixtureOptions: FixtureOptions,
            transportConfig: TransportConfig
        ): FixturePreview = TODO("not implemented")
    }
}

class FakeController(
    val name: String,
    override val defaultFixtureOptions: FixtureOptions? = null,
    override val defaultTransportConfig: TransportConfig? = null,
    private val anonymousFixtureMapping: FixtureMapping? = null
) : Controller {
    override val state: ControllerState = object : ControllerState() {
        override val title: String get() = TODO("not implemented")
        override val address: String get() = TODO("not implemented")
        override val onlineSince: Instant? get() = TODO("not implemented")
        override val firmwareVersion: String get() = TODO("not implemented")
        override val lastErrorMessage: String get() = TODO("Not yet implemented")
        override val lastErrorAt: Instant? get() = TODO("Not yet implemented")
    }
    override val transportType: TransportType
        get() = DmxTransport

    lateinit var transport: FakeTransport
    override val controllerId: ControllerId = ControllerId(type, name)
    override fun createTransport(
        entity: Model.Entity?,
        fixtureConfig: FixtureConfig,
        transportConfig: TransportConfig?
    ): Transport = FakeTransport(transportConfig).also { transport = it }

    override fun getAnonymousFixtureMappings(): List<FixtureMapping> = listOfNotNull(anonymousFixtureMapping)

    inner class FakeTransport(
        override val config: TransportConfig?
    ) : Transport {
        override val name: String get() = this@FakeController.name
        override val controller: Controller
            get() = this@FakeController

        override fun deliverBytes(byteArray: ByteArray) {}

        override fun deliverComponents(
            componentCount: Int,
            bytesPerComponent: Int,
            fn: (componentIndex: Int, buf: ByteArrayWriter) -> Unit
        ) {
        }
    }

    companion object {
        const val type = "FAKE"
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