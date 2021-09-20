package baaahs.plugin.beatlink

import baaahs.app.ui.appContext
import baaahs.show.live.ControlProps
import baaahs.sim.ui.SimulatorStyles
import baaahs.ui.addObserver
import baaahs.ui.on
import baaahs.ui.xComponent
import baaahs.util.percent
import kotlinx.css.Display
import kotlinx.css.display
import kotlinx.css.flex
import materialui.components.card.card
import materialui.components.paper.enums.PaperStyle
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import react.*
import react.dom.b
import react.dom.br
import react.dom.div
import react.dom.span
import styled.StyleSheet
import styled.css
import styled.styledDiv
import kotlin.math.roundToInt

private val beatLinkControl = xComponent<BeatLinkControlProps>("BeatLinkControl") { _ ->
    val appContext = useContext(appContext)
    val clock = appContext.clock
    val beatSource = appContext.plugins.findPlugin<BeatLinkPlugin>().beatSource

    val beat1 = ref<Element>()
    val beat2 = ref<Element>()
    val beat3 = ref<Element>()
    val beat4 = ref<Element>()
    val beats = listOf(beat1, beat2, beat3, beat4)
    val confidence = ref<HTMLElement>()
    val bpmSpan = ref<HTMLElement>()
    val timeout = ref<Int>()

    fun update() {
        val beatData = beatSource.getBeatData()
        val beat = beatData.beatWithinMeasure(clock).toInt()
        val bpm = beatData.bpm
        val beatConfidence = beatData.confidence

        beats.forEachIndexed { index, ref ->
            ref.current!!.className =
                if (beat == index) "selected" else ""
        }

        confidence.current!!.innerText = beatConfidence.percent()
        bpmSpan.current!!.innerText = "${bpm.roundToInt()} BPM"

        bpmSpan.current!!.className =
            if (beat % 1 == 0) "bpmDisplay bpmDisplay-beatOn" else "bpmDisplay bpmDisplay-beatOff"


        if (beatData.beatIntervalMs != 0) {
            val millisTillNextBeat = beatData.millisTillNextBeat(clock)
            timeout.current = baaahs.window.setTimeout({ update() }, timeout = millisTillNextBeat)
        }
    }

    fun cancelUpdate() {
        timeout.current?.let {
            baaahs.window.clearTimeout(it)
            timeout.current = null
        }
    }

    beatSource.addObserver {
        cancelUpdate()
        update()
    }

    onMount {
        update()
        withCleanup { cancelUpdate() }
    }

    card(Styles.visualizerCard on PaperStyle.root) {
        styledDiv {
            css { +SimulatorStyles.beatsDiv }

            b { +"Beats: " }
            +"[confidence: "
            span { ref = confidence }
            +"]"

            br { }
            div { ref = beat1; +"1" }
            div { ref = beat2; +"2" }
            div { ref = beat3; +"3" }
            div { ref = beat4; +"4" }

            span { ref = bpmSpan }
        }
    }
}

object Styles : StyleSheet("baaahs.Beatlink", isStatic = true) {
    val visualizerCard by css {
        display = Display.flex
        flex(1.0, 0.0)
    }
}

external interface BeatLinkControlProps : RProps {
    var controlProps: ControlProps
    var beatLinkControl: OpenBeatLinkControl
}

fun RBuilder.beatLinkControl(handler: RHandler<BeatLinkControlProps>) =
    child(beatLinkControl, handler = handler)