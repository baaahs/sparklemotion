package baaahs.net

import baaahs.io.Fs.File
import baaahs.sm.brain.proto.Message
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

interface Network {
    fun link(name: String): Link

    interface Link {
        val myAddress: Address
        val myHostname: String
        val udpMtu: Int
        val mdns: Mdns

        fun listenUdp(port: Int, udpListener: UdpListener): UdpSocket

        fun createHttpServer(port: Int): HttpServer

        suspend fun httpGetRequest(
            address: Address,
            port: Int = 80,
            path: String
        ): String

        fun connectWebSocket(
            toAddress: Address,
            port: Int,
            path: String,
            webSocketListener: WebSocketListener
        ): TcpConnection

        fun createAddress(name: String): Address
    }

    interface Mdns {
        fun register(
            hostname: String,
            type: String,
            proto: String,
            port: Int,
            domain: String = "local.",
            params: Map<String, String> = mutableMapOf()
        ): MdnsRegisteredService

        fun unregister(inst: MdnsRegisteredService)
        fun listen(type: String, proto: String, domain: String, handler: MdnsListenHandler)

        companion object {
            fun String.normalizeMdnsDomain(): String {
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
    }

    interface MdnsService {
        // todo: add service methods

        val hostname: String
        val type: String
        val proto: String
        val port: Int
        val domain: String

        fun getAddress(): Address?
        fun getTXT(key: String): String?
        fun getAllTXTs(): Map<String, String>
    }

    interface MdnsRegisteredService : MdnsService {
        fun unregister()
        fun updateTXT(txt: Map<String, String>)
        fun updateTXT(key: String, value: String)
        // todo: unsetTXT
        // todo: unsetAllTXT
    }

    interface MdnsListenHandler {
        fun added(service: MdnsService)
        fun removed(service: MdnsService)
        fun resolved(service: MdnsService)
    }

    interface Address {
        fun asString(): String
    }

    interface UdpListener {
        fun receive(fromAddress: Address, fromPort: Int, bytes: ByteArray)
    }

    interface UdpSocket {
        val serverPort: Int

        fun sendUdp(toAddress: Address, port: Int, bytes: ByteArray)
        fun sendUdp(toAddress: Address, port: Int, message: Message) = sendUdp(toAddress, port, message.toBytes())
        fun broadcastUdp(port: Int, bytes: ByteArray)
        fun broadcastUdp(port: Int, message: Message) = broadcastUdp(port, message.toBytes())
        fun close()
    }

    interface TcpConnection {
        val fromAddress: Address
        val toAddress: Address
        val port: Int

        fun send(bytes: ByteArray)

        fun send(message: Message) {
            send(message.toBytes())
        }

        fun close()
    }

    interface HttpServer {
        fun listenWebSocket(path: String, webSocketListener: WebSocketListener) {
            listenWebSocket(path) { webSocketListener }
        }

        fun routing(config: HttpRouting.() -> Unit)
        fun listenWebSocket(path: String, onConnect: (incomingConnection: TcpConnection) -> WebSocketListener)

        fun start()
        fun stop() {}

        interface HttpRequest {
            fun param(name: String): String?
        }

        interface HttpRouting {
            fun get(path: String, handler: suspend HttpHandling.() -> Unit)
            fun staticResources(path: String, basePackage: String)
            fun staticFiles(path: String, dir: File)
        }

        interface HttpHandling {
            suspend fun redirect(path: String)
            suspend fun respondWithResource(path: String, resourcePackage: String)
        }
    }

    interface HttpResponse {
        val statusCode: Int
        val contentType: String
        val body: ByteArray
    }

    class TextResponse(override val statusCode: Int = 200, body: String) : HttpResponse {
        override val body = body.encodeToByteArray()
        override val contentType = "text/plain"
    }

    class JsonResponse(val json: Json, override val statusCode: Int = 200, element: JsonElement) : HttpResponse {
        companion object {
            fun <T : Any>create(json: Json, statusCode: Int = 200, data: T, serializer: KSerializer<T>) =
                JsonResponse(json, statusCode, json.encodeToJsonElement(serializer, data))
        }

        override val body = json.encodeToString(JsonElement.serializer(), element).encodeToByteArray()
        override val contentType = "application/json"
    }

    interface WebSocketListener {
        fun connected(tcpConnection: TcpConnection)
        suspend fun receive(tcpConnection: TcpConnection, bytes: ByteArray)
        fun reset(tcpConnection: TcpConnection)
    }

    object UdpProxy {
        val BROADCAST_OP = 'B'
        val LISTEN_OP = 'L'
        val SEND_OP = 'S'
        val RECEIVE_OP = 'R'
    }
}