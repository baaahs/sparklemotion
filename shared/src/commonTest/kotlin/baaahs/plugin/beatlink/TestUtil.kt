package baaahs.plugin.beatlink

class FakeBeatSource : BeatSource {
    private val listeners = BeatLinkListeners()

    override fun addListener(listener: BeatLinkListener) = listeners.addListener(listener)
    override fun removeListener(listener: BeatLinkListener) = listeners.removeListener(listener)

    fun setBeatData(beatData: BeatData) {
        listeners.notifyListeners { it.onBeatData(beatData) }
    }
}