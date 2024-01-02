package baaahs.sim

import baaahs.net.Network

public class FakeMdns : Network.Mdns {
    private var services = mutableMapOf<String, FakeMdns.FakeMdnsService>()
    private var listeners = mutableMapOf<String, MutableList<Network.MdnsListenHandler>>()
    private var nextServiceId : Int = 0

    override fun register(
        hostname: String,
        type: String,
        proto: String,
        port: Int,
        domain: String,
        params: Map<String, String>
    ): Network.MdnsRegisteredService {
        val fullname = "$hostname.$type.$proto.${domain.normalizeMdnsDomain()}"
        val inst = FakeRegisteredService(hostname, type, proto, port, domain.normalizeMdnsDomain(), params)
        services[fullname] = inst
        inst.announceResolved()
        return inst
    }

    override fun unregister(inst: Network.MdnsRegisteredService) { inst.unregister() }

    override fun listen(type: String, proto: String, domain: String, handler: Network.MdnsListenHandler) {
        listeners.getOrPut("$type.$proto.${domain.normalizeMdnsDomain()}") { mutableListOf() }.add(handler)
    }

    public open inner class FakeMdnsService(
        override val hostname: String,
        override val type: String,
        override val proto: String,
        override val port: Int,
        override val domain: String,
        params: Map<String, String>
    ) : Network.MdnsService {
        private var id : Int = nextServiceId++
        protected val params: MutableMap<String, String> = params.toMutableMap()

        override fun getAddress(): Network.Address? = FakeNetwork.FakeAddress(hostname)

        override fun getTXT(key: String): String? = params[key]

        override fun getAllTXTs(): Map<String, String> = params
    }

    public inner class FakeRegisteredService(
        hostname: String,
        type: String,
        proto: String,
        port: Int,
        domain: String,
        params: Map<String, String>
    ) : FakeMdnsService(hostname, type, proto, port, domain.normalizeMdnsDomain(), params),
        Network.MdnsRegisteredService {
        override fun unregister() {
            val fullname = "$hostname.$type.$proto.${domain.normalizeMdnsDomain()}"
            (services.remove(fullname) as? FakeRegisteredService)?.announceRemoved()
        }

        override fun updateTXT(txt: Map<String, String>) {
            params.putAll(txt)
            announceResolved()
        }

        override fun updateTXT(key: String, value: String) {
            params[key] = value
            announceResolved()
        }

        internal fun announceResolved() {
            listeners["$type.$proto.${domain.normalizeMdnsDomain()}"]?.forEach { it.resolved(this) }
        }

        internal fun announceRemoved() {
            listeners["$type.$proto.${domain.normalizeMdnsDomain()}"]?.forEach { it.removed(this) }
        }
    }

}