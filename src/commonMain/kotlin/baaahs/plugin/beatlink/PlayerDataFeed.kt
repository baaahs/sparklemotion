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
import baaahs.glsl.FloatUniform
import baaahs.plugin.beatlink.BeatLinkPlugin.Companion.PLAYER_COUNT
import baaahs.plugin.beatlink.PlayerState.Companion.asTotalTimeMs
import baaahs.plugin.objectSerializer
import baaahs.show.Feed
import baaahs.show.FeedBuilder
import baaahs.show.FeedOpenContext
import baaahs.util.Clock
import baaahs.util.RefCounted
import baaahs.util.RefCounter
import kotlinx.serialization.SerialName

@SerialName("baaahs.BeatLink:PlayerData")
class PlayerDataFeed internal constructor(
    private val facade: BeatLinkPlugin.BeatLinkFacade,
    private val clock: Clock
) : Feed {
    override val pluginPackage: String get() = BeatLinkPlugin.id
    override val title: String get() = "Player Data"
    override fun getType(): GlslType = contentType.glslType
    override val contentType: ContentType
        get() = PlayerDataFeed.contentType

    override fun open(feedOpenContext: FeedOpenContext, id: String): FeedContext {
        val varPrefix = getVarName(id)

        class PlayerState(val playerNumber: Int) {
            var currentPlayerState: baaahs.plugin.beatlink.PlayerState? = null
            var trackStartTimeUniform: FloatUniform? = null
            var trackLengthUniform: FloatUniform? = null

            val isBound: Boolean get() =
                trackStartTimeUniform != null || trackLengthUniform != null

            fun bind(glslProgram: GlslProgram) {
                trackStartTimeUniform = glslProgram.getFloatUniform("${varPrefix}.players[$playerNumber].trackStartTime")
                trackLengthUniform = glslProgram.getFloatUniform("${varPrefix}.players[$playerNumber].trackLength")
            }

            fun setFrom(playerState: baaahs.plugin.beatlink.PlayerState?) {
                if (currentPlayerState != playerState) {
                    currentPlayerState = playerState

                    if (playerState != null) {
                        val sampleCount = playerState.sampleCount

                        trackStartTimeUniform?.set(playerState.trackStartTime.toFloat())
                        trackLengthUniform?.set(sampleCount.asTotalTimeMs())
                    }
                }
            }
        }

        return object : FeedContext, RefCounted by RefCounter() {
            override fun bind(gl: GlContext): EngineFeedContext = object : EngineFeedContext {
                private val playerStates = (0 until PLAYER_COUNT).map {
                    PlayerState(it + 1)
                }

                override fun bind(glslProgram: GlslProgram): ProgramFeedContext {
                    playerStates.forEach { it.bind(glslProgram) }

                    return object : ProgramFeedContext {
                        override val isValid: Boolean
                            get() = playerStates.any { it.isBound }

                        override fun setOnProgram() {
                            playerStates.forEach {
                                it.setFrom(facade.playerStates.byDeviceNumber[it.playerNumber])
                            }
                        }
                    }
                }
            }
        }
    }

    companion object {
        /**
         * Ideally the player state would include `sampler2D waveform`, but webgl
         * obnoxiously forbids samplers in structs.
         *
         * Instead, we get the waveform data in via [WaveformsFeed].
         */
        val playerStateStruct = GlslType.Struct(
            "PlayerState",
            "trackStartTime" to GlslType.Float,
            "trackLength" to GlslType.Float
        )

        val playerDataStruct = GlslType.Struct(
            "PlayerData",
            "players" to playerStateStruct.arrayOf(PLAYER_COUNT)
        )

        val contentType = ContentType(
            "beatlink-player-data",
            "Player Data",
            playerDataStruct
        )

        init {
            println("PlayerData: ${playerDataStruct.toGlsl(null, emptySet())}")
            println("PlayerState: ${playerStateStruct.toGlsl(null, emptySet())}")
        }
    }

    inner class Builder : FeedBuilder<PlayerDataFeed> {
        override val title: String get() = "Player Data"
        override val description: String get() = "Information about the current state of CDJ players."
        override val resourceName: String get() = "PlayerData"
        override val contentType: ContentType get() = PlayerDataFeed.contentType
        override val serializerRegistrar
            get() = objectSerializer("${BeatLinkPlugin.id}:$resourceName", this@PlayerDataFeed)
        override val internalOnly: Boolean
            get() = true

        override fun looksValid(inputPort: InputPort, suggestedContentTypes: Set<ContentType>): Boolean =
            inputPort.contentType == contentType
                    || suggestedContentTypes.contains(contentType)
                    || inputPort.type == contentType.glslType

        override fun build(inputPort: InputPort): PlayerDataFeed = this@PlayerDataFeed
    }
}