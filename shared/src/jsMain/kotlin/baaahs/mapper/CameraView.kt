package baaahs.mapper

import baaahs.ui.xComponent
import kotlinx.css.px
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.canvas

private val CameraView = xComponent<CameraViewProps>("CameraView") { props ->

    canvas(classes = "mapperUi-2d-canvas") {
        attrs.width = props.width.px.toString()
        attrs.height = props.height.px.toString()
    }
}

external interface CameraViewProps : Props {
    var mapper: Mapper
    var width: Int
    var height: Int
}

fun RBuilder.cameraView(handler: RHandler<CameraViewProps>) =
    child(CameraView, handler = handler)