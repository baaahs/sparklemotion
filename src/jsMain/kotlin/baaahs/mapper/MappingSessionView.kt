package baaahs.mapper

import baaahs.app.ui.editor.betterSelect
import baaahs.ui.and
import baaahs.ui.asTextNode
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import external.react_draggable.Draggable
import materialui.icon
import org.w3c.dom.HTMLElement
import org.w3c.dom.get
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.*
import react.useContext

private val MappingSessionView = xComponent<MappingSessionProps>("MappingSession") { props ->
    val appContext = useContext(mapperAppContext)
    val styles = appContext.allStyles.mapper

    var selectedSurface by state<MappingSession.SurfaceData?> { null }
    var selectedPixelIndex by state<Int?> { null }

    val handleSelectSurface by handler { surface: MappingSession.SurfaceData? ->
        selectedSurface = surface
        selectedPixelIndex = null
        props.onSelectEntityPixel?.invoke(selectedSurface?.entityName, null)
        Unit
    }

    val handlePixelClick by mouseEventHandler(props.onSelectEntityPixel) { e ->
        (e.target as? HTMLElement)?.let {
            val i = it.dataset["pixelIndex"]?.toIntOrNull()
            selectedPixelIndex = i
            props.onSelectEntityPixel?.invoke(selectedSurface?.entityName, i)
        }
    }

    Draggable {
        val styleForDragHandle = "MappingSessionDragHandle"
        attrs.handle = ".$styleForDragHandle"

        div(+styles.sessionInfo) {
            div(+baaahs.app.ui.Styles.dragHandle and styleForDragHandle) {
                icon(mui.icons.material.DragIndicator)
            }

            header { +props.name }

            betterSelect<MappingSession.SurfaceData?> {
                attrs.values = listOf(null) + props.session.surfaces
                attrs.renderValueOption = { (it?.entityName ?: "-").asTextNode() }
                attrs.onChange = handleSelectSurface
            }

            selectedSurface?.let { surface ->
                ul {
                    li { +"Pixel Count: ${surface.pixelCount}" }
                    li { +"Pixels Attempted: ${surface.pixels?.count { it != null } ?: 0}" }
                    li { +"Pixels Mapped: ${surface.pixels?.filterNotNull()?.count { it.modelPosition != null } ?: 0}" }
                }

                div(+styles.pixels) {
                    val maxPixel = surface.pixelCount ?: surface.pixels?.size ?: 0
                    for (i in 0 until maxPixel) {
                        val pixel = surface.pixels?.get(i)
                        div(
                            +when {
                                pixel == null -> styles.skippedPixel
                                pixel.modelPosition == null -> styles.unmappedPixel
                                else -> styles.mappedPixel
                            } and if (i == selectedPixelIndex) styles.selectedPixel else null
                        ) {
                            attrs["data-pixel-index"] = i.toString()
                            attrs.onClick = handlePixelClick
                        }
                    }
                }

                div {
                    +"Selected Pixel: ${selectedPixelIndex ?: "None"}"
                    div {
                        selectedPixelIndex?.let {
                            surface.pixels?.get(it)?.modelPosition?.let { v ->
                                div { b { +"x: " }; +v.x.toString() }
                                div { b { +"y: " }; +v.y.toString() }
                                div { b { +"z: " }; +v.z.toString() }
                            }
                        }
                    }
                }
            }
        }
    }
}

external interface MappingSessionProps : Props {
    var name: String
    var session: MappingSession
    var onSelectEntityPixel: ((entityName: String?, pixelIndex: Int?) -> Unit)?
}

fun RBuilder.mappingSession(handler: RHandler<MappingSessionProps>) =
    child(MappingSessionView, handler = handler)