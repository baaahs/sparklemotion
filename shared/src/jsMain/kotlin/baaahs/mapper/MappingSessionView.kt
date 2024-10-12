package baaahs.mapper

import baaahs.ui.and
import baaahs.ui.asTextNode
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import kotlinx.css.pct
import kotlinx.css.width
import kotlinx.html.tabIndex
import mui.material.Tab
import mui.material.Tabs
import mui.material.TabsVariant
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.*
import react.useContext
import styled.inlineStyles
import web.html.HTMLElement
import kotlin.math.max
import kotlin.math.min

private val MappingSessionView = xComponent<MappingSessionProps>("MappingSession") { props ->
    observe(props.mapper)
    val appContext = useContext(mapperAppContext)
    val styles = appContext.allStyles.mapper

    var selectedEntity by state { props.session.surfaces.firstOrNull() }
    val selectedPixelIndex = props.mapper.selectedPixelIndex
    val pixelData = selectedPixelIndex?.let { i ->
        val pixels = selectedEntity?.pixels
        if (pixels != null && i >= 0 && i < pixels.size) pixels[i] else null
    }

    val handleSelectEntityPixel by handler(props.mapper) { entityName: String?, index: Int? ->
        props.mapper.selectEntityPixel(entityName, index)
    }

    val handleSelectEntity by syntheticEventHandler<MappingSession.SurfaceData>(handleSelectEntityPixel) { _, entity ->
        selectedEntity = entity
        handleSelectEntityPixel(selectedEntity?.entityName, null)
    }

    val handlePixelClick by mouseEventHandler(handleSelectEntityPixel, pixelData) { e ->
        (e.target as? HTMLElement)?.let {
            val i = it.dataset["pixelIndex"]?.toIntOrNull()
            handleSelectEntityPixel(selectedEntity?.entityName, i)
        }
    }

    val handleKeyDown by keyboardEventHandler(selectedPixelIndex, handleSelectEntityPixel) { e ->
        when (e.key) {
            "ArrowLeft" -> {
                selectedPixelIndex?.let { i ->
                    val newIndex = max(i - 1, 0)
                    handleSelectEntityPixel(selectedEntity?.entityName, newIndex)
                }
                e.stopPropagation()
                e.preventDefault()
            }
            "ArrowRight" -> {
                selectedPixelIndex?.let { i ->
                    val newIndex = min(i + 1, selectedEntity!!.myPixelCount - 1)
                    handleSelectEntityPixel(selectedEntity?.entityName, newIndex)
                }
                e.stopPropagation()
                e.preventDefault()
            }
        }
    }

    div(+styles.sessionInfo) {
        attrs.tabIndex = "-1" // So we can receive key events.
        attrs.onKeyDown = handleKeyDown

        Tabs {
            attrs.variant = TabsVariant.scrollable
            attrs.value = selectedEntity
            attrs.onChange = handleSelectEntity

            for (entity in props.session.surfaces) {
                Tab {
                    attrs.label = entity.entityName.asTextNode()
                    attrs.value = entity
                }
            }
        }

        selectedEntity?.let { surface ->
            table {
                inlineStyles { width = 100.pct }
                thead {
                    tr {
                        th { +"Pixels" }
                        th { +"Attempted" }
                        th { +"Mapped" }
                    }

                    tr {
                        td { +surface.pixelCount.toString() }
                        td { +(surface.pixels?.count { it != null } ?: 0).toString() }
                        td { +(surface.pixels?.filterNotNull()?.count { it.modelPosition != null } ?: 0).toString() }
                    }
                }
            }

            div(+styles.pixels) {
                val pixelCount = surface.myPixelCount
                for (i in 0 until pixelCount) {
                    val pixel = surface.pixels?.get(i)
                    val twoLogNMetadata = pixel?.metadata as? TwoLogNMappingStrategy.TwoLogNPixelMetadata
                    div(
                        +when {
                            pixel == null -> styles.skippedPixel
                            pixel.modelPosition == null -> styles.unmappedPixel
                            twoLogNMetadata?.singleImage != null -> styles.individuallyMappedPixel
                            twoLogNMetadata != null -> styles.twoLogNMappedPixel
                            else -> styles.individuallyMappedPixel
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
                    if (pixelData != null) {
                        pixelData.modelPosition?.let { v ->
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

private val MappingSession.SurfaceData.myPixelCount get() = pixelCount ?: pixels?.size ?: 0

external interface MappingSessionProps : Props {
    var name: String
    var session: MappingSession
    var mapper: JsMapper
}

fun RBuilder.mappingSession(handler: RHandler<MappingSessionProps>) =
    child(MappingSessionView, handler = handler)