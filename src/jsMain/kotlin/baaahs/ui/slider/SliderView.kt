package baaahs.ui.slider

import baaahs.ui.xComponent
import baaahs.util.useResizeListener
import js.objects.jso
import react.PropsWithChildren
import react.RBuilder
import react.RHandler
import react.dom.div
import react.dom.events.KeyboardEvent
import react.dom.events.PointerEvent
import web.dom.Element
import web.events.EventHandler
import web.html.HTMLElement
import web.uievents.MouseButton
import kotlin.math.abs

/**
 * Generic slider component derived from react-compound-slider.
 */

private val defaultDomain = 0.0..1.0

private val Slider = xComponent<BetterSliderProps>("Slider") { props ->
    val step = props.step
    val domain = props.domain?.properlyOrdered() ?: defaultDomain
    val vertical = props.vertical == true
    val reversed = props.reversed == true

    var activeHandle by state<Handle?> { null }
    val pointerDownOffset = ref<Double>()

    val valueToPercent = memo(domain, reversed) {
        LinearScale(domain, 0.0..100.0, reversed)
    }
    val valueToValue = memo(domain, step) {
        DiscreteScale(step, domain, domain)
    }

    onMount(props.handles, valueToValue) {
        props.handles.forEach { handle ->
            val adjustedValue = valueToValue.getValue(handle.value)
            if (adjustedValue != handle.value) {
                handle.value = adjustedValue
                logger.warn { "Value ${handle.value} is not a valid step value. Using $adjustedValue instead." }
            }
        }
    }

    val handles = props.handles
    val handlesById = memo(handles) { handles.associateBy { handle -> handle.id } }

    val pixelToValue = memo(domain, step, reversed) {
        DiscreteScale(step, domain, domain, reversed)
    }

    val slider = ref<Element>()
    useResizeListener(slider) { _, _ ->
        pixelToValue.domain = getSliderDomain(slider.current, vertical)
    }
    onMount(pixelToValue, slider, vertical) {
        pixelToValue.domain = getSliderDomain(slider.current, vertical)
    }

    val getEventValue by handler(domain, pixelToValue, vertical, pointerDownOffset) { e: PointerEvent<*> ->
        domain.clamp(
            pixelToValue.getValue(if (vertical) e.clientY else e.pageX) +
                    (pointerDownOffset.current ?: 0.0)
        )
    }

    val handlePointerMove by handler(getEventValue) { e: PointerEvent<*> ->
        val updateValue = getEventValue(e)
        activeHandle?.changeTo(updateValue, false)
        Unit
    }

    val handlePointerUp by handler(getEventValue, handlePointerMove, slider) { e: PointerEvent<*> ->
        val updateValue = getEventValue(e)
        activeHandle?.let {
            it.changeTo(updateValue, true)
            props.onSlideEnd?.invoke(it)
        }
        activeHandle = null

        val sliderEl = slider.current!!
        sliderEl.releasePointerCapture(e.pointerId)
        sliderEl.removeEventListener(
            web.uievents.PointerEvent.POINTER_MOVE,
            handlePointerMove.unsafeCast<EventHandler<web.uievents.PointerEvent>>()
        )
    }

    val handleKeyDown by handler(vertical, reversed, handles) { e: KeyboardEvent<*>, handleId: String ->
        val rightUpKeys = arrayOf("ArrowRight", "ArrowUp")
        val downLeftKeys = arrayOf("ArrowDown", "ArrowLeft")
        val validUpKeys = if (vertical) rightUpKeys else downLeftKeys
        val validDownKeys = if (vertical) downLeftKeys else rightUpKeys

        val key = e.key.ifBlank { e.code.toString() }

        if (!(validUpKeys + validDownKeys).contains(key)) {
            return@handler
        }

        e.stopPropagation()
        e.preventDefault()

        val handle = handlesById[handleId]
            ?: return@handler

        val currVal = handle.value
        var newVal = currVal

        if (step != null && validUpKeys.contains(key)) {
            newVal = getNextValue(currVal, step, domain, reversed)
        } else if (step != null && validDownKeys.contains(key)) {
            newVal = getPrevValue(currVal, step, domain, reversed)
        }
        handle.changeTo(newVal, false)
    }

    val handleRailAndTrackClicks by handler(getEventValue) { e: PointerEvent<*> ->
        val updateValue = getEventValue(e)

        // find the closest handle key
        var updateHandle: Handle? = null
        var minDiff = Double.POSITIVE_INFINITY

        for (handle in handles) {
            val diff = abs(handle.value - updateValue)

            if (diff < minDiff) {
                updateHandle = handle
                minDiff = diff
            }
        }

        updateHandle?.changeTo(updateValue, false)
        activeHandle = updateHandle
    }

    val handlePointerDown by handler<EmitPointer>(
        handlesById, handlePointerMove, handlePointerUp, handleRailAndTrackClicks, props.onSlideStart,
        pointerDownOffset, slider, getEventValue
    ) { e: PointerEvent<*>, _: Location, handleId: String? ->
        if (e.button != MouseButton.MAIN) return@handler

        e.stopPropagation()

        val sliderEl = slider.current!!
        sliderEl.setPointerCapture(e.pointerId)
        sliderEl.addEventListener(
            web.uievents.PointerEvent.POINTER_MOVE,
            handlePointerMove.unsafeCast<EventHandler<web.uievents.PointerEvent>>()
        )
        sliderEl.addEventListener(
            web.uievents.PointerEvent.POINTER_UP,
            handlePointerUp.unsafeCast<EventHandler<web.uievents.PointerEvent>>(),
            jso { once = true })

        if (handleId != null) {
            val handle = handlesById[handleId]
                ?: error("Cannot find handle with id $handleId.")

            pointerDownOffset.current = null
            pointerDownOffset.current = handle.value - getEventValue(e)
            activeHandle = handle
            println("active handle.id = ${handle.id}")
            props.onSlideStart?.invoke(handle)
        } else {
            pointerDownOffset.current = null
            activeHandle = null
            handleRailAndTrackClicks(e)
        }
    }

    val context =
        memo(domain, reversed, vertical, valueToPercent, handlePointerDown, handleKeyDown, handles) {
            jso<SliderContext> {
                this.domain = domain
                this.isReversed = reversed
                this.isVertical = vertical
                this.scale = valueToPercent
                this.emitPointer = handlePointerDown
                this.emitKeyboard = handleKeyDown
                this.handles = handles
                this.onHandlePointerDowns = handles.associate {
                    it.id to { e: PointerEvent<*> ->
                        (e.target as? HTMLElement)?.focus()
                        handlePointerDown(e, Location.Handle, it.id)
                    }
                }
                this.onHandleKeyDowns = handles.associate {
                    it.id to { e: KeyboardEvent<*> ->
                        handleKeyDown(e, it.id)
                    }
                }
            }
        }

    // render():
    sliderContext.Provider {
        attrs.value = context

        div(props.className) {
            ref = slider

            props.children?.let { child(it) }
        }
    }
}

external interface BetterSliderProps : PropsWithChildren {
    /** CSS class name applied to the root element of the slider. */
    var className: String?

    /**
     * Range of numbers providing the min and max values for the slider [min, max] e.g. [0, 100].
     * It does not matter if the slider is reversed on the screen, domain is always [min, max] with min < max.
     */
    var domain: Range?

    /** A list of handles. */
    var handles: List<Handle>

    /** The step value for the slider. */
    var step: Double?

    /** Set to true if the slider is displayed vertically to tell the slider to use the height to calculate positions. */
    var vertical: Boolean?

    /** Reverse the display of slider values. */
    var reversed: Boolean?

//    /** Function triggered when the value of the slider has changed. This will receive changes at the end of a slide as well as changes from clicks on rails and tracks. Receives values. */
//    var onChange: ((values: Map<String, Double>) -> Unit)?
//
//    /** Function called with the values at each update (caution: high-volume updates when dragging). Receives values. */
//    var onUpdate: ((values: Map<String, Double>) -> Unit)?

    /** Function triggered with ontouchstart or onmousedown on a handle. Receives values. */
    var onSlideStart: ((Handle) -> Unit)?

    /** Function triggered on ontouchend or onmouseup on a handle. Receives values. */
    var onSlideEnd: ((Handle) -> Unit)?

    /** Ignore all mouse, touch and keyboard events. */
    var disabled: Boolean
}

fun RBuilder.slider(handler: RHandler<BetterSliderProps>) =
    child(Slider, handler = handler)
