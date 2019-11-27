@file:Suppress("ConstantConditionIf")

package baaahs.util

import kotlin.math.floor
import kotlin.math.pow

const val comments = false
const val storeFractional = false

fun main() {
    val gamma = 2.2
    for (v in 0..255) {
        val adjustedV = (v / 255.0).pow(gamma) * 255
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
            for (b in arrayOf(0, 4, 2, 6, 1, 6, 3, 7)) {
                val bitOn = quantError > b / 8.0 + 1 / 16.0
                dither = dither.shl(1).or(if (bitOn) 1 else 0)
                ditherV += if (bitOn) 1 else 0
            }
            if (vInt == 255) dither = 0

            print("{$vInt,0b${dither.toString(2)}}")
            if (comments) {
                println(" // origV=$v quantError=${quantError.round()} diff=${(ditherV / 8.0 - quantError).round()}")
            }
        }

        if (!comments) {
            if (v != 255) print(",")
            if (v % 8 == 7) print("\n")
        }
    }
}

fun Double.round(): String {
    return ((this * 100).toInt() / 100.0).toString()
}