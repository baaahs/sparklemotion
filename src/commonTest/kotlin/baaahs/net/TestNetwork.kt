package baaahs.net

import baaahs.sim.FakeNetwork

class TestNetwork(var defaultMtu: Int = 1400) : Network {
    val links = mutableListOf<Link>()
    private var services = mutableMapOf<String, Link.TestMdns.TestMdnsService>()
    private var listeners = mutableMapOf<String, MutableList<Network.MdnsListenHandler>>()
    private var nextServiceId : Int = 0

    override fun link(): Link {
        return Link(defaultMtu).also { links.add(it) }
    }

    inner class Link(mtu: Int) : Network.Link {
        override val myAddress = Address()
        override val myHostname: String get() = "TestHost"
        val packetsToSend = mutableListOf<ByteArray>()
        val receviedPackets = mutableListOf<ByteArray>()

        private var udpListener: Network.UdpListener? = null

        fun sendTo(link: Link) {
            packetsToSend.forEach { bytes ->
                link.receiveUdp(bytes)
            }
            packetsToSend.clear()
        }

        private fun receiveUdp(bytes: ByteArray) {
            receviedPackets += bytes
            udpListener?.receive(myAddress, 1234, bytes)
        }

        override val udpMtu = mtu

        override fun listenUdp(port: Int, udpListener: Network.UdpListener): Network.UdpSocket {
            this.udpListener = udpListener
            return TestUdpSocket(port)
        }

        override fun mdns(): Network.Mdns {
            return mdns
        }

        inner class TestUdpSocket(override val serverPort: Int) : Network.UdpSocket {
            override fun sendUdp(toAddress: Network.Address, port: Int, bytes: ByteArray) {
                packetsToSend += bytes
            }

            override fun broadcastUdp(port: Int, bytes: ByteArray) {
                packetsToSend += bytes
            }

        }

        override fun startHttpServer(port: Int): Network.HttpServer = object : Network.HttpServer {
            override fun listenWebSocket(
                path: String,
                onConnect: (incomingConnection: Network.TcpConnection) -> Network.WebSocketListener
            ) {
//                TODO("TestNetwork.Link.listenWebSocket not implemented")
            }
        }

        override fun connectWebSocket(
            toAddress: Network.Address,
            port: Int,
            path: String,
            webSocketListener: Network.WebSocketListener
        ): Network.TcpConnection {
            TODO("Link.connectWebSocket not implemented")
        }

        private val mdns = TestMdns()

        inner class TestMdns() : Network.Mdns {

            override fun register(
                hostname: String,
                type: String,
                proto: String,
                port: Int,
                domain: String,
                params: MutableMap<String, String>
            ): Network.MdnsRegisteredService? {
                val fullname = "$hostname.$type.$proto.${domain.normalizeMdnsDomain()}"
                val inst = TestRegisteredService(hostname, type, proto, port, domain.normalizeMdnsDomain(), params)
                services[fullname] = inst
                (inst as? TestRegisteredService)?.announceResolved()
                return inst
            }

            override fun unregister(inst: Network.MdnsRegisteredService?) { inst?.unregister() }

            override fun listen(type: String, proto: String, domain: String, handler: Network.MdnsListenHandler) {
                listeners.getOrPut("$type.$proto.${domain.normalizeMdnsDomain()}") { mutableListOf() }.add(handler)
            }

            open inner class TestMdnsService(override val hostname: String, override val type: String, override val proto: String, override val port: Int, override val domain: String, val params: MutableMap<String, String>) : Network.MdnsService {
                private var id : Int = nextServiceId++

                override fun getAddress(): Network.Address? = Address("test-svc-$id")

                override fun getTXT(key: String): String? = params[key]

                override fun getAllTXTs(): MutableMap<String, String> = params
            }

            inner class TestRegisteredService(hostname: String, type: String, proto: String, port: Int, domain: String, params: MutableMap<String, String>) : TestMdnsService(hostname, type, proto, port, domain.normalizeMdnsDomain(), params), Network.MdnsRegisteredService {
                override fun unregister() {
                    val fullname = "$hostname.$type.$proto.${domain.normalizeMdnsDomain()}"
                    (services.remove(fullname) as? FakeNetwork.FakeLink.FakeMdns.FakeRegisteredService)?.announceRemoved()
                }

                override fun updateTXT(txt: MutableMap<String, String>) {
                    params.putAll(txt)
                    announceResolved()
                }

                override fun updateTXT(key: String, value: String) {
                    params[key] = value
                    announceResolved()
                }

                internal fun announceResolved() {
                    listeners.getOrPut("$type.$proto.${domain.normalizeMdnsDomain()}") { mutableListOf() }.forEach { it.resolved(this) }
                }

                internal fun announceRemoved() {
                    listeners.getOrPut("$type.$proto.${domain.normalizeMdnsDomain()}") { mutableListOf() }.forEach { it.removed(this) }
                }
            }
        }
    }

    class Address(private val name: String = "some address") : Network.Address {
        override fun toString(): String =  name
    }
}