package baaahs.plugin.beatlink

import org.deepsymmetry.beatlink.data.TrackPositionListener
import org.deepsymmetry.beatlink.data.TrackPositionUpdate

interface TimeFinder {
    val isRunning: Boolean
    fun start()
    fun addTrackPositionListener(player: Int, listener: TrackPositionListener)
    fun removeTrackPositionListener(listener: TrackPositionListener?)
    fun getLatestPositionFor(deviceNumber: Int): TrackPositionUpdate?

    companion object {
        fun getInstance(): TimeFinder = object : TimeFinder {
            val delegate = org.deepsymmetry.beatlink.data.TimeFinder.getInstance()
            override val isRunning: Boolean
                get() = delegate.isRunning

            override fun start() =
                delegate.start()

            override fun addTrackPositionListener(player: Int, listener: TrackPositionListener) =
                delegate.addTrackPositionListener(player, listener)

            override fun removeTrackPositionListener(listener: TrackPositionListener?) =
                delegate.removeTrackPositionListener(listener)

            override fun getLatestPositionFor(deviceNumber: Int): TrackPositionUpdate? =
                delegate.getLatestPositionFor(deviceNumber)
        }
    }
}