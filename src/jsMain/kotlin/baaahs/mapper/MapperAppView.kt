package baaahs.mapper

import baaahs.MediaDevices
import baaahs.app.ui.editor.betterSelect
import baaahs.app.ui.editor.textFieldEditor
import baaahs.device.PixelFormat
import baaahs.mapper.twologn.twoLogNSlices
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
import web.html.InputType
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

    val handlePixelCountChange by handler(ui.mappingController) { pixelCount: String ->
        ui.mappingController?.expectedPixelCount = pixelCount.toIntOrNull()
        forceRender()
    }

    val handlePixelFormatChange by handler(ui.mappingController) { pixelFormat: PixelFormat? ->
        ui.mappingController?.pixelFormat = pixelFormat
        forceRender()
    }

    val handleLoadMappingSession by handler(uiActions.loadMappingSession) { name: String? ->
        uiActions.loadMappingSession(name)
    }

    val handleLoadImage by handler(uiActions.loadMappingSession) { name: String? ->
        uiActions.loadImage(name, true)
    }

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
                    attrs.renderValueOption = { device -> buildElement { +(device?.label ?: "None") } }
                    attrs.onChange = uiActions.changedCamera
                    attrs.value = ui.selectedDevice
                }

                if (ui.mappingEnabled) {
                    betterSelect<MappingStrategy> {
                        attrs.label = "Mapping Strategy:"
                        attrs.values = MappingStrategy.options
                        attrs.renderValueOption = { mappingStrategy -> buildElement { +mappingStrategy.title } }
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
                        attrs.renderValueOption = { entity -> buildElement { +(entity?.name ?: "None" ) } }
                        attrs.value = mappingController.guessedEntity
                        attrs.onChange = handleChangeEntity
                    }

                    div {
                        inlineStyles {
                            display = Display.grid
                            gridTemplateColumns = GridTemplateColumns(50.pct, 50.pct)
                        }

                        textFieldEditor {
                            attrs.type = InputType.number
                            attrs.label = "Pixel Count"
                            attrs.fullWidth = false
                            attrs.getValue = { mappingController.expectedPixelCount?.toString() ?: "" }
                            attrs.setValue = handlePixelCountChange
                            attrs.onChange = {}
                        }

                        betterSelect<PixelFormat?> {
                            attrs.label = "Pixel Format"
                            attrs.values = listOf(null) + PixelFormat.entries
                            attrs.renderValueOption = { (it?.name ?: "Default").asTextNode() }
                            attrs.value = mappingController.pixelFormat
                            attrs.onChange = handlePixelFormatChange
                        }
                    }
                }

                if (!ui.mappingEnabled) {
                    betterSelect<String?> {
                        attrs.label = "Load Session:"
                        attrs.values = listOf(null) + ui.sessions.map { it }
                        attrs.renderValueOption = { name -> buildElement { +(name ?: "None" ) } }
                        attrs.value = ui.selectedMappingSessionName
                        attrs.onChange = handleLoadMappingSession
                    }
                }

                ui.selectedMappingSession?.let { session ->
                    mappingSession {
                        attrs.name = ui.selectedMappingSessionName ?: "unknown session!?"
                        attrs.session = session
                        attrs.mapper = ui
                    }

                    betterSelect<String?> {
                        attrs.label = "Load Image:"
                        attrs.values = listOf(null) + ui.images.map { it }
                        attrs.renderValueOption = { name -> buildElement { +(name ?: "None" ) } }
                        attrs.value = ui.selectedImageName
                        attrs.onChange = handleLoadImage
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

        ui.selectedMappingSession?.let { session ->
            val metadata = session.metadata
            if (metadata is TwoLogNMappingStrategy.TwoLogNSessionMetadata) {
                Draggable {
                    val styleForDragHandle = "MappingSessionDragHandleTwoN"
                    attrs.handle = ".$styleForDragHandle"

                    div(+styles.twoLogNMasksPalette) {
                        div(+baaahs.app.ui.Styles.dragHandle and styleForDragHandle) {
                            icon(mui.icons.material.DragIndicator)
                        }

                        twoLogNSlices {
                            attrs.mapper = ui
                            attrs.sessionMetadata = metadata
                        }
                    }
                }
            }
        }

        Draggable {
            val styleForDragHandle = "MappingSessionDragHandle"
            attrs.handle = ".$styleForDragHandle"

            div(+styles.threeDControls) {
                div(+baaahs.app.ui.Styles.dragHandle and styleForDragHandle) {
                    icon(mui.icons.material.DragIndicator)
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
