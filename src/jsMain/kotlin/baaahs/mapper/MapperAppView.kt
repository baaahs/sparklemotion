package baaahs.mapper

import baaahs.ui.unaryPlus
import baaahs.ui.withEvent
import baaahs.ui.xComponent
import baaahs.util.useResizeListener
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import kotlinx.html.tabIndex
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLElement
import react.*
import react.dom.*

val MapperAppView = xComponent<MapperAppViewProps>("baaahs.mapper.MapperAppView") { props ->
    val appContext = useContext(mapperAppContext)
    val styles = appContext.allStyles.mapper

    val ui = props.mapperUi
    observe(ui)
    val uiActions = memo(ui) { MemoizedJsMapperUi(ui) }

    // onscreen renderer for registration UI:
    val wireframe = ui.wireframe

    val screenRef = useRef<HTMLElement>(null)
    val ui2dCanvasRef = useRef<HTMLCanvasElement>(null)
    val ui3dDivRef = useRef<HTMLElement>(null)
    val baseCanvasRef = useRef<HTMLCanvasElement>(null)
    val diffCanvasRef = useRef<HTMLCanvasElement>(null)
    val snapshotCanvasRef = useRef<HTMLCanvasElement>(null)
    val panelMaskCanvasRef = useRef<HTMLCanvasElement>(null)
    val perfStatsRef = useRef<HTMLElement>(null)

    // init:
    onMount(props.statusListener) {
        ui.onMount(
            ui2dCanvasRef.current!!, ui3dDivRef.current!!, snapshotCanvasRef.current!!,
            baseCanvasRef.current!!, diffCanvasRef.current!!, panelMaskCanvasRef.current!!,
            perfStatsRef.current!!,
            screenRef.current!!.offsetWidth, screenRef.current!!.offsetHeight
        )

        screenRef.current!!.focus()
        screenRef.current!!.addEventListener("keydown", uiActions.onKeydown)

        withCleanup {
            ui.onUnmount()

        }
    }

    useResizeListener(screenRef) {
        ui.onResize(screenRef.current!!.offsetWidth, screenRef.current!!.offsetHeight)
    }


    div(+styles.screen) {
        ref = screenRef
        attrs.tabIndex = "-1" // So we can receive key events.

        div(+styles.controls) {
            button { +"Start"; attrs.onClickFunction = { uiActions.clickedStart.withEvent() } }
            button { +"▲"; attrs.onClickFunction = { wireframe.position.y += 10 } }
            button { +"▼"; attrs.onClickFunction = { wireframe.position.y -= 10 } }
//            button { i(classes="fas fa-crosshairs"); attrs.onClickFunction = { target() } }
            button {
                i(classes = "fas fa-play") {}
                attrs.disabled = !ui.playButtonEnabled
                attrs.onClickFunction = uiActions.clickedPlay.withEvent()
            }
            button {
                i(classes = "fas fa-pause") {}
                attrs.disabled = !ui.pauseButtonEnabled
                attrs.onClickFunction = uiActions.clickedPause.withEvent()
            }
            button {
                i(classes = "fas fa-redo") {}
                attrs.disabled = ui.redoFn != null
                attrs.onClickFunction = uiActions.clickedRedo.withEvent()
            }
            button {
                i(classes = "fas fa-stop") {}
                attrs.onClickFunction = uiActions.clickedStop.withEvent()
            }
            button {
                i(classes = "fas fa-sign-in-alt") {}
                attrs.onClickFunction = uiActions.clickedGoToSurface.withEvent()
            }

            select {
                attrs.onChangeFunction = uiActions.loadMappingSession

                option {
                    attrs.label = "-"
                    attrs.value = ""
                }

                ui.sessions.forEach { name ->
                    option {
                        attrs.label = name
                        attrs.value = name
                        if (name == ui.selectedMappingSession) {
                            attrs.selected = true
                        }
                    }
                }
            }

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
//        cameraView {
//            attrs.mapperUi = ui
//            attrs.width = width
//            attrs.height = height
//        }
        canvas(classes = +styles.mapping2dCanvas) {
            ref = ui2dCanvasRef
//            inlineStyles {
//                width = ui.containerDimen.width.px
//                height = ui.containerDimen.height.px
//            }
        }
        div(+styles.mapping3dContainer) {
            ref = ui3dDivRef
//            inlineStyles {
//                width = ui.containerDimen.width.px
//                height = ui.containerDimen.width.px
//            }
        }

        div(+styles.snapshotDiv) {
            div(+styles.thumbnailTitle) { +"Snapshot" }
            canvas {
                ref = snapshotCanvasRef
//                inlineStyles {
//                    width = (ui.browserDimen.width * ui.thumbnailCanvasScale).px
//                    height = (ui.browserDimen.height * ui.thumbnailCanvasScale).px
//                }
            }
        }

        div(+styles.baseDiv) {
            div(+styles.thumbnailTitle) { +"Base" }
            canvas {
                ref = baseCanvasRef
//                inlineStyles {
//                    width = (ui.browserDimen.width * ui.thumbnailCanvasScale).px
//                    height = (ui.browserDimen.height * ui.thumbnailCanvasScale).px
//                }
            }
        }

        div(+styles.diffDiv) {
            div(+styles.thumbnailTitle) { +"Diff" }
            canvas {
                ref = diffCanvasRef
//                inlineStyles {
//                    width = (ui.browserDimen.width * ui.thumbnailCanvasScale).px
//                    height = (ui.browserDimen.height * ui.thumbnailCanvasScale).px
//                }
            }
        }

        div(+styles.panelMaskDiv) {
            div(+styles.thumbnailTitle) { +"Panel Mask" }
            canvas {
                ref = panelMaskCanvasRef
//                inlineStyles {
//                    width = (ui.browserDimen.width * ui.thumbnailCanvasScale).px
//                    height = (ui.browserDimen.height * ui.thumbnailCanvasScale).px
//                }
            }
        }

        statusBar {
            attrs.mapperStatus = props.mapperUi.mapperStatus
        }

        div(+styles.perfStats) {
            ref = perfStatsRef
        }
    }
}

external interface MapperAppViewProps : RProps {
    var mapper: Mapper.Facade
    var mapperUi: JsMapperUi
    var listener: MapperUi.Listener
    var statusListener: JsMapperUi.StatusListener?
}

fun RBuilder.mapperApp(handler: RHandler<MapperAppViewProps>) =
    child(MapperAppView, handler = handler)
