package external.react_compound_slider

import baaahs.ui.Observable
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
    val sliderItems = memo(
        values, reversed, valueToStep, valueToPerc, props.onUpdate, props.onChange, props.warnOnChanges
    ) {
        var changes = 0
        values.entries.associate { (handleId, value) ->
            val adjustedValue = valueToStep.getValue(value)
            if (adjustedValue != value) {
                changes += 1
                if (props.warnOnChanges) {
                    logger.warn { "Value $value is not a valid step value. Using $adjustedValue instead." }
                }
            }
            handleId to BetterSliderItem(handleId, adjustedValue, valueToPerc.getValue(adjustedValue))
        }.also {
            if (changes > 0) {
                val updatedHandles = it.entries.associate { (k, v) -> k to v.value }
                props.onUpdate?.invoke(updatedHandles)
                props.onChange?.invoke(updatedHandles)
            }
        }
    }

    val sliders = memo(sliderItems) { sliderItems.values.toTypedArray<SliderItem>() }

    val slider = ref<Element>()

    val submitUpdate = callback(
        valueToStep, valueToPerc, sliderItems, props.onUpdate, props.onChange, props.onSlideEnd
    ) { newHandles: Array<HandleItem>, callOnChange: Boolean, callOnSlideEnd: Boolean ->
        val updatedHandles = newHandles.associate { updatedHandle ->
            sliderItems[updatedHandle.key]?.let { sliderItem ->
                sliderItem.value = updatedHandle.value
                sliderItem.percent = valueToPerc.getValue(updatedHandle.value)
                sliderItem.notifyChanged()
            }
            updatedHandle.key to updatedHandle.value
        }
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

    val updateSliderItems by handler(sliderItems, reversed) { updateKey: String, updateValue: Double ->
        sliderItems.map { (key, sliderItem) ->
            handleItem(key, if (key == updateKey) updateValue else sliderItem.value)
        }.toTypedArray()
    }

    val handlePointerMove by handler(getEventData, updateSliderItems, submitUpdate) { e: web.uievents.PointerEvent ->
        val updateValue = getEventValue(e)

        // generate a "candidate" set of values - a suggestion of what to do
        val nextHandles = updateSliderItems(activeHandleId, updateValue)

        // submit the candidate values
        submitUpdate(nextHandles, false, false)
        Unit
    }

    val handlePointerUp by handler(getEventValue, updateSliderItems, handlePointerMove) { e: web.uievents.PointerEvent ->
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

        val found = sliderItems[handleId]
            ?: return@handler

        val currVal = found.value
        var newVal = currVal

        if (validUpKeys.contains(key)) {
            newVal = getNextValue(currVal, step, domain, reversed)
        } else if (validDownKeys.contains(key)) {
            newVal = getPrevValue(currVal, step, domain, reversed)
        }
        val nextHandles = sliderItems.map { (k, v) ->
            handleItem(key, if (k == handleId) newVal else v.value)
        }.toTypedArray()

        submitUpdate(nextHandles, true, false)
    }

    val handleRailAndTrackClicks by handler(getEventValue, sliderItems, updateSliderItems) { e: PointerEvent<*> ->
        val updateValue = getEventValue(e.nativeEvent)

        // find the closest handle key
        var updateKey = ""
        var minDiff = Double.POSITIVE_INFINITY

        for ((key, item) in sliderItems) {
            val diff = abs(item.value - updateValue)

            if (diff < minDiff) {
                updateKey = key
                minDiff = diff
            }
        }

        // generate a "candidate" set of values - a suggestion of what to do
        val nextHandles = updateSliderItems(updateKey, updateValue)

        // submit the candidate values
        activeHandleId = updateKey
        submitUpdate(nextHandles, true, false)
    }

    val handlePointerDown by handler<EmitPointer>(
        sliderItems, handlePointerMove, handlePointerUp, handleRailAndTrackClicks, props.onSlideStart
    ) { e: PointerEvent<*>, _: Location, handleID: String? ->
        if (e.button != 0) return@handler

        e.stopPropagation()

        val sliderEl = slider.current!!
        sliderEl.setPointerCapture(e.pointerId)
        sliderEl.addEventListener(web.uievents.PointerEvent.POINTER_MOVE, handlePointerMove)
        sliderEl.addEventListener(web.uievents.PointerEvent.POINTER_UP, handlePointerUp, jso { once = true })

        if (handleID != null) {
            val sliderItem = sliderItems[handleID]
                ?: error("Cannot find handle with id $handleID.")

            pointerDownOffset.current = null
            pointerDownOffset.current = sliderItem.value - getEventValue(e.nativeEvent)
            activeHandleId = handleID
            props.onSlideStart?.invoke(
                sliderItems.entries.associate { (key, item) -> key to item.value },
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
    val children = react.Children.map(props.children) { child ->
        if (isRCSComponent(child)) {
            react.cloneElement(child.unsafeCast<react.ReactElement<RcsProps>>(), jso {
                this.scale = valueToPerc
                this.handles = sliders
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

class BetterSliderItem(
    override var id: String,
    override var value: Double,
    override var percent: Double
) : Observable(), SliderItem

fun RBuilder.betterSlider(handler: RHandler<SliderProps>) =
    child(BetterSlider, handler = handler)
