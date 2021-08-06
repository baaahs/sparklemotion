package baaahs.controller

import baaahs.*
import baaahs.controllers.FakeMappingManager
import baaahs.device.PixelArrayDevice
import baaahs.fixtures.Fixture
import baaahs.fixtures.FixtureListener
import baaahs.geom.Vector3F
import baaahs.gl.override
import baaahs.mapper.*
import baaahs.model.LightBar
import baaahs.net.TestNetwork
import baaahs.scene.ControllerConfig
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
        val model by value { ModelForTest(entity("bar1"), entity("bar2")) }
        val sacnManager by value { SacnManager(link, TestRig().server, ImmediateDispatcher, FakeClock()) }
        val listener by value { SpyFixtureListener() }
        val mappings by value { mapOf<ControllerId, List<FixtureMapping>>() }
        val mappingManager by value { FakeMappingManager().also { it.data.putAll(mappings); it.dataHasLoaded = true } }
        val controllersManager by value { ControllersManager(listOf(sacnManager), mappingManager, TestModel, listener) }
        val configs by value { listOf<ControllerConfig>() }

        beforeEachTest {
            sacnManager.onConfigChange(configs)
            controllersManager.start()
        }

        context("with no declared controllers") {
            it("no notifications are sent") {
                expect(listener.added).isEmpty()
            }
        }

        context("with a controller has two fixtures") {
            override(configs) {
                listOf(SacnControllerConfig("sacn1", "SACN Controller", "192.168.1.150", 1))
            }

            val bar1Mapping by value { fixtureMapping(model, "bar1", 0, 2) }
            val bar2Mapping by value { fixtureMapping(model, "bar2", 6, 2) }

            override(mappings) {
                mapOf(ControllerId(SacnManager.controllerTypeName, "sacn1") to listOf(bar1Mapping, bar2Mapping))
            }

            val bar1Fixture by value { listener.added[0] }
            val bar2Fixture by value { listener.added[1] }

            it("notifies listener of controller") {
                expect(listener.added.map { it.name }).containsExactly("bar1@SACN:sacn1", "bar2@SACN:sacn1")
            }

            context("when a frame is sent") {
                val bar1Bytes by value { pixelColors(1, 2) }
                val bar2Bytes by value { pixelColors(3, 2) }

                beforeEachTest {
                    controllersManager.beforeFrame()
                    bar1Fixture.transport.deliverBytes(bar1Bytes)
                    bar2Fixture.transport.deliverBytes(bar2Bytes)
                    controllersManager.afterFrame()
                }

                it("sends a DMX frame to the universe") {
                    expect(link.packetsToSend.size).toEqual(1)
                    val dataFrame = SacnLink.readDataFrame(link.packetsToSend.first().data)
                    expect(dataFrame.universe).toEqual(1)
                    expect(dataFrame.channels.toList())
                        .toEqual(listOf<Byte>(1, 2, 3, 2, 2, 3, 3, 2, 3, 4, 2, 3).paddedTo(512))
                }

                context("spanning multiple universes") {
                    // A single DMX universe has 512 channels, which can accommodate 170 and 2/3 pixels.
                    override(bar1Mapping) { fixtureMapping(model, "bar1", 0, 180) }
                    override(bar2Mapping) { fixtureMapping(model, "bar2", 540, 2) }
                    override(bar1Bytes) { pixelColors(1, 180) }
                    override(bar2Bytes) { pixelColors(602, 2) }
                    override(configs) {
                        listOf(SacnControllerConfig("sacn1", "SACN Controller", "192.168.1.150", 2))
                    }

                    it("sends a DMX frame to multiple universes") {
                        expect(link.packetsToSend.size).toEqual(2)

                        val universe1Frame = link.packetsToSend[0]
                        val universe1Data = SacnLink.readDataFrame(universe1Frame.data)
                        expect(universe1Data.universe).toEqual(1)
                        expect(universe1Data.channels.toList()).toEqual(
                            bar1Bytes.toList().subList(0, 512)
                        )

                        val universe2Frame = link.packetsToSend[1]
                        val universe2Data = SacnLink.readDataFrame(universe2Frame.data)
                        expect(universe2Data.universe).toEqual(2)
                        expect(universe2Data.channels.toList()).toEqual(
                            (bar1Bytes.toList().subList(512, 512 + 28) + bar2Bytes.toList()).paddedTo(512)
                        )
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
                            channels = MappingSession.SurfaceData.Channels(0, 511)
                        )
                    )
                )

            }
            val mappingData by value { SessionMappingResults(model, listOf(mappingSession)) }

            it("transforms it into FixtureConfigs") {
                val data = mappingData.dataForController(ControllerId("SACN", "sacn1"))!!
                val transportConfig = data.transportConfig as SacnTransportConfig
                expect(transportConfig.startChannel..transportConfig.endChannel).toEqual(0..511)
            }
        }
    }
})

private fun fixtureMapping(model: ModelForTest, entityName: String, baseChannel: Int, pixelCount: Int) =
    FixtureMapping(
        model.findEntity(entityName), pixelCount, null,
        transportConfig = SacnTransportConfig(baseChannel, pixelCount * 3)
    )

fun entity(name: String) = LightBar(name, name, PixelArrayDevice, Vector3F.origin, Vector3F(1f, 1f, 1f))

fun pixelColors(startingAt: Int, count: Int): ByteArray {
    return (startingAt until startingAt + count).flatMap { i ->
        listOf(i.toByte(), 2, 3)
    }.toByteArray()
}

fun List<Byte>.paddedTo(size: Int): List<Byte> {
    return if (this.size > size) this.subList(0, size - 1) else this +
            List(size - this.size) { 0 }
}

class SpyFixtureListener : FixtureListener {
    val added = arrayListOf<Fixture>()
    val removed = arrayListOf<Fixture>()

    override fun fixturesChanged(addedFixtures: Collection<Fixture>, removedFixtures: Collection<Fixture>) {
        added.addAll(addedFixtures)
        removed.addAll(removedFixtures)
    }
}
