package external.midi

import org.w3c.dom.events.Event
import org.w3c.dom.events.EventTarget
import react.dom.events.EventHandler
import org.khronos.webgl.Uint8Array

external class MIDIOptions {
    var sysex: Boolean
}

external class MIDIAccess: EventTarget {
    val inputs: MIDIInputMap
    val outputs: MIDIOutputMap
    var onstatechange: EventHandler<MIDIConnectionEvent>
    val sysexEnabled: Boolean
}

external class MIDIConnectionEvent : Event {
    val port: MIDIPort
}

external class MIDIInput: MIDIPort
external class MIDIOutputMap
external class MIDIInputMap

open external class MIDIPort {
    val name: String
    val manufacturer: String
    val version: String
    var onmidimessage: ((e: MIDIMessageEvent) -> Any)? = definedExternally
    fun open()
    fun close()
}

external class MIDIMessageEvent {
    val data: Uint8Array
}
