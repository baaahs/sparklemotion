package baaahs.plugin.beatlink

import baaahs.Color
import baaahs.util.Time
import kotlinx.serialization.Serializable

@Serializable
data class Waveform(
    private val encoded: String,
    val trackStartTime: Time
) {
    val sampleCount get() = encoded.length / 8

    /**
     * The total time of the waveform in milliseconds.
     *
     * See WaveformDetail.getTotalTime() in BeatLink.
     */
    val totalTimeMs get() = sampleCount.asTotalTimeMs()

    fun heightAt(index: Int): Int {
        val hex = encoded.substring(index * 8, index * 8 + 2)
        return hex.toInt(16)
    }

    fun colorAt(index: Int): Color {
        val hex = encoded.substring(index * 8 + 2, index * 8 + 8)
        return Color(hex.toInt(16) or 0xff000000.toInt())
    }

    class Builder(val trackStartTime: Time) {
        private val encoded = StringBuilder()

        fun add(height: Int, color: Color) {
            encoded.append(height.toHexString().substring(6))
            encoded.append(color.rgb.toHexString().substring(2))
        }

        fun build(): Waveform = Waveform(encoded.toString(), trackStartTime)
    }

    companion object {
        /** Per [org.deepsymmetry.beatlink.Util#halfFrameToTime], there are 150 samples per second. */
        fun Int.asTotalTimeMs() = this * 100 / 15f
    }
}

@Serializable
data class PlayerWaveforms(
    val byDeviceNumber: Map<Int, Waveform> = emptyMap()
) {
    fun updateWith(deviceNumber: Int, waveform: Waveform): PlayerWaveforms {
        return copy(byDeviceNumber = byDeviceNumber + (deviceNumber to waveform))
    }
}