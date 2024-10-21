package baaahs.mapper

import baaahs.describe
import baaahs.toEqual
import ch.tutteli.atrium.api.verbs.expect
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import org.spekframework.spek2.Spek

@Suppress("unused")
object MappingStoreSpec : Spek({
    describe<MappingStore> {
        it("formats times consistently") {
            val instant = Instant.fromEpochSeconds(1703876061L, 123_456_789)
            expect(MappingStore.formatDateTime(instant, TimeZone.of("America/Los_Angeles")))
                .toEqual("20231229-105421")
        }
    }
})