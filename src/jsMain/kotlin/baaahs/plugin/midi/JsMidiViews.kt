package baaahs.plugin.midi

import baaahs.plugin.midi.MidiViews
import baaahs.plugin.midi.OpenMidiControl
import baaahs.show.live.ControlProps
import baaahs.ui.renderWrapper

object JsMidiViews : MidiViews {
    override fun forControl(openButtonControl: OpenMidiControl, controlProps: ControlProps) = renderWrapper {
        midiControl {
            attrs.controlProps = controlProps
            attrs.midiControl = openButtonControl
        }
    }
}

actual fun getMidiViews(): MidiViews = JsMidiViews
