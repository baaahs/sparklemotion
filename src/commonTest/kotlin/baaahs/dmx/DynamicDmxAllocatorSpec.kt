package baaahs.dmx

import baaahs.describe
import baaahs.toEqual
import ch.tutteli.atrium.api.fluent.en_GB.containsExactly
import ch.tutteli.atrium.api.verbs.expect
import org.spekframework.spek2.Spek

object DynamicDmxAllocatorSpec : Spek({
    describe<DynamicDmxAllocator> {
        val universeCount by value { 3 }
        val allocator by value { DynamicDmxAllocator(universeCount) }

        it("allocates channels as requested") {
            expect(allocator.allocate(DmxTransportConfig(), 10, 3))
                .toEqual(StaticDmxMapping(0, 30, true))
            expect(allocator.allocate(DmxTransportConfig(), 10, 3))
                .toEqual(StaticDmxMapping(30, 30, true))
            expect(allocator.allocate(DmxTransportConfig(), 100, 3))
                .toEqual(StaticDmxMapping(60, 300, true))
            expect(allocator.allocate(DmxTransportConfig(), 10, 3))
                .toEqual(StaticDmxMapping(360, 30, true))
        }

        it("skips to new universes when needed") {
            expect(allocator.allocate(DmxTransportConfig(fixtureStartsInFreshUniverse = true), 10, 3))
                .toEqual(StaticDmxMapping(0, 30, true))
            expect(allocator.allocate(DmxTransportConfig(fixtureStartsInFreshUniverse = true), 10, 3))
                .toEqual(StaticDmxMapping(512, 30, true))
            expect(allocator.allocate(DmxTransportConfig(fixtureStartsInFreshUniverse = true), 100, 3))
                .toEqual(StaticDmxMapping(1024, 300, true))
            expect(allocator.allocate(DmxTransportConfig(fixtureStartsInFreshUniverse = true), 10, 3))
                .toEqual(StaticDmxMapping(1536, 30, true))
        }

        context("static dmx mappings") {
            it("writes to channels correctly") {
                val dmxMapping = StaticDmxMapping(0, 6, true)
                val dmxUniverses = DmxUniverses(2, 10)
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
    }
})