package baaahs.mapper.twologn

import baaahs.mapper.JsMapper
import baaahs.mapper.TwoLogNMappingStrategy
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
    val metadata = props.sessionMetadata
    var showExclusives by state { false }

    div(+styles.twoLogNMasks) {
        table {
            thead {
                tr {
                    th { +"Slice" }
                    th { +"Frame 0" }
                    th { +"Frame 1" }
                    th { +"Overlap" }
                }
            }

            tbody {
                metadata.sliceImageNames?.forEachIndexed { sliceIndex, frames ->
                    twoLogNSlice {
                        attrs.sliceIndex = sliceIndex
                        attrs.imageNames = frames ?: emptyList()
                        attrs.mapper = props.mapper
                        attrs.showExclusives = showExclusives
                    }
                }
            }
        }
    }

    FormControlLabel {
        attrs.control = buildElement {
            Switch {
                attrs.checked = showExclusives
                attrs.onChange = { _, checked ->  showExclusives = checked }
            }
        }
        attrs.label = buildElement { +"Exclude Overlap" }
    }
}

external interface TwoLogNSlicesProps : Props {
    var sessionMetadata: TwoLogNMappingStrategy.TwoLogNSessionMetadata
    var mapper: JsMapper
}

fun RBuilder.twoLogNSlices(handler: RHandler<TwoLogNSlicesProps>) =
    child(TwoLogNSlicesView, handler = handler)