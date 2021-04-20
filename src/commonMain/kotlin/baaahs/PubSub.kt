package baaahs

import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter
import baaahs.net.Network
import baaahs.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.modules.SerializersModule
import kotlin.js.JsName
import kotlin.jvm.Synchronized
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

abstract class PubSub {

    companion object {
        val verbose = true

        fun listen(httpServer: Network.HttpServer, coroutineScope: CoroutineScope): Server {
            return Server(httpServer, coroutineScope)
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
        serialModule: SerializersModule = SerializersModule {}
    ) {
        private val json = Json { serializersModule = serialModule }

        fun toJson(command: C): String = json.encodeToString(serializer, command)
        fun fromJson(command: String): C = json.decodeFromString(serializer, command)
        fun replyToJson(command: R): String = json.encodeToString(replySerializer, command)
        fun replyFromJson(command: String): R = json.decodeFromString(replySerializer, command)
    }

    data class Topic<T>(
        val name: String,
        val serializer: KSerializer<T>,
        val serialModule: SerializersModule = SerializersModule { }
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
        internal lateinit var jsonValue: JsonElement private set

        private val listeners: MutableList<Listener> = mutableListOf()
        internal val listeners_TEST_ONLY: MutableList<Listener> get() = listeners
        private val json = Json { serializersModule = topic.serialModule }

        fun notify(newValue: T, origin: Origin) {
            if (topic.serializer.descriptor.isNullable) {
                // Workaround for https://github.com/Kotlin/kotlinx.serialization/issues/539
                notify(json.encodeToJsonElement(ListSerializer(topic.serializer), listOf(newValue)), origin)
            } else {
                notify(json.encodeToJsonElement(topic.serializer, newValue), origin)
            }
        }

        fun notify(s: String, origin: Origin) = notify(json.parseToJsonElement(s), origin)

        private fun notify(newData: JsonElement, origin: Origin) {
            maybeUpdateValueAndGetListeners(newData).forEach { listener ->
                listener.onUpdate(newData, origin)
            }
        }

        @Synchronized
        private fun maybeUpdateValueAndGetListeners(newData: JsonElement): List<Listener> {
            return if (!this::jsonValue.isInitialized || newData != jsonValue) {
                jsonValue = newData
                listeners.toList()
            } else {
                emptyList()
            }
        }

        @ExperimentalSerializationApi
        fun deserialize(jsonElement: JsonElement): T {
            return if (topic.serializer.descriptor.isNullable) {
                // Workaround for https://github.com/Kotlin/kotlinx.serialization/issues/539
                json.decodeFromJsonElement(ListSerializer(topic.serializer), jsonElement)[0]
            } else {
                json.decodeFromJsonElement(topic.serializer, jsonElement)
            }
        }

        fun stringify(jsonElement: JsonElement): String = json.encodeToString(JsonElement.serializer(), jsonElement)

        @Synchronized
        fun addListener(listener: Listener) {
            listeners.add(listener)
            if (this::jsonValue.isInitialized) {
                listener.onUpdate(jsonValue)
            }
        }

        @Synchronized
        fun removeListener(listener: Listener) {
            listeners.remove(listener)
        }

        // If only the server listener remains, effectively no listeners.
        @Synchronized
        fun noRemainingListeners(): Boolean = listeners.all { it.isServerListener }

        @Synchronized
        fun removeListeners(block: (Listener) -> Boolean) {
            listeners.removeAll(block)
        }
    }

    interface CommandChannel<C, R> {
        suspend fun send(command: C): R
    }

    class Topics {
        private val map = hashMapOf<String, TopicInfo<*>>()

        @Synchronized
        operator fun get(topicName: String): TopicInfo<*>? = map[topicName]

        @Synchronized
        fun find(topicName: String): TopicInfo<*> = map.getBang(topicName, "topic")

        @Synchronized
        fun <T> getOrPut(topicName: String, function: () -> TopicInfo<T>): TopicInfo<*> {
            return map.getOrPut(topicName, function)
        }

        @Synchronized
        private fun values() = map.values.toList()

        fun forEach(function: (TopicInfo<*>) -> Unit) {
            values().forEach(function)
        }
    }

    class CommandChannels(private val handlerScope: CoroutineScope) {
        private val serverChannels: MutableMap<String, ServerCommandChannel<*, *>> = hashMapOf()
        private val clientChannels: MutableMap<String, ClientCommandChannel<*, *>> = hashMapOf()

        @Synchronized
        private fun hasServerChannel(name: String) = serverChannels.containsKey(name)

        @Synchronized
        fun getServerChannel(name: String) = serverChannels.getBang(name, "command channel")

        @Synchronized
        private fun putServerChannel(name: String, channel: ServerCommandChannel<*, *>) {
            serverChannels[name] = channel
        }


        fun <C, R> open(
            commandPort: CommandPort<C, R>,
            client: Client
        ): CommandChannel<C, R> {
            val name = commandPort.name
            if (hasClientChannel(name)) error("Command channel $name already exists.")
            return ClientCommandChannel(commandPort, handlerScope, client).also {
                putClientChannel(name, it)
            }
        }

        @Synchronized
        private fun hasClientChannel(name: String) = clientChannels.containsKey(name)

        @Synchronized
        fun getClientChannel(name: String) = clientChannels.getBang(name, "command channel")

        @Synchronized
        private fun putClientChannel(name: String, channel: ClientCommandChannel<*, *>) {
            clientChannels[name] = channel
        }

        fun <C, R> listen(
            commandPort: CommandPort<C, R>,
            callback: suspend (command: C) -> R
        ) {
            val name = commandPort.name
            if (hasServerChannel(name)) error("Command channel $name already exists.")
            putServerChannel(name,
                ServerCommandChannel(commandPort, handlerScope) { command -> callback(command) })
        }

        class ServerCommandChannel<C, R>(
            private val commandPort: CommandPort<C, R>,
            private val handlerScope: CoroutineScope,
            private val callback: suspend (command: C) -> R
        ) {
            fun receiveCommand(commandJson: String, commandId: String, fromConnection: Connection) {
                handlerScope.launch {
                    val reply = try {
                        callback.invoke(commandPort.fromJson(commandJson))
                    } catch (e: Exception) {
                        logger.warn(e) { "Error in remote command invocation ($commandId)." }
                        fromConnection.sendError(commandPort, e.message ?: "unknown error", commandId)
                        return@launch
                    }
                    fromConnection.sendReply(commandPort, reply, commandId)
                }
            }
        }

        class ClientCommandChannel<C, R>(
            private val commandPort: CommandPort<C, R>,
            private val handlerScope: CoroutineScope,
            private val client: Client
        ) : CommandChannel<C, R> {
            private val handlers = mutableMapOf<String, Client.CommandHandler<R>>()

            override suspend fun send(command: C): R {
                val commandId = client.clientId + ":" + client.nextCommandId++
                val handler = Client.CommandHandler<R>(commandId)
                handlers[commandId] = handler
                client.connectionToServer.sendCommand(commandPort, command, commandId)
                return handler.receive()
            }

            fun receiveReply(replyJson: String, commandId: String) {
                handlers.remove(commandId)?.let {
                    val reply = commandPort.replyFromJson(replyJson)
                    handlerScope.launch { it.onReply(reply) }
                }
            }

            fun receiveError(message: String, commandId: String) {
                handlers.remove(commandId)?.let {
                    handlerScope.launch { it.onError(message) }
                }
            }
        }
    }

    abstract class Connection(
        private val name: String,
        private val topics: Topics,
        private val commandChannels: CommandChannels
    ) : Origin("connection $name"), Network.WebSocketListener {
        var isConnected: Boolean = false

        private var connection: Network.TcpConnection? = null
        private val cleanups = Cleanups()

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
                    val topicInfo = topics.find(topicName)

                    val listener = ClientListener(topicInfo, tcpConnection)
                    topicInfo.addListener(listener)
                    cleanups.add { topicInfo.removeListener(listener) }
                }

                "unsub" -> {
                    val topicName = reader.readString()
                    val topicInfo = topics.find(topicName)

                    topicInfo.removeListeners { it is ClientListener && it.tcpConnection === tcpConnection }
                }

                "update" -> {
                    val topicName = reader.readString()
                    val topicInfo = topics.find(topicName)

                    topicInfo.notify(reader.readString(), this)
                }

                "command" -> {
                    val name = reader.readString()
                    val commandChannel = commandChannels.getServerChannel(name)
                    val commandId = reader.readString()
                    commandChannel.receiveCommand(reader.readString(), commandId, this)
                }

                "commandError" -> {
                    val name = reader.readString()
                    val commandChannel = commandChannels.getClientChannel(name)
                    val commandId = reader.readString()
                    commandChannel.receiveError(reader.readString(), commandId)
                }

                "commandReply" -> {
                    val name = reader.readString()
                    val commandChannel = commandChannels.getClientChannel(name)
                    val commandId = reader.readString()
                    commandChannel.receiveReply(reader.readString(), commandId)
                }

                else -> {
                    throw IllegalArgumentException("huh? don't know what to do with $command")
                }
            }
        }

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

        fun <C, R> sendCommand(commandPort: CommandPort<C, R>, command: C, commandId: String) {
            if (isConnected) {
                if (verbose) debug("command ${commandPort.name} ${commandPort.toJson(command)}")

                val writer = ByteArrayWriter()
                writer.writeString("command")
                writer.writeString(commandPort.name)
                writer.writeString(commandId)
                writer.writeString(commandPort.toJson(command))
                sendMessage(writer.toBytes())
            } else {
                debug("not connected, so no command ${commandPort.name}")
            }
        }

        fun <C, R> sendReply(commandPort: CommandPort<C, R>, reply: R, commandId: String) {
            if (isConnected) {
                if (verbose) debug("commandReply ${commandPort.name} $commandId ${commandPort.replyToJson(reply)}")

                val writer = ByteArrayWriter()
                writer.writeString("commandReply")
                writer.writeString(commandPort.name)
                writer.writeString(commandId)
                writer.writeString(commandPort.replyToJson(reply))
                sendMessage(writer.toBytes())
            } else {
                debug("not connected, so no reply ${commandPort.name}")
            }
        }

        fun <C, R> sendError(commandPort: CommandPort<C, R>, message: String, commandId: String) {
            if (isConnected) {
                if (verbose) debug("commandError ${commandPort.name} $commandId $message")

                val writer = ByteArrayWriter()
                writer.writeString("commandError")
                writer.writeString(commandPort.name)
                writer.writeString(commandId)
                writer.writeString(message)
                sendMessage(writer.toBytes())
            } else {
                debug("not connected, so no error ${commandPort.name}")
            }
        }

        override fun reset(tcpConnection: Network.TcpConnection) {
            logger.info { "PubSub client $name disconnected." }
            isConnected = false
            cleanups.invokeAll()
        }

        private fun sendMessage(bytes: ByteArray) {
            connection?.send(bytes)
        }

        private fun debug(message: String) {
            logger.info { "[$name${if (!isConnected) " (not connected)" else ""}]: $message" }
        }

        override fun toString(): String = "Connection from $name"
    }

    class ConnectionFromClient(
        name: String,
        topics: Topics,
        commandChannels: CommandChannels
    ) : Connection(name, topics, commandChannels)

    abstract class Endpoint(handlerScope: CoroutineScope) {
        protected val commandChannels = CommandChannels(handlerScope)

        protected fun <T> buildTopicInfo(topic: Topic<T>): TopicInfo<T> {
            return TopicInfo(topic)
        }

        abstract fun <T : Any?> openChannel(topic: Topic<T>, initialValue: T, onUpdate: (T) -> Unit): Channel<T>

        fun <C, R> listenOnCommandChannel(
            commandPort: CommandPort<C, R>,
            callback: suspend (command: C) -> R
        ) {
            commandChannels.listen(commandPort, callback)
        }
    }

    class Server(httpServer: Network.HttpServer, private val handlerScope: CoroutineScope) : Endpoint(handlerScope) {
        private val publisher = Origin("Server-side publisher")
        private val topics: Topics = Topics()

        init {
            httpServer.listenWebSocket("/sm/ws") { incomingConnection ->
                val name = "server ${incomingConnection.toAddress} to ${incomingConnection.fromAddress}"
                ConnectionFromClient(name, topics, commandChannels)
            }
        }

        override fun <T> openChannel(topic: Topic<T>, initialValue: T, onUpdate: (T) -> Unit): Channel<T> {
            return publish(topic, initialValue, onUpdate)
        }

        fun <T : Any?> publish(topic: Topic<T>, data: T, onUpdate: (T) -> Unit): Channel<T> {
            val topicName = topic.name

            @Suppress("UNCHECKED_CAST")
            val topicInfo = topics.getOrPut(topicName) { TopicInfo(topic) } as TopicInfo<T>
            val listener = PublisherListener(topicInfo, publisher) {
                handlerScope.launch { onUpdate(it) }
            }
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
    }

    open class Client(
        private val link: Network.Link,
        private val serverAddress: Network.Address,
        private val port: Int,
        private val coroutineScope: CoroutineScope = GlobalScope
    ) : Endpoint(coroutineScope) {
        @JsName("isConnected")
        val isConnected: Boolean
            get() = connectionToServer.isConnected
        private val stateChangeListeners = mutableListOf<() -> Unit>()

        private val topics: Topics = Topics()

        internal var connectionToServer: Connection = ConnectionToServer()
        internal var nextCommandId: Int = 0

        // TODO: this should be issued by the server instead.
        val clientId = randomId("pubsub-client")

        inner class ConnectionToServer : Connection("Client-side connection from ${link.myAddress} to server at $serverAddress", topics, commandChannels) {
            override fun connected(tcpConnection: Network.TcpConnection) {
                super.connected(tcpConnection)

                // If any topics were subscribed to before this connection was established, send the sub command now.
                topics.forEach { topicInfo -> sendTopicSub(topicInfo) }

                notifyChangeListeners()
            }

            override fun reset(tcpConnection: Network.TcpConnection) {
                super.reset(tcpConnection)
                notifyChangeListeners()

                coroutineScope.launch {
                    delay(1000)
                    connectWebSocket()
                }
            }
        }

        init {
            connectWebSocket()
        }

        private fun connectWebSocket() {
            link.connectWebSocket(serverAddress, port, "/sm/ws", connectionToServer)
        }

        override fun <T> openChannel(topic: Topic<T>, initialValue: T, onUpdate: (T) -> Unit): Channel<T> {
            return subscribe(topic, onUpdate)
        }

        @JsName("subscribe")
        fun <T> subscribe(topic: Topic<T>, onUpdateFn: (T) -> Unit): Channel<T> {
            val subscriber = Origin("Client-side subscriber at ${link.myAddress}")

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
            commandPort: CommandPort<C, R>
        ): CommandChannel<C, R> {
            return commandChannels.open(commandPort, this)
        }

        fun <C, R> commandSender(
            commandPort: CommandPort<C, R>
        ): suspend (command: C) -> R {
            val commandChannel = openCommandChannel(commandPort)
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

        class CommandHandler<R>(private val commandId: String) {
            private val coroutineChannel = kotlinx.coroutines.channels.Channel<Pair<R?, String?>>()

            suspend fun receive(): R {
                val (reply, error) = coroutineChannel.receive()
                return reply ?: error(error ?: "Unknown error; command=$commandId")
            }

            suspend fun onReply(reply: R) = coroutineChannel.send(reply to null)

            suspend fun onError(message: String) = coroutineChannel.send(null to message)

//            fun receiveError(reply: R)
        }
    }

    private class Cleanups {
        private val cleanups = mutableListOf<() -> Unit>()

        @Synchronized
        fun add(cleanup: () -> Unit) { cleanups.add(cleanup) }

        @Synchronized
        fun invokeAll() {
            cleanups.forEach { it.invoke() }
            cleanups.clear()
        }
    }
}