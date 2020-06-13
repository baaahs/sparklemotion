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

    open class Origin(val id: String) {
        override fun toString(): String ="Origin($id)"
    }

    interface Channel<T> {
        @JsName("onChange")
        fun onChange(t: T)

        @JsName("replaceOnUpdate")
        fun replaceOnUpdate(onUpdate: (T) -> Unit)

        @JsName("unsubscribe")
        fun unsubscribe()
    }

    data class Topic<T>(
        val name: String,
        val serializer: KSerializer<T>
    )

    abstract class Listener(val origin: Origin) {
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
    ) : Origin("connection $name"), Network.WebSocketListener {
        var isConnected: Boolean = false

        protected var connection: Network.TcpConnection? = null
        private val cleanup: MutableList<() -> Unit> = mutableListOf()

        override fun connected(tcpConnection: Network.TcpConnection) {
            debug("connection $name established")
            connection = tcpConnection
            isConnected = true
        }

        inner class ClientListener(
            private val topicName: String,
            val tcpConnection: Network.TcpConnection
        ): Listener(this) {
            override fun onUpdate(data: JsonElement) = sendTopicUpdate(topicName, data)
        }

        override fun receive(tcpConnection: Network.TcpConnection, bytes: ByteArray) {
            val reader = ByteArrayReader(bytes)
            when (val command = reader.readString()) {
                "sub" -> {
                    val topicName = reader.readString()
                    val topicInfo = topics[topicName] ?: throw IllegalArgumentException("Unknown topic $topicName")

                    val listener = ClientListener(topicName, tcpConnection)
                    topicInfo.listeners.add(listener)
                    cleanup.add {
                        topicInfo.listeners.remove(listener)
                    }

                    val topicData = topicInfo.data
                    if (topicData != JsonNull) {
                        listener.onUpdate(topicData)
                    }
                }

                "unsub" -> {
                    val topicName = reader.readString()
                    val topicInfo = topics[topicName] ?: throw IllegalArgumentException("Unknown topic $topicName")

                    topicInfo.listeners.removeAll { it is ClientListener && it.tcpConnection === tcpConnection }
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
            if (isConnected) {
                debug("update $name $data")

                val writer = ByteArrayWriter()
                writer.writeString("update")
                writer.writeString(name)
                writer.writeString(json.stringify(JsonElementSerializer, data))
                sendCommand(writer.toBytes())
            } else {
                debug("not connected, so no update $name $data")
            }
        }

        fun sendTopicSub(topicName: String) {
            if (isConnected) {
                debug("sub $topicName")

                val writer = ByteArrayWriter()
                writer.writeString("sub")
                writer.writeString(topicName)
                sendCommand(writer.toBytes())
            } else {
                debug("not connected, so no sub $topicName")
            }
        }

        fun sendTopicUnsub(topicName: String) {
            if (isConnected) {
                debug("unsub $topicName")

                val writer = ByteArrayWriter()
                writer.writeString("unsub")
                writer.writeString(topicName)
                sendCommand(writer.toBytes())
            } else {
                debug("not connected, so no unsub $topicName")
            }
        }

        override fun reset(tcpConnection: Network.TcpConnection) {
            logger.info { "PubSub client $name disconnected." }
            isConnected = false
            cleanup.forEach { it.invoke() }
        }

        private fun sendCommand(bytes: ByteArray) {
            connection?.send(bytes)
        }

        private fun debug(message: String) {
            logger.info { "[$name ${if (!isConnected) "(not connected)" else ""}]: $message" }
        }

        override fun toString(): String = "Connection from $name"
    }

    open class Endpoint {
        var serialModule: SerialModule = EmptyModule
        var json = Json(JsonConfiguration.Stable, serialModule)

        fun install(toInstall: SerialModule) {
            serialModule = serialModule.plus(toInstall)
            json = Json(JsonConfiguration.Stable.copy(classDiscriminator = "#type"), serialModule)
        }
    }

    class Server(httpServer: Network.HttpServer) : Endpoint() {
        private val publisher = Origin("Server")
        private val topics: MutableMap<String, TopicInfo> = hashMapOf()

        init {
            httpServer.listenWebSocket("/sm/ws") { incomingConnection ->
                Connection("server ${incomingConnection.toAddress} to ${incomingConnection.fromAddress}", topics, json)
                    .apply { connected(incomingConnection) }
            }
        }

        fun <T : Any> publish(topic: Topic<T>, data: T, onUpdate: (T) -> Unit): Channel<T> {
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
        val link: Network.Link,
        serverAddress: Network.Address,
        port: Int,
        coroutineScope: CoroutineScope = GlobalScope
    ) : Endpoint() {
        @JsName("isConnected")
        val isConnected: Boolean
            get() = connectionToServer.isConnected
        private val stateChangeListeners = mutableListOf<() -> Unit>()

        private val topics: MutableMap<String, TopicInfo> = hashMapOf()
        private var connectionToServer: Connection = object : Connection("client ${link.myAddress} to $serverAddress", topics, json) {
            override fun connected(tcpConnection: Network.TcpConnection) {
                super.connected(tcpConnection)

                // If any topics were subscribed to before this connection was established, send the sub command now.
                topics.values.forEach { topic -> sendTopicSub(topic.name) }

                notifyChangeListeners()
            }

            override fun reset(tcpConnection: Network.TcpConnection) {
                super.reset(tcpConnection)
                notifyChangeListeners()

                coroutineScope.launch {
                    delay(1000)
                    connectWebSocket(link, serverAddress, port)
                }
            }
        }

        init {
            connectWebSocket(link, serverAddress, port)
        }

        private fun connectWebSocket(link: Network.Link, serverAddress: Network.Address, port: Int) {
            link.connectWebSocket(serverAddress, port, "/sm/ws", connectionToServer)
        }

        @JsName("subscribe")
        fun <T> subscribe(topic: Topic<T>, onUpdate: (T) -> Unit): Channel<T> {
            val subscriber = Origin("Subscriber at ${link.myAddress}")

            val topicName = topic.name
            val topicInfo = topics.getOrPut(topicName) {
                TopicInfo(topicName)
                    .apply {
                        listeners.add(object : Listener(connectionToServer) {
                            override fun onUpdate(data: JsonElement) =
                                connectionToServer.sendTopicUpdate(topicName, data)
                        })
                    }
                    .also { connectionToServer.sendTopicSub(topicName) }
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
                    topicInfo.listeners.remove(listener)

                    // If there's only one listener left, it's the server listener, and we can safely go away.
                    if (topicInfo.listeners.size == 1) {
                        connectionToServer.sendTopicUnsub(topicName)
                    }
                }
            }
        }

        @JsName("addStateChangeListener")
        fun addStateChangeListener(callback: () -> Unit) {
            stateChangeListeners.add(callback)
        }

        @JsName("removeStateChangeListener")
        fun removeStateChangeListener(callback: () -> Unit) {
            stateChangeListeners.remove(callback)
        }

        private fun notifyChangeListeners() {
            stateChangeListeners.forEach { callback -> callback() }
        }
    }
}