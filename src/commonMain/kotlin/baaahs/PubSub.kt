package baaahs

import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter
import baaahs.net.Network
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.list
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonElementSerializer
import kotlinx.serialization.modules.SerialModule
import kotlinx.serialization.modules.SerializersModule
import kotlin.js.JsName
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

abstract class PubSub {

    companion object {
        val verbose = true

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

    class CommandPort<C, R>(
        val name: String,
        private val serializer: KSerializer<C>,
        private val replySerializer: KSerializer<R>,
        serialModule: SerialModule = SerializersModule {}
    ) {
        private val json = Json(JsonConfiguration.Stable, serialModule)

        fun toJson(command: C): String = json.stringify(serializer, command)
        fun fromJson(command: String): C = json.parse(serializer, command)
        fun replyToJson(command: R): String = json.stringify(replySerializer, command)
        fun replyFromJson(command: String): R = json.parse(replySerializer, command)
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

        fun notify(newValue: T, origin: Origin) {
            if (topic.serializer.descriptor.isNullable) {
                // Workaround for https://github.com/Kotlin/kotlinx.serialization/issues/539
                notify(json.toJson(topic.serializer.list, listOf(newValue)), origin)
            } else {
                notify(json.toJson(topic.serializer, newValue), origin)
            }
        }

        fun notify(s: String, origin: Origin) = notify(json.parseJson(s), origin)

        private fun notify(newData: JsonElement, origin: Origin) {
            if (!this::jsonValue.isInitialized || newData != jsonValue) {
                jsonValue = newData
                listeners.forEach { listener -> listener.onUpdate(newData, origin) }
            }
        }

        fun deserialize(jsonElement: JsonElement): T {
            return if (topic.serializer.descriptor.isNullable) {
                // Workaround for https://github.com/Kotlin/kotlinx.serialization/issues/539
                json.fromJson(topic.serializer.list, jsonElement)[0]
            } else {
                json.fromJson(topic.serializer, jsonElement)
            }
        }

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

    interface CommandChannel<C, R> {
        fun send(command: C)
    }

    class Topics : MutableMap<String, TopicInfo<*>> by hashMapOf()
    class CommandChannels {
        private val serverChannels: MutableMap<String, Server.ServerCommandChannel<*, *>> = hashMapOf()
        private val clientChannels: MutableMap<String, Client.ClientCommandChannel<*, *>> = hashMapOf()

        fun hasServerChannel(name: String) = serverChannels.containsKey(name)
        fun getServerChannel(name: String) = serverChannels.getBang(name, "command channel")
        fun putServerChannel(name: String, channel: Server.ServerCommandChannel<*, *>) {
            serverChannels[name] = channel
        }

        fun hasClientChannel(name: String) = clientChannels.containsKey(name)
        fun getClientChannel(name: String) = clientChannels.getBang(name, "command channel")
        fun putClientChannel(name: String, channel: Client.ClientCommandChannel<*, *>) {
            clientChannels[name] = channel
        }
    }

    open class Connection(
        private val name: String,
        private val topics: Topics,
        private val commandChannels: CommandChannels
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
        ) : Listener(this) {
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

                "command" -> {
                    val name = reader.readString()
                    val commandChannel = commandChannels.getServerChannel(name)
                    commandChannel.receiveCommand(reader.readString(), this)
                }

                "commandReply" -> {
                    val name = reader.readString()
                    val commandChannel = commandChannels.getClientChannel(name)
                    commandChannel.receiveReply(reader.readString())
                }

                else -> {
                    throw IllegalArgumentException("huh? don't know what to do with $command")
                }
            }
        }

        private fun getTopic(topicName: String) = topics.getBang(topicName, "topic")

        fun sendTopicUpdate(topicInfo: TopicInfo<*>, data: JsonElement) {
            if (isConnected) {
                if (verbose) debug("update ${topicInfo.name} ${topicInfo.stringify(data)}")

                val writer = ByteArrayWriter()
                writer.writeString("update")
                writer.writeString(topicInfo.name)
                writer.writeString(topicInfo.stringify(data))
                sendMessage(writer.toBytes())
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
                sendMessage(writer.toBytes())
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
                sendMessage(writer.toBytes())
            } else {
                debug("not connected, so no unsub ${topicInfo.name}")
            }
        }

        fun <C, R> sendCommand(commandPort: CommandPort<C, R>, command: C) {
            if (isConnected) {
                if (verbose) debug("command ${commandPort.name} ${commandPort.toJson(command)}")

                val writer = ByteArrayWriter()
                writer.writeString("command")
                writer.writeString(commandPort.name)
                writer.writeString(commandPort.toJson(command))
                sendMessage(writer.toBytes())
            } else {
                debug("not connected, so no command ${commandPort.name}")
            }
        }

        fun <C, R> sendReply(commandPort: CommandPort<C, R>, reply: R) {
            if (isConnected) {
                if (verbose) debug("commandReply ${commandPort.name} ${commandPort.replyToJson(reply)}")

                val writer = ByteArrayWriter()
                writer.writeString("commandReply")
                writer.writeString(commandPort.name)
                writer.writeString(commandPort.replyToJson(reply))
                sendMessage(writer.toBytes())
            } else {
                debug("not connected, so no reply ${commandPort.name}")
            }
        }

        override fun reset(tcpConnection: Network.TcpConnection) {
            logger.info { "PubSub client $name disconnected." }
            isConnected = false
            cleanup.forEach { it.invoke() }
        }

        private fun sendMessage(bytes: ByteArray) {
            connection?.send(bytes)
        }

        private fun debug(message: String) {
            logger.info { "[$name${if (!isConnected) " (not connected)" else ""}]: $message" }
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
        private val topics: Topics = Topics()
        private val commandChannels = CommandChannels()

        init {
            httpServer.listenWebSocket("/sm/ws") { incomingConnection ->
                val name = "server ${incomingConnection.toAddress} to ${incomingConnection.fromAddress}"
                Connection(name, topics, commandChannels)
            }
        }

        fun <T : Any?> publish(topic: Topic<T>, data: T, onUpdate: (T) -> Unit): Channel<T> {
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

        fun <C, R> listenOnCommandChannel(
            commandPort: CommandPort<C, R>,
            callback: (command: C, reply: (R) -> Unit) -> Unit
        ) {
            val name = commandPort.name
            if (commandChannels.hasServerChannel(name)) error("Command channel $name already exists.")
            commandChannels.putServerChannel(name, ServerCommandChannel(commandPort, callback))
        }

        fun <T> state(topic: Topic<T>, initialValue: T, callback: (T) -> Unit = {}): ReadWriteProperty<Any, T> {
            return object : ReadWriteProperty<Any, T> {
                private var value: T = initialValue

                private val channel = publish(topic, initialValue) {
                    value = it
                    callback(it)
                }

                override fun getValue(thisRef: Any, property: KProperty<*>): T {
                    return value
                }

                override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
                    this.value = value
                    channel.onChange(value)
                }
            }
        }

        internal fun getTopicInfo(topicName: String) = topics[topicName]

        inner class PublisherListener<T : Any?>(
            private val topicInfo: TopicInfo<T>,
            origin: Origin,
            var onUpdateFn: (T) -> Unit
        ) : Listener(origin) {
            override fun onUpdate(data: JsonElement) {
                onUpdateFn(topicInfo.deserialize(data))
            }
        }

        inner class ServerCommandChannel<C, R>(
            private val commandPort: CommandPort<C, R>,
            private val callback: (command: C, reply: (R) -> Unit) -> Unit
        ) {
            fun receiveCommand(commandJson: String, fromConnection: Connection) {
                callback.invoke(commandPort.fromJson(commandJson)) { reply ->
                    fromConnection.sendReply(commandPort, reply)
                }
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

        private val topics: Topics = Topics()
        private val commandChannels = CommandChannels()

        private var connectionToServer: Connection =
            object : Connection("client ${link.myAddress} to $serverAddress", topics, commandChannels) {
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

        fun <C, R> openCommandChannel(
            commandPort: CommandPort<C, R>,
            replyCallback: (R) -> Unit
        ): CommandChannel<C, R> {
            val name = commandPort.name
            if (commandChannels.hasClientChannel(name)) error("Command channel $name already exists.")
            return ClientCommandChannel(commandPort, replyCallback).also {
                commandChannels.putClientChannel(name, it)
            }
        }

        fun <C, R> commandSender(
            commandPort: CommandPort<C, R>,
            replyCallback: (R) -> Unit
        ): (command: C) -> Unit {
            val commandChannel = openCommandChannel(commandPort, replyCallback)
            return { command: C -> commandChannel.send(command) }
        }

        fun <T> state(topic: Topic<T>, initialValue: T? = null, callback: (T) -> Unit = {}): ReadWriteProperty<Any, T?> {
            return object : ReadWriteProperty<Any, T?> {
                private var value: T? = initialValue

                private val channel = subscribe(topic) {
                    value = it
                    callback(it)
                }

                override fun getValue(thisRef: Any, property: KProperty<*>): T? {
                    return value
                }

                override fun setValue(thisRef: Any, property: KProperty<*>, value: T?) {
                    this.value = value
                    channel.onChange(value!!)
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

        inner class ClientCommandChannel<C, R>(
            private val commandPort: CommandPort<C, R>,
            private val replyCallback: (R) -> Unit
        ) : CommandChannel<C, R> {
            override fun send(command: C) {
                connectionToServer.sendCommand(commandPort, command)
            }

            fun receiveReply(replyJson: String) {
                replyCallback.invoke(commandPort.replyFromJson(replyJson))
            }
        }
    }
}