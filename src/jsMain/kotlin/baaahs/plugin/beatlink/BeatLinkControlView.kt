package baaahs.plugin.beatlink

import baaahs.app.ui.appContext
import baaahs.app.ui.shaderPreview
import baaahs.futureAsync
import baaahs.io.getResourceAsync
import baaahs.onAvailable
import baaahs.show.Shader
import baaahs.show.live.ControlProps
import baaahs.ui.*
import baaahs.util.percent
import js.core.jso
import kotlinx.css.*
import mui.material.Card
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.useContext
import styled.StyleSheet
import web.html.HTMLElement
import kotlin.math.roundToInt

private val beatLinkVisualizerShader =
    futureAsync {
        Shader("BeatLink Visualizer", getResourceAsync("baaahs/plugin/beatlink/BeatLinkControl.glsl"))
    }

private val beatLinkControl = xComponent<BeatLinkControlProps>("BeatLinkControl") { _ ->
    val appContext = useContext(appContext)
    val beatSource = appContext.plugins.getPlugin<BeatLinkPlugin>().beatSource

    val bpmDiv = ref<HTMLElement>()
    val confidenceDiv = ref<HTMLElement>()

    fun update(beatData: BeatData) {
        val bpm = beatData.bpm
        bpmDiv.current!!.innerText = "${bpm.roundToInt()} BPM"

        val beatConfidence = beatData.confidence
        confidenceDiv.current!!.innerText = "Confidence: ${beatConfidence.percent()}"
    }

    onMount {
        val observer = beatSource.addObserver(fireImmediately = true) { beatSource ->
            val beatData = beatSource.getBeatData()
            update(beatData)
        }
        withCleanup { observer.remove() }
    }

    var shader by state<Shader?> { null }
    beatLinkVisualizerShader.onAvailable { shader = it }

    Card {
        attrs.classes = jso { this.root = -Styles.card }
        div(+Styles.card) {
            shaderPreview {
                attrs.shader = shader
            }

            div(+Styles.bpm) { ref = bpmDiv }
            div(+Styles.confidence) { ref = confidenceDiv }
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

external interface BeatLinkControlProps : Props {
    var controlProps: ControlProps
    var beatLinkControl: OpenBeatLinkControl
}

fun RBuilder.beatLinkControl(handler: RHandler<BeatLinkControlProps>) =
    child(beatLinkControl, handler = handler)