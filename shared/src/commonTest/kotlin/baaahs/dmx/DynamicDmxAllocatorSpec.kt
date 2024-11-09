package baaahs.dmx

import baaahs.describe
import baaahs.gl.override
import baaahs.kotest.value
import baaahs.toEqual
import ch.tutteli.atrium.api.fluent.en_GB.containsExactly
import ch.tutteli.atrium.api.fluent.en_GB.toThrow
import ch.tutteli.atrium.api.verbs.expect
import io.kotest.core.spec.style.DescribeSpec

@Suppress("unused")
object DynamicDmxAllocatorSpec : DescribeSpec({
    describe<DynamicDmxAllocator> {
        val universeCount by value { 3 }
        val dmxUniverses by value { DmxUniverses(universeCount) }
        val allocator by value { DynamicDmxAllocator() }

        context("when start channel is specified") {
            it("starts there") {
                expect(allocator.allocate(10, 3, DmxTransportConfig(1)))
                    .toEqual(StaticDmxMapping(1, 10, 3, true))
            }

            context("when the start channel has already been allocated") {
                it("throws an error") {
                    allocator.allocate(10, 3, DmxTransportConfig())
                    expect {
                        allocator.allocate(10, 3, DmxTransportConfig(2))
                    }.toThrow<Exception>()
                }
            }
        }

        context("when multiple fixtures per universe are allowed") {
            it("packs multiple fixtures into a single universe") {
                expect(allocator.allocate(10, 3, DmxTransportConfig()))
                    .toEqual(StaticDmxMapping(0, 10, 3, true))
                expect(allocator.allocate(10, 3, DmxTransportConfig()))
                    .toEqual(StaticDmxMapping(30, 10, 3, true))
                expect(allocator.allocate(100, 3, DmxTransportConfig()))
                    .toEqual(StaticDmxMapping(60, 100, 3, true))
                expect(allocator.allocate(10, 3, DmxTransportConfig()))
                    .toEqual(StaticDmxMapping(360, 10, 3, true))
            }
        }

        context("when each fixture gets its own universe") {
            it("skips to a new universe for each fixture") {
                expect(allocator.allocate(10, 3, DmxTransportConfig(fixtureStartsInFreshUniverse = true)))
                    .toEqual(StaticDmxMapping(0, 10, 3, true))
                expect(allocator.allocate(10, 3, DmxTransportConfig(fixtureStartsInFreshUniverse = true)))
                    .toEqual(StaticDmxMapping(512, 10, 3, true))
                expect(allocator.allocate(100, 3, DmxTransportConfig(fixtureStartsInFreshUniverse = true)))
                    .toEqual(StaticDmxMapping(1024, 100, 3, true))
                expect(allocator.allocate(10, 3, DmxTransportConfig(fixtureStartsInFreshUniverse = true)))
                    .toEqual(StaticDmxMapping(1536, 10, 3, true))
            }
        }

        context("static dmx mappings") {
            context("in a 10-channel universe") {
                override(dmxUniverses) { DmxUniverses(2, 10) }

                it("writes to channels correctly") {
                    val dmxMapping = StaticDmxMapping(0, 6, 3, true)
                    dmxMapping.writeComponents(4, 3, dmxUniverses) { componentIndex, buf ->
                        buf.writeBytes(componentIndex * 10)
                        buf.writeBytes(componentIndex * 10 + 1)
                        buf.writeBytes(componentIndex * 10 + 2)
                    }
                    expect(dmxUniverses.channels.map { it.toInt() }).containsExactly(
                        0, 1, 2, 10, 11, 12, 20, 21, 22, 0, // first universe, three components
                        30, 31, 32, 0, 0, 0, 0, 0, 0, 0 // second universe, one component
                    )
                }
            }

            context("#calculateEndChannel") {
                override(dmxUniverses) { DmxUniverses(3, 10) }

                it("000_______") {
                    expect(StaticDmxMapping(0, 1, 3, true).calculateEndChannel(dmxUniverses.channelsPerUniverse))
                        .toEqual(2)
                }

                it("_000______") {
                    expect(StaticDmxMapping(1, 1, 3, true).calculateEndChannel(dmxUniverses.channelsPerUniverse))
                        .toEqual(3)
                }

                it("000111222_ 333444555_") {
                    expect(StaticDmxMapping(0, 6, 3, true).calculateEndChannel(dmxUniverses.channelsPerUniverse))
                        .toEqual(18)
                }

                it("_000111222 333444555_") {
                    expect(StaticDmxMapping(1, 6, 3, true).calculateEndChannel(dmxUniverses.channelsPerUniverse))
                        .toEqual(18)
                }

                it("__000111__ 222333444_ 555") {
                    expect(StaticDmxMapping(2, 6, 3, true).calculateEndChannel(dmxUniverses.channelsPerUniverse))
                        .toEqual(22)
                }

                it("________ __111222__ 333444555_") {
                    expect(StaticDmxMapping(10, 6, 3, true).calculateEndChannel(dmxUniverses.channelsPerUniverse))
                        .toEqual(28)
                }
            }

            context("#validate") {
                override(dmxUniverses) { DmxUniverses(452) }

                it("validates end channel fits in universes") {
                    val mapping = allocator.allocate(76800, 3, DmxTransportConfig(0))
                    dmxUniverses.validate(mapping)
                }
            }
        }
    }
})