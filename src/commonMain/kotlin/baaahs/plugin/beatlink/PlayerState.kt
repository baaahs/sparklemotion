package baaahs.plugin.beatlink

import baaahs.Color
import baaahs.gl.GlContext
import baaahs.util.Time
import com.danielgergely.kgl.*
import kotlinx.serialization.Serializable
import kotlin.math.min
import kotlin.math.roundToInt

@Serializable
data class PlayerState(
    /** Each sample is 8 hex bytes: HHRRGGBB where 'H' is height. */
    private val encodedWaveform: String,
    val trackStartTime: Time?
) {
    val sampleCount get() = encodedWaveform.length / 8

    /**
     * The total time of the waveform in milliseconds.
     *
     * See WaveformDetail.getTotalTime() in BeatLink.
     */
    val totalTimeMs get() = sampleCount.asTotalTimeMs()

    fun heightAt(index: Int): Int {
        val hex = encodedWaveform.substring(index * 8, index * 8 + 2)
        return hex.toInt(16)
    }

    fun colorAt(index: Int): Color {
        val hex = encodedWaveform.substring(index * 8 + 2, index * 8 + 8)
        return Color(hex.toInt(16) or 0xff000000.toInt())
    }

    fun updateTexture(gl: GlContext, texture: Texture) {
        val textureWidth = min(sampleCount, gl.maxTextureSize)

        val bytes = ByteBuffer(textureWidth * 4)
        for (i in 0 until textureWidth) {
            val sampleI = i * sampleCount / textureWidth

            val height = heightAt(sampleI) * 8
            val color = colorAt(sampleI)

            bytes[i * 4 + 0] = color.redB
            bytes[i * 4 + 1] = color.greenB
            bytes[i * 4 + 2] = color.blueB
            bytes[i * 4 + 3] = height.toByte()
        }

        with(gl) {
            texture.configure(GL_LINEAR, GL_LINEAR)

            texture.upload(
                0, GL_RGBA, textureWidth, 1, 0,
                GL_RGBA, GL_UNSIGNED_BYTE, bytes
            )
        }
    }

    class Builder(private val trackStartTime: Time?) {
        private val encodedWaveform = StringBuilder()

        fun add(height: Int, color: Color) {
            encodedWaveform.append(height.toHexString().substring(6))
            encodedWaveform.append(color.rgb.toHexString().substring(2))
        }

        fun build(): PlayerState = PlayerState(encodedWaveform.toString(), trackStartTime)
    }

    companion object {
        /** Per [org.deepsymmetry.beatlink.Util#halfFrameToTime], there are 150 samples per second. */
        fun Int.asTotalTimeMs() = this * 100 / 15f
        fun secondsToFrameCount(seconds: Double) = (seconds * 150).roundToInt()
    }
}

@Serializable
data class PlayerStates(
    val byDeviceNumber: Map<Int, PlayerState> = emptyMap()
) {
    fun updateWith(deviceNumber: Int, playerState: PlayerState): PlayerStates {
        return copy(byDeviceNumber = byDeviceNumber + (deviceNumber to playerState))
    }
}