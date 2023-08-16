package baaahs.mapper.twologn

import baaahs.imaging.Bitmap
import baaahs.mapper.JsMapper
import baaahs.mapper.TwoLogNMappingStrategy
import baaahs.mapper.mapperImage
import baaahs.ui.xComponent
import baaahs.util.globalLaunch
import kotlinx.coroutines.CompletableDeferred
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.img
import react.dom.td
import react.dom.tr
import web.html.HTMLImageElement

private val TwoLogNSliceView = xComponent<TwoLogNSliceProps>("TwoLogNSlice") { props ->
    val overlapImg = ref<HTMLImageElement>()

    val deferredBitmaps = memo(props.imageNames) {
        props.imageNames.map { CompletableDeferred<Bitmap>() }
    }
    var overlapBitmap by state<Bitmap?> { null }

    onMount(props.imageNames) {
        globalLaunch {
            val origBitmaps = deferredBitmaps.map { it.await() }
            val slice = TwoLogNMappingStrategy.Slice.build(origBitmaps[0], null, origBitmaps[1], null)
            overlapImg.current!!.src = slice.overlapBitmap.toDataUrl()
            overlapBitmap = slice.overlapBitmap
        }
    }

    tr {
        td { +props.sliceIndex.toString() }

        props.imageNames.forEachIndexed { frameIndex, origImageName ->
            td {
                if (origImageName != null) {
                    mapperImage {
                        attrs.mapper = props.mapper
                        attrs.imageName = origImageName
                        attrs.width = 100
                        attrs.onBitmap = { deferredBitmaps[frameIndex].complete(it) }
                    }

                    if (props.showExclusives == true) {
                        img {
                            val theOverlapBitmap = overlapBitmap
                            if (theOverlapBitmap != null) {
                                attrs.src =
                                    deferredBitmaps[frameIndex].getCompleted()
                                        .clone()
                                        .subtract(theOverlapBitmap)
                                        .toDataUrl()
                            }
                            attrs.width = "100"
                        }
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
    var imageNames: List<String?>
    var showExclusives: Boolean?
    var mapper: JsMapper
}

fun RBuilder.twoLogNSlice(handler: RHandler<TwoLogNSliceProps>) =
    child(TwoLogNSliceView, handler = handler)