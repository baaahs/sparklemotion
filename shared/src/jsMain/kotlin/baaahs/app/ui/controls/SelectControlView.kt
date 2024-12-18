package baaahs.app.ui.controls

import baaahs.Gadget
import baaahs.control.OpenSelectControl
import baaahs.show.live.ControlProps
import baaahs.ui.*
import baaahs.util.useResizeListener
import mui.material.ToggleButton
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import web.html.HTMLDivElement
import web.html.HTMLElement

private val SelectControlView = xComponent<SelectProps>("SelectControl") { props ->
    val selectControl = props.selectControl
    val options = selectControl.select.options
    var selectedIndex by state<Int> { selectControl.select.selectionIndex }

    val handleClick by eventHandler(props.selectControl) {
        val newlySelectedIndex = it.currentTarget?.value?.toIntOrNull() ?: 0
        selectControl.select.selectionIndex = newlySelectedIndex
        selectedIndex = newlySelectedIndex
    }

    val containerRef = ref<HTMLElement>()
    val titleDivRef = List(options.size) { ref<HTMLDivElement>() }
    useResizeListener(containerRef) { _, _ ->
        titleDivRef.forEach { it.current?.fitText() }
    }

    val gadgetListener by handler { _: Gadget ->
        selectedIndex = selectControl.select.selectionIndex
    }
    onMount(gadgetListener, selectControl) {
        selectControl.select.listen(gadgetListener)
        withCleanup { selectControl.select.unlisten(gadgetListener) }
    }

    div(+Styles.controlRoot and Styles.controlButton) {
        ref = containerRef

        options.forEachIndexed { index, (_, title) ->
            ToggleButton {
                attrs.value = index
                // Yep, for some reason you need to set it directly or it doesn't work.
                attrs.selected = index == selectControl.select.selectionIndex
                attrs.onClick = handleClick.withTMouseEvent()

                div {
                    ref = titleDivRef[index]
                    +title
                }
            }
        }
    }
}

external interface SelectProps : Props {
    var controlProps: ControlProps
    var selectControl: OpenSelectControl
}

fun RBuilder.selectControl(handler: RHandler<SelectProps>) =
    child(SelectControlView, handler = handler)