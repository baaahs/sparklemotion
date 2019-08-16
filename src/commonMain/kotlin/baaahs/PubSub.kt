package baaahs

import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter
import baaahs.net.Network
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.modules.EmptyModule
import kotlinx.serialization.modules.SerialModule
import kotlinx.serialization.modules.plus
import kotlin.js.JsName

abstract class PubSub {

    companion object {
        fun listen(httpServer: Network.HttpServer): Server {
            return Server(httpServer)
        }

        fun connect(networkLink: Network.Link, address: Network.Address, port: Int): Client {
            return Client(networkLink, address, port)
        }

        val logger = Logger("PubSub")
    }

    open class Origin

    interface Channel<T> {
        @JsName("onChange")
        fun onChange(t: T)

        fun replaceOnUpdate(onUpdate: (T) -> Unit)

        fun unsubscribe()
    }

    data class Topic<T>(
        val name: String,
        val serializer: KSerializer<T>
    )

    abstract class Listener(private val origin: Origin) {
        fun onUpdate(data: String, fromOrigin: Origin) {
            if (origin !== fromOrigin) {
                onUpdate(data)
            }
        }

        abstract fun onUpdate(data: String)
    }

    class TopicInfo(val name: String, var data: String? = null) {
        val listeners: MutableList<Listener> = mutableListOf()

        fun notify(jsonData: String, origin: Origin) {
            data = jsonData
            listeners.forEach { listener -> listener.onUpdate(jsonData, origin) }
        }
    }

    open class Connection(
        private val name: String,
        private val topics: MutableMap<String, TopicInfo>
    ) : Origin(), Network.WebSocketListener {
        protected var connection: Network.TcpConnection? = null
        private val toSend: MutableList<ByteArray> = mutableListOf()
        private val cleanup: MutableList<() -> Unit> = mutableListOf()

        override fun connected(tcpConnection: Network.TcpConnection) {
            debug("connection $this established")
            connection = tcpConnection
            toSend.forEach { tcpConnection.send(it) }
            toSend.clear()
        }

        override fun receive(tcpConnection: Network.TcpConnection, bytes: ByteArray) {
            val reader = ByteArrayReader(bytes)
            when (val command = reader.readString()) {
                "sub" -> {
                    val topicName = reader.readString()
                    val topicInfo = topics.getOrPut(topicName) { TopicInfo(topicName) }
                    val listener = object : Listener(this) {
                        override fun onUpdate(data: String) = sendTopicUpdate(topicName, data)
                    }
                    topicInfo.listeners.add(listener)
                    cleanup.add {
                        topicInfo.listeners.remove(listener)
                    }

                    val topicData = topicInfo.data
                    if (topicData != null) {
                        listener.onUpdate(topicData)
                    }
                }

                "update" -> {
                    val topicName = reader.readString()
                    val data = reader.readString()
                    val topicInfo = topics[topicName]
                    topicInfo?.notify(data, this)
                }

                else -> {
                    throw IllegalArgumentException("huh? don't know what to do with $command")
                }
            }
        }

        fun sendTopicUpdate(name: String, data: String) {
            debug("update $name $data")

            val writer = ByteArrayWriter()
            writer.writeString("update")
            writer.writeString(name)
            writer.writeString(data)
            sendCommand(writer.toBytes())
        }

        fun sendTopicSub(topicName: String) {
            debug("sub $topicName")

            val writer = ByteArrayWriter()
            writer.writeString("sub")
            writer.writeString(topicName)
            sendCommand(writer.toBytes())
        }

        override fun reset(tcpConnection: Network.TcpConnection) {
            logger.info { "PubSub client $name disconnected." }
            cleanup.forEach { it.invoke() }
        }

        private fun sendCommand(bytes: ByteArray) {
            val tcpConnection = connection
            if (tcpConnection == null) {
                toSend.add(bytes)
            } else {
                tcpConnection.send(bytes)
            }
        }

        private fun debug(message: String) {
            logger.debug { "[PubSub $name -> ${connection?.toAddress ?: "(deferred)"}]: $message" }
        }
    }

    open class Endpoint {
        var serialModule: SerialModule = EmptyModule
        var json = Json(JsonConfiguration.Stable, serialModule)

        fun install(toInstall: SerialModule) {
            serialModule = serialModule.plus(toInstall)
            json = Json(JsonConfiguration.Stable, serialModule)
        }
    }

    class Server(httpServer: Network.HttpServer) : Endpoint() {
        private val topics: MutableMap<String, TopicInfo> = hashMapOf()

        init {
            httpServer.listenWebSocket("/sm/ws") { incomingConnection ->
                Connection("server at ${incomingConnection.toAddress}", topics)
            }
        }

        fun <T : Any> publish(topic: Topic<T>, data: T, onUpdate: (T) -> Unit): Channel<T> {
            val publisher = Origin()
            val topicName = topic.name
            val jsonData = json.stringify(topic.serializer, data)
            val topicInfo = topics.getOrPut(topicName) { TopicInfo(topicName) }
            val listener = PublisherListener(topic, publisher, onUpdate)
            topicInfo.listeners.add(listener)
            topicInfo.notify(jsonData, publisher)

            return object : Channel<T> {
                override fun onChange(t: T) {
                    topicInfo.notify(json.stringify(topic.serializer, t), publisher)
                }

                override fun replaceOnUpdate(onUpdate: (T) -> Unit) {
                    listener.onUpdate = onUpdate
                }

                override fun unsubscribe() {
                    // TODO("${CLASS_NAME}.unsubscribe not implemented")
                }
            }
        }

        internal fun getTopicInfo(topicName: String) = topics[topicName]

        inner class PublisherListener<T : Any>(
            private val topic: Topic<T>,
            origin: Origin,
            var onUpdate: (T) -> Unit
        ) : Listener(origin) {
            override fun onUpdate(data: String) {
                onUpdate(json.parse(topic.serializer, data))
            }
        }
    }

    class Client(link: Network.Link, serverAddress: Network.Address, port: Int) : Endpoint() {
        private val topics: MutableMap<String, TopicInfo> = hashMapOf()
        private var server: Connection = Connection("client at ${link.myAddress}", topics)

        init {
            link.connectWebSocket(serverAddress, port, "/sm/ws", server)
        }

        @JsName("subscribe")
        fun <T> subscribe(topic: Topic<T>, onUpdate: (T) -> Unit): Channel<T> {
            val subscriber = Origin()

            val topicName = topic.name
            val topicInfo = topics.getOrPut(topicName) {
                TopicInfo(topicName)
                    .apply {
                        listeners.add(object : Listener(server) {
                            override fun onUpdate(data: String) = server.sendTopicUpdate(topicName, data)
                        })
                    }
                    .also { server.sendTopicSub(topicName) }
            }

            val listener = object : Listener(subscriber) {
                override fun onUpdate(data: String) = onUpdate(json.parse(topic.serializer, data))
            }
            topicInfo.listeners.add(listener)
            val data = topicInfo.data
            if (data != null) {
                listener.onUpdate(data)
            }

            return object : Channel<T> {
                override fun onChange(t: T) {
                    val jsonData = json.stringify(topic.serializer, t)
                    topicInfo.notify(jsonData, subscriber)
                }

                override fun replaceOnUpdate(onUpdate: (T) -> Unit) {
                    TODO("Client.channel.replaceOnUpdate not implemented")
                }

                override fun unsubscribe() {
                    // TODO("${CLASS_NAME}.unsubscribe not implemented")
                }
            }
        }
    }
}