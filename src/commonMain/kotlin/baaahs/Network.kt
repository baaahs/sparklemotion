package baaahs

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.jvm.Synchronized
import kotlin.random.Random

interface Network {
    fun link(): Link

    interface Link {
        val myAddress: Address

        fun listen(port: Int, listener: Listener)
        fun send(toAddress: Address, port: Int, bytes: ByteArray)
        fun broadcast(port: Int, bytes: ByteArray)
    }

    interface Address

    interface Listener {
        fun receive(fromAddress: Address, bytes: ByteArray)
    }
}


class FakeNetwork(
    private val networkDelay: Long = 1L,
    private val display: NetworkDisplay
) : Network {
    private val listeners: MutableMap<Pair<Network.Address, Int>, Network.Listener> = hashMapOf()
    private val listenersByPort: MutableMap<Int, MutableList<Network.Listener>> = hashMapOf()
    private var nextAddress = 0xb00f

    @Synchronized
    override fun link(): Network.Link {
        val address = FakeAddress(nextAddress++)
        return FakeLink(address)
    }

    @Synchronized
    private fun listen(address: Network.Address, port: Int, listener: Network.Listener) {
        listeners.put(Pair(address, port), listener)
        val portListeners = listenersByPort.getOrPut(port) { mutableListOf() }
        portListeners.add(listener)
    }

    @Synchronized
    private fun send(fromAddress: Network.Address, toAddress: Network.Address, port: Int, bytes: ByteArray) {
        val listener = listeners[Pair(toAddress, port)]
        if (listener != null) transmit(fromAddress, listener, bytes)
    }

    @Synchronized
    private fun broadcast(fromAddress: Network.Address, port: Int, bytes: ByteArray) {
        listenersByPort[port]?.forEach { listener ->
            transmit(fromAddress, listener, bytes)
        }
    }

    private fun transmit(fromAddress: Network.Address, listener: Network.Listener, bytes: ByteArray) {
        GlobalScope.launch {
            delay(networkDelay)
            if (Random.nextInt() % 10 != 1) {
                display.receivedPacket()
                listener.receive(fromAddress, bytes)
            } else {
                display.droppedPacket()
            }
        }
    }

    private inner class FakeLink(override val myAddress: Network.Address) : Network.Link {
        override fun listen(port: Int, listener: Network.Listener) {
            this@FakeNetwork.listen(myAddress, port, listener)
        }

        override fun send(toAddress: Network.Address, port: Int, bytes: ByteArray) {
            this@FakeNetwork.send(myAddress, toAddress, port, bytes)
        }

        override fun broadcast(port: Int, bytes: ByteArray) {
            this@FakeNetwork.broadcast(myAddress, port, bytes)
        }
    }
}

private data class FakeAddress(val id: Int) : Network.Address {
    override fun toString(): String = "x${id.toString(16)}"
}

