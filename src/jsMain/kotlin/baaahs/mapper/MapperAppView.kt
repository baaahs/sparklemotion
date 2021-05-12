package baaahs.mapper

import baaahs.JsMapperUi
import baaahs.Mapper
import baaahs.MapperUi
import baaahs.MemoizedJsMapperUi
import baaahs.jsx.useResizeListener
import baaahs.ui.withEvent
import baaahs.ui.xComponent
import kotlinx.css.properties.scale
import kotlinx.css.properties.transform
import kotlinx.css.px
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import kotlinx.html.tabIndex
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLElement
import react.*
import react.dom.*
import styled.inlineStyles
import three.js.Clock
import kotlin.math.min

val MapperAppView = xComponent<MapperAppViewProps>("baaahs.mapper.MapperAppView") { props ->
    val ui = props.mapperUi
    observe(ui)
    val uiActions = memo(ui) { MemoizedJsMapperUi(ui) }
    val width = ui.width
    val height = ui.height

    val clock = Clock()

    // onscreen renderer for registration UI:
    val wireframe = ui.wireframe

    val screenRef = useRef<HTMLElement?>(null)
    val ui2dCanvasRef = useRef<HTMLCanvasElement?>(null)
    val ui3dDivRef = useRef<HTMLElement?>(null)
    val diffCanvasRef = useRef<HTMLCanvasElement?>(null)
    val beforeCanvasRef = useRef<HTMLCanvasElement?>(null)
    val afterCanvasRef = useRef<HTMLCanvasElement?>(null)

    // init:
    onMount(props.statusListener) {
        ui.resizeTo(screenRef.current!!.offsetWidth, screenRef.current!!.offsetHeight)
        ui.onMount(
            ui2dCanvasRef.current!!, ui3dDivRef.current!!, diffCanvasRef.current!!,
            beforeCanvasRef.current!!, afterCanvasRef.current!!
        )

        screenRef.current!!.focus()
        screenRef.current!!.addEventListener("keydown", uiActions.onKeydown)

        withCleanup {
            ui.onUnmount()

        }
    }

    useResizeListener(screenRef) {
        ui.resizeTo(screenRef.current!!.offsetWidth, screenRef.current!!.offsetHeight)
    }


    div("mapperUi-screen") {
        ref = screenRef
        attrs.tabIndex = "-1" // So we can receive key events.

        div("mapperUi-controls") {
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
            select("mapperUi-sessionSelector") {
                ui.sessions.forEach { name ->
                    option {
                        attrs.label = name
                        attrs.value = name
                    }
                }
            }

            select("mapperUi-cameraSelector") {
                attrs.onChangeFunction = uiActions.changedCamera

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
        canvas(classes = "mapperUi-2d-canvas") {
            ref = ui2dCanvasRef
            attrs.width = ui.uiWidth.px.toString()
            attrs.height = ui.uiHeight.px.toString()
            inlineStyles {
                transform {
                    val scale = min(
                        1f,
                        min(
                            (ui.width - 10).toFloat() / ui.camWidth,
                            (ui.height - 10).toFloat() / ui.camHeight
                        )
                    )

                    scale(scale)
                }

            }
        }
        div("mapperUi-3d-div") {
            ref = ui3dDivRef
        }
        canvas(classes = "mapperUi-diff-canvas") {
            ref = diffCanvasRef
            attrs.width = (ui.uiWidth * ui.diffCanvasScale).px.toString()
            attrs.height = (ui.uiHeight * ui.diffCanvasScale).px.toString()
        }
        canvas(classes = "mapperUi-before-canvas") {
            ref = beforeCanvasRef
            attrs.width = (ui.uiWidth * ui.diffCanvasScale).px.toString()
            attrs.height = (ui.uiHeight * ui.diffCanvasScale).px.toString()
        }
        canvas(classes = "mapperUi-after-canvas") {
            ref = afterCanvasRef
            attrs.width = (ui.uiWidth * ui.diffCanvasScale).px.toString()
            attrs.height = (ui.uiHeight * ui.diffCanvasScale).px.toString()
        }

        statusBar {
            attrs.mapperStatus = props.mapperUi.mapperStatus
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
