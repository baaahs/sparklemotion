package baaahs.app.ui.controls

import baaahs.app.ui.appContext
import baaahs.control.OpenVacuityControl
import baaahs.show.live.ControlProps
import baaahs.ui.and
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.useContext

private val VacuityControlView = xComponent<VacuityProps>("VacuityControl") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.controls

    val relevantUnplacedControls = props.controlProps.relevantUnplacedControls

    div(+Styles.controlRoot and styles.vacuityContainer and Styles.notExplicitlySized) {
        relevantUnplacedControls.forEachIndexed { index, unplacedControl ->
//                val draggableId = "unplaced-${unplacedControl.id}"
//                draggable({
//                    this.key = draggableId
//                    this.draggableId = draggableId
//                    this.isDragDisabled = !props.editMode
//                    this.index = index
//                }) { draggableProvided, snapshot ->
//                    if (snapshot.isDragging) {
//                                    // Correct for translated parent.
//                                    unplacedControlPaletteDiv.current?.let {
//                                        val draggableStyle = draggableProvided.draggableProps.asDynamic().style
//                                        draggableStyle.left -= it.offsetLeft
//                                        draggableStyle.top -= it.offsetTop
//                                    }
//                    }

                controlWrapper {
                    attrs.control = unplacedControl
                    attrs.controlProps = props.controlProps
                    attrs.disableEdit = true
//                        attrs.draggableProvided = draggableProvided
                }
//                }
        }
    }
}

external interface VacuityProps : Props {
    var controlProps: ControlProps
    var vacuityControl: OpenVacuityControl
}

fun RBuilder.vacuityControl(handler: RHandler<VacuityProps>) =
    child(VacuityControlView, handler = handler)