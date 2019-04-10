package baaahs

expect fun getDisplay(): Display

interface Display {
    fun forNetwork(): NetworkDisplay
    fun forPinky(): PinkyDisplay
    fun forBrain(): BrainDisplay
    fun forMapper(): MapperDisplay
    fun forUi(): UiDisplay
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
    var onPrimaryColorChange: (() -> Unit)?
    var selectedShow: ShowMeta?
}

interface BrainDisplay {
    fun haveLink(link: Network.Link)
}

interface MapperDisplay {
    var onStart: (() -> Unit)?
    var onStop: (() -> Unit)?
}

interface UiDisplay {
    var color: Color?
    var onColorChanged: ((Color) -> Unit)?
}