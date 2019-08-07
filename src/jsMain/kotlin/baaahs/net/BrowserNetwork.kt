package baaahs.net

import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Int8Array
import org.khronos.webgl.get
import org.w3c.dom.ARRAYBUFFER
import org.w3c.dom.BinaryType
import org.w3c.dom.WebSocket

/**
 * Uses WebSockets for TCP, and proxies UDP via Pinky.
 */
class BrowserNetwork(private val udpProxyAddress: BrowserAddress? = null, private val udpProxyPort: Int = 0) : Network {
    override fun link(): Network.Link = object : Network.Link {
        override val myAddress: Network.Address = object : Network.Address {}

        var udpProxy: UdpProxy? = null
        init {
            udpProxyAddress?.let {
                udpProxy = UdpProxy(this, it, udpProxyPort)
            }
        }

        override val udpMtu = 1500

        override fun listenUdp(port: Int, udpListener: Network.UdpListener): Network.UdpSocket {
            return udpProxy!!.listenUdp(port, udpListener)
        }

        override fun startHttpServer(port: Int): Network.HttpServer =
            TODO("BrowserNetwork.startHttpServer not implemented")

        override fun connectWebSocket(
            toAddress: Network.Address,
            port: Int,
            path: String,
            webSocketListener: Network.WebSocketListener
        ): Network.TcpConnection {
            val webSocket = WebSocket((toAddress as BrowserAddress).urlString.trimEnd('/') + path)
            webSocket.binaryType = BinaryType.ARRAYBUFFER

            val tcpConnection = object : Network.TcpConnection {
                override val fromAddress: Network.Address = myAddress
                override val toAddress: Network.Address = myAddress
                override val port: Int get() = port

                override fun send(bytes: ByteArray) {
                    webSocket.send(Int8Array(bytes.toTypedArray()))
                }
            }

            webSocket.onopen = {
                console.log("WebSocket open!", it)
                webSocketListener.connected(tcpConnection)
            }

            webSocket.onmessage = {
                // TODO: be less woefully inefficient...
                val buf = it.data as ArrayBuffer
                val byteBuf = Int8Array(buf)
                val bytes = ByteArray(byteBuf.length)
                for (i in 0 until byteBuf.length) {
                    bytes[i] = byteBuf[i]
                }
                webSocketListener.receive(tcpConnection, bytes)
            }

            webSocket.onerror = { console.log("WebSocket error!", it) }
            webSocket.onclose = { console.log("WebSocket close!", it) }

            return tcpConnection
        }
    }

    class BrowserAddress(val urlString: String) : Network.Address

    private class UdpProxy(link: Network.Link, address: BrowserAddress, port: Int) : Network.WebSocketListener {
        private var udpListener: Network.UdpListener? = null

        val tcpConnection = link.connectWebSocket(address, port, "/sm/udpProxy", this)
        var connected: Boolean = false
        val toSend = mutableListOf<ByteArray>()

        override fun connected(tcpConnection: Network.TcpConnection) {
            connected = true

            toSend.forEach { tcpConnection.send(it) }
            toSend.clear()
        }

        override fun receive(tcpConnection: Network.TcpConnection, bytes: ByteArray) {
            ByteArrayReader(bytes).apply {
                val op = readByte()
                when (op) {
                    'R'.toByte() -> {
                        val fromAddress = UdpProxyAddress(readBytes())
                        val fromPort = readInt()
                        val data = readBytes()
//                        log("UDP: Received ${data.size} bytes from $fromAddress:$fromPort")
                        udpListener!!.receive(fromAddress, fromPort, data)
                    }
                }
            }
        }

        override fun reset(tcpConnection: Network.TcpConnection) {
            TODO("UdpProxy.reset not implemented")
        }

        fun listenUdp(port: Int, udpListener: Network.UdpListener): Network.UdpSocket {
            if (this.udpListener != null) {
                throw IllegalStateException("UDP proxy is already listening")
            }

            this.udpListener = udpListener

            if (port != 0) {
                throw IllegalArgumentException("UDP proxy can't listen on a specific port, sorry!")
            }

            tcpConnectionSend(ByteArrayWriter().apply {
                writeByte('L'.toByte())
                log("UDP: Listen")
            }.toBytes())

            return UdpSocketProxy(port)
        }

        inner class UdpSocketProxy(requestedPort: Int) : Network.UdpSocket {
            override val serverPort = requestedPort // TODO: this is probably wrong

            override fun sendUdp(toAddress: Network.Address, port: Int, bytes: ByteArray) {
                if (toAddress !is UdpProxyAddress) {
                    throw IllegalArgumentException("UDP proxy can't send to $toAddress!")
                }

                tcpConnectionSend(ByteArrayWriter().apply {
                    writeByte('S'.toByte())
                    writeBytes(toAddress.bytes)
                    writeInt(port)
                    writeBytes(bytes)
//                    log("UDP: Sent ${bytes.size} bytes to $toAddress:$port")
                }.toBytes())
            }

            override fun broadcastUdp(port: Int, bytes: ByteArray) {
                tcpConnectionSend(ByteArrayWriter().apply {
                    writeByte('B'.toByte())
                    writeInt(port)
                    writeBytes(bytes)
//                    log("UDP: Broadcast ${bytes.size} bytes to *:$port")
                }.toBytes())
            }

        }

        private fun tcpConnectionSend(bytes: ByteArray) {
            if (connected) {
                tcpConnection.send(bytes)
            } else {
                toSend.add(bytes)
            }
        }

        private fun log(s: String) {
            println(s)
        }

        private data class UdpProxyAddress(val bytes: ByteArray) : Network.Address {
            override fun toString(): String {
                return bytes.joinToString(".") { it.toInt().and(0xff).toString() }
            }

            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other == null || this::class.js != other::class.js) return false

                other as UdpProxyAddress

                if (!bytes.contentEquals(other.bytes)) return false

                return true
            }

            override fun hashCode(): Int {
                return bytes.contentHashCode()
            }
        }
    }
}