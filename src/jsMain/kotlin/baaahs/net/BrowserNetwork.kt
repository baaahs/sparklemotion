package baaahs.net

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

        var udpProxy: BrowserUdpProxy? = null
        init {
            udpProxyAddress?.let {
                udpProxy = BrowserUdpProxy(this, it, udpProxyPort)
            }
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

            webSocket.onerror = { console.error("WebSocket error!", it) }
            webSocket.onclose = {
                console.error("WebSocket close!", it)
                webSocketListener.reset(tcpConnection)
            }

            return tcpConnection
        }

        override suspend fun httpGetRequest(address: Network.Address, port: Int, path: String): String {
            TODO("not implemented")
        }

        override fun createAddress(name: String): Network.Address = BrowserAddress(name)
    }

    class BrowserAddress(val urlString: String) : Network.Address {
        override fun asString(): String = urlString
        override fun toString(): String = asString()
    }

}