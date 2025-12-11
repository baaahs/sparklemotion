package baaahs.mapper.twologn

import baaahs.mapper.JsMapper
import baaahs.mapper.LoadingSlices
import baaahs.mapper.mapperAppContext
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import mui.material.FormControlLabel
import mui.material.Switch
import react.*
import react.dom.*

private val TwoLogNSlicesView = xComponent<TwoLogNSlicesProps>("TwoLogNSlices") { props ->
    val appContext = useContext(mapperAppContext)
    val styles = appContext.allStyles.mapper
    var showWithoutOverlap by state { false }

    div(+styles.twoLogNMasks) {
        table {
            thead {
                tr {
                    th { +"Slice" }
                    th { +"First Half" }
                    th { +"Second Half" }
                    th { +"Overlap" }
                }
            }

            tbody {
                props.loadingSlices.loadingSlices.forEachIndexed { sliceIndex, loadingSlice ->
                    twoLogNSlice {
                        attrs.sliceIndex = sliceIndex
                        attrs.loadingSlice = loadingSlice
                        attrs.mapper = props.mapper
                        attrs.showWithoutOverlap = showWithoutOverlap
                    }
                }
            }
        }
    }

    FormControlLabel {
        attrs.control = buildElement {
            Switch {
                attrs.checked = showWithoutOverlap
                attrs.onChange = { _, checked ->  showWithoutOverlap = checked }
            }
        }
        attrs.label = buildElement { +"Show Without Overlap" }
    }
}

external interface TwoLogNSlicesProps : Props {
    var loadingSlices: LoadingSlices
    var mapper: JsMapper
}

fun RBuilder.twoLogNSlices(handler: RHandler<TwoLogNSlicesProps>) =
    child(TwoLogNSlicesView, handler = handler)