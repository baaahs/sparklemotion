package baaahs.mapper

import baaahs.ui.xComponent
import baaahs.util.globalLaunch
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.img
import web.cssom.px

private val MapperImageView = xComponent<MapperImageProps>("MapperImage") { props ->
    var imageSrc by state<String?> { null }
    onMount(props.loadingImage, props.loadingSlice, props.showWithoutOverlap) {
        globalLaunch {
            if (props.showWithoutOverlap == true) {
                val slice = props.loadingSlice.deferredSlice.await()
                val overlapBitmap = slice.overlap

                imageSrc = props.loadingImage.deferredBitmap.await()
                    .clone()
                    .subtract(overlapBitmap)
                    .toDataUrl()
            } else {
                imageSrc = props.loadingImage.deferredSrc.await()
            }
        }
    }

    img {
        attrs.src = imageSrc ?: ""
        attrs.alt = props.loadingImage.name
        props.width?.let { attrs.width = it.px.toString() }
        props.height?.let { attrs.height = it.px.toString() }
    }
}

external interface MapperImageProps : Props {
    var mapper: JsMapper
    var loadingSlice: LoadingSlice
    var loadingImage: LoadingImage
    var width: Int?
    var height: Int?
    var showWithoutOverlap: Boolean?
}

fun RBuilder.mapperImage(handler: RHandler<MapperImageProps>) =
    child(MapperImageView, handler = handler)