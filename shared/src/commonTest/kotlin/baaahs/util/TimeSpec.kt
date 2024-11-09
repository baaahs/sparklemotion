package baaahs.util

import baaahs.describe
import baaahs.toEqual
import ch.tutteli.atrium.api.verbs.expect
import io.kotest.core.spec.style.DescribeSpec
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

object TimeSpec : DescribeSpec({
    describe<Duration> {
        context("toHHMMSS") {
            it("renders correctly") {
                expect((0.hours + 0.minutes + 17.seconds + 123.milliseconds).toHHMMSS())
                    .toEqual("0:17")
                expect((0.hours + 3.minutes + 17.seconds + 123.milliseconds).toHHMMSS())
                    .toEqual("3:17")
                expect((3.hours + 0.minutes + 17.seconds + 999.milliseconds).toHHMMSS())
                    .toEqual("3:00:17")
                expect((3.hours + 17.minutes + 0.seconds + 123.milliseconds).toHHMMSS())
                    .toEqual("3:17:00")
            }
        }
    }
})