package baaahs.plugin.SoundAnalysis

import baaahs.app.ui.shaderPreview
import baaahs.futureAsync
import baaahs.io.getResourceAsync
import baaahs.onAvailable
import baaahs.plugin.sound_analysis.OpenSoundAnalysisControl
import baaahs.show.Shader
import baaahs.show.live.ControlProps
import baaahs.ui.important
import baaahs.ui.unaryMinus
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import kotlinx.css.*
import kotlinx.js.jso
import mui.material.Card
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import styled.StyleSheet

private val soundAnalysisVisualizerShader =
    futureAsync {
        Shader("SoundAnalysis Visualizer", getResourceAsync("baaahs/plugin/sound_analysis/SoundAnalysisControl.glsl"))
    }

private val SoundAnalysisControl = xComponent<SoundAnalysisControlProps>("SoundAnalysisControl") { _ ->
    var shader by state<Shader?> { null }
    soundAnalysisVisualizerShader.onAvailable { shader = it }

    Card {
        attrs.classes = jso { this.root = -Styles.card }
        div(+Styles.card) {
            shaderPreview {
                attrs.shader = shader
                attrs.width = 300.px
                attrs.height = 200.px
            }
        }
    }
}

object Styles : StyleSheet("plugin-SoundAnalysis", isStatic = true) {
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

external interface SoundAnalysisControlProps : Props {
    var controlProps: ControlProps
    var soundAnalysisControl: OpenSoundAnalysisControl
}

fun RBuilder.soundAnalysisControl(handler: RHandler<SoundAnalysisControlProps>) =
    child(SoundAnalysisControl, handler = handler)