package baaahs

interface Dmx {
    val allocated: MutableMap<String, List<DmxAddress>>

    fun allocate(name: String, addressCount: Int): List<DmxAddress> {
        val dmxAddresses = (0 until addressCount).map { allocate() }
        allocated[name] = dmxAddresses
        return dmxAddresses
    }

    fun get(name: String) = allocated[name]!!

    fun allocate(): DmxAddress

    interface DmxAddress
}

class MovingHeadBuffer(private val byteArray: ByteArray,
                       var colorIllicitDontUse: Color,
                       var rotAIllicitDontUse: Float,
                       var rotBIllicitDontUse: Float)

class FakeDmx : Dmx {
    override val allocated: MutableMap<String, List<Dmx.DmxAddress>> = hashMapOf()

    override fun allocate(): Dmx.DmxAddress = FakeDmxAddress()

    class FakeDmxAddress : Dmx.DmxAddress {

    }

}