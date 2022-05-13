package baaahs.plugin.beatlink

import baaahs.ui.Observable

class FakeBeatSource(
    beatData: BeatData = BeatData(0.0, 0, confidence = 0f)
) : Observable(), BeatSource {
    private var fakeData = beatData

    override fun getBeatData(): BeatData = fakeData

    fun setBeatData(beatData: BeatData) {
        fakeData = beatData
        notifyChanged()
    }
}