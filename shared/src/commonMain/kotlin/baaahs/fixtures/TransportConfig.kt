package baaahs.fixtures

import baaahs.scene.MutableTransportConfig

interface TransportConfig {
    val transportType: TransportType

    fun edit(): MutableTransportConfig

    /** Merges two configs, preferring values from [other]. */
    operator fun plus(other: TransportConfig?): TransportConfig

    fun preview(): ConfigPreview
}