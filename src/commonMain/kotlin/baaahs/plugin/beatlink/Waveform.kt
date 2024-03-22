package baaahs.plugin.beatlink

import baaahs.Color
import baaahs.gl.GlContext
import baaahs.plugin.beatlink.PlayerState.Companion.asTotalTimeMs
import com.danielgergely.kgl.*
import kotlin.math.min

data class Waveform(
    internal val encodedWaveform: String,
    internal val scale: Int
) {
    internal val sampleCount get() = encodedWaveform.length / 8
    /**
     * The total time of the waveform in milliseconds.
     *
     * See WaveformDetail.getTotalTime() in BeatLink.
     */
    internal val totalTimeMs get() = sampleCount.asTotalTimeMs() * scale
    val totalTimeInSeconds get() = totalTimeMs / 1000

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

    override fun toString(): String = buildString {
        append("Waveform(")
        append(encodedWaveform.length / 8)
        append(" samples)")
    }

    class Builder(private val waveformScale: Int) {
        internal val encodedWaveform = StringBuilder()

        fun add(height: Int, color: Color) {
            encodedWaveform.append(height.toHexString().substring(6))
            encodedWaveform.append(color.rgb.toHexString().substring(2))
        }

        fun build(): Waveform = Waveform(encodedWaveform.toString(), waveformScale)
    }


}