package baaahs.imaging

import baaahs.FakeClock
import baaahs.describe
import baaahs.kotest.value
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.*
import kotlinx.datetime.Instant

object AnimatorSpec : DescribeSpec({
    describe<Animator> {
        context("Given some animation frames") {
            val durations by value { listOf(50, 50, 100, 50) }
            val fakeClock by value { FakeClock(1.0) }
            val animator by value { Animator(durations, fakeClock) }

            it("knows their duration") {
                animator.durationMs.shouldBe(250)
            }

            it("keeps track of start time") {
                animator.startedAt.shouldBe(Instant.fromEpochMilliseconds(1000))
            }

            context("#getFrameAt") {
                it("is 0 at start time") {
                    animator.getFrameAt(0).shouldBe(0)
                }

                it("is 0 when slightly before second frame") {
                    animator.getFrameAt(49).shouldBe(0)
                }

                it("is 1 at second frame") {
                    animator.getFrameAt(50).shouldBe(1)
                }

                it("is 1 right before third frame") {
                    animator.getFrameAt(99).shouldBe(1)
                }

                it("is 2 at third frame") {
                    animator.getFrameAt(100).shouldBe(2)
                }

                it("is 2 right before fourth frame") {
                    animator.getFrameAt(199).shouldBe(2)
                }

                it("is 3 at fourth frame") {
                    animator.getFrameAt(200).shouldBe(3)
                }

                it("is 3 right before repeat") {
                    animator.getFrameAt(249).shouldBe(3)
                }

                it("is 0 at repeat") {
                    animator.getFrameAt(250).shouldBe(0)
                }

                it("is 3 right before second repeat") {
                    animator.getFrameAt(499).shouldBe(3)
                }

                it("is 0 at second repeat") {
                    animator.getFrameAt(500).shouldBe(0)
                }
            }
        }
    }
})