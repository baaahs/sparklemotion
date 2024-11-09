package baaahs.imaging

import baaahs.FakeClock
import baaahs.describe
import baaahs.kotest.value
import baaahs.toEqual
import ch.tutteli.atrium.api.verbs.expect
import io.kotest.core.spec.style.DescribeSpec
import kotlinx.datetime.Instant

object AnimatorSpec : DescribeSpec({
    describe<Animator> {
        context("Given some animation frames") {
            val durations by value { listOf(50, 50, 100, 50) }
            val fakeClock by value { FakeClock(1.0) }
            val animator by value { Animator(durations, fakeClock) }

            it("knows their duration") {
                expect(animator.durationMs).toEqual(250)
            }

            it("keeps track of start time") {
                expect(animator.startedAt).toEqual(Instant.fromEpochMilliseconds(1000))
            }

            context("#getFrameAt") {
                it("is 0 at start time") {
                    expect(animator.getFrameAt(0)).toEqual(0)
                }

                it("is 0 when slightly before second frame") {
                    expect(animator.getFrameAt(49)).toEqual(0)
                }

                it("is 1 at second frame") {
                    expect(animator.getFrameAt(50)).toEqual(1)
                }

                it("is 1 right before third frame") {
                    expect(animator.getFrameAt(99)).toEqual(1)
                }

                it("is 2 at third frame") {
                    expect(animator.getFrameAt(100)).toEqual(2)
                }

                it("is 2 right before fourth frame") {
                    expect(animator.getFrameAt(199)).toEqual(2)
                }

                it("is 3 at fourth frame") {
                    expect(animator.getFrameAt(200)).toEqual(3)
                }

                it("is 3 right before repeat") {
                    expect(animator.getFrameAt(249)).toEqual(3)
                }

                it("is 0 at repeat") {
                    expect(animator.getFrameAt(250)).toEqual(0)
                }

                it("is 3 right before second repeat") {
                    expect(animator.getFrameAt(499)).toEqual(3)
                }

                it("is 0 at second repeat") {
                    expect(animator.getFrameAt(500)).toEqual(0)
                }
            }
        }
    }
})