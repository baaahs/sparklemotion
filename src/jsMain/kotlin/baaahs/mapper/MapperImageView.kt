package baaahs.mapper

import baaahs.ui.xComponent
import baaahs.util.globalLaunch
import csstype.px
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.img

private val MapperImageView = xComponent<MapperImageProps>("MapperImage") { props ->
    var imageSrc by state<String?> { null }
    onMount(props.imageName) {
        globalLaunch {
            logger.warn { "Loading ${props.imageName}…" }
            imageSrc = props.mapper.getImageUrl(props.imageName)
        }
    }

    img {
        attrs.src = imageSrc ?: ""
        attrs.alt = props.imageName
        props.width?.let { attrs.width = it.px.toString() }
        props.height?.let { attrs.height = it.px.toString() }
    }
}

external interface MapperImageProps : Props {
    var mapper: JsMapper
    var imageName: String
    var width: Int?
    var height: Int?
}

fun RBuilder.mapperImage(handler: RHandler<MapperImageProps>) =
    child(MapperImageView, handler = handler)