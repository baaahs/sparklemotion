package baaahs.net

import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Int8Array
import org.khronos.webgl.get
import org.w3c.dom.ARRAYBUFFER
import org.w3c.dom.BinaryType
import org.w3c.dom.WebSocket

class BrowserNetwork : Network {
    override fun link(): Network.Link = object : Network.Link {
        override val myAddress: Network.Address = object : Network.Address {}

        override val udpMtu = 1500

        override fun listenUdp(port: Int, udpListener: Network.UdpListener): Network.UdpSocket =
            TODO("BrowserNetwork.listenUdp not implemented")
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
}