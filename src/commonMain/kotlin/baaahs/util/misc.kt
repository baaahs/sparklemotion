package baaahs.util

import kotlin.math.roundToInt

fun Float?.percent() = this?.let { "${(it * 100f).roundToInt()}%" } ?: "—%"