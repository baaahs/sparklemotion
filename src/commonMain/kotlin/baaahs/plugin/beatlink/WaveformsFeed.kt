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
import baaahs.glsl.TextureUniform
import baaahs.plugin.beatlink.BeatLinkPlugin.Companion.PLAYER_COUNT
import baaahs.plugin.objectSerializer
import baaahs.show.Feed
import baaahs.show.FeedBuilder
import baaahs.show.FeedOpenContext
import baaahs.util.Clock
import baaahs.util.RefCounted
import baaahs.util.RefCounter
import kotlinx.serialization.SerialName

@SerialName("baaahs.BeatLink:Waveforms")
class WaveformsFeed internal constructor(
    private val facade: BeatLinkPlugin.BeatLinkFacade,
    private val clock: Clock
) : Feed {
    override val pluginPackage: String get() = BeatLinkPlugin.id
    override val title: String get() = "Player Waveforms"
    override fun getType(): GlslType = contentType.glslType
    override val contentType: ContentType
        get() = WaveformsFeed.contentType

    override fun open(feedOpenContext: FeedOpenContext, id: String): FeedContext {
        val varPrefix = getVarName(id)

        class PlayerWaveform(gl: GlContext, val playerNumber: Int) {
            val textureUnit = gl.getTexture("${id}_player$playerNumber")
            val texture = gl.check { createTexture() }
            var currentPlayerState: PlayerState? = null
            var textureUniform: TextureUniform? = null

            val isBound: Boolean get() = textureUniform != null

            fun bind(glslProgram: GlslProgram) {
                textureUniform = glslProgram.getTextureUniform("${varPrefix}[${playerNumber - 1}]")
            }

            fun setFrom(playerState: PlayerState?) {
                if (currentPlayerState != playerState) {
                    currentPlayerState = playerState

                    if (playerState != null) {
                        playerState.updateTexture(gl, texture)
                        textureUniform?.set(texture)
                    }
                }
            }

            fun release() {
                gl.check { deleteTexture(texture) }
            }
        }

        return object : FeedContext, RefCounted by RefCounter() {
            override fun bind(gl: GlContext): EngineFeedContext = object : EngineFeedContext {
                private val playerWaveforms = (0 until PLAYER_COUNT).map {
                    PlayerWaveform(gl, it + 1)
                }

                override fun bind(glslProgram: GlslProgram): ProgramFeedContext {
                    playerWaveforms.forEach { it.bind(glslProgram) }

                    return object : ProgramFeedContext {
                        override val isValid: Boolean
                            get() = playerWaveforms.any { it.isBound }

                        override fun setOnProgram() {
                            playerWaveforms.forEach {
                                it.setFrom(facade.playerStates.byDeviceNumber[it.playerNumber])
                            }
                        }
                    }
                }

                override fun release() {
                    playerWaveforms.forEach { it.release() }
                }
            }
        }
    }

    companion object {
        val contentType = ContentType(
            "beatlink-waveforms",
            "Player Waveforms",
            GlslType.Sampler2D.arrayOf(PLAYER_COUNT)
        )
    }

    inner class Builder : FeedBuilder<WaveformsFeed> {
        override val title: String get() = "Player Waveforms"
        override val description: String get() = "An array of waveform textures for CDJ players."
        override val resourceName: String get() = "Waveforms"
        override val contentType: ContentType get() = WaveformsFeed.contentType
        override val serializerRegistrar
            get() = objectSerializer("${BeatLinkPlugin.id}:$resourceName", this@WaveformsFeed)
        override val internalOnly: Boolean
            get() = true

        override fun looksValid(inputPort: InputPort, suggestedContentTypes: Set<ContentType>): Boolean =
            inputPort.contentType == contentType
                    || suggestedContentTypes.contains(contentType)
                    || inputPort.type == contentType.glslType

        override fun build(inputPort: InputPort): WaveformsFeed = this@WaveformsFeed
    }
}