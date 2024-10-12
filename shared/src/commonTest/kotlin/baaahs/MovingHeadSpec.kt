package baaahs

import baaahs.dmx.Dmx
import baaahs.plugin.core.MovingHeadParams
import ch.tutteli.atrium.api.verbs.expect
import org.spekframework.spek2.Spek
import kotlin.math.PI

object MovingHeadSpec : Spek({
    describe<MovingHeadParams> {
        val testMovingHead by value { TestMovingHeadAdapter() }
        val channels by value { Dmx.Buffer(ByteArray(16)) }
        val buffer by value { testMovingHead.newBuffer(channels) }

        describe("ranges") {
            val range by value { -.5f .. .5f }

            describe("scale") {
                it("scales a stored value in 0..1 to min..max") {
                    expect(range.scale(0f)).toEqual(-.5f)
                    expect(range.scale(1f)).toEqual(.5f)
                    expect(range.scale(.5f)).toEqual(0f)
                }
            }

            describe("unscale") {
                it("unscales a value in min..max to 0..1") {
                    expect(range.unscale(-.5f)).toEqual(0f)
                    expect(range.unscale(.5f)).toEqual(1f)
                    expect(range.unscale(0f)).toEqual(.5f)
                }
            }

            describe("clamp") {
                it("clamps values to min..max") {
                    expect(range.clamp(-.51f)).toEqual(-.5f)
                    expect(range.clamp(.51f)).toEqual(.5f)
                    expect(range.clamp(-1f)).toEqual(-.5f)
                    expect(range.clamp(1f)).toEqual(.5f)
                    expect(range.clamp(0f)).toEqual(0f)
                }
            }
        }

        it("scales pan to the proper range") {
            buffer.pan = 0f
            expect(channels[testMovingHead.panChannel])
                .toEqual(0)
            expect(channels[testMovingHead.panFineChannel])
                .toEqual(0)

            buffer.pan = (PI / 2).toFloat()
            expect(channels[testMovingHead.panChannel])
                .toEqual(42.toByte()) // 255 / 6
            expect(channels[testMovingHead.panFineChannel])
                .toEqual(-86)

            buffer.pan = (PI * 2).toFloat()
            expect(channels[testMovingHead.panChannel])
                .toEqual((255 * 2 / 3).toByte())
            expect(channels[testMovingHead.panFineChannel])
                .toEqual((255 * 2 / 3).toByte())

            buffer.pan = (PI * 3).toFloat()
            expect(channels[testMovingHead.panChannel])
                .toEqual(255.toByte())
            expect(channels[testMovingHead.panFineChannel])
                .toEqual(255.toByte())
        }

        it("scales tilt to the proper range") {
            buffer.tilt = 0f
            expect(channels[testMovingHead.tiltChannel])
                .toEqual(127)
            expect(channels[testMovingHead.tiltFineChannel])
                .toEqual(-1)

            buffer.tilt = toRadians(-90f)
            expect(channels[testMovingHead.tiltChannel])
                .toEqual(42)
            expect(channels[testMovingHead.tiltFineChannel])
                .toEqual(-86)

            buffer.tilt = toRadians(-135f)
            expect(channels[testMovingHead.tiltChannel])
                .toEqual(0)
            expect(channels[testMovingHead.tiltFineChannel])
                .toEqual(0)

            buffer.tilt = toRadians(135f)
            expect(channels[testMovingHead.tiltChannel])
                .toEqual(255.toByte())
            expect(channels[testMovingHead.tiltFineChannel])
                .toEqual(255.toByte())
        }

        it("includes prism control") {
            buffer.prism = false
            buffer.prismRotation = 0f
            expect(channels[testMovingHead.prismChannel])
                .toEqual(0)
            expect(channels[testMovingHead.prismRotationChannel])
                .toEqual(-65)

            buffer.prism = true
            buffer.prismRotation = .7f
            expect(channels[testMovingHead.prismChannel])
                .toEqual(255.toByte())
            expect(channels[testMovingHead.prismRotationChannel])
                .toEqual(-22)

            buffer.prism = true
            buffer.prismRotation = .3f
            expect(channels[testMovingHead.prismChannel])
                .toEqual(255.toByte())
            expect(channels[testMovingHead.prismRotationChannel])
                .toEqual(-47)
        }
    }
})