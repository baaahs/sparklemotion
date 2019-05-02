package baaahs

import baaahs.net.Network

expect fun getDisplay(): Display

interface Display {
    fun forNetwork(): NetworkDisplay
    fun forPinky(): PinkyDisplay
    fun forBrain(): BrainDisplay
}

interface NetworkDisplay {
    var packetLossRate: Float
    fun receivedPacket()
    fun droppedPacket()
}

interface PinkyDisplay {
    fun listShows(showMetas: List<Show.MetaData>)

    var brainCount: Int
    var beat: Int
    var color: Color?
    var onPrimaryColorChange: (() -> Unit)?
    var selectedShow: Show.MetaData?
}

interface BrainDisplay {
    fun haveLink(link: Network.Link)
}
