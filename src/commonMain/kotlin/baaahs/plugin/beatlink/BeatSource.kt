package baaahs.plugin.beatlink

interface BeatSource {
    fun addListener(listener: BeatLinkListener)
    fun removeListener(listener: BeatLinkListener)

    object None : BeatSource {
        val none = BeatData.unknown

        override fun addListener(listener: BeatLinkListener) {}
        override fun removeListener(listener: BeatLinkListener) {}
    }
}