package baaahs.plugin.SoundAnalysis

import baaahs.app.ui.appContext
import baaahs.app.ui.shaderPreview
import baaahs.futureAsync
import baaahs.io.getResourceAsync
import baaahs.onAvailable
import baaahs.plugin.sound_analysis.AudioInput
import baaahs.plugin.sound_analysis.OpenSoundAnalysisControl
import baaahs.plugin.sound_analysis.SoundAnalysisPlugin
import baaahs.show.Shader
import baaahs.show.live.ControlProps
import baaahs.ui.important
import baaahs.ui.unaryMinus
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import js.objects.jso
import kotlinx.css.*
import mui.material.Card
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.useContext
import styled.StyleSheet
import web.html.HTMLElement

private val soundAnalysisVisualizerShader =
    futureAsync {
        Shader("SoundAnalysis Visualizer", getResourceAsync("baaahs/plugin/sound_analysis/SoundAnalysisControl.glsl"))
    }

private val SoundAnalysisControl = xComponent<SoundAnalysisControlProps>("SoundAnalysisControl") { _ ->
    val appContext = useContext(appContext)
    val soundAnalyzer = appContext.plugins.getPlugin<SoundAnalysisPlugin>().soundAnalyzer

    val inputDiv = ref<HTMLElement>()

    fun update(audioInput: AudioInput?) {
        inputDiv.current!!.innerText = audioInput?.title ?: "No Input"
    }

    onMount(soundAnalyzer) {
        val listener = soundAnalyzer.listen { _, audioInput -> update(audioInput) }
        update(soundAnalyzer.currentAudioInput)
        withCleanup { soundAnalyzer.unlisten(listener) }
    }

    var shader by state<Shader?> { null }
    soundAnalysisVisualizerShader.onAvailable { shader = it }

    Card {
        attrs.classes = jso { this.root = -Styles.card }
        div(+Styles.card) {
            shaderPreview {
                attrs.shader = shader
            }

            div(+Styles.input) { ref = inputDiv }
        }
    }
}

object Styles : StyleSheet("plugin-SoundAnalysis", isStatic = true) {
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

    val input by css {
        position = Position.absolute
        bottom = 0.px
        left = 0.px
        color = Color.white
        put("textShadow", "0px 1px 1px black")
    }
}

external interface SoundAnalysisControlProps : Props {
    var controlProps: ControlProps
    var soundAnalysisControl: OpenSoundAnalysisControl
}

fun RBuilder.soundAnalysisControl(handler: RHandler<SoundAnalysisControlProps>) =
    child(SoundAnalysisControl, handler = handler)