package baaahs.fixtures

import baaahs.controller.Controller
import baaahs.controller.NullController
import baaahs.io.ByteArrayWriter
import baaahs.scene.MutableTransportConfig

interface Transport {
    val name: String
    val controller: Controller
    val config: TransportConfig?

    fun deliverBytes(byteArray: ByteArray)
    fun deliverComponents(
        componentCount: Int,
        bytesPerComponent: Int,
        fn: (componentIndex: Int, buf: ByteArrayWriter) -> Unit
    )
}

open class NullTransport(
    override val name: String = "Null Transport",
    override val controller: Controller = NullController
) : Transport {
    override val config: TransportConfig?
        get() = null

    override fun deliverBytes(byteArray: ByteArray) {
        // No-op.
    }

    override fun deliverComponents(
        componentCount: Int,
        bytesPerComponent: Int,
        fn: (componentIndex: Int, buf: ByteArrayWriter) -> Unit
    ) {
        // No-op.
    }

    companion object : NullTransport("Null Transport", NullController)
}

object NullTransportType : TransportType {
    override val id: String
        get() = "Null"
    override val title: String
        get() = "Null Transport"
    override val emptyConfig: TransportConfig
        get() = NullTransportConfig
}

object NullTransportConfig : TransportConfig {
    override val transportType: TransportType
        get() = NullTransportType

    override fun edit(): MutableTransportConfig {
        TODO("not implemented")
    }

    override fun plus(other: TransportConfig?) = this

    override fun preview(): ConfigPreview {
        TODO("not implemented")
    }
}