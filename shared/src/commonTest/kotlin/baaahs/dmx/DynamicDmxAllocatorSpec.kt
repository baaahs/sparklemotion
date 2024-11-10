package baaahs.dmx

import baaahs.describe
import baaahs.gl.override
import baaahs.kotest.value
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.*
import io.kotest.matchers.collections.shouldContainExactly

@Suppress("unused")
class DynamicDmxAllocatorSpec : DescribeSpec({
    describe<DynamicDmxAllocator> {
        val universeCount by value { 3 }
        val dmxUniverses by value { DmxUniverses(universeCount) }
        val allocator by value { DynamicDmxAllocator() }

        context("when start channel is specified") {
            it("starts there") {
                allocator.allocate(10, 3, DmxTransportConfig(1))
                    .shouldBe(StaticDmxMapping(1, 10, 3, true))
            }

            context("when the start channel has already been allocated") {
                it("throws an error") {
                    allocator.allocate(10, 3, DmxTransportConfig())
                    shouldThrow<Exception> {
                        allocator.allocate(10, 3, DmxTransportConfig(2))
                    }
                }
            }
        }

        context("when multiple fixtures per universe are allowed") {
            it("packs multiple fixtures into a single universe") {
                allocator.allocate(10, 3, DmxTransportConfig())
                    .shouldBe(StaticDmxMapping(0, 10, 3, true))
                allocator.allocate(10, 3, DmxTransportConfig())
                    .shouldBe(StaticDmxMapping(30, 10, 3, true))
                allocator.allocate(100, 3, DmxTransportConfig())
                    .shouldBe(StaticDmxMapping(60, 100, 3, true))
                allocator.allocate(10, 3, DmxTransportConfig())
                    .shouldBe(StaticDmxMapping(360, 10, 3, true))
            }
        }

        context("when each fixture gets its own universe") {
            it("skips to a new universe for each fixture") {
                allocator.allocate(10, 3, DmxTransportConfig(fixtureStartsInFreshUniverse = true))
                    .shouldBe(StaticDmxMapping(0, 10, 3, true))
                allocator.allocate(10, 3, DmxTransportConfig(fixtureStartsInFreshUniverse = true))
                    .shouldBe(StaticDmxMapping(512, 10, 3, true))
                allocator.allocate(100, 3, DmxTransportConfig(fixtureStartsInFreshUniverse = true))
                    .shouldBe(StaticDmxMapping(1024, 100, 3, true))
                allocator.allocate(10, 3, DmxTransportConfig(fixtureStartsInFreshUniverse = true))
                    .shouldBe(StaticDmxMapping(1536, 10, 3, true))
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
                    dmxUniverses.channels.map { it.toInt() }.shouldContainExactly(
                        0, 1, 2, 10, 11, 12, 20, 21, 22, 0, // first universe, three components
                        30, 31, 32, 0, 0, 0, 0, 0, 0, 0 // second universe, one component
                    )
                }
            }

            context("#calculateEndChannel") {
                override(dmxUniverses) { DmxUniverses(3, 10) }

                it("000_______") {
                    StaticDmxMapping(0, 1, 3, true).calculateEndChannel(dmxUniverses.channelsPerUniverse)
                        .shouldBe(2)
                }

                it("_000______") {
                    StaticDmxMapping(1, 1, 3, true).calculateEndChannel(dmxUniverses.channelsPerUniverse)
                        .shouldBe(3)
                }

                it("000111222_ 333444555_") {
                    StaticDmxMapping(0, 6, 3, true).calculateEndChannel(dmxUniverses.channelsPerUniverse)
                        .shouldBe(18)
                }

                it("_000111222 333444555_") {
                    StaticDmxMapping(1, 6, 3, true).calculateEndChannel(dmxUniverses.channelsPerUniverse)
                        .shouldBe(18)
                }

                it("__000111__ 222333444_ 555") {
                    StaticDmxMapping(2, 6, 3, true).calculateEndChannel(dmxUniverses.channelsPerUniverse)
                        .shouldBe(22)
                }

                it("________ __111222__ 333444555_") {
                    StaticDmxMapping(10, 6, 3, true).calculateEndChannel(dmxUniverses.channelsPerUniverse)
                        .shouldBe(28)
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