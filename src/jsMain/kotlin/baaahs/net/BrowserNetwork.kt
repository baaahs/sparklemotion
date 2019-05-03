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

        override fun listenUdp(port: Int, udpListener: Network.UdpListener) {
            TODO("BrowserNetwork.listenUdp not implemented")
        }

        override fun sendUdp(toAddress: Network.Address, port: Int, bytes: ByteArray) {
            TODO("BrowserNetwork.sendUdp not implemented")
        }

        override fun broadcastUdp(port: Int, bytes: ByteArray) {
            TODO("BrowserNetwork.broadcastUdp not implemented")
        }

        override fun listenTcp(port: Int, tcpServerSocketListener: Network.TcpServerSocketListener) {
            TODO("BrowserNetwork.listenTcp not implemented")
        }

        override fun connectTcp(
            toAddress: Network.Address,
            port: Int,
            tcpListener: Network.TcpListener
        ): Network.TcpConnection {
            val webSocket = WebSocket((toAddress as BrowserAddress).urlString + "sm/ws")
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
                tcpListener.connected(tcpConnection)
            }

            webSocket.onmessage = {
                // TODO: be less woefully inefficient...
                val buf = it.data as ArrayBuffer
                val byteBuf = Int8Array(buf)
                val bytes = ByteArray(byteBuf.length)
                for (i in 0 until byteBuf.length) {
                    bytes[i] = byteBuf[i]
                }
                tcpListener.receive(tcpConnection, bytes)
            }

            webSocket.onerror = {
                console.log("WebSocket error!", it)
            }

            webSocket.onclose = {
                console.log("WebSocket close!", it)
            }


            return tcpConnection
        }
    }

    class BrowserAddress(val urlString: String) : Network.Address
}