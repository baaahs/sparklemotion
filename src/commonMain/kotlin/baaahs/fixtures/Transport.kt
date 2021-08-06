package baaahs.fixtures

interface Transport {
    val name: String

    fun deliverBytes(byteArray: ByteArray)
}

object NullTransport : Transport {
    override val name: String
        get() = "Null Transport"

    override fun deliverBytes(byteArray: ByteArray) {
        // No-op.
    }
}