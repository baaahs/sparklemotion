package baaahs.mapper

import baaahs.imaging.Bitmap
import baaahs.imaging.ImageBitmapImage
import baaahs.ui.xComponent
import baaahs.util.globalLaunch
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.img
import react.dom.onLoad
import web.cssom.px
import web.html.HTMLImageElement

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
        props.onBitmap?.let { onBitmap ->
            attrs.onLoad = { event ->
                val img = event.target as HTMLImageElement
                globalLaunch {
                    onBitmap(ImageBitmapImage.fromImg(img).toBitmap())
                }
            }
        }
    }
}

external interface MapperImageProps : Props {
    var mapper: JsMapper
    var imageName: String
    var width: Int?
    var height: Int?
    var onBitmap: ((Bitmap) -> Unit)?
}

fun RBuilder.mapperImage(handler: RHandler<MapperImageProps>) =
    child(MapperImageView, handler = handler)