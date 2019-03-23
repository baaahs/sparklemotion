package baaahs

expect fun getDisplay(): Display

interface Display {
    fun forNetwork(): NetworkDisplay
    fun forPinky(): PinkyDisplay
    fun forBrain(): BrainDisplay
    fun forMapper(): MapperDisplay
}

interface NetworkDisplay {
    var packetLossRate: Float
    fun receivedPacket()
    fun droppedPacket()
}

interface PinkyDisplay {
    fun listShows(showMetas: List<ShowMeta>)

    var brainCount: Int
    var beat: Int
    var color: Color?
    var selectedShow: ShowMeta?
}

interface BrainDisplay {
    fun haveLink(link: Network.Link)
}

interface MapperDisplay {
    var onStart: (() -> Unit)?
    var onStop: (() -> Unit)?
}
