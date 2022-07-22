package baaahs.mapper

import baaahs.ui.*
import baaahs.ui.components.palette
import baaahs.util.useResizeListener
import kotlinx.html.js.onChangeFunction
import kotlinx.html.tabIndex
import mui.material.Button
import mui.material.FormControlLabel
import mui.material.Switch
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLImageElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent
import react.*
import react.dom.*

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

    val handleSelectEntityPixel by handler { entityName: String?, index: Int? ->
        ui.selectEntityPixel(entityName, index)
    }

    val handleLoadMappingSession by handler(uiActions.onLoadMappingSession) { event: Event ->
        uiActions.onLoadMappingSession(event.target?.value)
    }

    useResizeListener(screenRef) {
        screenRef.current?.let { el ->
            ui.onResize(el.offsetWidth, el.offsetHeight)
        }
    }

    fun moveIsFine(): Boolean = shiftDown.current == true

    div(+styles.screen) {
        ref = screenRef
        attrs.tabIndex = "-1" // So we can receive key events.

        palette {
            attrs.initialWidth = 240
            attrs.initialHeight = 400

            header { +"Mapping Tools" }

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

                div {
                    FormControlLabel {
                        attrs.control = buildElement {
                            Switch {
                                attrs.checked = ui.mappingEnabled
                                attrs.onChange = handleMappingEnabledChange
                            }
                        }
                        attrs.label = "Mapping".asTextNode()
                    }
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

                div(+styles.controlsRow) {
                    div {
                        div {
                            p { +"Camera:" }

                            select {
                                attrs.onChangeFunction = uiActions.changedCamera

                                option {
                                    attrs.label = "Unknown"
                                    attrs.value = ""
                                }

                                ui.devices.forEach { device ->
                                    option {
                                        attrs.label = device.label
                                        attrs.value = device.deviceId
                                        if (device == ui.selectedDevice) {
                                            attrs.selected = true
                                        }
                                    }
                                }
                            }
                        }

                        div {
                            p { +"Load Mapping Data:" }

                            select {
                                attrs.onChangeFunction = handleLoadMappingSession

                                option {
                                    attrs.label = "-"
                                    attrs.value = ""
                                }

                                ui.sessions.forEach { name ->
                                    option {
                                        attrs.label = name
                                        attrs.value = name
                                        if (name == ui.selectedMappingSessionName) {
                                            attrs.selected = true
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        canvas(classes = +styles.mapping2dCanvas) {
            ref = ui2dCanvasRef
        }

        img(classes = +styles.savedImage) { ref = savedImageRef }

        div(+styles.mapping3dContainer) { ref = ui3dDivRef }

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
        }

        div(+styles.panelMaskDiv) {
            div(+styles.thumbnailTitle) { +"Panel Mask" }
            canvas { ref = panelMaskCanvasRef }
        }

        statusBar {
            attrs.mapperStatus = props.mapper.mapperStatus
        }

        ui.selectedMappingSession?.let { session ->
            mappingSession {
                attrs.name = ui.selectedMappingSessionName ?: "unknown session!?"
                attrs.session = session
                attrs.onSelectEntityPixel = handleSelectEntityPixel
            }
        }

        div(+styles.perfStats) {
            ref = perfStatsRef
        }
    }
}

external interface MapperAppViewProps : Props {
    var mapper: JsMapper
}

fun RBuilder.mapperApp(handler: RHandler<MapperAppViewProps>) =
    child(MapperAppView, handler = handler)
