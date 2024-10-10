package baaahs.ui.slider

import js.objects.jso
import react.createContext
import react.dom.events.KeyboardEvent
import react.dom.events.PointerEvent

external interface SliderContext {
    var domain: Range
    var isReversed: Boolean
    var isVertical: Boolean
    var scale: LinearScale

    var emitPointer: EmitPointer
    var emitKeyboard: EmitKeyboard
    var handles: List<Handle>

    var onHandlePointerDowns: Map<String, (event: PointerEvent<*>) -> Unit>
    var onHandleKeyDowns: Map<String, (event: KeyboardEvent<*>) -> Unit>
}

fun SliderContext.getPointerDownHandlerFor(handle: Handle): (event: PointerEvent<*>) -> Unit =
    onHandlePointerDowns[handle.id] ?: { _: PointerEvent<*> -> }

fun SliderContext.getKeyDownHandlerFor(handle: Handle): (event: KeyboardEvent<*>) -> Unit =
    onHandleKeyDowns[handle.id] ?: { _: KeyboardEvent<*> -> }

val sliderContext = createContext<SliderContext>(jso {})