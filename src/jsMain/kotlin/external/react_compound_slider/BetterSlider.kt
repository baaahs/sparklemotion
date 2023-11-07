package external.react_compound_slider

import baaahs.ui.xComponent
import js.core.jso
import react.RBuilder
import react.RHandler
import react.dom.div
import react.dom.events.KeyboardEvent
import react.dom.events.PointerEvent
import web.dom.Element
import web.dom.document
import web.uievents.POINTER_MOVE
import web.uievents.POINTER_UP
import kotlin.math.abs

private val defaultDomain = arrayOf(0.0, 1.0)

val BetterSlider = xComponent<SliderProps>("BetterSlider") { props ->
    val step = props.step ?: 0.1
    val domain = props.domain ?: defaultDomain
    val vertical = props.vertical == true
    val reversed = props.reversed == true
    var activeHandleID by state { "" }

    val baseSlider = memo { Slider.asDynamic().getDerivedStateFromProps(props, jso()) }
//    console.log("BetterSlider render", props)

    val valueToPerc = memo(domain, step, reversed) {
        (baseSlider.valueToPerc.unsafeCast<LinearScale>()).apply {
            this.domain = domain
            this.range = if (reversed) arrayOf(100.0, 0.0) else arrayOf(0.0, 100.0)
//            this.range = arrayOf(0.0, 100.0).letIf(reversed) { it.reversedArray() }
        }
    }
    val valueToStep = memo(domain, step, reversed) {
        (baseSlider.valueToStep.unsafeCast<DiscreteScale>()).apply {
            this.step = step
            this.range = domain
            this.domain = domain
        }
    }
    val pixelToStep = memo(domain, step, reversed) {
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
        props.onUpdate?.invoke(handles.map { it.value }.toTypedArray())
        props.onChange?.invoke(handles.map { it.value }.toTypedArray())
    }

    val slider = ref<Element>()

    val submitUpdate = callback(valueToStep) { newHandles: Array<HandleItem>, callOnChange: Boolean ->
        props.onUpdate?.invoke(newHandles.map { it.value }.toTypedArray())
        if (callOnChange) {
            props.onChange?.invoke(newHandles.map { it.value }.toTypedArray())
        }

        newHandles
    }


    val handlePointerMove by handler(handles, pixelToStep, vertical, reversed, submitUpdate) { e: web.uievents.PointerEvent ->
        // double check the dimensions of the slider
        pixelToStep.setDomain(getSliderDomain(slider.current, vertical))

        // find the closest value (aka step) to the event location
        val updateValue = pixelToStep.getValue(if (vertical) e.clientY.toDouble() else e.pageX)

        // generate a "candidate" set of values - a suggestion of what to do
        val nextHandles = getUpdatedHandles(handles, activeHandleID, updateValue, reversed)

        // submit the candidate values
        submitUpdate(nextHandles, false)
        Unit
    }

    val handlePointerUp by handler(handles, handlePointerMove) { e: web.uievents.PointerEvent ->
        // find the closest value (aka step) to the event location
        val updateValue = pixelToStep.getValue(if (vertical) e.clientY.toDouble() else e.pageX)

        // generate a "candidate" set of values - a suggestion of what to do
        val nextHandles = getUpdatedHandles(handles, activeHandleID, updateValue, reversed)

        // submit the candidate values
        submitUpdate(nextHandles, true)
        props.onSlideEnd?.invoke(nextHandles.map { it.value }.toTypedArray(), jso { this.activeHandleID = activeHandleID })
        activeHandleID = ""

        val sliderEl = slider.current!!
        sliderEl.releasePointerCapture(e.pointerId)
        sliderEl.removeEventListener(web.uievents.PointerEvent.POINTER_MOVE, handlePointerMove)
        sliderEl.removeEventListener(web.uievents.PointerEvent.POINTER_UP, handlePointerMove)
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

        submitUpdate(nextHandles, true)
    }

    val handleRailAndTrackClicks by handler(handles, pixelToStep, vertical, reversed) { e: PointerEvent<*> ->
        // double check the dimensions of the slider
        pixelToStep.setDomain(getSliderDomain(slider.current, vertical))

        // find the closest value (aka step) to the event location
        val updateValue = pixelToStep.getValue(if (vertical) e.clientY else e.pageX)

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
        val nextHandles = getUpdatedHandles(handles, updateKey, updateValue, reversed)

        // submit the candidate values
        activeHandleID = updateKey
        // TODO: on state change:
        submitUpdate(nextHandles, true)
    }

    val handlePointerDown by handler<EmitPointer>(handles, handlePointerMove, handlePointerUp, handleRailAndTrackClicks) { e: PointerEvent<*>, location: String, handleID: String? ->
//        if (!isTouch) {
//            e.preventDefault()
//        }

        e.stopPropagation()

        val sliderEl = slider.current!!
        sliderEl.setPointerCapture(e.pointerId)
        sliderEl.addEventListener(web.uievents.PointerEvent.POINTER_MOVE, handlePointerMove)
        sliderEl.addEventListener(web.uievents.PointerEvent.POINTER_UP, handlePointerUp, jso { once = true })

        if (handleID != null) {
            handles.firstOrNull { it.key == handleID }
                ?: error("Cannot find handle with id $handleID.")

            activeHandleID = handleID
            props.onSlideStart?.invoke(
                handles.map { it.value }.toTypedArray(),
                jso { this.activeHandleID = handleID }
            )
        } else {
            activeHandleID = ""
            handleRailAndTrackClicks(e)
        }
    }


    val getEventData by handler(pixelToStep, vertical, valueToPerc) { e: PointerEvent<*> ->
        // double check the dimensions of the slider
        pixelToStep.setDomain(getSliderDomain(slider.current, vertical))

        val value = pixelToStep.getValue(if (vertical) e.clientY else e.pageX)

        jso<EventData> {
            this.value = value
            this.percent = valueToPerc.getValue(value)
        }
    }

    onMount(slider, props.vertical) {
        pixelToStep.domain = getSliderDomain(slider.current, props.vertical == true)
        withCleanup {
            document.removeEventListener(web.uievents.PointerEvent.POINTER_MOVE, handlePointerMove)
            document.removeEventListener(web.uievents.PointerEvent.POINTER_UP, handlePointerUp)
        }
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
                this.activeHandleID = activeHandleID
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
