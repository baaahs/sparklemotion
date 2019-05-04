package baaahs.net

import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter
import baaahs.proto.Message
import kotlin.math.min

interface Network {
    fun link(): Link

    interface Link {
        val myAddress: Address

        val udpMtu: Int
        fun listenUdp(port: Int, udpListener: UdpListener)
        fun sendUdp(toAddress: Address, port: Int, bytes: ByteArray)
        fun broadcastUdp(port: Int, bytes: ByteArray)

        fun sendUdp(toAddress: Address, port: Int, message: Message) {
            sendUdp(toAddress, port, message.toBytes())
        }

        fun broadcastUdp(port: Int, message: Message) {
            broadcastUdp(port, message.toBytes())
        }

        fun listenTcp(port: Int, tcpServerSocketListener: TcpServerSocketListener)
        fun connectTcp(toAddress: Address, port: Int, tcpListener: TcpListener): TcpConnection
    }

    class MoreReliableUdpLink(val link: Link) : Link {
        override val myAddress: Address get() = link.myAddress
        override val udpMtu: Int get() = link.udpMtu

        /**
         * Header format is:
         * * message ID (short)
         * * total payload size (short)
         * * this frame offset (short)
         * * this frame size (short)
         */

        val mtu = link.udpMtu
        val headerSize = 8
        var nextMessageId: Short = 0

        var fragments = arrayListOf<Fragment>()

        data class Fragment(val messageId: Short, val totalSize: Short, val offset: Short, val bytes: ByteArray)

        override fun listenUdp(port: Int, udpListener: UdpListener) {
            link.listenUdp(port, object : UdpListener {
                override fun receive(fromAddress: Address, bytes: ByteArray) {
                    // reassemble fragmented payloads...
                    val reader = ByteArrayReader(bytes)
                    val messageId = reader.readShort()
                    val totalSize = reader.readShort()
                    val offset = reader.readShort()
                    val size = reader.readShort()
                    val frameBytes = reader.readNBytes(size.toInt())
                    if (offset.toInt() == 0 && size == totalSize) {
                        udpListener.receive(fromAddress, frameBytes)
                    } else {
                        val thisFragment = Fragment(messageId, totalSize, offset, frameBytes)
                        fragments.add(thisFragment)

//                        println("received fragment: ${thisFragment}")
                        if (offset + size == totalSize.toInt()) {
                            // final fragment, try to reassemble…

                            val myFragments = arrayListOf<Fragment>()
                            fragments.removeAll {
                                val remove = it.messageId == messageId
                                if (remove) myFragments.add(it)
                                remove
                            }

                            if (!fragments.isEmpty()) {
                                println("remaining fragments = ${fragments}")
                            }

                            val actualTotalSize = myFragments.map { it.bytes.size }.reduce { acc, i -> acc + i }
                            if (actualTotalSize != totalSize.toInt()) {
                                IllegalArgumentException("can't reassemble packet, $actualTotalSize != $totalSize for $messageId")
                            }

                            val reassembleBytes = ByteArray(totalSize.toInt())
                            myFragments.forEach {
                                it.bytes.copyInto(reassembleBytes, it.offset.toInt())
                            }

                            udpListener.receive(fromAddress, reassembleBytes)
                        }
                    }
                }
            })
        }

        /** Sends payloads which might be larger than the network's MTU. */
        override fun sendUdp(toAddress: Address, port: Int, bytes: ByteArray) {
            transmitMultipartUdp(bytes) {
                link.sendUdp(toAddress, port, it)
            }
        }

        /** Broadcasts payloads which might be larger than the network's MTU. */
        override fun broadcastUdp(port: Int, bytes: ByteArray) {
            transmitMultipartUdp(bytes) {
                link.broadcastUdp(port, it)
            }
        }

        override fun sendUdp(toAddress: Address, port: Int, message: Message) {
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
                writer.writeShort(bytes.size.toShort())
                writer.writeShort(offset.toShort())
                writer.writeShort(thisFrameSize.toShort())
                writer.writeNBytes(bytes, offset, offset + thisFrameSize)
                fn(writer.toBytes())

                offset += thisFrameSize
            }
        }

        override fun listenTcp(port: Int, tcpServerSocketListener: TcpServerSocketListener): Unit =
            link.listenTcp(port, tcpServerSocketListener)

        override fun connectTcp(toAddress: Address, port: Int, tcpListener: TcpListener): TcpConnection =
            link.connectTcp(toAddress, port, tcpListener)

    }

    interface Address

    interface UdpListener {
        fun receive(fromAddress: Address, bytes: ByteArray)
    }

    interface TcpConnection {
        val fromAddress: Address
        val toAddress: Address
        val port: Int

        fun send(bytes: ByteArray)

        fun send(message: Message) {
            send(message.toBytes())
        }
    }

    interface TcpListener {
        fun connected(tcpConnection: TcpConnection)
        fun receive(tcpConnection: TcpConnection, bytes: ByteArray)
        fun reset(tcpConnection: TcpConnection)
    }

    interface TcpServerSocketListener {
        fun incomingConnection(fromConnection: TcpConnection): TcpListener
    }
}