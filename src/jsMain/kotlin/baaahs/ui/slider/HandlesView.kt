package baaahs.ui.slider

import baaahs.ui.xComponent
import emotion.css.css
import js.core.jso
import kotlinx.css.pct
import react.*
import react.dom.aria.AriaRole
import react.dom.aria.ariaValueMax
import react.dom.aria.ariaValueMin
import react.dom.aria.ariaValueNow
import react.dom.events.MouseEvent
import react.dom.events.PointerEvent
import styled.css
import web.html.HTMLElement
import web.cssom.pct as cssomPct

fun autofocus(e: MouseEvent<*, *>) {
    val target = e.target
    if (target is HTMLElement) {
        target.focus()
    }
}

private val Handles = xComponent<HandlesProps>("BetterHandles") { props ->
    val handleKeyDowns = memo(props.handles) {
        props.handles.map { handle ->
            { e: react.dom.events.KeyboardEvent<*> ->
                props.emitKeyboard?.invoke(e, handle.id)
                Unit
            }
        }
    }
    val handlePointerDowns = memo(props.handles) {
        props.handles.map { handle ->
            { e: PointerEvent<*> ->
                autofocus(e)
                props.emitPointer?.invoke(e, Location.Handle, handle.id)
                Unit
            }
        }
    }

    val handleRefs = memo { props.handles.map { ref<HTMLElement>() } }
    val domain = props.domain

    props.handles.forEachIndexed { index, handle ->
        observe(handle) {
            handleRefs[index].current?.let { handleRef ->
                handleRef.style.top = props.scale.getValue(handle.value).pct.toString()
                handleRef.ariaValueNow = handle.value.toString()
            }
        }
    }

    useInsertionEffect {
        handleRefs.forEachIndexed { index, handleRef ->
            handleRef.current?.let { handle ->
                handle.style.top = props.scale.getValue(props.handles[index].value).pct.toString()
                handle.ariaValueNow = props.handles[index].value.toString()
            }
        }
    }

    props.handles.forEachIndexed { index, handle ->
        react.dom.html.ReactHTML.div {
            ref = handleRefs[index]
            attrs.key = handle.id
            attrs.role = AriaRole.slider
            attrs.ariaValueMin = domain.start
            attrs.ariaValueMax = domain.endInclusive
            attrs.ariaValueNow = handle.value

            attrs.style = jso {
                top = props.scale.getValue(handle.value).cssomPct
            }

            attrs.onKeyDown = handleKeyDowns[index]
            attrs.onPointerDown = handlePointerDowns[index]

            +cloneElement(handle.component) {
                key = handle.id
                ref = handleRefs[index]
                this.domain = props.domain
                this.handle = handle
                this.isActive = props.activeHandleId == handle.id
            }
        }
    }
}

external interface HandlesProps : Props {
    var domain: Range
    var scale: LinearScale
    var handles: List<ExtHandle>
    var activeHandleId: String?
    var emitKeyboard: EmitKeyboard?
    var emitPointer: EmitPointer?
}

fun RBuilder.handles(handler: RHandler<HandlesProps>) =
    child(Handles, handler = handler)

external interface HandleItem {
    var key: String
    var value: Double
}
