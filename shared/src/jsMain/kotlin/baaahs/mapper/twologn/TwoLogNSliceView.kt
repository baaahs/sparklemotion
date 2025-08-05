package baaahs.mapper.twologn

import baaahs.mapper.JsMapper
import baaahs.mapper.LoadingSlice
import baaahs.mapper.mapperImage
import baaahs.ui.xComponent
import baaahs.util.globalLaunch
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.img
import react.dom.td
import react.dom.tr
import web.html.HTMLImageElement

private val TwoLogNSliceView = xComponent<TwoLogNSliceProps>("TwoLogNSlice") { props ->
    val overlapImg = ref<HTMLImageElement>()

    val loadingSlice = props.loadingSlice
    onMount(loadingSlice) {
        globalLaunch {
            loadingSlice.deferredSlice.await()
            val slice = loadingSlice.deferredSlice.await()
            overlapImg.current!!.src = slice.overlap.toDataUrl()
        }
    }

    tr {
        td { +props.sliceIndex.toString() }

        loadingSlice.halves.forEachIndexed { frameIndex, loadingImage ->
            td {
                if (loadingImage != null) {
                    mapperImage {
                        attrs.mapper = props.mapper
                        attrs.loadingSlice = loadingSlice
                        attrs.loadingImage = loadingImage
                        attrs.width = 100
                        attrs.showWithoutOverlap = props.showWithoutOverlap
                    }
                }
            }
        }

        td {
            img {
                ref = overlapImg
                attrs.width = "100"
            }
        }
    }
}

external interface TwoLogNSliceProps : Props {
    var sliceIndex: Int
    var loadingSlice: LoadingSlice
    var showWithoutOverlap: Boolean?
    var mapper: JsMapper
}

fun RBuilder.twoLogNSlice(handler: RHandler<TwoLogNSliceProps>) =
    child(TwoLogNSliceView, handler = handler)