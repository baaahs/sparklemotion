package baaahs.mapper

import baaahs.MediaDevices
import baaahs.app.ui.editor.betterSelect
import baaahs.app.ui.editor.numberFieldEditor
import baaahs.device.PixelFormat
import baaahs.model.Model
import baaahs.ui.*
import baaahs.ui.components.palette
import baaahs.util.useResizeListener
import external.react_draggable.Draggable
import kotlinx.css.*
import kotlinx.html.tabIndex
import materialui.icon
import mui.icons.material.*
import mui.material.Button
import mui.material.FormControlLabel
import mui.material.Switch
import react.*
import react.dom.canvas
import react.dom.div
import react.dom.img
import styled.inlineStyles
import web.html.HTMLCanvasElement
import web.html.HTMLElement
import web.html.HTMLImageElement
import web.uievents.KeyboardEvent

val MapperAppView = xComponent<MapperAppViewProps>("baaahs.mapper.MapperAppView") { props ->
    val appContext = useContext(mapperAppContext)
    val styles = appContext.allStyles.mapper

    val ui = memo(props.mapperBuilder) { props.mapperBuilder.build() }
    observe(ui)
    val uiActions = memo(ui) { MemoizedJsMapper(ui) }

    val screenRef = useRef<HTMLElement>(null)
    val ui2dCanvasRef = useRef<HTMLCanvasElement>(null)
    val ui3dDivRef = useRef<HTMLElement>(null)
    val savedImageRef = useRef<HTMLImageElement>(null)
    val baseCanvasRef = useRef<HTMLCanvasElement>(null)
    val diffCanvasRef = useRef<HTMLCanvasElement>(null)
    val snapshotCanvasRef = useRef<HTMLCanvasElement>(null)
    val panelMaskCanvasRef = useRef<HTMLCanvasElement>(null)
    val perfStatsRef = useRef<HTMLElement>(null)

    val handleMappingEnabledChange by syntheticEventHandler<Boolean> { _, value ->
        ui.mappingEnabled = value
        forceRender()
    }

    val shiftDown = ref(false)

    val mappingSessionDraggableRef = ref<HTMLElement>()

    // init:
    onMount {
        ui.onMount(
            ui2dCanvasRef.current!!, ui3dDivRef.current!!, snapshotCanvasRef.current!!,
            baseCanvasRef.current!!, diffCanvasRef.current!!, panelMaskCanvasRef.current!!,
            perfStatsRef.current!!,
            screenRef.current!!.offsetWidth, screenRef.current!!.offsetHeight,
            savedImageRef.current!!
        )

        val handler = appContext.keyboard.handle { keypress: Keypress, event: KeyboardEvent ->
            shiftDown.current = keypress.shiftKey
            uiActions.keyHandler(keypress, event)
        }

        withCleanup {
            handler.remove()
            ui.onUnmount()

        }
    }

    ui.setSizes()

    val handleChangeEntity by handler(ui.mappingController) { entity: Model.Entity? ->
        ui.mappingController?.guessedEntity = entity
        forceRender()
    }

    val handlePixelCountChange by handler(ui.mappingController) { pixelCount: Int? ->
        ui.mappingController?.expectedPixelCount = pixelCount
        forceRender()
    }

    val handlePixelFormatChange by handler(ui.mappingController) { pixelFormat: PixelFormat? ->
        ui.mappingController?.pixelFormat = pixelFormat
        ui.mappingController?.shadeSolidColor()
        forceRender()
    }

    val handleLoadMappingSession by handler(uiActions.loadMappingSession) { name: String? ->
        uiActions.loadMappingSession(name)
    }

    val handleLoadImage by handler(uiActions.loadMappingSession) { name: String? ->
        uiActions.loadImage(name, true)
    }

    var findingLastPixel by state { false }
    val handleFindLastPixel by mouseEventHandler { findingLastPixel = true }
    val handleFindLastPixelCancel by handler { findingLastPixel = false }

    useResizeListener(screenRef) { _, _ ->
        screenRef.current?.let { el ->
            ui.onResize(el.offsetWidth, el.offsetHeight)
        }
    }

    fun moveIsFine(): Boolean = shiftDown.current == true

    div(+styles.screen) {
        ref = screenRef
        attrs.tabIndex = "-1" // So we can receive key events.

        palette {
            attrs.title = "Mapping Tools"
            attrs.initialWidth = 240
            attrs.initialHeight = 500

            div(+styles.controls) {
                FormControlLabel {
                    attrs.control = buildElement {
                        Switch {
                            attrs.checked = ui.mappingEnabled
                            attrs.onChange = handleMappingEnabledChange
                        }
                    }
                    attrs.label = "Mapping".asTextNode()
                }

                betterSelect<MediaDevices.Device?> {
                    attrs.label = "Camera:"
                    attrs.values = listOf(null) + ui.devices
                    attrs.renderValueOption = { device, _ -> buildElement { +(device?.label ?: "None") } }
                    attrs.onChange = uiActions.changedCamera
                    attrs.value = ui.selectedDevice
                }

                if (ui.mappingEnabled) {
                    betterSelect<MappingStrategy> {
                        attrs.label = "Mapping Strategy:"
                        attrs.values = MappingStrategy.options
                        attrs.renderValueOption = { mappingStrategy, _ -> buildElement { +mappingStrategy.title } }
                        attrs.onChange = uiActions.changedMappingStrategy
                        attrs.value = ui.mappingStrategy
                    }

                    div(+styles.controlsRow) {
                        Button {
                            attrs.disabled = !ui.playButtonEnabled
                            attrs.onClick = uiActions.clickedPlay.withMouseEvent()
                            PlayArrow {}
                        }
                        Button {
                            attrs.disabled = !ui.pauseButtonEnabled
                            attrs.onClick = uiActions.clickedPause.withMouseEvent()
                            Pause {}
                        }
                        Button {
                            attrs.disabled = ui.redoFn != null
                            attrs.onClick = uiActions.clickedRedo.withMouseEvent()
                            Redo {}
                        }
                        Button {
                            attrs.onClick = uiActions.clickedStop.withMouseEvent()
                            Stop {}
                        }
                        Button {
                            attrs.onClick = uiActions.clickedGoToSurface.withMouseEvent()
                            Search {}
                        }
                    }
                }

                ui.mappingController?.let { mappingController ->
                    betterSelect<Model.Entity?> {
                        attrs.label = "Entity:"
                        attrs.values = ui.entitiesByName.values.toList().map<_, Model.Entity?>{ it }.plus(null)
                        attrs.renderValueOption = { entity, _ -> buildElement { +(entity?.name ?: "None" ) } }
                        attrs.value = mappingController.guessedEntity
                        attrs.onChange = handleChangeEntity
                    }

                    div {
                        inlineStyles {
                            display = Display.grid
                            gridTemplateColumns = GridTemplateColumns(50.pct, 50.pct)
                        }

                        if (!findingLastPixel) {
                            numberFieldEditor<Int?> {
                                attrs.label = "Pixel Count"
                                attrs.isInteger = true
                                attrs.isNullable = true
                                attrs.getValue = { mappingController.expectedPixelCount }
                                attrs.setValue = handlePixelCountChange
                            }

                            Button {
                                attrs.onClick = handleFindLastPixel
                                +"Find Last Pixel"
                            }
                        } else {
                            findLastPixel {
                                attrs.mapper = ui
                                attrs.onFoundPixel = handlePixelCountChange
                                attrs.onCancel = handleFindLastPixelCancel
                                attrs.maxPossiblePixel = 2048
                            }
                        }

                        betterSelect<PixelFormat?> {
                            attrs.label = "Pixel Format"
                            attrs.values = listOf(null) + PixelFormat.entries
                            attrs.renderValueOption = { it, _ -> (it?.name ?: "Default").asTextNode() }
                            attrs.value = mappingController.pixelFormat
                            attrs.onChange = handlePixelFormatChange
                        }
                    }
                }

                if (!ui.mappingEnabled) {
                    betterSelect<String?> {
                        attrs.label = "Load Session:"
                        attrs.values = listOf(null) + ui.sessions.map { it }.sorted()
                        attrs.renderValueOption = { name, _ -> buildElement { +(name ?: "None" ) } }
                        attrs.value = ui.selectedMappingSessionName
                        attrs.onChange = handleLoadMappingSession
                    }
                }

                if (!ui.mappingEnabled) {
                    ui.selectedMappingSession?.let { session ->
                        mappingSession {
                            attrs.name = ui.selectedMappingSessionName ?: "unknown session!?"
                            attrs.session = session
                            attrs.mapper = ui
                        }

                        betterSelect<String?> {
                            attrs.label = "Load Image:"
                            attrs.values = listOf(null) + ui.images.map { it }
                            attrs.renderValueOption = { name, _ -> buildElement { +(name ?: "None") } }
                            attrs.value = ui.selectedImageName
                            attrs.onChange = handleLoadImage
                        }
                    }
                }
            }
        }

        canvas(classes = +styles.mapping2dCanvas) {
            ref = ui2dCanvasRef
        }

        div(+styles.mapping3dContainer) {
            ref = ui3dDivRef
        }

        div(+styles.snapshotDiv) {
            div(+styles.thumbnailTitle) { +"Snapshot" }
            canvas { ref = snapshotCanvasRef }
        }

        div(+styles.baseDiv) {
            div(+styles.thumbnailTitle) { +"Base" }
            canvas { ref = baseCanvasRef }
        }

        div(+styles.diffDiv) {
            div(+styles.thumbnailTitle) { +"Diff" }
            canvas { ref = diffCanvasRef }
            img(classes = +styles.savedImage) { ref = savedImageRef }
        }

        div(+styles.panelMaskDiv) {
            div(+styles.thumbnailTitle) { +"Panel Mask" }
            canvas { ref = panelMaskCanvasRef }
        }

        statusBar {
            attrs.mapperStatus = ui.mapperStatus
        }

        div(+styles.perfStats) {
            ref = perfStatsRef
        }


        Draggable {
            attrs.nodeRef = mappingSessionDraggableRef
            val styleForDragHandle = "MappingSessionDragHandle"
            attrs.handle = ".$styleForDragHandle"

            div(+styles.threeDControls) {
                ref = mappingSessionDraggableRef
                div(+baaahs.app.ui.Styles.dragHandle and styleForDragHandle) {
                    icon(DragIndicator)
                }

                div(+styles.controls) {
                    div(+styles.controlsRow) {
                        div {
                            Button {
                                attrs.title = "Zoom Out (-)"
                                attrs.onClick = { ui.adjustCameraZoom(zoomIn = false, fine = moveIsFine()) }
                                ZoomOut {}
                            }
                            Button {
                                attrs.title = "Zoom In (+)"
                                attrs.onClick = { ui.adjustCameraZoom(zoomIn = true, fine = moveIsFine()) }
                                ZoomIn {}
                            }
                        }

                        Button {
                            attrs.title = "Left (◀)"
                            attrs.onClick = { ui.adjustCameraX(moveRight = false, fine = moveIsFine()) }
                            ArrowBack {}
                        }
                        div {
                            Button {
                                attrs.title = "Up (▲)"
                                attrs.onClick = { ui.adjustCameraY(moveUp = true, fine = moveIsFine()) }
                                ArrowUpward {}
                            }
                            Button {
                                attrs.title = "Down (▼)"
                                attrs.onClick = { ui.adjustCameraY(moveUp = false, fine = moveIsFine()) }
                                ArrowDownward {}
                            }
                        }
                        Button {
                            attrs.title = "Right (▶)"
                            attrs.onClick = { ui.adjustCameraX(moveRight = true, fine = moveIsFine()) }
                            ArrowForward {}
                        }

                        div {
                            Button {
                                attrs.title = "Rotate Counter-clockwise (Q)"
                                attrs.onClick = { ui.adjustCameraRotation(clockwise = false, fine = moveIsFine()) }
                                RotateLeft {}
                            }
                            Button {
                                attrs.title = "Rotate Clockwise (W)"
                                attrs.onClick = { ui.adjustCameraRotation(clockwise = true, fine = moveIsFine()) }
                                RotateRight {}
                            }
                        }
                    }
                }
            }
        }
    }
}

external interface MapperAppViewProps : Props {
    var mapperBuilder: JsMapperBuilder
}

fun RBuilder.mapperApp(handler: RHandler<MapperAppViewProps>) =
    child(MapperAppView, handler = handler)
