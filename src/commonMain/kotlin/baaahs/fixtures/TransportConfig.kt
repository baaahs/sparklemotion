package baaahs.fixtures

import baaahs.scene.MutableTransportConfig

interface TransportConfig {
    fun edit(): MutableTransportConfig
}