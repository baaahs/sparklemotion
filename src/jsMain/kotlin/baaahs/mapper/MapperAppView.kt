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
import mui.material.Button
import mui.material.FormControlLabel
import mui.material.Switch
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLImageElement
import org.w3c.dom.events.KeyboardEvent
import react.*
import react.dom.canvas
import react.dom.div
import react.dom.html.InputType
import react.dom.i
import react.dom.img
import styled.inlineStyles

val MapperAppView = xComponent<MapperAppViewProps>("baaahs.mapper.MapperAppView") { props ->
    val appContext = useContext(mapperAppContext)
    val styles = appContext.allStyles.mapper

    val ui = props.mapper
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

    val handleChangeEntity by handler(props.mapper.mappingController) { entity: Model.Entity? ->
        props.mapper.mappingController?.guessedEntity = entity
        forceRender()
    }

    val handlePixelCountChange by handler(props.mapper.mappingController) { pixelCount: String ->
        props.mapper.mappingController?.expectedPixelCount = pixelCount.toIntOrNull()
        forceRender()
    }

    val handlePixelFormatChange by handler(props.mapper.mappingController) { pixelFormat: PixelFormat? ->
        props.mapper.mappingController?.pixelFormat = pixelFormat
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
                            i(classes = "fas fa-play") {}
                            attrs.disabled = !ui.playButtonEnabled
                            attrs.onClick = uiActions.clickedPlay.withMouseEvent()
                        }
                        Button {
                            i(classes = "fas fa-pause") {}
                            attrs.disabled = !ui.pauseButtonEnabled
                            attrs.onClick = uiActions.clickedPause.withMouseEvent()
                        }
                        Button {
                            i(classes = "fas fa-redo") {}
                            attrs.disabled = ui.redoFn != null
                            attrs.onClick = uiActions.clickedRedo.withMouseEvent()
                        }
                        Button {
                            i(classes = "fas fa-stop") {}
                            attrs.onClick = uiActions.clickedStop.withMouseEvent()
                        }
                        Button {
                            i(classes = "fas fa-sign-in-alt") {}
                            attrs.onClick = uiActions.clickedGoToSurface.withMouseEvent()
                        }
                    }
                }

                props.mapper.mappingController?.let { mappingController ->
                    betterSelect<Model.Entity?> {
                        attrs.label = "Entity:"
                        attrs.values = props.mapper.entitiesByName.values.toList()
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
                            attrs.values = listOf(null) + PixelFormat.values().toList()
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
            attrs.mapperStatus = props.mapper.mapperStatus
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
                            attrs.mapper = props.mapper
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
                                i(classes = "fas fa-search-minus") {}
                                attrs.onClick = { ui.adjustCameraZoom(zoomIn = false, fine = moveIsFine()) }
                            }
                            Button {
                                attrs.title = "Zoom In (+)"
                                i(classes = "fas fa-search-plus") {}
                                attrs.onClick = { ui.adjustCameraZoom(zoomIn = true, fine = moveIsFine()) }
                            }
                        }

                        Button {
                            attrs.title = "Left (◀)"
                            i(classes = "fas fa-arrow-left") {}
                            attrs.onClick = { ui.adjustCameraX(moveRight = false, fine = moveIsFine()) }
                        }
                        div {
                            Button {
                                attrs.title = "Up (▲)"
                                i(classes = "fas fa-arrow-up") {}
                                attrs.onClick = { ui.adjustCameraY(moveUp = true, fine = moveIsFine()) }
                            }
                            Button {
                                attrs.title = "Down (▼)"
                                i(classes = "fas fa-arrow-down") {}
                                attrs.onClick = { ui.adjustCameraY(moveUp = false, fine = moveIsFine()) }
                            }
                        }
                        Button {
                            attrs.title = "Right (▶)"
                            i(classes = "fas fa-arrow-right") {}
                            attrs.onClick = { ui.adjustCameraX(moveRight = true, fine = moveIsFine()) }
                        }

                        div {
                            Button {
                                attrs.title = "Rotate Counter-clockwise (Q)"
                                i(classes = "fas fa-undo") {}
                                attrs.onClick = { ui.adjustCameraRotation(clockwise = false, fine = moveIsFine()) }
                            }
                            Button {
                                attrs.title = "Rotate Clockwise (Q)"
                                i(classes = "fas fa-redo") {}
                                attrs.onClick = { ui.adjustCameraRotation(clockwise = true, fine = moveIsFine()) }
                            }
                        }
                    }
                }
            }
        }
    }
}

external interface MapperAppViewProps : Props {
    var mapper: JsMapper
}

fun RBuilder.mapperApp(handler: RHandler<MapperAppViewProps>) =
    child(MapperAppView, handler = handler)
