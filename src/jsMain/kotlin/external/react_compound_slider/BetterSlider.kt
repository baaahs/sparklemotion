package external.react_compound_slider

import baaahs.ui.xComponent
import js.core.jso
import react.RBuilder
import react.RHandler
import react.dom.div
import react.dom.events.KeyboardEvent
import react.dom.events.PointerEvent
import web.dom.Element
import web.uievents.POINTER_MOVE
import web.uievents.POINTER_UP
import kotlin.math.abs

private val defaultDomain = arrayOf(0.0, 1.0)

val BetterSlider = xComponent<SliderProps>("BetterSlider") { props ->
    val step = props.step ?: 0.1
    val domain = props.domain ?: defaultDomain
    val vertical = props.vertical == true
    val reversed = props.reversed == true
    var activeHandleId by state { "" }
    val pointerDownOffset = ref<Double>()

    // Finagle access to the Slider's scales:
    val baseSlider = memo(domain, step, reversed) {
        Slider.asDynamic().getDerivedStateFromProps(jso<SliderProps> {
            this.domain = props.domain
            this.step = props.step
            this.reversed = props.reversed
        }, jso()) }

    val valueToPerc = memo(baseSlider, domain, step, reversed) {
        (baseSlider.valueToPerc.unsafeCast<LinearScale>()).apply {
            this.domain = domain
            this.range = if (reversed) arrayOf(100.0, 0.0) else arrayOf(0.0, 100.0)
//            this.range = arrayOf(0.0, 100.0).letIf(reversed) { it.reversedArray() }
        }
    }
    val valueToStep = memo(baseSlider, domain, step, reversed) {
        (baseSlider.valueToStep.unsafeCast<DiscreteScale>()).apply {
            this.step = step
            this.range = domain
            this.domain = domain
        }
    }
    val pixelToStep = memo(baseSlider, domain, step, reversed) {
        (baseSlider.pixelToStep.unsafeCast<DiscreteScale>()).apply {
            this.step = step
            this.range = if (reversed) domain.reversedArray() else domain
        }
    }

    val (min, max) = domain
    if (max < min) {
        console.warn("Max must be greater than min (even if reversed). Max is $max. Min is $min.")
    }

    val values = props.values
    val (handles, changes) = memo(values, reversed, valueToStep) {
        getHandles(values, reversed, valueToStep, props.warnOnChanges, logger)
    }
    if (changes > 0) {
        val updatedHandles = handles.associate { it.key to it.value }
        props.onUpdate?.invoke(updatedHandles)
        props.onChange?.invoke(updatedHandles)
    }

    val slider = ref<Element>()

    val submitUpdate = callback(valueToStep) { newHandles: Array<HandleItem>, callOnChange: Boolean, callOnSlideEnd: Boolean ->
        val updatedHandles = newHandles.associate { it.key to it.value }
        props.onUpdate?.invoke(updatedHandles)
        if (callOnChange) props.onChange?.invoke(updatedHandles)
        if (callOnSlideEnd) props.onSlideEnd?.invoke(updatedHandles, jso { this.activeHandleID = activeHandleId })

        newHandles
    }

    val getEventValue by handler(pixelToStep, vertical, valueToPerc) { e: web.uievents.PointerEvent ->
        // double-check the dimensions of the slider
        pixelToStep.setDomain(getSliderDomain(slider.current, vertical))
        pixelToStep.getValue(if (vertical) e.clientY.toDouble() else e.pageX) +
                (pointerDownOffset.current ?: 0.0)
    }

    val getEventData by handler(getEventValue, valueToPerc) { e: web.uievents.PointerEvent ->
        val value = getEventValue(e)
        jso<EventData> {
            this.value = value
            this.percent = valueToPerc.getValue(value)
        }
    }

    val handlePointerMove by handler(getEventData, handles, submitUpdate) { e: web.uievents.PointerEvent ->
        val updateValue = getEventValue(e)

        // generate a "candidate" set of values - a suggestion of what to do
        val nextHandles = updateHandles(handles, activeHandleId, updateValue, reversed)

        // submit the candidate values
        submitUpdate(nextHandles, false, false)
        Unit
    }

    val handlePointerUp by handler(handles, handlePointerMove) { e: web.uievents.PointerEvent ->
        val updateValue = getEventValue(e)

        // generate a "candidate" set of values - a suggestion of what to do
        val nextHandles = updateHandles(handles, activeHandleId, updateValue, reversed)

        // submit the candidate values
        submitUpdate(nextHandles, true, true)
        activeHandleId = ""

        val sliderEl = slider.current!!
        sliderEl.releasePointerCapture(e.pointerId)
        sliderEl.removeEventListener(web.uievents.PointerEvent.POINTER_MOVE, handlePointerMove)
    }

    val handleKeyDown by handler(handles, vertical, reversed) { e: KeyboardEvent<*>, handleId: String ->
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

        val found = handles.find { item ->
            item.key == handleId
        } ?: return@handler

        val currVal = found.value
        var newVal = currVal

        if (validUpKeys.contains(key)) {
            newVal = getNextValue(currVal, step, domain, reversed)
        } else if (validDownKeys.contains(key)) {
            newVal = getPrevValue(currVal, step, domain, reversed)
        }
        val nextHandles = handles.map { v ->
            if (v.key == handleId) handleItem(key, newVal) else v
        }.toTypedArray()

        submitUpdate(nextHandles, true, false)
    }

    val handleRailAndTrackClicks by handler(handles, pixelToStep, vertical, reversed) { e: PointerEvent<*> ->
        val updateValue = getEventValue(e.nativeEvent)

        // find the closest handle key
        var updateKey = ""
        var minDiff = Double.POSITIVE_INFINITY

        for (item in handles) {
            val diff = abs(item.value - updateValue)

            if (diff < minDiff) {
                updateKey = item.key
                minDiff = diff
            }
        }

        // generate a "candidate" set of values - a suggestion of what to do
        val nextHandles = updateHandles(handles, updateKey, updateValue, reversed)

        // submit the candidate values
        activeHandleId = updateKey
        submitUpdate(nextHandles, true, false)
    }

    val handlePointerDown by handler<EmitPointer>(
        handles, handlePointerMove, handlePointerUp, handleRailAndTrackClicks, props.onSlideStart
    ) { e: PointerEvent<*>, location: Location, handleID: String? ->
        if (e.button != 0) return@handler

        e.stopPropagation()

        val sliderEl = slider.current!!
        sliderEl.setPointerCapture(e.pointerId)
        sliderEl.addEventListener(web.uievents.PointerEvent.POINTER_MOVE, handlePointerMove)
        sliderEl.addEventListener(web.uievents.PointerEvent.POINTER_UP, handlePointerUp, jso { once = true })

        if (handleID != null) {
            val handle = handles.firstOrNull { it.key == handleID }
                ?: error("Cannot find handle with id $handleID.")

            pointerDownOffset.current = null
            pointerDownOffset.current = handle.value - getEventValue(e.nativeEvent)
            activeHandleId = handleID
            props.onSlideStart?.invoke(
                handles.associate { it.key to it.value },
                jso { this.activeHandleID = handleID }
            )
        } else {
            pointerDownOffset.current = null
            activeHandleId = ""
            handleRailAndTrackClicks(e)
        }
    }

    onMount(pixelToStep, slider, vertical) {
        pixelToStep.domain = getSliderDomain(slider.current, vertical)
    }

    // render():
    val mappedHandles = memo(handles) {
        handles.map { item ->
            jso<SliderItem> {
                this.id = item.key
                this.value = item.value
                this.percent = valueToPerc.getValue(item.value)
            }
        }.toTypedArray()
    }

    val children = react.Children.map(props.children) { child ->
        if (isRCSComponent(child)) {
            react.cloneElement(child.unsafeCast<react.ReactElement<RcsProps>>(), jso {
                this.scale = valueToPerc
                this.handles = mappedHandles
                this.activeHandleID = activeHandleId
                this.getEventData = getEventData
                this.emitKeyboard = if (props.disabled) null else handleKeyDown
                this.emitPointer = if (props.disabled) null else handlePointerDown
            })
        } else {
            child
        }
    }

    div(props.className) {
        ref = slider
        children?.forEach { child(it) }
    }
}

fun RBuilder.betterSlider(handler: RHandler<SliderProps>) =
    child(BetterSlider, handler = handler)
