package baaahs.ui.slider

import baaahs.app.ui.gadgets.slider.HandleProps
import baaahs.app.ui.gadgets.slider.TickProps
import baaahs.app.ui.gadgets.slider.TrackProps
import baaahs.ui.Observable
import baaahs.ui.xComponent
import baaahs.util.useResizeListener
import js.core.jso
import react.*
import react.dom.div
import react.dom.events.KeyboardEvent
import react.dom.events.PointerEvent
import web.dom.Element
import web.html.HTMLElement
import web.uievents.POINTER_MOVE
import web.uievents.POINTER_UP

/**
 * Generic slider component derived from react-compound-slider.
 */

private val defaultDomain = 0.0..1.0

private val Slider = xComponent<BetterSliderProps>("Slider") { props ->
    val step = props.step
    val domain = props.domain?.properlyOrdered() ?: defaultDomain
    val vertical = props.vertical == true
    val reversed = props.reversed == true
    val tickCount = props.tickCount ?: 10

    var activeHandleId by state { "" }
    val pointerDownOffset = ref<Double>()

    val valueToPercent = memo(domain, reversed) {
        LinearScale(domain, 0.0..100.0, reversed)
    }
    val valueToValue = memo(domain, step) {
        DiscreteScale(step, domain, domain)
    }

    onMount(props.handles) {
        props.handles.forEach { handle ->
            val adjustedValue = valueToValue.getValue(handle.value)
            if (adjustedValue != handle.value) {
                handle.value = adjustedValue
                logger.warn { "Value ${handle.value} is not a valid step value. Using $adjustedValue instead." }
            }
        }
    }

    val handlesById = memo(props.handles) {
        props.handles.associateBy { handle -> handle.id }
    }

    val sliderItems = memo(
        props.handles, valueToValue, valueToPercent, props.onUpdate, props.onChange
    ) {
//        var changes = 0
//        values.entries.associate { (handleId, value) ->
//            val adjustedValue = valueToValue.getValue(value)
//            if (adjustedValue != value) {
//                changes += 1
//            }
//            handleId to Handle(handleId, adjustedValue, valueToPercent.getValue(adjustedValue))
//        }.also {
//            if (changes > 0) {
//                val updatedHandles = it.entries.associate { (k, v) -> k to v.value }
//                props.onUpdate?.invoke(updatedHandles)
//                props.onChange?.invoke(updatedHandles)
//            }
//        }
        props.handles.map { handle ->
            Handle(handle.id, handle.value)
        }
    }

    val handles = props.handles //memo(sliderItems) { sliderItems.toTypedArray<Handle>() }

    val slider = ref<Element>()

    val pixelToValue = memo(domain, step, reversed) {
        DiscreteScale(step, domain, domain, reversed)
    }
    useResizeListener(slider) { _, _ ->
        pixelToValue.domain = getSliderDomain(slider.current, vertical)
    }
    onMount(pixelToValue, slider, vertical) {
        pixelToValue.domain = getSliderDomain(slider.current, vertical)
    }

    val submitUpdate = callback(
        valueToValue, valueToPercent, sliderItems, props.onUpdate, props.onChange, props.onSlideEnd
    ) { newHandles: Any/*Array<HandleItem>*/, callOnChange: Boolean, callOnSlideEnd: Boolean ->
//        val updatedHandles = newHandles.associate { updatedHandle ->
//            sliderItems[updatedHandle.key]?.let { sliderItem ->
//                sliderItem.value = updatedHandle.value
////                sliderItem.percent = valueToPercent.getValue(updatedHandle.value)
//                sliderItem.notifyChanged()
//            }
//            updatedHandle.key to updatedHandle.value
//        }
//        props.onUpdate?.invoke(updatedHandles)
//        if (callOnChange) props.onChange?.invoke(updatedHandles)
//        if (callOnSlideEnd) props.onSlideEnd?.invoke(updatedHandles, jso { this.activeHandleID = activeHandleId })

        newHandles
    }

    val getEventValue by handler(domain, pixelToValue, vertical, pointerDownOffset) { e: web.uievents.PointerEvent ->
        domain.clamp(pixelToValue.getValue(if (vertical) e.clientY.toDouble() else e.pageX) +
                (pointerDownOffset.current ?: 0.0))
    }

    val getEventData by handler(getEventValue, valueToPercent) { e: web.uievents.PointerEvent ->
        val value = getEventValue(e)
        jso<EventData> {
            this.value = value
            this.percent = valueToPercent.getValue(value)
        }
    }

    val updateSliderItems by handler(sliderItems, reversed) { updateKey: String, updateValue: Double ->
//        sliderItems.map { (key, sliderItem) ->
//            handleItem(key, if (key == updateKey) updateValue else sliderItem.value)
//        }.toTypedArray()
    }

    val handlePointerMove by handler(getEventData, updateSliderItems, submitUpdate) { e: web.uievents.PointerEvent ->
        val updateValue = getEventValue(e)

        // generate a "candidate" set of values - a suggestion of what to do
        val nextHandles = updateSliderItems(activeHandleId, updateValue)

        // submit the candidate values
        handles.find { it.id == activeHandleId }?.let { handle ->
            handle.value = updateValue
        }
        submitUpdate(nextHandles, false, false)
        Unit
    }

    val handlePointerUp by handler(getEventValue, updateSliderItems, handlePointerMove, slider) { e: web.uievents.PointerEvent ->
        val updateValue = getEventValue(e)

        // generate a "candidate" set of values - a suggestion of what to do
        val nextHandles = updateSliderItems(activeHandleId, updateValue)

        // submit the candidate values
        submitUpdate(nextHandles, true, true)
        activeHandleId = ""

        val sliderEl = slider.current!!
        sliderEl.releasePointerCapture(e.pointerId)
        sliderEl.removeEventListener(web.uievents.PointerEvent.POINTER_MOVE, handlePointerMove)
    }

    val handleKeyDown by handler(sliderItems, vertical, reversed) { e: KeyboardEvent<*>, handleId: String ->
//        val rightUpKeys = arrayOf("ArrowRight", "ArrowUp")
//        val downLeftKeys = arrayOf("ArrowDown", "ArrowLeft")
//        val validUpKeys = if (vertical) rightUpKeys else downLeftKeys
//        val validDownKeys = if (vertical) downLeftKeys else rightUpKeys
//
//        val key = e.key.ifBlank { e.code.toString() }
//
//        if (!(validUpKeys + validDownKeys).contains(key)) {
//            return@handler
//        }
//
//        e.stopPropagation()
//        e.preventDefault()
//
//        val found = sliderItems[handleId]
//            ?: return@handler
//
//        val currVal = found.value
//        var newVal = currVal
//
//        if (step != null && validUpKeys.contains(key)) {
//            newVal = getNextValue(currVal, step, domain, reversed)
//        } else if (step != null && validDownKeys.contains(key)) {
//            newVal = getPrevValue(currVal, step, domain, reversed)
//        }
//        val nextHandles = sliderItems.map { (k, v) ->
//            handleItem(key, if (k == handleId) newVal else v.value)
//        }.toTypedArray()
//
//        submitUpdate(nextHandles, true, false)
    }

    val handleRailAndTrackClicks by handler(getEventValue, sliderItems, updateSliderItems) { e: PointerEvent<*> ->
        val updateValue = getEventValue(e.nativeEvent)

        // find the closest handle key
        var updateKey = ""
        var minDiff = Double.POSITIVE_INFINITY

//        for ((key, item) in sliderItems) {
//            val diff = abs(item.value - updateValue)
//
//            if (diff < minDiff) {
//                updateKey = key
//                minDiff = diff
//            }
//        }

        // generate a "candidate" set of values - a suggestion of what to do
        val nextHandles = updateSliderItems(updateKey, updateValue)

        // submit the candidate values
        activeHandleId = updateKey
        submitUpdate(nextHandles, true, false)
    }

    val handlePointerDown by handler<EmitPointer>(
        sliderItems, handlePointerMove, handlePointerUp, handleRailAndTrackClicks, props.onSlideStart,
        pointerDownOffset, slider, getEventValue
    ) { e: PointerEvent<*>, _: Location, handleId: String? ->
        if (e.button != 0) return@handler

        e.stopPropagation()

        val sliderEl = slider.current!!
        sliderEl.setPointerCapture(e.pointerId)
        sliderEl.addEventListener(web.uievents.PointerEvent.POINTER_MOVE, handlePointerMove)
        sliderEl.addEventListener(web.uievents.PointerEvent.POINTER_UP, handlePointerUp, jso { once = true })

        if (handleId != null) {
            val handle = handlesById[handleId]
                ?: error("Cannot find handle with id $handleId.")

            pointerDownOffset.current = null
            pointerDownOffset.current = handle.value - getEventValue(e.nativeEvent)
            activeHandleId = handleId
//            props.onSlideStart?.invoke(
//                sliderItems.entries.associate { (key, item) -> key to item.value },
//                jso { this.activeHandleID = handleId }
//            )
        } else {
            pointerDownOffset.current = null
            activeHandleId = ""
            handleRailAndTrackClicks(e)
        }
    }

    val ticks = memo(props.tickGenerator, valueToPercent, tickCount) {
        val tickGenerator = props.tickGenerator
            ?: { count: Int -> valueToPercent.getTicks(count).toList() }
        tickGenerator.invoke(tickCount)
    }

    // render():
//    val children = react.Children.map(props.children) { child ->
//        if (isRCSComponent(child)) {
//            react.cloneElement(child.unsafeCast<react.ReactElement<RcsProps>>(), jso {
//                this.scale = valueToPercent
//                this.handles = handles
//                this.activeHandleId = activeHandleId
//                this.getEventData = getEventData
//                this.emitKeyboard = if (props.disabled) null else handleKeyDown
//                this.emitPointer = if (props.disabled) null else handlePointerDown
//            })
//        } else {
//            child
//        }
//    }

    div(props.className) {
        ref = slider

        props.renderRail?.let { renderRail ->
            rails {
                attrs.renderRail = renderRail
                attrs.emitPointer = if (props.disabled) null else handlePointerDown
            }
        }

        handles {
            attrs.domain = domain
            attrs.scale = valueToPercent
            attrs.handles = handles
            attrs.activeHandleId = activeHandleId
            attrs.emitKeyboard = if (props.disabled) null else handleKeyDown
            attrs.emitPointer = if (props.disabled) null else handlePointerDown
        }

        props.trackComponent?.let { trackComponent ->
            tracks {
                attrs.handles = handles
                attrs.domain = domain
                attrs.scale = valueToPercent
                attrs.renderTrack2 = trackComponent
                attrs.emitPointer = if (props.disabled) null else handlePointerDown
            }
        }

        props.tickComponent?.let { tickComponent ->
            ticks.forEach { tick ->
                +cloneElement(tickComponent) {
                    key = "tick-$tick"
                    this.value = tick
                    this.percent = valueToPercent.getValue(tick)
                    this.formatter = props.tickFormatter
                    this.reversed = reversed
                }
            }
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
    var handles: List<ExtHandle>
    /** The step value for the slider. */
    var step: Double?
    /** Set to true if the slider is displayed vertically to tell the slider to use the height to calculate positions. */
    var vertical: Boolean?
    /** Reverse the display of slider values. */
    var reversed: Boolean?
    /** Function triggered when the value of the slider has changed. This will receive changes at the end of a slide as well as changes from clicks on rails and tracks. Receives values. */
    var onChange: ((values: Map<String, Double>) -> Unit)?
    /** Function called with the values at each update (caution: high-volume updates when dragging). Receives values. */
    var onUpdate: ((values: Map<String, Double>) -> Unit)?
    /** Function triggered with ontouchstart or onmousedown on a handle. Receives values. */
    var onSlideStart: ((values: Map<String, Double>, data: SliderData) -> Unit)?
    /** Function triggered on ontouchend or onmouseup on a handle. Receives values. */
    var onSlideEnd: ((values: Map<String, Double>, data: SliderData) -> Unit)?
    /** Ignore all mouse, touch and keyboard events. */
    var disabled: Boolean

    var renderRail: ((railProps: RailProps) -> ReactNode)?
    var renderHandle: ((handleProps: HandleProps) -> ReactNode)?
    var renderTrack: ((trackProps: TrackProps) -> ReactNode)?
    var tickComponent: ReactElement<TickProps>?
    var trackComponent: ReactElement<TrackProps>?

    var tickCount: Int?
    var tickGenerator: ((Int) -> List<Double>)?
    var tickFormatter: ((Double) -> String)?
}

external interface SliderData {
    var activeHandleID: String
}

class Handle(
    var id: String,
    var value: Double/*,
    var percent: Double*/
) : Observable()

class ExtHandle(
    val id: String,
    var value: Double,
    val component: ReactElement<out HandleProps>
) : Observable()

external interface RailProps : Props, StandardEventHandlers

external interface HandleProps : PropsWithRef<HTMLElement>, StandardEventHandlers {
    var domain: Range
    var handle: ExtHandle
    var isActive: Boolean
}

external interface TrackProps : Props {
    var id: String
    var fromHandle: ExtHandle?
    var toHandle: ExtHandle?
    var scale: LinearScale
}

fun RBuilder.slider(handler: RHandler<BetterSliderProps>) =
    child(Slider, handler = handler)
