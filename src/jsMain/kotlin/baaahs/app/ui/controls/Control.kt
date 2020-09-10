package baaahs.app.ui.controls

import baaahs.app.ui.appContext
import baaahs.jsx.RangeSlider
import baaahs.plugin.CorePlugin
import baaahs.show.live.OpenButtonControl
import baaahs.show.live.OpenButtonGroupControl
import baaahs.show.live.OpenControl
import baaahs.show.live.OpenGadgetControl
import baaahs.ui.copyInto
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import external.DraggableProvided
import external.copyFrom
import kotlinext.js.jsObject
import kotlinx.html.js.onClickFunction
import materialui.DragIndicator
import materialui.Edit
import materialui.icon
import react.*
import react.dom.div

val Control = xComponent<ControlProps>("Control") { props ->
    val control = props.control
    val specialControlProps = props.specialControlProps

    div(+Styles.controlBox) {
        ref = props.draggableProvided.innerRef
        copyFrom(props.draggableProvided.draggableProps)

        div(+Styles.editButton) {
            attrs.onClickFunction = {
                // TODO
            }

            icon(Edit)
        }
        div(+Styles.dragHandle) {
            copyFrom(props.draggableProvided.dragHandleProps)
            icon(DragIndicator)
        }

        when (control) {
            is OpenButtonControl -> {
                child(Button, jsObject {
                    specialControlProps.copyInto(this)
                    button = control
                })
            }

            is OpenButtonGroupControl -> {
                child(ButtonGroup, jsObject {
                    specialControlProps.copyInto(this)
                    buttonGroupControl = control
                })
            }

            is OpenGadgetControl -> {
                val appContext = useContext(appContext)
                val dataSource = control.controlledDataSource
                val dataFeed = appContext.showPlayer.useDataFeed(dataSource)
                val title = control.gadget.title
                when (dataSource.getRenderType()) {
                    "Slider" -> {
                        RangeSlider {
                            attrs.gadget = (dataFeed as CorePlugin.GadgetDataFeed).gadget
                        }
                        div(+Styles.dataSourceTitle) { +title }
                    }

                    else -> {
                        div(+Styles.dataSourceLonelyTitle) { +title }
                    }
                }
            }

            else -> {
                +"Huh? What's $control?"
            }
        }
    }
}

external interface ControlProps : RProps {
    var control: OpenControl
    var specialControlProps: SpecialControlProps
    var draggableProvided: DraggableProvided
}

fun RBuilder.control(handler: RHandler<ControlProps>): ReactElement =
    child(Control, handler = handler)