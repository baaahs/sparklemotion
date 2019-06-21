package baaahs

import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter
import baaahs.net.JvmNetwork
import baaahs.net.Network
import baaahs.proto.Ports
import io.ktor.application.install
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.InetAddress
import java.nio.file.Paths
import java.time.Duration

object SimBridge {
    fun main() {
        val classesDir = Paths.get(Pinky::class.java.getResource(".").file)
        val jsResDir = classesDir.parent.parent.parent.parent.parent.parent
            .resolve("build/processedResources/js/main")
        println("jsResDir = ${jsResDir}")
        val httpServer = embeddedServer(Netty, Ports.PINKY_UI_TCP) {
            routing {
                application.install(io.ktor.websocket.WebSockets) {
                    pingPeriod = Duration.ofSeconds(15)
                    timeout = Duration.ofSeconds(15)
                    maxFrameSize = Long.MAX_VALUE
                    masking = false
                }
            }
        }

        GlobalScope.launch {
            val udpProxy = UdpProxy(httpServer)
        }

        httpServer.start(true)
    }

    class UdpProxy(httpServer: NettyApplicationEngine) {
        private val network = JvmNetwork(httpServer)
        private val link = network.link()
        private var tcpConnection: Network.TcpConnection? = null

        init {
            link.listenUdp(Ports.PINKY, object : Network.UdpListener {
                override fun receive(fromAddress: Network.Address, bytes: ByteArray) {
                    tcpConnection?.let {
                        val writer = ByteArrayWriter()
                        writer.writeString("UDP")
                        writer.writeBytes((fromAddress as JvmNetwork.IpAddress).address.address)
                        writer.writeBytes(bytes)
                        it.send(writer.toBytes())
                    }
                }
            })

            link.listenTcp(Ports.SIM_BRIDGE, object : Network.TcpServerSocketListener {
                override fun incomingConnection(fromConnection: Network.TcpConnection): Network.TcpListener {
                    return object : Network.TcpListener {
                        override fun connected(tcpConnection: Network.TcpConnection) {
                        }

                        override fun receive(tcpConnection: Network.TcpConnection, bytes: ByteArray) {
                            val reader = ByteArrayReader(bytes)
                            val command = reader.readString()
                            if (command == "UDP") {
                                val address = JvmNetwork.IpAddress(InetAddress.getByAddress(reader.readBytes()))
                                val payload = reader.readBytes()
                                link.sendUdp(address, Ports.BRAIN, payload)
                            }
                        }

                        override fun reset(tcpConnection: Network.TcpConnection) {
                        }
                    }
                }
            })
        }
    }
}

fun main(args: Array<String>) {
    SimBridge.main()
}