package external.react_compound_slider

import baaahs.util.Logger
import react.ReactNode
import react.dom.events.TouchEvent
import react.dom.events.UIEvent
import web.dom.Element
import kotlin.math.max
import kotlin.math.min

fun isRCSComponent(item: ReactNode): Boolean {
    if (!react.isValidElement(item)) return false
    val type = item.asDynamic().type
    val name = type?.displayName ?: type?.name ?: ""
    return (name == "Rail" ||
            name == "Handles" || name == "BetterHandles" ||
            name == "Ticks" ||
            name == "Tracks"
            )
}

fun getNextValue(
    curr: Double,
    step: Double,
    domain: Array<Double>,
    reversed: Boolean
): Double {
    val newVal = if (reversed) curr - step else curr + step
    return if (reversed) max(domain[0], newVal) else min(domain[1], newVal)
}

fun getPrevValue(
    curr: Double,
    step: Double,
    domain: Array<Double>,
    reversed: Boolean
): Double {
    val newVal = if (reversed) curr + step else curr - step
    return if (reversed) min(domain[1], newVal) else max(domain[0], newVal)
}

fun getHandles(
    values: Array<Double>,
    reversed: Boolean,
    valueToStep: DiscreteScale,
    warn: Boolean,
    logger: Logger
): Pair<Array<HandleItem>, Int> {
    var changes = 0
    val handles = values.mapIndexed { index, x ->
        val value = valueToStep.getValue(x)
        if (x != value) {
            changes += 1
            if (warn) {
                logger.warn { "Value $x is not a valid step value. Using $value instead." }
            }
        }
        handleItem("\$\$-$index", value)
    }
        .sortedBy { it.value }
        .let { if (reversed) it.reversed() else it }
        .toTypedArray()
    return handles to changes
}

fun getUpdatedHandles(
    handles: Array<HandleItem>,
    updateKey: String,
    updateValue: Double,
    reversed: Boolean = false
): Array<HandleItem> {
    val index = handles.indexOfFirst { item -> item.key == updateKey }

    if (index != -1) {
        val handleItem = handles[index]

        if (handleItem.value == updateValue) {
            return handles
        }

        return buildList {
            addAll(handles.slice(0 until index))
            add(handleItem(updateKey, updateValue))
            addAll(handles.slice(index + 1 until handles.size))
        }
            .sortedBy { it.value }
            .let { if (reversed) it.reversed() else it }
            .toTypedArray()
    }

    return handles;
}

fun getSliderDomain(slider: Element?, vertical: Boolean): Range {
    if (slider == null) {
        return arrayOf(0.0, 0.0)
    }

    val s = slider.getBoundingClientRect()

    val d0 = if (vertical) s.top else s.left
    val d1 = if (vertical) s.bottom else s.right

    return arrayOf(d0, d1)
}

fun isNotValidTouch(event: UIEvent<*, *>, touches: Array<TouchEvent<Element>>? = null): Boolean {
    return touches == null ||
            touches.size > 1 ||
            (event.asDynamic().toLowerCase() == "touchend" && touches.isNotEmpty())
}

fun isNotValidTouch(event: web.uievents.UIEvent, touches: Array<TouchEvent<Element>>? = null): Boolean {
    return touches == null ||
            touches.size > 1 ||
            (event.asDynamic().toLowerCase() == "touchend" && touches.isNotEmpty())
}

fun getTouchPosition(vertical: Boolean, e: web.uievents.TouchEvent): Double =
    if (vertical) e.touches[0].clientY else e.touches[0].pageX