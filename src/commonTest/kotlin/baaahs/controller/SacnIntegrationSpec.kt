package baaahs.controller

import baaahs.*
import baaahs.controllers.FakeMappingManager
import baaahs.device.PixelArrayDevice
import baaahs.dmx.DmxTransportConfig
import baaahs.fixtures.Fixture
import baaahs.fixtures.FixtureListener
import baaahs.fixtures.Transport
import baaahs.geom.Vector3F
import baaahs.gl.override
import baaahs.mapper.MappingSession
import baaahs.mapper.SessionMappingResults
import baaahs.model.LightBar
import baaahs.net.TestNetwork
import baaahs.scene.ControllerConfig
import baaahs.scene.FixtureMappingData
import baaahs.scene.OpenScene
import baaahs.scene.SceneMonitor
import ch.tutteli.atrium.api.fluent.en_GB.containsExactly
import ch.tutteli.atrium.api.fluent.en_GB.isEmpty
import ch.tutteli.atrium.api.verbs.expect
import kotlinx.coroutines.InternalCoroutinesApi
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

@Suppress("unused")
@OptIn(InternalCoroutinesApi::class)
object SacnIntegrationSpec : Spek({
    describe("SACN integration") {
        val link by value { TestNetwork().link("sacn") }
        val model by value { modelForTest(entity("bar1"), entity("bar2")) }
        val controllerConfigs by value { mapOf<ControllerId, ControllerConfig>() }
        val sacn1Id by value { ControllerId(SacnManager.controllerTypeName, "sacn1") }
        val scene by value { OpenScene(model, controllerConfigs) }
        val sacnManager by value { SacnManager(link, ImmediateDispatcher, FakeClock()) }
        val listener by value { SpyFixtureListener() }
        val sacn1Fixtures by value { emptyList<FixtureMappingData>() }
        val mappingManager by value { FakeMappingManager(emptyMap()) }
        val controllersManager by value {
            ControllersManager(listOf(sacnManager), mappingManager, SceneMonitor(scene), listOf(listener))
        }

        beforeEachTest {
            controllersManager.start()
            sacnManager.onConfigChange(controllerConfigs)
        }

        context("with no declared controllers") {
            it("no notifications are sent") {
                expect(listener.added).isEmpty()
            }
        }

        context("with a controller which has two fixtures") {
            override(controllerConfigs) {
                mapOf(sacn1Id to SacnControllerConfig("SACN Controller", "192.168.1.150", 1, sacn1Fixtures))
            }

            val bar1Mapping by value { fixtureMappingData("bar1", 0, 2, false) }
            val bar2Mapping by value { fixtureMappingData("bar2", 6, 2, false) }

            override(sacn1Fixtures) { listOf(bar1Mapping, bar2Mapping) }

            val bar1Fixture by value { listener.added[0] }
            val bar2Fixture by value { listener.added[1] }

            it("notifies listener of controller") {
                expect(listener.added.map { it.name }).containsExactly("bar1@SACN:sacn1", "bar2@SACN:sacn1")
            }

            context("when a frame is sent") {
                val bar1Bytes by value { PixelColors(1, 2) }
                val bar2Bytes by value { PixelColors(3, 2) }

                beforeEachTest {
                    controllersManager.beforeFrame()
                    bar1Bytes.deliverTo(bar1Fixture.transport)
                    bar2Bytes.deliverTo(bar2Fixture.transport)
                    controllersManager.afterFrame()
                }

                it("sends a DMX frame to the universe") {
                    expect(link.packetsToSend.size).toEqual(1)
                    val dataFrame = SacnLink.readDataFrame(link.packetsToSend.first().data)
                    expect(dataFrame.universe).toEqual(1)
                    expect(dataFrame.channels.toList())
                        .toEqual(listOf(1, 2, 3, 2, 2, 3, 3, 2, 3, 4, 2, 3))
                }

                context("spanning multiple universes") {
                    // A single DMX universe has 512 channels, which can accommodate 170 and 2/3 pixels.
                    override(bar1Mapping) { fixtureMappingData("bar1", 0, 180, false) }
                    override(bar2Mapping) { fixtureMappingData("bar2", 540, 2, false) }
                    override(bar1Bytes) { PixelColors(1, 180) }
                    override(bar2Bytes) { PixelColors(602, 2) }
                    override(controllerConfigs) {
                        mapOf(sacn1Id to SacnControllerConfig("SACN Controller", "192.168.1.150", 2, sacn1Fixtures))
                    }

                    it("sends a DMX frame to multiple universes") {
                        expect(link.packetsToSend.size).toEqual(2)

                        val universe1Frame = link.packetsToSend[0]
                        val universe1Data = SacnLink.readDataFrame(universe1Frame.data)
                        expect(universe1Data.universe).toEqual(1)
                        expect(universe1Data.channels.toList()).toEqual(
                            bar1Bytes.bytes.subList(0, 512)
                        )

                        val universe2Frame = link.packetsToSend[1]
                        val universe2Data = SacnLink.readDataFrame(universe2Frame.data)
                        expect(universe2Data.universe).toEqual(2)
                        expect(universe2Data.channels.toList()).toEqual(
                            (bar1Bytes.bytes.subList(512, 512 + 28) + bar2Bytes.bytes)
                        )
                    }


                    context("when components must start at universe boundaries") {
                        override(bar1Mapping) { fixtureMappingData("bar1", 0, 180, true) }
                        override(bar2Mapping) { fixtureMappingData("bar2", 542, 2, true) }

                        it("sends a DMX frame to multiple universes, with pixels honoring universe boundaries") {
                            expect(link.packetsToSend.size).toEqual(2)

                            val universe1Frame = link.packetsToSend[0]
                            val universe1Data = SacnLink.readDataFrame(universe1Frame.data)
                            expect(universe1Data.universe).toEqual(1)
                            expect(universe1Data.channels.toList()).toEqual(
                                bar1Bytes.bytes.subList(0, 510)
                            )

                            val universe2Frame = link.packetsToSend[1]
                            val universe2Data = SacnLink.readDataFrame(universe2Frame.data)
                            expect(universe2Data.universe).toEqual(2)
                            expect(universe2Data.channels.toList()).toEqual(
                                (bar1Bytes.bytes.subList(510, 510 + 30) + bar2Bytes.bytes)
                            )
                        }

                        context("when universes are skipped") {
                            override(bar1Mapping) { fixtureMappingData("bar1", 540, 2, true) }
                            override(bar2Mapping) { fixtureMappingData("bar2", 530, 2, true) }
                            override(bar1Bytes) { PixelColors(1, 2) }
                            override(bar2Bytes) { PixelColors(3, 2) }

                            it("sends a DMX frame to active universes only") {
                                expect(link.packetsToSend.size).toEqual(1)

                                val universe2Frame = link.packetsToSend[0]
                                val universe2Data = SacnLink.readDataFrame(universe2Frame.data)
                                expect(universe2Data.universe).toEqual(2)
                                expect(universe2Data.channels.toList()).toEqual(
                                    ByteArray(18).toList() +  // starting at 512
                                            bar2Bytes.bytes.subList(0, 6) + // starting at 530
                                            ByteArray(4).toList() +    // starting at 540
                                            bar1Bytes.bytes
                                )
                            }
                        }
                    }
                }
            }
        }

        describe("SACN fixture config from legacy mapping files") {
            val mappingSession by value {
                MappingSession(
                    0.0, listOf(
                        MappingSession.SurfaceData(
                            "SACN", "sacn1", "bar1",
                            pixelCount = 3,
                            channels = MappingSession.SurfaceData.Channels(0, 511)
                        )
                    )
                )

            }
            val mappingData by value { SessionMappingResults(scene, listOf(mappingSession)) }

            it("transforms it into FixtureConfigs") {
                val data = mappingData.dataForController(sacn1Id)
                    .only("fixture config")

                val fixtureConfig = data.fixtureConfig as PixelArrayDevice.Config
                expect(fixtureConfig.componentCount).toEqual(3)
                expect(fixtureConfig.pixelFormat).toEqual(null)

                val transportConfig = data.transportConfig as DmxTransportConfig
                expect(transportConfig.startChannel..transportConfig.endChannel).toEqual(0..511)
            }
        }
    }
})

private fun fixtureMappingData(
    entityName: String,
    baseChannel: Int,
    pixelCount: Int,
    componentsStartAtUniverseBoundaries: Boolean
) =
    FixtureMappingData(
        entityName,
        PixelArrayDevice.Config(pixelCount, PixelArrayDevice.PixelFormat.RGB8),
        DmxTransportConfig(baseChannel, pixelCount * 3, componentsStartAtUniverseBoundaries)
    )

fun entity(name: String) = LightBar(name, name, startVertex = Vector3F.origin, endVertex = Vector3F.unit3d)

class PixelColors(private val startingAt: Int, private val count: Int) {
    val bytes get() =
        (startingAt until startingAt + count).flatMap { i ->
            listOf(i.toByte(), 2, 3)
        }

    fun deliverTo(transport: Transport) {
        transport.deliverComponents(count, 3) { componentIndex, buf ->
            buf.writeBytes((startingAt + componentIndex).toByte(), 2.toByte(), 3.toByte())
        }
    }
}

class SpyFixtureListener : FixtureListener {
    val added = arrayListOf<Fixture>()
    val removed = arrayListOf<Fixture>()

    override fun fixturesChanged(addedFixtures: Collection<Fixture>, removedFixtures: Collection<Fixture>) {
        added.addAll(addedFixtures)
        removed.addAll(removedFixtures)
    }
}
