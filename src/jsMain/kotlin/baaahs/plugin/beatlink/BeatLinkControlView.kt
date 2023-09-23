package baaahs.plugin.beatlink

import baaahs.app.ui.appContext
import baaahs.app.ui.shaderPreview
import baaahs.futureAsync
import baaahs.gl.GlContext
import baaahs.gl.glsl.GlslProgram
import baaahs.imaging.Dimen
import baaahs.io.getResourceAsync
import baaahs.onAvailable
import baaahs.show.Shader
import baaahs.show.live.ControlProps
import baaahs.ui.*
import baaahs.util.*
import js.core.jso
import kotlinx.css.*
import kotlinx.css.properties.s
import mui.material.Card
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.useContext
import styled.StyleSheet
import styled.inlineStyles
import web.dom.Element
import web.html.HTMLElement
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.milliseconds

private val beatVisualizerShaderLoader = futureAsync {
    Shader("Beat Visualizer", getResourceAsync("baaahs/plugin/beatlink/BeatLinkControl.glsl"))
}

private val waveformDetailShaderLoader = futureAsync {
    Shader("Waveform Detail Visualizer", getResourceAsync("baaahs/plugin/beatlink/WaveformDetail.glsl"))
}

private val waveformOverviewShaderLoader = futureAsync {
    Shader("Waveform Overview Visualizer", getResourceAsync("baaahs/plugin/beatlink/WaveformOverview.glsl"))
}

fun PlayerState.remainingTime(clock: Clock): String? =
    trackEndTime?.let { endTime ->
        val remainingTime = endTime - clock.now()
        if (remainingTime >= 0)
            remainingTime.toHHMMSS()
        else
            null
    }

fun HTMLElement.setInnerText(text: String?) {
    if (text != null) {
        if (innerText != text)
            innerText = text
    } else {
        if (innerText.isNotEmpty())
            innerText = ""
    }
}

private val beatLinkControl = xComponent<BeatLinkControlProps>("BeatLinkControl") { _ ->
    val appContext = useContext(appContext)
    val beatLink = appContext.plugins.getPlugin<BeatLinkPlugin>().facade

    var playerStates by state<PlayerStates?> { beatLink.playerStates }
    val playerStateViews = memo {
        mutableMapOf<Int, PlayerStateView>().also {
            withCleanup { it.values.forEach { it.release() } }
        }
    }

    val allPlayers = listOf(1, 2, 3, 4, 5, 6).associateWith { playerNumber ->
        beatLink.playerStates.byDeviceNumber[playerNumber]
    }
    val activePlayerCount = allPlayers.values.filterNotNull().count()

    val bpmDiv = ref<HTMLElement>()
    val confidenceDiv = ref<HTMLElement>()
    val trackIdsRefs = allPlayers.map { ref<HTMLElement>() }
    val timeRemainingRefs = allPlayers.map { ref<HTMLElement>() }

    fun update(beatData: BeatData) {
        val bpm = beatData.bpm
        bpmDiv.current?.setInnerText("${bpm.roundToInt()} BPM")

        val beatConfidence = beatData.confidence
        confidenceDiv.current?.setInnerText("Confidence: ${beatConfidence.percent()}")

        playerStates = beatLink.playerStates
        beatLink.playerStates.byDeviceNumber.forEach { (playerNumber, playerState) ->
            val texture = playerStateViews[playerNumber]
            texture?.playerState = playerState

            trackIdsRefs[playerNumber].current?.setInnerText(
                listOfNotNull(playerState.trackTitle, playerState.trackArtist).joinToString(" – ")
            )

            timeRemainingRefs[playerNumber].current?.setInnerText(
                playerState.remainingTime(appContext.clock)
            )
        }
    }

    onMount {
        val observer = beatLink.addObserver(fireImmediately = true) { beatLink ->
            val beatData = beatLink.beatData
            update(beatData)
        }
        withCleanup { observer.remove() }
    }

    var layoutDimens by state { Dimen(0, 0) }
    val containerRef = ref<Element>()
    useResizeListener(containerRef) { width, height ->
        layoutDimens = Dimen(width, height)
    }

    // Refresh remaining time every 100ms.
    useInterval(100.milliseconds) {
        playerStates?.byDeviceNumber?.forEach { (playerNumber, playerState) ->
            playerState.trackEndTime?.let {
                val div = timeRemainingRefs[playerNumber].current
                val remainingTimeStr = playerState.remainingTime(appContext.clock) ?: ""
                div?.setInnerText(remainingTimeStr)
            }
        }
    }

    var beatVisualizerShader by state<Shader?> { null }
    beatVisualizerShaderLoader.onAvailable { beatVisualizerShader = it }
    var waveformDetailShader by state<Shader?> { null }
    waveformDetailShaderLoader.onAvailable { waveformDetailShader = it }
    var waveformOverviewShader by state<Shader?> { null }
    waveformOverviewShaderLoader.onAvailable { waveformOverviewShader = it }

    fun PlayerState?.heightUnits() = if (this == null) 0 else if (isOnAir == true) 2 else 1
    fun PlayerState?.opacity() = if (this == null) 0.0 else if (isOnAir == true) 1.0 else .5
    val playerStateHeightUnits = allPlayers.values.sumOf { it.heightUnits() }

    Card {
        attrs.classes = jso { this.root = -Styles.card }
        div(+Styles.card) {
            ref = containerRef

            val beatVisHeight =
                if (activePlayerCount == 0) layoutDimens.height
                else (layoutDimens.height * .20).roundToInt()

            div(+Styles.beatVisualizer) {
                inlineStyles { height = beatVisHeight.px }
                shaderPreview {
                    attrs.shader = beatVisualizerShader
//                    attrs.height = beatVisHeight.px
                    attrs.onRenderCallback = { shaderPreview ->
                        val program = shaderPreview.program
                        program?.getFloatUniform("brightness")?.set(.5f)
                    }
                }

                div(+Styles.bpm) { ref = bpmDiv }
                div(+Styles.confidence) { ref = confidenceDiv }
            }

            var layoutY = beatVisHeight
            allPlayers.forEach { (playerNumber, playerState) ->
                val waveformVisHeight = (layoutDimens.height - beatVisHeight) *
                        playerState.heightUnits() / playerStateHeightUnits

                div(+Styles.waveformVisualizer) {
                    inlineStyles {
                        top = layoutY.px
                        height = waveformVisHeight.px * .75
                        opacity = playerState.opacity()
                    }

                    if (playerState != null) {
                        div(+Styles.playerNumber) { +playerNumber.toString() }
                        div(+Styles.trackId) { ref = trackIdsRefs[playerNumber] }

                        shaderPreview {
                            attrs.shader = waveformDetailShader
//                        attrs.height = waveformVisHeight.px
                            attrs.onRenderCallback = { shaderPreview ->
                                shaderPreview.program?.let { program ->
                                    val playerStateView = playerStateViews.getOrPut(playerNumber) {
                                        PlayerStateView(
                                            shaderPreview.renderEngine.gl, program, playerState, appContext.clock)
                                    }

                                    playerStateView.setOnProgram()
                                }
                            }
                        }
                    }
                }

                div(+Styles.waveformVisualizer) {
                    inlineStyles {
                        top = layoutY.px + (waveformVisHeight * .75).px
                        height = waveformVisHeight.px * .25
                        opacity = playerState.opacity()
                    }

                    if (playerState != null) {
                        div(+Styles.timeRemaining) { ref = timeRemainingRefs[playerNumber] }

                        shaderPreview {
                            attrs.shader = waveformOverviewShader
//                        attrs.height = waveformVisHeight.px
                            attrs.onRenderCallback = { shaderPreview ->
                                shaderPreview.program?.let { program ->
                                    val playerStateView = PlayerStateView(
                                        shaderPreview.renderEngine.gl, program, playerState, appContext.clock)

                                    playerStateView.setOnProgram()
                                }
                            }
                        }
                    }
                }

                layoutY += waveformVisHeight
            }
        }
    }
}

object Styles : StyleSheet("plugin-Beatlink", isStatic = true) {
    val card by css {
        display = Display.flex
        flex = Flex(1.0, 0.0)

        // Needed because of [SharedGlContext]. TODO: remove that requirement.
        important(::backgroundColor, Color.transparent)
        userSelect = UserSelect.none
    }

    val beatVisualizer by css {
        position = Position.absolute
        top = 0.px
        left = 0.px
        right = 0.px
        height = 100.pct
        transition(duration = .5.s)
    }

    val waveformVisualizer by css {
        position = Position.absolute
        left = 0.px
        right = 0.px
        height = 0.pct
        transition(duration = .5.s)
    }

    private val overlayText by css {
        position = Position.absolute
        color = Color.white
        put("textShadow", "0px 1px 1px black")
    }

    val playerNumber by css(overlayText) {
        bottom = 0.px
        left = 0.px
        fontWeight = FontWeight.bold
    }

    val timeRemaining by css(overlayText) {
        bottom = 0.px
        right = 0.px
    }

    val trackId by css(overlayText) {
        bottom = 0.px
        right = 0.px
        fontSize = .6.em
        fontFamily = "'Press Start 2P', sans-serif"
    }

    val div by css {
        position = Position.relative
    }

    val bpm by css(overlayText) {
        bottom = 0.px
        left = 0.px
    }

    val confidence by css(overlayText) {
        bottom = 0.px
        right = 0.px
    }
}

private class PlayerStateView(
    private val gl: GlContext,
    program: GlslProgram,
    playerState: PlayerState,
    private val clock: Clock
) {
    val trackElapsedTimeUniform = program.getFloatUniform("trackElapsedTime")
    val trackLengthUniform = program.getFloatUniform("trackLength")
    val waveformUniform = program.getTextureUniform("waveform")
    val texture = with(gl) { check { createTexture() } }

    var playerState: PlayerState = playerState
        set(value) {
            if (field != value) {
                field = value
                playerState.waveform?.updateTexture(gl, texture)
            }
        }

    init {
        playerState.waveform?.updateTexture(gl, texture)
    }

    fun setOnProgram() {
        val trackElapsedTime = playerState.trackStartTime?.let { clock.now() - it }
        trackElapsedTimeUniform?.set(trackElapsedTime?.toFloat() ?: 0f)
        trackLengthUniform?.set(playerState.waveform?.totalTime ?: 0f)
        waveformUniform?.set(texture)
    }

    fun release() {
        gl.check { deleteTexture(texture) }
    }
}

external interface BeatLinkControlProps : Props {
    var controlProps: ControlProps
    var beatLinkControl: OpenBeatLinkControl
}

fun RBuilder.beatLinkControl(handler: RHandler<BeatLinkControlProps>) =
    child(beatLinkControl, handler = handler)