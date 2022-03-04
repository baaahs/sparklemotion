package baaahs.fixtures

import baaahs.scene.MutableTransportConfig

interface TransportConfig {
    val transportType: TransportType

    fun edit(): MutableTransportConfig
}