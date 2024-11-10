package baaahs.mapper

import baaahs.describe
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.*
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone

@Suppress("unused")
class MappingStoreSpec : DescribeSpec({
    describe<MappingStore> {
        it("formats times consistently") {
            val instant = Instant.fromEpochSeconds(1703876061L, 123_456_789)
            MappingStore.formatDateTime(instant, TimeZone.of("America/Los_Angeles"))
                .shouldBe("20231229-105421")
        }
    }
})