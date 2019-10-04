package baaahs

import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter
import baaahs.net.Network
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.*
import kotlinx.serialization.modules.EmptyModule
import kotlinx.serialization.modules.SerialModule
import kotlinx.serialization.modules.plus
import kotlin.coroutines.CoroutineContext
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
        fun onUpdate(data: JsonElement, fromOrigin: Origin) {
            if (origin !== fromOrigin) {
                onUpdate(data)
            }
        }

        abstract fun onUpdate(data: JsonElement)
    }

    class TopicInfo(val name: String, var data: JsonElement = JsonNull) {
        val listeners: MutableList<Listener> = mutableListOf()

        fun notify(jsonData: JsonElement, origin: Origin) {
            if (jsonData != data) {
                data = jsonData
                listeners.forEach { listener -> listener.onUpdate(jsonData, origin) }
            }
        }
    }

    open class Connection(
        private val name: String,
        private val topics: MutableMap<String, TopicInfo>,
        private val json: Json
    ) : Origin(), Network.WebSocketListener {
        var isConnected: Boolean = false

        protected var connection: Network.TcpConnection? = null
        private val toSend: MutableList<ByteArray> = mutableListOf()
        private val cleanup: MutableList<() -> Unit> = mutableListOf()

        override fun connected(tcpConnection: Network.TcpConnection) {
            debug("connection $this established")
            connection = tcpConnection
            isConnected = true

            toSend.forEach { tcpConnection.send(it) }
            toSend.clear()
        }

        override fun receive(tcpConnection: Network.TcpConnection, bytes: ByteArray) {
            val reader = ByteArrayReader(bytes)
            when (val command = reader.readString()) {
                "sub" -> {
                    val topicName = reader.readString()
                    val topicInfo = topics[topicName] ?: throw IllegalArgumentException("Unknown topic $topicName")

                    val listener = object : Listener(this) {
                        override fun onUpdate(data: JsonElement) = sendTopicUpdate(topicName, data)
                    }
                    topicInfo.listeners.add(listener)
                    cleanup.add {
                        topicInfo.listeners.remove(listener)
                    }

                    val topicData = topicInfo.data
                    if (topicData != JsonNull) {
                        listener.onUpdate(topicData)
                    }
                }

                "update" -> {
                    val topicName = reader.readString()
                    val data = json.parseJson(reader.readString())
                    val topicInfo = topics[topicName]
                    topicInfo?.notify(data, this)
                }

                else -> {
                    throw IllegalArgumentException("huh? don't know what to do with $command")
                }
            }
        }

        fun sendTopicUpdate(name: String, data: JsonElement) {
            debug("update $name $data")

            val writer = ByteArrayWriter()
            writer.writeString("update")
            writer.writeString(name)
            writer.writeString(json.stringify(JsonElementSerializer, data))
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
            isConnected = false
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
                Connection("server at ${incomingConnection.toAddress}", topics, json)
            }
        }

        fun <T : Any> publish(topic: Topic<T>, data: T, onUpdate: (T) -> Unit): Channel<T> {
            val publisher = Origin()
            val topicName = topic.name
            val jsonData = json.toJson(topic.serializer, data)
            val topicInfo = topics.getOrPut(topicName) { TopicInfo(topicName) }
            val listener = PublisherListener(topic, publisher, onUpdate)
            topicInfo.listeners.add(listener)
            topicInfo.notify(jsonData, publisher)

            return object : Channel<T> {
                override fun onChange(t: T) {
                    topicInfo.notify(json.toJson(topic.serializer, t), publisher)
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
            override fun onUpdate(data: JsonElement) {
                onUpdate(json.fromJson(topic.serializer, data))
            }
        }
    }

    class Client(
        link: Network.Link,
        serverAddress: Network.Address,
        port: Int,
        coroutineScope: CoroutineScope = GlobalScope
    ) : Endpoint() {
        @JsName("isConnected")
        val isConnected: Boolean
            get() = server.isConnected
        private val stateChangeListeners = mutableListOf<() -> Unit>()

        private val topics: MutableMap<String, TopicInfo> = hashMapOf()
        private var server: Connection = object : Connection("client at ${link.myAddress}", topics, json) {
            override fun connected(tcpConnection: Network.TcpConnection) {
                super.connected(tcpConnection)

                notifyChangeListeners()
            }

            override fun reset(tcpConnection: Network.TcpConnection) {
                super.reset(tcpConnection)
                notifyChangeListeners()

                coroutineScope.launch {
                    println("we got called!")
                    delay(1000)
                    println("a second passed!")
                    connectWebSocket(link, serverAddress, port)
                }
            }
        }

        init {
            connectWebSocket(link, serverAddress, port)
        }

        private fun connectWebSocket(link: Network.Link, serverAddress: Network.Address, port: Int) {
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
                            override fun onUpdate(data: JsonElement) = server.sendTopicUpdate(topicName, data)
                        })
                    }
                    .also { server.sendTopicSub(topicName) }
            }

            val listener = object : Listener(subscriber) {
                override fun onUpdate(data: JsonElement) = onUpdate(json.fromJson(topic.serializer, data))
            }
            topicInfo.listeners.add(listener)
            val data = topicInfo.data
            if (data != JsonNull) {
                listener.onUpdate(data)
            }

            return object : Channel<T> {
                override fun onChange(t: T) {
                    val jsonData = json.toJson(topic.serializer, t)
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

        @JsName("addStateChangeListener")
        fun addStateChangeListener(callback: () -> Unit) {
            stateChangeListeners.add(callback)
        }

        private fun notifyChangeListeners() {
            stateChangeListeners.forEach { callback -> callback() }
        }
    }
}