package baaahs.mapper

import baaahs.MapperUi
import baaahs.ui.*
import kotlinx.css.px
import react.*
import react.dom.canvas
import react.dom.div

private val CameraView = xComponent<CameraViewProps>("CameraView") { props ->

    canvas(classes = "mapperUi-2d-canvas") {
        attrs.width = props.width.px.toString()
        attrs.height = props.height.px.toString()
    }
}

external interface CameraViewProps : RProps {
    var mapperUi: MapperUi
    var width: Int
    var height: Int
}

fun RBuilder.cameraView(handler: RHandler<CameraViewProps>) =
    child(CameraView, handler = handler)