package baaahs.plugin.midi

import baaahs.show.live.ControlProps
import baaahs.ui.View

interface MidiViews {
    fun forControl(openButtonControl: OpenMidiControl, controlProps: ControlProps): View
}

val midiViews by lazy { getMidiViews() }
expect fun getMidiViews(): MidiViews