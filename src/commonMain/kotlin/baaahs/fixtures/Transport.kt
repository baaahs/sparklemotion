package baaahs.fixtures

import baaahs.io.ByteArrayWriter

interface Transport {
    val name: String

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