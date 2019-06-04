package baaahs.net

import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter
import baaahs.proto.Message
import kotlin.math.min

class FragmentingUdpLink(private val link: Network.Link) : Network.Link {
    override val myAddress: Network.Address get() = link.myAddress
    override val udpMtu: Int get() = link.udpMtu

    /**
     * Header is 12 bytes long; format is:
     * * message ID (short)
     * * this frame size (short)
     * * total payload size (long)
     * * this frame offset (long)
     */
    companion object {
        const val headerSize = 12
    }

    private val mtu = link.udpMtu
    private var nextMessageId: Short = 0

    private var fragments = arrayListOf<Fragment>()

    class Fragment(val messageId: Short, val offset: Int, val bytes: ByteArray)

    override fun listenUdp(port: Int, udpListener: Network.UdpListener) {
        link.listenUdp(port, object : Network.UdpListener {
            override fun receive(fromAddress: Network.Address, bytes: ByteArray) {
                // reassemble fragmented payloads...
                val reader = ByteArrayReader(bytes)
                val messageId = reader.readShort()
                val size = reader.readShort()
                val totalSize = reader.readInt()
                val offset = reader.readInt()
                val frameBytes = reader.readNBytes(size.toInt())
                if (offset == 0 && size.toInt() == totalSize) {
                    udpListener.receive(fromAddress, frameBytes)
                } else {
                    val thisFragment = Fragment(messageId, offset, frameBytes)
                    fragments.add(thisFragment)

//                        println("received fragment: ${thisFragment}")
                    if (offset + size == totalSize) {
                        // final fragment, try to reassemble…

                        val myFragments = arrayListOf<Fragment>()
                        fragments.removeAll { fragment ->
                            val remove = fragment.messageId == messageId
                            if (remove) myFragments.add(fragment)
                            remove
                        }

                        if (!fragments.isEmpty()) {
                            // println("remaining fragments = ${fragments}")
                        }

                        val actualTotalSize = myFragments.map { it.bytes.size }.reduce { acc, i -> acc + i }
                        if (actualTotalSize != totalSize) {
                            IllegalArgumentException("can't reassemble packet, $actualTotalSize != $totalSize for $messageId")
                        }

                        val reassembleBytes = ByteArray(totalSize)
                        myFragments.forEach {
                            it.bytes.copyInto(reassembleBytes, it.offset)
                        }

                        udpListener.receive(fromAddress, reassembleBytes)
                    }
                }
            }
        })
    }

    /** Sends payloads which might be larger than the network's MTU. */
    override fun sendUdp(toAddress: Network.Address, port: Int, bytes: ByteArray) {
        transmitMultipartUdp(bytes) { fragment -> link.sendUdp(toAddress, port, fragment) }
    }

    /** Broadcasts payloads which might be larger than the network's MTU. */
    override fun broadcastUdp(port: Int, bytes: ByteArray) {
        transmitMultipartUdp(bytes) { fragment -> link.broadcastUdp(port, fragment) }
    }

    override fun sendUdp(toAddress: Network.Address, port: Int, message: Message) {
        sendUdp(toAddress, port, message.toBytes())
    }

    override fun broadcastUdp(port: Int, message: Message) {
        broadcastUdp(port, message.toBytes())
    }

    /** Sends payloads which might be larger than the network's MTU. */
    private fun transmitMultipartUdp(bytes: ByteArray, fn: (bytes: ByteArray) -> Unit) {
        if (bytes.size > 65535) {
            IllegalArgumentException("buffer too big! ${bytes.size} must be < 65536")
        }
        val messageId = nextMessageId++
        val messageCount = (bytes.size - 1) / (mtu - headerSize) + 1
        val buf = ByteArray(mtu)
        var offset = 0
        for (i in 0 until messageCount) {
            val writer = ByteArrayWriter(buf)
            val thisFrameSize = min((mtu - headerSize), bytes.size - offset)
            writer.writeShort(messageId)
            writer.writeShort(thisFrameSize.toShort())
            writer.writeInt(bytes.size)
            writer.writeInt(offset)
            writer.writeNBytes(bytes, offset, offset + thisFrameSize)
            fn(writer.toBytes())

            offset += thisFrameSize
        }
    }

    override fun listenTcp(port: Int, tcpServerSocketListener: Network.TcpServerSocketListener): Unit =
        link.listenTcp(port, tcpServerSocketListener)

    override fun connectTcp(toAddress: Network.Address, port: Int, tcpListener: Network.TcpListener): Network.TcpConnection =
        link.connectTcp(toAddress, port, tcpListener)

}
