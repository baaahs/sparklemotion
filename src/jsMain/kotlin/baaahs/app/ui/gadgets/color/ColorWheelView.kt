package baaahs.app.ui.gadgets.color

import baaahs.Color
import baaahs.geom.Vector2F
import baaahs.ui.*
import baaahs.util.useResizeListener
import dom.html.HTMLCanvasElement
import dom.html.HTMLElement
import external.react_draggable.Draggable
import external.react_draggable.DraggableBounds
import external.react_draggable.DraggableData
import kotlinx.css.*
import kotlinx.js.jso
import mui.material.Button
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.*
import react.dom.events.MouseEvent
import react.useRef
import styled.inlineStyles
import web.timers.requestAnimationFrame
import kotlin.math.min

private const val pickerRadius = 12

val ColorWheelView = xComponent<ColorWheelProps>("ColorWheelView") { props ->
    var radius by state { 0 }
    var harmonyMode by state { HarmonyMode.triad }
    var colors by state { Array(1) { Color.WHITE } }
    var selectedIndex by state<Int?> { null }
    var grabbingIndex by state<Int?> { null }
    val mouseDraggingState = useRef(false)

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

    val checkRadius = { width: Int, height: Int ->
        val newRadius = min(width, height) / 2
        if (radius != newRadius) {
            radius = newRadius
        }
    }
    onMount {
        val el = containerDiv.current!!
        checkRadius(el.clientWidth, el.clientHeight)
    }
    useResizeListener(containerDiv, checkRadius)

    fun handleColorChange() {
        props.onChange(colors)
    }


    requestAnimationFrame {
        colorWheel?.drawWheel()
    }

    div(+ColorWheelStyles.root) {
        ref = containerDiv

        div(+ColorWheelStyles.canvasWrapper) {
            inlineStyles {
                width = (radius * 2).px
                height = (radius * 2).px
            }

            fun updateColors(e: MouseEvent<*, *>, index: Int) {
                val target = e.target as HTMLElement
                val bounds = target.getBoundingClientRect()
                val clickX = e.clientX - bounds.left - radius
                val clickY = e.clientY - bounds.top - radius
                val xy = Vector2F(clickX.toFloat(), clickY.toFloat())
                colors = colorWheel!!.getUpdatedColors(xy, index)
            }

            canvas(+ColorWheelStyles.canvas) {
                ref = canvasEl

                if (mouseDraggingState.current == true) {
                    inlineStyles {
                        cursor = Cursor.grabbing
                    }
                }

                attrs.width = (radius * 2).toString()
                attrs.height = (radius * 2).toString()
                attrs.onMouseDown = { e ->
                    if (e.buttons == Events.ButtonMask.primary) {
                        mouseDraggingState.current = true
                        updateColors(e, selectedIndex ?: 0)
                        handleColorChange()
                        e.preventDefault()
                    }
                }
                attrs.onMouseMove = { e ->
                    if (mouseDraggingState.current == true) {
                        updateColors(e, selectedIndex ?: 0)
                        handleColorChange()
                    }
                }
                attrs.onMouseUp = { e ->
                    mouseDraggingState.current = false
                    updateColors(e, selectedIndex ?: 0)
                    handleColorChange()
                    e.preventDefault()
                }
            }
        }
        if (radius > 0) {
            colors.forEachIndexed { index, color ->
                val position = color.toPolar().toXy() * radius.toFloat()

                fun updateColors(data: DraggableData) {
                    val xy = Vector2F(data.x.toFloat() - radius, data.y.toFloat() - radius)
                    colors = colorWheel!!.getUpdatedColors(xy, index)
                }

                Draggable {
                    key = index.toString()
                    attrs.defaultClassName = +ColorWheelStyles.draggablePicker
                    attrs.defaultClassNameDragging = +ColorWheelStyles.dragging
                    attrs.position = jso {
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
                        disableScroll()
                        updateColors(data)
                        grabbingIndex = index
                        handleColorChange()
                        true
                    }
                    attrs.onStop = { _, data ->
                        enableScroll()
                        updateColors(data)
                        grabbingIndex = null
                        handleColorChange()
                        true
                    }
                    attrs.bounds = jso<DraggableBounds> {
                        top = 0
                        left = 0
                        right = radius * 2
                        bottom = radius * 2
                    }

                    div {
                        if (mouseDraggingState.current == true) {
                            inlineStyles {
                                pointerEvents = PointerEvents.none
                            }
                        }

                        var classes = +ColorWheelStyles.picker
                        if (index == grabbingIndex) classes += " " + ColorWheelStyles.grabbing.name
                        if (index == selectedIndex) classes += " " + ColorWheelStyles.selected.name
                        div(classes) {
                            inlineStyles {
                                width = (pickerRadius * 2).px
                                height = (pickerRadius * 2).px
                                backgroundColor = color.toCssColor()
                            }

                            attrs.onMouseDown = {
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
                    if (theHarmonyMode == harmonyMode) classes += " " + ColorWheelStyles.active.name

                    Button {
                        key = theHarmonyMode.name
                        attrs.onClick = {
                            harmonyMode = theHarmonyMode
                        }

                        +theHarmonyMode.name
                    }
                }
            }
        }
    }
}

private fun Color.toCssColor()
    = kotlinx.css.Color("rgb(${redI}, ${greenI}, ${blueI})")

external interface ColorWheelProps : Props {
    var colors: Array<Color>
    var onChange: (Array<Color>) -> Unit
    var isPalette: Boolean?
}

fun RBuilder.colorWheel(handler: RHandler<ColorWheelProps>) =
    child(ColorWheelView, handler = handler)