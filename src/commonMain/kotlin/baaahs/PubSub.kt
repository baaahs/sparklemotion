package baaahs

import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter
import baaahs.net.Network
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonElementSerializer
import kotlinx.serialization.modules.SerialModule
import kotlinx.serialization.modules.SerializersModule
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
        override fun toString(): String = "Origin($id)"
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
        val serializer: KSerializer<T>,
        val serialModule: SerialModule = SerializersModule { }
    )

    abstract class Listener(private val origin: Origin) {
        fun onUpdate(data: JsonElement, fromOrigin: Origin) {
            if (origin !== fromOrigin) {
                onUpdate(data)
            }
        }

        abstract fun onUpdate(data: JsonElement)

        open val isServerListener = false
    }

    class TopicInfo<T>(private val topic: Topic<T>) {
        val name: String get() = topic.name
        lateinit var jsonValue: JsonElement private set

        private val listeners: MutableList<Listener> = mutableListOf()
        internal val listeners_TEST_ONLY: MutableList<Listener> get() = listeners
        private val json = Json(JsonConfiguration.Stable, topic.serialModule)

        fun notify(newValue: T, origin: Origin) = notify(json.toJson(topic.serializer, newValue), origin)

        fun notify(s: String, origin: Origin) = notify(json.parseJson(s), origin)

        private fun notify(newData: JsonElement, origin: Origin) {
            if (!this::jsonValue.isInitialized || newData != jsonValue) {
                jsonValue = newData
                listeners.forEach { listener -> listener.onUpdate(newData, origin) }
            }
        }

        fun deserialize(jsonElement: JsonElement): T = json.fromJson(topic.serializer, jsonElement)
        fun stringify(jsonElement: JsonElement): String = json.stringify(JsonElementSerializer, jsonElement)

        fun addListener(listener: Listener) {
            listeners.add(listener)
            if (this::jsonValue.isInitialized) {
                listener.onUpdate(jsonValue)
            }
        }

        fun removeListener(listener: Listener) {
            listeners.remove(listener)
        }

        // If only the server listener remains, effectively no listeners.
        fun noRemainingListeners(): Boolean = listeners.all { it.isServerListener }

        fun removeListeners(block: (Listener) -> Boolean) {
            listeners.removeAll(block)
        }
    }

    open class Connection(
        private val name: String,
        private val topics: MutableMap<String, TopicInfo<*>>
    ) : Origin("connection $name"), Network.WebSocketListener {
        var isConnected: Boolean = false

        private var connection: Network.TcpConnection? = null
        private val cleanup: MutableList<() -> Unit> = mutableListOf()

        override fun connected(tcpConnection: Network.TcpConnection) {
            debug("connection $name established")
            connection = tcpConnection
            isConnected = true
        }

        inner class ClientListener(
            private val topicInfo: TopicInfo<*>,
            val tcpConnection: Network.TcpConnection
        ): Listener(this) {
            override fun onUpdate(data: JsonElement) = sendTopicUpdate(topicInfo, data)
        }

        override fun receive(tcpConnection: Network.TcpConnection, bytes: ByteArray) {
            val reader = ByteArrayReader(bytes)
            when (val command = reader.readString()) {
                "sub" -> {
                    val topicName = reader.readString()
                    val topicInfo = topics[topicName] ?: throw IllegalArgumentException("Unknown topic $topicName")

                    val listener = ClientListener(topicInfo, tcpConnection)
                    topicInfo.addListener(listener)
                    cleanup.add {
                        topicInfo.removeListener(listener)
                    }
                }

                "unsub" -> {
                    val topicName = reader.readString()
                    val topicInfo = getTopic(topicName)

                    topicInfo.removeListeners { it is ClientListener && it.tcpConnection === tcpConnection }
                }

                "update" -> {
                    val topicName = reader.readString()
                    getTopic(topicName).notify(reader.readString(), this)
                }

                else -> {
                    throw IllegalArgumentException("huh? don't know what to do with $command")
                }
            }
        }

        private fun getTopic(topicName: String) =
            (topics[topicName] ?: error(unknown("topic", topicName, topics.keys)))

        fun sendTopicUpdate(topicInfo: TopicInfo<*>, data: JsonElement) {
            if (isConnected) {
                debug("update ${topicInfo.name} $data")

                val writer = ByteArrayWriter()
                writer.writeString("update")
                writer.writeString(topicInfo.name)
                writer.writeString(topicInfo.stringify(data))
                sendCommand(writer.toBytes())
            } else {
                debug("not connected, so no update $name $data")
            }
        }

        fun sendTopicSub(topicInfo: TopicInfo<*>) {
            if (isConnected) {
                debug("sub ${topicInfo.name}")

                val writer = ByteArrayWriter()
                writer.writeString("sub")
                writer.writeString(topicInfo.name)
                sendCommand(writer.toBytes())
            } else {
                debug("not connected, so no sub ${topicInfo.name}")
            }
        }

        fun sendTopicUnsub(topicInfo: TopicInfo<*>) {
            if (isConnected) {
                debug("unsub ${topicInfo.name}")

                val writer = ByteArrayWriter()
                writer.writeString("unsub")
                writer.writeString(topicInfo.name)
                sendCommand(writer.toBytes())
            } else {
                debug("not connected, so no unsub ${topicInfo.name}")
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

    abstract class Endpoint {
        protected fun <T> buildTopicInfo(topic: Topic<T>): TopicInfo<T> {
            return TopicInfo(topic)
        }
    }

    class Server(httpServer: Network.HttpServer) : Endpoint() {
        private val publisher = Origin("Server")
        private val topics: MutableMap<String, TopicInfo<*>> = hashMapOf()

        init {
            httpServer.listenWebSocket("/sm/ws") { incomingConnection ->
                val name = "server ${incomingConnection.toAddress} to ${incomingConnection.fromAddress}"
                Connection(name, topics)
            }
        }

        fun <T : Any> publish(topic: Topic<T>, data: T, onUpdate: (T) -> Unit): Channel<T> {
            val topicName = topic.name

            @Suppress("UNCHECKED_CAST")
            val topicInfo = topics.getOrPut(topicName) { TopicInfo(topic) } as TopicInfo<T>
            val listener = PublisherListener(topicInfo, publisher, onUpdate)
            topicInfo.addListener(listener)
            topicInfo.notify(data, publisher)

            return object : Channel<T> {
                override fun onChange(t: T) {
                    topicInfo.notify(t, publisher)
                }

                override fun replaceOnUpdate(onUpdate: (T) -> Unit) {
                    listener.onUpdateFn = onUpdate
                }

                override fun unsubscribe() {
                    // TODO("${CLASS_NAME}.unsubscribe not implemented")
                }
            }
        }

        internal fun getTopicInfo(topicName: String) = topics[topicName]

        inner class PublisherListener<T : Any>(
            private val topicInfo: TopicInfo<T>,
            origin: Origin,
            var onUpdateFn: (T) -> Unit
        ) : Listener(origin) {
            override fun onUpdate(data: JsonElement) {
                onUpdateFn(topicInfo.deserialize(data))
            }
        }
    }

    class Client(
        private val link: Network.Link,
        serverAddress: Network.Address,
        port: Int,
        coroutineScope: CoroutineScope = GlobalScope
    ) : Endpoint() {
        @JsName("isConnected")
        val isConnected: Boolean
            get() = connectionToServer.isConnected
        private val stateChangeListeners = mutableListOf<() -> Unit>()

        private val topics: MutableMap<String, TopicInfo<*>> = hashMapOf()
        private var connectionToServer: Connection = object : Connection("client ${link.myAddress} to $serverAddress", topics) {
            override fun connected(tcpConnection: Network.TcpConnection) {
                super.connected(tcpConnection)

                // If any topics were subscribed to before this connection was established, send the sub command now.
                topics.values.forEach { topicInfo -> sendTopicSub(topicInfo) }

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
        fun <T> subscribe(topic: Topic<T>, onUpdateFn: (T) -> Unit): Channel<T> {
            val subscriber = Origin("Subscriber at ${link.myAddress}")

            val topicName = topic.name

            @Suppress("UNCHECKED_CAST")
            val topicInfo = topics.getOrPut(topicName) {
                buildTopicInfo(topic)
                    .apply {
                        addListener(object : Listener(connectionToServer) {
                            override fun onUpdate(data: JsonElement) =
                                connectionToServer.sendTopicUpdate(this@apply, data)

                            override val isServerListener: Boolean get() = true
                        })
                    }
                    .also { connectionToServer.sendTopicSub(it) }
            } as TopicInfo<T>

            val listener = object : Listener(subscriber) {
                override fun onUpdate(data: JsonElement) = onUpdateFn(topicInfo.deserialize(data))
            }
            topicInfo.addListener(listener)

            return object : Channel<T> {
                override fun onChange(t: T) {
                    topicInfo.notify(t, subscriber)
                }

                override fun replaceOnUpdate(onUpdate: (T) -> Unit) {
                    TODO("Client.channel.replaceOnUpdate not implemented")
                }

                override fun unsubscribe() {
                    topicInfo.removeListener(listener)

                    if (topicInfo.noRemainingListeners()) {
                        connectionToServer.sendTopicUnsub(topicInfo)
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