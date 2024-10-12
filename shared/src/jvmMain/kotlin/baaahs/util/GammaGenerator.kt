@file:Suppress("ConstantConditionIf")

package baaahs.util

import kotlin.math.*

class GammaGenerator(
    val gamma: Double = 2.2,
    val ditherStateBits: Int = 8,
    val powerAdjust: Double = 1.0,
    val comments: Boolean = true,
    val storeFractional: Boolean = false
) {
    fun run() {
        val ditherLevels = calculateDitherThresholds()
        val ditherStatesBitsD = ditherStateBits.toDouble()
        var largestAdjustedQuantError = 0.0

        for (v in 0..255) {
            val adjustedV = (v / 255.0).pow(gamma) * 255 * powerAdjust
            val vInt = floor(adjustedV).toInt()
            val quantError = adjustedV - vInt

            if (storeFractional) {
                print("{$vInt,${floor(quantError * 255).toInt()}}")
                if (comments) {
                    println(" // origV=$v")
                }
            } else {
                var dither = 0
                var ditherV = 0
                for (b in ditherLevels) {
                    val bitOn = quantError > b / ditherStatesBitsD + 1 / ditherStatesBitsD / 2

//                    println("$b: $quantError > ${b / ditherStatesD + 1 / ditherStatesD / 2}")
                    dither = dither.shl(1).or(if (bitOn) 1 else 0)
                    ditherV += if (bitOn) 1 else 0
                }
                if (vInt == 255) dither = 0

                print("{$vInt,0b${dither.toString(2)}}")

                val adjustedQuantError = ditherV / ditherStatesBitsD - quantError
                largestAdjustedQuantError = max(largestAdjustedQuantError, abs(adjustedQuantError))

                if (comments) {
                    println(" // origV=$v adjustedV=${adjustedV.toS()} " +
                            "quantError=${quantError.toS()} " +
                            "adjustedQuantError=${adjustedQuantError.toS()}")
                }
            }

            if (!comments) {
                if (v != 255) print(",")
                if (v % 8 == 7) print("\n")
            }
        }

        if (comments) {
            println("\nLargest quant error after dithering: ${largestAdjustedQuantError.toS()}")
        }
    }

    /**
     * Creates an array of ints representing the threshold at which a particular dither state
     * should round up.
     *
     * To introduce more variegation/flipping between dither levels, thresholds are stored out
     * of order by reversing their bits, e.g. with 8 dither states, we return:
     *
     *
     * ```
     * arrayOf(  0,   1,   2,   3,   4,   5,   6,   7)
     * bits   (000, 001, 010, 011, 100, 101, 110, 111)
     * to bits(000, 100, 010, 110, 001, 101, 011, 111)
     * arrayOf(  0,   4,   2,   6,   1,   5,   3,   7)
     * ```
     */
    internal fun calculateDitherThresholds(): IntArray {
        val stateBits = log(ditherStateBits.toDouble(), 2.0).toInt()

        return IntArray(ditherStateBits) {
            var value = 0
            for (i in 0 until stateBits) {
                val fromBitmask = 1.shl(i) and 0xFFFF
                val toBitmask = 1.shl(stateBits - i - 1) and 0xFFFF

                if (it and fromBitmask != 0) {
                    value = value.or(toBitmask)
                }
            }
            value
        }
    }

    private fun Double.toS() = String.format("%.2f", this)
}

fun main() {
    GammaGenerator(ditherStateBits = 8, comments = false).run()
}