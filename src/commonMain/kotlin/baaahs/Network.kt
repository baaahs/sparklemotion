package baaahs

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.jvm.Synchronized

interface Network {
    fun link(listener: Listener): Link

    interface Link {
        fun broadcast(bytes: ByteArray)

        val myAddress: Address
    }

    interface Address {
    }

    interface Listener {
        fun receive(fromAddress: Address, bytes: ByteArray)
    }
}

class FakeNetwork(private val networkDelay: Long = 1L) : Network {
    private val listeners: MutableMap<Network.Address, Network.Listener> = hashMapOf()
    private var nextAddress = 0xb00f

    @Synchronized override fun link(listener: Network.Listener): Network.Link {
        val address = FakeAddress(nextAddress++)
        listeners.put(address, listener)
        return FakeLink(address)
    }

    private inner class FakeLink(override val myAddress: Network.Address) : Network.Link {
        override fun broadcast(bytes: ByteArray) {
            this@FakeNetwork.broadcast(myAddress, bytes)
        }
    }

    @Synchronized private fun broadcast(fromAddress: Network.Address, bytes: ByteArray) {
        listeners.values.forEach { listener ->
            GlobalScope.launch {
                delay(networkDelay)
                listener.receive(fromAddress, bytes)
            }
        }
    }
}

private data class FakeAddress(val id: Int): Network.Address {
    override fun toString(): String = "x${id.toString(16)}"
}

