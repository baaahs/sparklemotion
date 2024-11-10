package baaahs.util

import baaahs.describe
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class TimeSpec : DescribeSpec({
    describe<Duration> {
        context("toHHMMSS") {
            it("renders correctly") {
                (0.hours + 0.minutes + 17.seconds + 123.milliseconds).toHHMMSS()
                    .shouldBe("0:17")
                (0.hours + 3.minutes + 17.seconds + 123.milliseconds).toHHMMSS()
                    .shouldBe("3:17")
                (3.hours + 0.minutes + 17.seconds + 999.milliseconds).toHHMMSS()
                    .shouldBe("3:00:17")
                (3.hours + 17.minutes + 0.seconds + 123.milliseconds).toHHMMSS()
                    .shouldBe("3:17:00")
            }
        }
    }
})