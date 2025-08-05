package baaahs.mapper

import baaahs.imaging.Bitmap
import baaahs.imaging.CanvasBitmap
import baaahs.imaging.ImageBitmapImage
import baaahs.mapper.TwoLogNMappingStrategy.Slice
import baaahs.mapper.TwoLogNMappingStrategy.Slices
import baaahs.mapper.twologn.twoLogNSlices
import baaahs.ui.and
import baaahs.ui.asTextNode
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import baaahs.util.globalLaunch
import external.react_draggable.Draggable
import kotlinx.coroutines.CompletableDeferred
import kotlinx.css.pct
import kotlinx.css.width
import kotlinx.html.tabIndex
import materialui.icon
import mui.icons.material.DragIndicator
import mui.material.Tab
import mui.material.Tabs
import mui.material.TabsVariant
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.*
import react.useContext
import styled.inlineStyles
import web.events.EventHandler
import web.html.HTMLDivElement
import web.html.HTMLElement
import web.html.HTMLImageElement
import web.html.Image
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

    val metadata = props.session.metadata
    val logNMetaData = metadata as? TwoLogNMappingStrategy.TwoLogNSessionMetadata
    val loadingSlices = memo(metadata) {
        logNMetaData?.let { logNMetadata ->
            logNMetadata.sliceImageNames?.let { sliceImageNames -> LoadingSlices(sliceImageNames, props.mapper) }
        }
    }

    val pixelImgRef = ref<HTMLImageElement>()
    val brightSpotFinderRef = ref<HTMLDivElement>()
    val draggable2logNRef = ref<HTMLElement>()

    val handleSelectEntityPixel by handler(props.mapper) { entityName: String?, index: Int? ->
        if (loadingSlices != null) {
            if (index != null) {
                globalLaunch {
                    val slices = loadingSlices.deferredSlices.await()
                    val bitmap = slices.reconstructPixel(index)

                    val analysis = ImageProcessing.analyze(bitmap)
                    val changeRegion = analysis.detectChangeRegion(.9f)
                    brightSpotFinderRef.current!!.innerText = """
                        hasBrightSpots=${analysis.hasBrightSpots()}
                        changeRegion=${changeRegion.width}x${changeRegion.height} center==${changeRegion.centerX}x${changeRegion.centerY}
                        changedAmount=${changeRegion.changedAmount}
                    """.trimIndent()
                    brightSpotFinderRef.current!!.style.whiteSpace = "pre"

                    if (bitmap is CanvasBitmap) {
                        bitmap.withContext {
                            strokeStyle = "rgba(1, 0, 0, 0.75)"
                            rect(
                                changeRegion.x0 - 2.0, changeRegion.y0 - 2.0,
                                changeRegion.x1 + 2.0, changeRegion.y1 + 2.0
                            )
                        }
                    }

                    pixelImgRef.current!!.src = bitmap.toDataUrl()
                }
            } else {
                pixelImgRef.current!!.src = ""
            }
        }

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
                        attrs["title"] = "$i"
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

                if (loadingSlices != null) {
                    img {
                        ref = pixelImgRef
                        attrs.width = "400"
                    }

                    div {
                        ref = brightSpotFinderRef
                    }
                }
            }
        }
    }

    if (logNMetaData != null) {
        Draggable {
            attrs.nodeRef = draggable2logNRef
            val styleForDragHandle = "MappingSessionDragHandleTwoN"
            attrs.handle = ".$styleForDragHandle"

            div(+styles.twoLogNMasksPalette) {
                ref = draggable2logNRef
                div(+baaahs.app.ui.Styles.dragHandle and styleForDragHandle) {
                    icon(DragIndicator)
                }

                if (loadingSlices != null) {
                    twoLogNSlices {
                        attrs.loadingSlices = loadingSlices
                        attrs.mapper = props.mapper
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

class LoadingSlices(slices: List<List<String?>>, val mapper: JsMapper) {
    val loadingSlices = slices.map { sliceImageNames -> LoadingSlice(sliceImageNames, mapper) }
    val deferredSlices = CompletableDeferred<Slices>()

    init {
        globalLaunch {
            deferredSlices.complete(
                Slices(loadingSlices.map { it.deferredSlice.await() })
            )
            console.log("Complete slices for $slices")
        }
    }
}

class LoadingSlice(sliceImageNames: List<String?>, val mapper: JsMapper) {
    val firstHalf = sliceImageNames[0]?.let { LoadingImage(it, mapper) }
    val secondHalf = sliceImageNames[1]?.let { LoadingImage(it, mapper) }
    val halves get() = listOf(firstHalf, secondHalf)
    val deferredSlice = CompletableDeferred<Slice>()

    init {
        globalLaunch {
            val firstHalfBitmap = firstHalf?.deferredBitmap?.await()
            val secondHalfBitmap = secondHalf?.deferredBitmap?.await()
            if (firstHalfBitmap != null && secondHalfBitmap != null) {
                deferredSlice.complete(
                    Slice(
                        firstHalfBitmap, sliceImageNames[0],
                        secondHalfBitmap, sliceImageNames[1]
                    )
                )
                console.log("Complete slice for $sliceImageNames")
            }
        }
    }
}

class LoadingImage(val name: String, val mapper: JsMapper) {
    val deferredSrc = CompletableDeferred<String>()
    val deferredBitmap = CompletableDeferred<Bitmap>()

    init {
        globalLaunch {
            val imageSrc = mapper.getImageUrl(name)
            deferredSrc.complete(imageSrc)
            console.log("Complete imageSrc for $name")

            val img = Image()
            img.onload = EventHandler {
                globalLaunch {
                    val bitmap = ImageBitmapImage.fromImg(img).toBitmap()
                    deferredBitmap.complete(bitmap)
                    console.log("Complete bitmap for $name")
                }
            }
            img.src = imageSrc
//
//            val origBitmaps = deferredBitmaps.map { it.await() }
//
//            val slice = TwoLogNMappingStrategy.Slice.build(origBitmaps[0], null, origBitmaps[1], null)
//            overlapImg.current!!.src = slice.overlap.toDataUrl()
//            overlapBitmap = slice.overlap
        }
    }

//    private suspend fun load() {
//        img.src = imageSrc
//    }
}