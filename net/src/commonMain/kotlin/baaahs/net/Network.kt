package baaahs.net

public interface Network {
    public fun link(name: String): Link

    public interface Link {
        public val myAddress: Address
        public val myHostname: String
        public val udpMtu: Int
        public val mdns: Mdns

        public fun listenUdp(port: Int, udpListener: UdpListener): UdpSocket

        public fun startHttpServer(port: Int): HttpServer

        public suspend fun httpGetRequest(
            address: Address,
            port: Int = 80,
            path: String
        ): String

        public fun connectWebSocket(
            toAddress: Address,
            port: Int,
            path: String,
            webSocketListener: WebSocketListener
        ): TcpConnection

        public fun createAddress(name: String): Address
    }

    public interface Mdns {
        public fun register(
            hostname: String,
            type: String,
            proto: String,
            port: Int,
            domain: String = "local.",
            params: Map<String, String> = mutableMapOf()
        ): MdnsRegisteredService

        public fun unregister(inst: MdnsRegisteredService)
        public fun listen(type: String, proto: String, domain: String, handler: MdnsListenHandler)

        public fun String.normalizeMdnsDomain(): String {
            var dom = this
            if (dom.startsWith(".")) {
                dom = dom.substring(1)
            }
            if (!dom.endsWith(".")) {
                dom += "."
            }
            return dom
        }
    }

    public interface MdnsService {
        // todo: add service methods

        public val hostname: String
        public val type: String
        public val proto: String
        public val port: Int
        public val domain: String

        public fun getAddress(): Address?
        public fun getTXT(key: String): String?
        public fun getAllTXTs(): Map<String, String>
    }

    public interface MdnsRegisteredService : MdnsService {
        public fun unregister()
        public fun updateTXT(txt: Map<String, String>)
        public fun updateTXT(key: String, value: String)
        // todo: unsetTXT
        // todo: unsetAllTXT
    }

    public interface MdnsListenHandler {
        public fun added(service: MdnsService)
        public fun removed(service: MdnsService)
        public fun resolved(service: MdnsService)
    }

    public interface Address {
        public fun asString(): String
    }

    public interface UdpListener {
        public fun receive(fromAddress: Address, fromPort: Int, bytes: ByteArray)
    }

    public interface UdpSocket {
        public val serverPort: Int

        public fun sendUdp(toAddress: Address, port: Int, bytes: ByteArray)
        public fun broadcastUdp(port: Int, bytes: ByteArray)
    }

    public interface TcpConnection {
        public val fromAddress: Address
        public val toAddress: Address
        public val port: Int

        public fun send(bytes: ByteArray)
        public fun close()
    }

    public interface HttpServer {
        public fun listenWebSocket(path: String, webSocketListener: WebSocketListener) {
            listenWebSocket(path) { webSocketListener }
        }

        public fun routing(config: HttpRouting.() -> Unit)
        public fun listenWebSocket(path: String, onConnect: (incomingConnection: TcpConnection) -> WebSocketListener)

        public interface HttpRequest {
            public fun param(name: String): String?
        }

        public interface HttpRouting {
            public fun get(path: String, handler: (HttpRequest) -> HttpResponse)
        }
    }

    public interface HttpResponse {
        public val statusCode: Int
        public val contentType: String
        public val body: ByteArray
    }

    public interface WebSocketListener {
        public fun connected(tcpConnection: TcpConnection)
        public suspend fun receive(tcpConnection: TcpConnection, bytes: ByteArray)
        public fun reset(tcpConnection: TcpConnection)
    }

    public object UdpProxy {
        public const val BROADCAST_OP: Char = 'B'
        public const val LISTEN_OP: Char = 'L'
        public const val SEND_OP: Char = 'S'
        public const val RECEIVE_OP: Char = 'R'
    }
}