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
import baaahs.util.Clock
import baaahs.util.percent
import baaahs.util.useResizeListener
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

private val beatVisualizerShaderLoader = futureAsync {
    Shader("Beat Visualizer", getResourceAsync("baaahs/plugin/beatlink/BeatLinkControl.glsl"))
}

private val waveformShaderLoader = futureAsync {
    Shader("Waveform Visualizer", getResourceAsync("baaahs/plugin/beatlink/Waveform.glsl"))
}

private val beatLinkControl = xComponent<BeatLinkControlProps>("BeatLinkControl") { _ ->
    val appContext = useContext(appContext)
    val beatLink = appContext.plugins.getPlugin<BeatLinkPlugin>().facade

    val bpmDiv = ref<HTMLElement>()
    val confidenceDiv = ref<HTMLElement>()

    var playerStates by state<PlayerStates?> { beatLink.playerStates }
    val playerStateViews = memo {
        mutableMapOf<Int, PlayerStateView>().also {
            withCleanup { it.values.forEach { it.release() } }
        }
    }

    fun update(beatData: BeatData) {
        val bpm = beatData.bpm
        bpmDiv.current?.innerText = "${bpm.roundToInt()} BPM"

        val beatConfidence = beatData.confidence
        confidenceDiv.current?.innerText = "Confidence: ${beatConfidence.percent()}"

        playerStates = beatLink.playerStates
        beatLink.playerStates.byDeviceNumber.forEach { (playerNumber, playerState) ->
            val texture = playerStateViews[playerNumber]
            texture?.playerState = playerState
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

    var beatVisualizerShader by state<Shader?> { null }
    beatVisualizerShaderLoader.onAvailable { beatVisualizerShader = it }
    var waveformShader by state<Shader?> { null }
    waveformShaderLoader.onAvailable { waveformShader = it }

    val allPlayers = listOf(1, 2, 3, 4).associate { playerNumber ->
        playerNumber to beatLink.playerStates.byDeviceNumber[playerNumber]
    }
    val activePlayerCount = allPlayers.values.filterNotNull().count()

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
                val waveformVisHeight = if (playerState == null) 0 else (layoutDimens.height - beatVisHeight) / activePlayerCount

                div(+Styles.waveformVisualizer) {
                    inlineStyles {
                        top = layoutY.px
                        height = waveformVisHeight.px
                        opacity = if (playerState == null) 0 else 1
                    }

                    if (playerState != null) {
                        div(+Styles.playerNumber) { +playerNumber.toString() }

                        shaderPreview {
                            attrs.shader = waveformShader
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

    val playerNumber by css {
        position = Position.absolute
        bottom = 0.px
        left = 0.px
        color = Color.white
        put("textShadow", "0px 1px 1px black")
    }

    val div by css {
        position = Position.relative
    }

    val bpm by css {
        position = Position.absolute
        bottom = 0.px
        left = 0.px
        color = Color.white
        put("textShadow", "0px 1px 1px black")
    }

    val confidence by css {
        position = Position.absolute
        bottom = 0.px
        right = 0.px
        color = Color.white
        put("textShadow", "0px 1px 1px black")
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
                playerState.updateTexture(gl, texture)
            }
        }

    init {
        playerState.updateTexture(gl, texture)
    }

    fun setOnProgram() {
        val trackElapsedTime = playerState.trackStartTime?.let { clock.now() - it } ?: 0.0
        trackElapsedTimeUniform?.set(trackElapsedTime.toFloat())
        trackLengthUniform?.set(playerState.totalTimeMs / 1000)
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