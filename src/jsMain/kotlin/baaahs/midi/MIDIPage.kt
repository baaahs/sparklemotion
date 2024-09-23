package baaahs.midi

import js.objects.jso
import react.RBuilder
import react.RComponent
import react.dom.b
import react.dom.div
import react.dom.header
import react.setState
import web.html.HTMLDivElement
import web.midi.MIDIAccess
import web.midi.MIDIInput
import web.navigator.navigator

class MIDIPage(props: MIDIPageProps) : RComponent<MIDIPageProps, MIDIPageState>(props) {
    private val container = react.createRef<HTMLDivElement>()
    override fun MIDIPageState.init() {
        midiInputs = emptyList()
    }
    override fun componentDidMount() {
        navigator.asDynamic().requestMIDIAccess(jso { sysex = true }).then { midiAccess: MIDIAccess ->
            setState {
                midiInputs = buildList {
                    midiAccess.inputs.asDynamic().forEach { input ->
                        add(input as MIDIInput)
                    }
                }
            }
            console.log(midiAccess)

            true
        }
        container.current?.appendChild(props.containerDiv)
    }

    override fun componentWillUnmount() {
        container.current?.removeChild(props.containerDiv)
    }

    override fun RBuilder.render() {
        div {
            ref = container
            header { +"MIDI" }
            div {
                if (state.midiInputs != null) {
                    state.midiInputs.forEach { input ->
                        div {
                            console.log(input)
                            b { +(input.name ?: "???") }
                        }
                    }
                }
            }
        }
    }
}

external interface MIDIPageProps : react.Props {
    var containerDiv: HTMLDivElement
}

external interface MIDIPageState : react.State {
    var midiInputs: List<MIDIInput>
}
