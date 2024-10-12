package baaahs.net

import baaahs.util.Logger
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
    override fun link(name: String): Network.Link = object : Network.Link {
        override val myAddress: Network.Address = object : Network.Address {
            override fun asString(): String = "BrowserNetwork:localhost"
        }

        override val myHostname: String get() = "Browser"

        private val udpProxy: BrowserUdpProxy? by lazy {
            udpProxyAddress?.let { BrowserUdpProxy(this, it, udpProxyPort) }
        }

        override val udpMtu = 1500

        override fun listenUdp(port: Int, udpListener: Network.UdpListener): Network.UdpSocket {
            return udpProxy!!.listenUdp(port, udpListener)
        }

        override val mdns: Network.Mdns get() {
            // this can, in fact, be implemented if we want, at least for browsing/discovery
            TODO("BrowserNetwork.mdns not yet implemented")
        }

        override fun startHttpServer(port: Int): Network.HttpServer =
            TODO("BrowserNetwork.startHttpServer not implemented")

        override fun connectWebSocket(
            toAddress: Network.Address,
            port: Int,
            path: String,
            webSocketListener: Network.WebSocketListener
        ): Network.TcpConnection {
            toAddress as BrowserAddress
            val proto = if (toAddress.isSSL) "wss" else "ws"
            val maybeSlash = if (path.startsWith("/")) "" else "/"
            val url = "$proto://${toAddress.host}:${toAddress.port}$maybeSlash$path"
            val webSocket = WebSocket(url)
            webSocket.binaryType = BinaryType.ARRAYBUFFER

            val tcpConnection = object : Network.TcpConnection {
                override val fromAddress: Network.Address = myAddress
                override val toAddress: Network.Address = myAddress
                override val port: Int get() = port

                override fun send(bytes: ByteArray) {
                    webSocket.send(Int8Array(bytes.toTypedArray()))
                }

                override fun close() {
                    webSocket.close()
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
                GlobalScope.launch {
                    webSocketListener.receive(tcpConnection, bytes)
                }
            }

            webSocket.onerror = {
                if (webSocket.readyState.toInt() == 3) {
                    // Closed
                } else {
                    logger.warn { "WebSocket error! readyState = ${webSocket.readyState}" }
                }
            }

            webSocket.onclose = {
                logger.info { "WebSocket closed! readyState = ${webSocket.readyState}" }
                webSocketListener.reset(tcpConnection)
            }

            return tcpConnection
        }

        override suspend fun httpGetRequest(address: Network.Address, port: Int, path: String): String {
            TODO("not implemented")
        }

        override fun createAddress(name: String): Network.Address =
            TODO("BrowserNetwork.createAddress not implemented")
    }

    data class BrowserAddress(
        private val protocol: String,
        val host: String,
        val port: String
    ) : Network.Address {
        val isSSL: Boolean get() = protocol == "https:"

        override fun asString(): String = "$protocol//$host:$port/"
        override fun toString(): String = asString()
    }

    companion object {
        private val logger = Logger<BrowserNetwork>()
    }
}