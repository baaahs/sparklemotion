package baaahs.fixtures

import baaahs.controller.Controller
import baaahs.controller.NullController
import baaahs.io.ByteArrayWriter
import baaahs.mapper.TransportConfig

interface Transport {
    val name: String
    val controller: Controller
    val config: TransportConfig? get() = null

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