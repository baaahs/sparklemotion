package baaahs.plugin.beatlink

import baaahs.app.ui.appContext
import baaahs.app.ui.shaderPreview
import baaahs.io.getResource
import baaahs.show.Shader
import baaahs.show.live.ControlProps
import baaahs.ui.*
import baaahs.util.percent
import kotlinx.css.*
import materialui.components.card.card
import materialui.components.paper.enums.PaperStyle
import org.w3c.dom.HTMLElement
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.useContext
import styled.StyleSheet
import kotlin.math.roundToInt

private val beatLinkVisualizerShader =
    Shader("BeatLink Visualizer", getResource("baaahs/plugin/beatlink/BeatLinkControl.glsl"))

private val beatLinkControl = xComponent<BeatLinkControlProps>("BeatLinkControl") { _ ->
    val appContext = useContext(appContext)
    val beatSource = appContext.plugins.findPlugin<BeatLinkPlugin>().beatSource

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

    card(Styles.card on PaperStyle.root) {
        div(+Styles.card) {
            shaderPreview {
                attrs.shader = beatLinkVisualizerShader
                attrs.width = 300.px
                attrs.height = 200.px
                attrs.dumpShader = true
            }

            div(+Styles.bpm) { ref = bpmDiv }
            div(+Styles.confidence) { ref = confidenceDiv }
        }
    }
}

object Styles : StyleSheet("plugin-Beatlink", isStatic = true) {
    val card by css {
        display = Display.flex
        flex(1.0, 0.0)

        // Needed because of [SharedGlContext]. TODO: remove that requirement.
        important(::backgroundColor, Color.transparent)
    }

    val div by css {
        position = Position.relative
    }

    val bpm by css {
        position = Position.absolute
        bottom = 0.px
        left = 0.px
        color = Color.white
        backgroundColor = Color.black
        fontWeight = FontWeight.bolder
    }

    val confidence by css {
        position = Position.absolute
        bottom = 0.px
        right = 0.px
        color = Color.white
        backgroundColor = Color.black
        fontWeight = FontWeight.bolder
    }
}

external interface BeatLinkControlProps : Props {
    var controlProps: ControlProps
    var beatLinkControl: OpenBeatLinkControl
}

fun RBuilder.beatLinkControl(handler: RHandler<BeatLinkControlProps>) =
    child(beatLinkControl, handler = handler)