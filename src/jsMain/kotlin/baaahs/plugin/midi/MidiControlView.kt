package baaahs.plugin.midi

import baaahs.app.ui.appContext
import baaahs.app.ui.shaderPreview
import baaahs.futureAsync
import baaahs.io.getResourceAsync
import baaahs.onAvailable
import baaahs.plugin.midi.MidiPlugin
import baaahs.show.Shader
import baaahs.show.live.ControlProps
import baaahs.ui.*
import baaahs.util.percent
import dom.html.HTMLElement
import kotlinx.css.*
import kotlinx.js.jso
import mui.material.Card
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.useContext
import styled.StyleSheet
import kotlin.math.roundToInt

private val midiVisualizerShader =
    futureAsync {
        Shader("Midi Visualizer", getResourceAsync("baaahs/plugin/midi/MidiControl.glsl"))
    }

private val midiControl = xComponent<MidiControlProps>("MidiControl") { _ ->
    val appContext = useContext(appContext)
    val midiSource = appContext.plugins.getPlugin<MidiPlugin>().midiSource

    val sustainPedalCountDiv = ref<HTMLElement>()
    val noteCountDiv = ref<HTMLElement>()

    fun update(midiData: MidiData) {
        val noteCount = midiData.noteCount
        noteCountDiv.current!!.innerText = "Note Count: $noteCount"

        val sustainPedalCount = midiData.sustainPedalCount
        sustainPedalCountDiv.current!!.innerText = "Sustain Pedal Count: $sustainPedalCount"
    }

    onMount {
        val observer = midiSource.addObserver(fireImmediately = true) { midiSource ->
            val midiData = midiSource.getMidiData()
            update(midiData)
        }
        withCleanup { observer.remove() }
    }

    var shader by state<Shader?> { null }
    midiVisualizerShader.onAvailable { shader = it }

    Card {
        attrs.classes = jso { this.root = -Styles.card }
        div(+Styles.card) {
            shaderPreview {
                attrs.shader = shader
            }

            div(+Styles.bpm) { ref = sustainPedalCountDiv }
            div(+Styles.confidence) { ref = noteCountDiv }
        }
    }
}

object Styles : StyleSheet("plugin-Midi", isStatic = true) {
    val card by css {
        display = Display.flex
        flex(1.0, 0.0)

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

external interface MidiControlProps : Props {
    var controlProps: ControlProps
    var midiControl: OpenMidiControl
}

fun RBuilder.midiControl(handler: RHandler<MidiControlProps>) =
    child(midiControl, handler = handler)