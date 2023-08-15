package external.midi

import org.w3c.dom.events.Event
import org.w3c.dom.events.EventTarget
import react.dom.events.EventHandler

//external fun Navigator.requestMIDIAccess(options: MIDIOptions? = definedExternally): Promise<MIDIAccess>

external interface MIDIOptions {
    var sysex: Boolean
}

abstract external class MIDIAccess: EventTarget {
    val inputs: MIDIInputMap
    val outputs: MIDIOutputMap
    val onstatechange: EventHandler<MIDIConnectionEvent>
    val sysexEnabled: Boolean
}

abstract external class MIDIConnectionEvent : Event {
    val port: MIDIPort
}

abstract external class MIDIInputMap
abstract external class MIDIOutputMap

abstract external class MIDIPort