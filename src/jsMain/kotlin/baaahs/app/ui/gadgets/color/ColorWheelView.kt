package baaahs.app.ui.gadgets.color

import baaahs.Color
import baaahs.app.ui.disableScroll
import baaahs.app.ui.enableScroll
import baaahs.geom.Vector2F
import baaahs.ui.unaryPlus
import baaahs.ui.withEvent
import baaahs.ui.xComponent
import baaahs.util.useResizeListener
import external.react_draggable.Draggable
import external.react_draggable.DraggableBounds
import external.react_draggable.DraggableData
import kotlinext.js.jsObject
import kotlinx.css.backgroundColor
import kotlinx.css.height
import kotlinx.css.px
import kotlinx.css.width
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onMouseDownFunction
import kotlinx.html.label
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLElement
import react.RBuilder
import react.RHandler
import react.RProps
import react.child
import react.dom.button
import react.dom.canvas
import react.dom.div
import styled.inlineStyles

private const val pickerRadius = 12

val ColorWheelView = xComponent<ColorWheelProps>("ColorWheelView") { props ->
    var radius by state { 0 }
    var harmonyMode by state { HarmonyMode.triad }
    var colors by state { Array(1) { Color.WHITE } }
    var selectedIndex by state<Int?> { null }
    var grabbingIndex by state<Int?> { null }

    val containerDiv = ref<HTMLElement>(null)
    val canvasEl = ref<HTMLCanvasElement>(null)
    val colorWheel = memo(canvasEl.current) {
        canvasEl.current?.let {
            ColorWheel(it, radius, harmonyMode, colors)
        }
    }
    colorWheel?.radius = radius
    colorWheel?.harmonyMode = harmonyMode
    colorWheel?.colors = colors

    val checkRadius = {
        val el = containerDiv.current!!
        val clientWidth = el.clientWidth
        val newRadius = clientWidth / 2
        if (radius != newRadius) {
            radius = newRadius
        }
    }
    onMount { checkRadius() }
    useResizeListener(containerDiv, checkRadius)

    fun handleColorChange() {
        props.onChange(colors)
    }


    baaahs.window.requestAnimationFrame {
        colorWheel?.drawWheel()
    }

    div(+ColorWheelStyles.root) {
        ref = containerDiv

        div(+ColorWheelStyles.canvasWrapper) {
            inlineStyles {
                width = (radius * 2).px
                height = (radius * 2).px
            }

            canvas(+ColorWheelStyles.canvas) {
                ref = canvasEl
                attrs.width = (radius * 2).toString()
                attrs.height = (radius * 2).toString()
                attrs.onMouseDownFunction = {
                    selectedIndex = -1
                }
            }
        }
        if (radius > 0) {
            colors.forEachIndexed { index, color ->
                val position = color.toXy(radius.toFloat())

                fun updateColors(data: DraggableData) {
                    val xy = Vector2F(data.x.toFloat() - radius, data.y.toFloat() - radius)
                    colors = colorWheel!!.getUpdatedColors(xy, index)
                }

                Draggable {
                    key = index.toString()
                    attrs.defaultClassName = +ColorWheelStyles.draggablePicker
                    attrs.defaultClassNameDragging = +ColorWheelStyles.dragging
                    attrs.position = jsObject {
                        x = position.x + radius
                        y = position.y + radius
                    }
                    attrs.onDrag = { _, data ->
                        updateColors(data)
                        selectedIndex = index
                        grabbingIndex = index
//                        throttledHandleColorChange()
                        handleColorChange()
                        true
                    }
                    attrs.onStart = { _, data ->
                        disableScroll.withEvent()
                        updateColors(data)
                        grabbingIndex = -1
                        true
                    }
                    attrs.onStop = { _, data ->
                        enableScroll()
                        updateColors(data)
                        grabbingIndex = -1
                        true
                    }
                    attrs.bounds = jsObject<DraggableBounds> {
                        top = 0
                        left = 0
                        right = radius * 2
                        bottom = radius * 2
                    }

                    div {
                        var classes = +ColorWheelStyles.picker
                        if (index == grabbingIndex) classes += " " + ColorWheelStyles.grabbing
                        if (index == selectedIndex) classes += " " + ColorWheelStyles.selected
                        div(classes) {
                            inlineStyles {
                                width = (pickerRadius * 2).px
                                height = (pickerRadius * 2).px
                                backgroundColor = color.toCssColor()
                            }

                            attrs.onMouseDownFunction = {
                                selectedIndex = index
                                grabbingIndex = index
                            }
                        }

                    }
                }
            }
        }

        if (props.isPalette == true) {
            div(+ColorWheelStyles.harmonyModes) {
                HarmonyMode.values().forEach { theHarmonyMode ->
                    var classes = +ColorWheelStyles.harmonyMode
                    if (theHarmonyMode == harmonyMode) classes += " " + ColorWheelStyles.active

                    button {
                        key = theHarmonyMode.name
                        attrs.onClickFunction = {
                            harmonyMode = theHarmonyMode
                        }

                        attrs.label { +theHarmonyMode.name }
                    }
                }
            }
        }
    }
}

private fun Color.toCssColor()
    = kotlinx.css.Color("rgb(${redI}, ${greenI}, ${blueI})")

external interface ColorWheelProps : RProps {
    var colors: Array<Color>
    var onChange: (Array<Color>) -> Unit
    var isPalette: Boolean?
}

fun RBuilder.colorWheel(handler: RHandler<ColorWheelProps>) =
    child(ColorWheelView, handler = handler)