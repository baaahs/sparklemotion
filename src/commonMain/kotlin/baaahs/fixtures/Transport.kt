package baaahs.fixtures

import baaahs.controller.ControllerId
import baaahs.io.ByteArrayWriter
import baaahs.mapper.TransportConfig

interface Transport {
    val name: String
    val controllerId: ControllerId
    val config: TransportConfig? get() = null

    fun deliverBytes(byteArray: ByteArray)
    fun deliverComponents(
        componentCount: Int,
        bytesPerComponent: Int,
        fn: (componentIndex: Int, buf: ByteArrayWriter) -> Unit
    )
}

object NullTransport : Transport {
    override val name: String
        get() = "Null Transport"
    override val controllerId: ControllerId = ControllerId("NULL", "null")

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
}