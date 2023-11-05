package external.react_compound_slider

import baaahs.ui.xComponent
import js.core.jso
import react.RBuilder
import react.RHandler
import react.dom.div
import react.dom.events.KeyboardEvent
import react.dom.events.MouseEvent
import react.dom.events.TouchEvent
import react.dom.events.UIEvent
import web.dom.Element
import web.dom.document
import web.uievents.MOUSE_MOVE
import web.uievents.MOUSE_UP
import web.uievents.TOUCH_END
import web.uievents.TOUCH_MOVE
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


    val handleMouseMove by handler(handles, pixelToStep, vertical, reversed, submitUpdate) { e: web.uievents.MouseEvent ->
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

    val handleTouchMove by handler(handles, pixelToStep, vertical, reversed, submitUpdate) { e: web.uievents.TouchEvent ->
        if (isNotValidTouch(e)) return@handler

        // double check the dimensions of the slider
        pixelToStep.setDomain(getSliderDomain(slider.current, vertical))

        // find the closest value (aka step) to the event location
        val updateValue = pixelToStep.getValue(getTouchPosition(vertical, e))

        // generate a "candidate" set of values - a suggestion of what to do
        val nextHandles = getUpdatedHandles(handles, activeHandleID, updateValue, reversed)

        // submit the candidate values
        submitUpdate(nextHandles, false)
    }

    val handleMouseUp by handler(handles, handleMouseMove) { e: web.uievents.MouseEvent ->
        // find the closest value (aka step) to the event location
        val updateValue = pixelToStep.getValue(if (vertical) e.clientY.toDouble() else e.pageX)

        // generate a "candidate" set of values - a suggestion of what to do
        val nextHandles = getUpdatedHandles(handles, activeHandleID, updateValue, reversed)

        // submit the candidate values
        submitUpdate(nextHandles, true)
        props.onSlideEnd?.invoke(nextHandles.map { it.value }.toTypedArray(), jso { this.activeHandleID = activeHandleID })
        activeHandleID = ""

        document.removeEventListener(web.uievents.MouseEvent.MOUSE_MOVE, handleMouseMove)
    }

    val handleTouchEnd by handler(handles, handleTouchMove) { e: web.uievents.TouchEvent ->
        if (isNotValidTouch(e)) return@handler

        // find the closest value (aka step) to the event location
        val updateValue = pixelToStep.getValue(getTouchPosition(vertical, e))

        // generate a "candidate" set of values - a suggestion of what to do
        val nextHandles = getUpdatedHandles(handles, activeHandleID, updateValue, reversed)

        // submit the candidate values
        submitUpdate(nextHandles, true)
        props.onSlideEnd?.invoke(nextHandles.map { it.value }.toTypedArray(), jso { this.activeHandleID = activeHandleID })
        activeHandleID = ""

        document.removeEventListener(web.uievents.TouchEvent.TOUCH_MOVE, handleTouchMove)
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

    val addMouseEvents by handler(handleMouseMove, handleMouseUp) {
        document.addEventListener(web.uievents.MouseEvent.MOUSE_MOVE, handleMouseMove)
        document.addEventListener(web.uievents.MouseEvent.MOUSE_UP, handleMouseUp, jso { once = true })
    }

    val addTouchEvents by handler(handleTouchMove, handleTouchEnd) {
        document.addEventListener(web.uievents.TouchEvent.TOUCH_MOVE, handleTouchMove)
        document.addEventListener(web.uievents.TouchEvent.TOUCH_END, handleTouchEnd, jso { once = true })
    }

    val handleRailAndTrackClicks by handler(handles, pixelToStep, vertical, reversed, addMouseEvents, addTouchEvents) { e: UIEvent<*, *>, isTouch: Boolean ->
        val curr = handles

        // double check the dimensions of the slider
        pixelToStep.setDomain(getSliderDomain(slider.current, vertical))

        // find the closest value (aka step) to the event location
        val updateValue = if (isTouch) {
            pixelToStep.getValue(getTouchPosition(vertical, e as web.uievents.TouchEvent))
        } else {
            e as MouseEvent<*, *>
            pixelToStep.getValue(if (vertical) e.clientY else e.pageX)
        }

        // find the closest handle key
        var updateKey = ""
        var minDiff = Double.POSITIVE_INFINITY

        for (item in curr) {
            val diff = abs(item.value - updateValue)

            if (diff < minDiff) {
                updateKey = item.key
                minDiff = diff
            }
        }

        // generate a "candidate" set of values - a suggestion of what to do
        val nextHandles = getUpdatedHandles(curr, updateKey, updateValue, reversed)

        // submit the candidate values
        activeHandleID = updateKey
        // TODO: on state change:
        submitUpdate(nextHandles, true)
        if (isTouch) addTouchEvents() else addMouseEvents()
    }

    val handleStart by handler(handles, handleRailAndTrackClicks) { e: UIEvent<*, *>, handleID: String, isTouch: Boolean ->
        if (!isTouch) {
            e.preventDefault()
        }

        e.stopPropagation()

        val found = handles.any { it.key == handleID }

        if (found) {
            activeHandleID = handleID
            props.onSlideStart?.invoke(
                handles.map { it.value }.toTypedArray(),
                jso { this.activeHandleID = handleID }
            )
            if (isTouch) addTouchEvents() else addMouseEvents()
        } else {
            activeHandleID = ""
            handleRailAndTrackClicks(e, isTouch)
        }
    }

    val handleMouseDown by handler(handleStart) { e: MouseEvent<*, *>, handleID: String ->
        handleStart(e, handleID, false)
    }

    val handleTouchStart by handler(handleStart) { e: TouchEvent<*>, handleID: String ->
        if (isNotValidTouch(e)) return@handler

        handleStart(e, handleID, true)
    }


    val getEventData by handler(pixelToStep, vertical, valueToPerc) { e: UIEvent<*, *>, isTouch: Boolean ->
        // double check the dimensions of the slider
        pixelToStep.setDomain(getSliderDomain(slider.current, vertical))

        val value = if (isTouch /*&& e instanceof TouchEvent*/) {
            pixelToStep.getValue(getTouchPosition(vertical, e as web.uievents.TouchEvent))
        } else /*if (e instanceof MouseEvent)*/ {
            e as MouseEvent<*, *>
            pixelToStep.getValue(if (vertical) e.clientY else e.pageX)
        }

        jso<EventData> {
            this.value = value
            this.percent = valueToPerc.getValue(value)
        }
    }

    onMount(slider, props.vertical) {
        pixelToStep.domain = getSliderDomain(slider.current, props.vertical == true)
        withCleanup {
            document.removeEventListener(web.uievents.MouseEvent.MOUSE_MOVE, handleMouseMove)
            document.removeEventListener(web.uievents.MouseEvent.MOUSE_UP, handleMouseUp)
            document.removeEventListener(web.uievents.TouchEvent.TOUCH_MOVE, handleTouchMove)
            document.removeEventListener(web.uievents.TouchEvent.TOUCH_END, handleTouchEnd)
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
                this.emitMouse = if (props.disabled) null else handleMouseDown
                this.emitTouch = if (props.disabled) null else handleTouchStart
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
