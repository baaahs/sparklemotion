package baaahs.plugin.beatlink

import baaahs.ShowPlayer
import baaahs.gl.GlContext
import baaahs.gl.data.EngineFeedContext
import baaahs.gl.data.FeedContext
import baaahs.gl.data.ProgramFeedContext
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.gl.shader.InputPort
import baaahs.glsl.Uniform
import baaahs.plugin.objectSerializer
import baaahs.show.Feed
import baaahs.show.FeedBuilder
import baaahs.util.Clock
import baaahs.util.RefCounted
import baaahs.util.RefCounter
import com.danielgergely.kgl.ByteBuffer
import com.danielgergely.kgl.GL_LINEAR
import com.danielgergely.kgl.GL_RGBA
import com.danielgergely.kgl.GL_UNSIGNED_BYTE
import kotlinx.serialization.SerialName

@SerialName("baaahs.BeatLink:Waveforms")
class WaveformsFeed internal constructor(
    private val facade: BeatLinkPlugin.BeatLinkFacade,
    private val clock: Clock
) : Feed {
    override val pluginPackage: String get() = BeatLinkPlugin.id
    override val title: String get() = "Waveforms"
    override fun getType(): GlslType = struct
    override val contentType: ContentType
        get() = WaveformsFeed.contentType

    override fun open(showPlayer: ShowPlayer, id: String): FeedContext {
        val varPrefix = getVarName(id)

        class PlayerThing(gl: GlContext) {
            val textureUnit = gl.getTextureUnit(id)
            val texture = gl.check { createTexture() }
            var currentWaveform: Waveform? = null
            var uniform: Uniform? = null

            fun setFrom(waveform: Waveform?) {
                if (currentWaveform != waveform) {
                    currentWaveform = waveform

                    if (waveform != null) {
                        val sampleCount = waveform.sampleCount

                        val bytes = ByteBuffer(sampleCount * 4)
                        for (i in 0 until sampleCount) {
                            val height = waveform.heightAt(i) * 8
                            val color = waveform.colorAt(i)

                            bytes[i * 4 + 0] = height.toByte()
                            bytes[i * 4 + 1] = color.redB
                            bytes[i * 4 + 2] = color.greenB
                            bytes[i * 4 + 3] = color.blueB
                        }

                        with(textureUnit) {
                            bindTexture(texture)
                            configure(GL_LINEAR, GL_LINEAR)

                            uploadTexture(
                                0, GL_RGBA, sampleCount, 1, 0,
                                GL_RGBA, GL_UNSIGNED_BYTE, bytes
                            )
                        }
                        uniform?.set(textureUnit)
                    }
                }
            }
        }

        return object : FeedContext, RefCounted by RefCounter() {
            override fun bind(gl: GlContext): EngineFeedContext = object : EngineFeedContext {
                private val playerThing1 = PlayerThing(gl)
                private val playerThing2 = PlayerThing(gl)
                private val playerThing3 = PlayerThing(gl)
                private val playerThing4 = PlayerThing(gl)

                override fun bind(glslProgram: GlslProgram): ProgramFeedContext {
                    playerThing1.uniform = glslProgram.getUniform("${varPrefix}.player1Waveform")
                    playerThing2.uniform = glslProgram.getUniform("${varPrefix}.player2Waveform")
                    playerThing3.uniform = glslProgram.getUniform("${varPrefix}.player3Waveform")
                    playerThing4.uniform = glslProgram.getUniform("${varPrefix}.player4Waveform")

                    return object : ProgramFeedContext {
                        override val isValid: Boolean
                            get() = playerThing1.uniform != null ||
                                    playerThing2.uniform != null ||
                                    playerThing3.uniform != null ||
                                    playerThing4.uniform != null

                        override fun setOnProgram() {
                            playerThing1.setFrom(facade.playerWaveforms.byDeviceNumber[1])
                            playerThing2.setFrom(facade.playerWaveforms.byDeviceNumber[2])
                            playerThing3.setFrom(facade.playerWaveforms.byDeviceNumber[3])
                            playerThing4.setFrom(facade.playerWaveforms.byDeviceNumber[4])
                        }
                    }
                }
            }
        }
    }

    companion object {
        val struct = GlslType.Struct(
            "Waveforms",
            "player1Waveform" to GlslType.Sampler2D,
            "player2Waveform" to GlslType.Sampler2D,
            "player3Waveform" to GlslType.Sampler2D,
            "player4Waveform" to GlslType.Sampler2D
        )

        val contentType = ContentType("beatlink-waveforms", "Waveforms", struct)
    }

    inner class Builder : FeedBuilder<WaveformsFeed> {
        override val title: String get() = "Waveforms"
        override val description: String get() = "A struct containing low-level information about the beat."
        override val resourceName: String get() = "Waveforms"
        override val contentType: ContentType get() = WaveformsFeed.contentType
        override val serializerRegistrar
            get() = objectSerializer("${BeatLinkPlugin.id}:Waveforms", this@WaveformsFeed)
        override val internalOnly: Boolean
            get() = true

        override fun looksValid(inputPort: InputPort, suggestedContentTypes: Set<ContentType>): Boolean =
            inputPort.contentType == contentType
                    || suggestedContentTypes.contains(contentType)
                    || inputPort.type == struct

        override fun build(inputPort: InputPort): WaveformsFeed = this@WaveformsFeed
    }
}