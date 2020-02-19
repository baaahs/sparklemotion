package baaahs.net

import baaahs.proto.Message

interface Network {
    fun link(): Link

    interface Link {
        val myAddress: Address
        val myHostname: String
        val udpMtu: Int
        fun listenUdp(port: Int, udpListener: UdpListener): UdpSocket
        fun mdns(): Mdns
        fun startHttpServer(port: Int): HttpServer
        fun connectWebSocket(
            toAddress: Address,
            port: Int,
            path: String,
            webSocketListener: WebSocketListener
        ): TcpConnection
    }

    interface Mdns {
        fun register(hostname: String, type: String, proto: String, port: Int, domain: String = "local.", params: MutableMap<String, String> = mutableMapOf()): MdnsRegisteredService?
        fun unregister(inst: MdnsRegisteredService?)
        fun listen(type: String, proto: String, domain: String, handler: MdnsListenHandler)
    }

    interface MdnsService {
        // todo: add service methods

        val hostname : String
        val type     : String
        val proto    : String
        val port     : Int
        val domain   : String

        fun getAddress() : Address?
        fun getTXT(key: String) : String?
        fun getAllTXTs() : MutableMap<String, String>
    }

    interface MdnsRegisteredService : MdnsService {
        fun unregister()
        fun updateTXT(txt: MutableMap<String, String>)
        fun updateTXT(key: String, value: String)
    }

    interface MdnsListenHandler {
        fun resolved(service: MdnsService)
        fun removed(service: MdnsService)
    }

    interface Address

    interface UdpListener {
        fun receive(fromAddress: Address, fromPort: Int, bytes: ByteArray)
    }

    interface UdpSocket {
        val serverPort: Int

        fun sendUdp(toAddress: Address, port: Int, bytes: ByteArray)
        fun sendUdp(toAddress: Address, port: Int, message: Message) = sendUdp(toAddress, port, message.toBytes())
        fun broadcastUdp(port: Int, bytes: ByteArray)
        fun broadcastUdp(port: Int, message: Message) = broadcastUdp(port, message.toBytes())
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

    interface HttpServer {
        fun listenWebSocket(path: String, webSocketListener: WebSocketListener) {
            listenWebSocket(path) { webSocketListener }
        }

        fun listenWebSocket(path: String, onConnect: (incomingConnection: TcpConnection) -> WebSocketListener)
    }

    interface WebSocketListener {
        fun connected(tcpConnection: TcpConnection)
        fun receive(tcpConnection: TcpConnection, bytes: ByteArray)
        fun reset(tcpConnection: TcpConnection)
    }

    object UdpProxy {
        val BROADCAST_OP = 'B'
        val LISTEN_OP = 'L'
        val SEND_OP = 'S'
        val RECEIVE_OP = 'R'
    }
}