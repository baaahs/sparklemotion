package baaahs

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

class PubSub(private val networkLink: Network.Link) {

    open class Origin

    interface Observer<T> {
        fun onChange(t: T)
    }

    companion object {
        fun listen(networkLink: Network.Link, port: Int): Server {
            return Server(networkLink, port)
        }

        fun connect(networkLink: Network.Link, address: Network.Address, port: Int): Client {
            return Client(networkLink, address, port)
        }
    }

    data class Topic<T>(
        val name: String,
        val serializer: KSerializer<T>
    )

    abstract class Listener(private val origin: Origin) {
        fun onUpdate(data: String, fromOrigin: PubSub.Origin) {
            if (origin !== fromOrigin) {
                onUpdate(data)
            }
        }

        abstract fun onUpdate(data: String)
    }

    class TopicInfo(val name: String, var data: String? = null) {
        val listeners: MutableList<Listener> = mutableListOf()

        fun notify(jsonData: String, origin: Origin) {
            listeners.forEach { listener -> listener.onUpdate(jsonData, origin) }
        }
    }

    open class Connection(
        private val name: String,
        private val topics: MutableMap<String, TopicInfo>
    ) : Origin(), Network.TcpListener {
        protected var connection: Network.TcpConnection? = null
        private val toSend: MutableList<ByteArray> = mutableListOf()

        override fun connected(tcpConnection: Network.TcpConnection) {
            logger.debug("[${tcpConnection.fromAddress} -> $name] PubSub: new $this connection")
            connection = tcpConnection
            toSend.forEach { tcpConnection.send(it) }
            toSend.clear()
        }

        override fun receive(tcpConnection: Network.TcpConnection, bytes: ByteArray) {
            val reader = ByteArrayReader(bytes)
            val command = reader.readString()
            when (command) {
                "sub" -> {
                    val topicName = reader.readString()
                    println("[${tcpConnection.fromAddress} -> $name] sub $topicName")

                    val topicInfo = topics.getOrPut(topicName) { TopicInfo(topicName) }
                    val listener = object : Listener(this) {
                        override fun onUpdate(data: String) = sendTopicUpdate(topicName, data)
                    }
                    topicInfo.listeners.add(listener)

                    val topicData = topicInfo.data
                    if (topicData != null) {
                        listener.onUpdate(topicData)
                    }
                }

                "update" -> {
                    val topicName = reader.readString()
                    val topic = Topic(topicName, String.serializer())
                    val data = reader.readString()
                    println("[${tcpConnection.fromAddress} -> $name] update $topicName $data")

                    val topicInfo = topics[topic.name]
                    topicInfo?.notify(data, this)
                }
            }
        }

        fun sendTopicUpdate(name: String, data: String) {
            val writer = ByteArrayWriter()
            println("-> update ${name} ${data} to ${connection?.toAddress}")
            writer.writeString("update")
            writer.writeString(name)
            writer.writeString(data)
            sendCommand(writer.toBytes())
        }

        fun sendTopicSub(topicName: String) {
            val writer = ByteArrayWriter()
            writer.writeString("sub")
            writer.writeString(topicName)
            sendCommand(writer.toBytes())
        }

        override fun reset(tcpConnection: Network.TcpConnection) {
            TODO("PubSub.Connection.reset not implemented")
        }

        internal fun sendCommand(bytes: ByteArray) {
            val tcpConnection = connection
            if (tcpConnection == null) {
                toSend.add(bytes)
            } else {
                tcpConnection.send(bytes)
            }
        }
    }

    class Server(link: Network.Link, port: Int) : Network.TcpServerSocketListener {
        private val topics: MutableMap<String, TopicInfo> = hashMapOf()

        init {
            link.listenTcp(port, this)
        }

        override fun incomingConnection(fromAddress: Network.TcpConnection): Network.TcpListener {
            return Connection("server", topics)
        }

        fun <T : Any> publish(topic: Topic<T>, data: T, onUpdate: (T) -> Unit): Observer<T> {
            val publisher = Origin()
            val topicName = topic.name
            val jsonData = Json.stringify(topic.serializer, data)
            val topicInfo = topics.getOrPut(topicName) { TopicInfo(topicName) }
            topicInfo.data = jsonData
            topicInfo.listeners.add(object : Listener(publisher) {
                override fun onUpdate(data: String) = onUpdate(Json.parse(topic.serializer, data))
            })
            topicInfo.notify(jsonData, publisher)

            return object : Observer<T> {
                override fun onChange(t: T) {
                    topicInfo.notify(Json.stringify(topic.serializer, t), publisher)
                }
            }
        }
    }

    class Client(link: Network.Link, serverAddress: Network.Address, port: Int) {
        private val topics: MutableMap<String, TopicInfo> = hashMapOf()
        private var server: Connection = Connection("client at ${link.myAddress}", topics)

        init {
            link.connectTcp(serverAddress, port, server)
        }

        fun <T> subscribe(topic: Topic<T>, onUpdate: (T) -> Unit): Observer<T> {
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
                override fun onUpdate(data: String) = onUpdate(Json.parse(topic.serializer, data))
            }
            topicInfo.listeners.add(listener)
            val data = topicInfo.data
            if (data != null) {
                listener.onUpdate(data)
            }

            return object : Observer<T> {
                override fun onChange(t: T) {
                    val jsonData = Json.stringify(topic.serializer, t)
                    topicInfo.notify(jsonData, subscriber)
                }
            }
        }
    }
}