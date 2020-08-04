package baaahs.app.ui.controls

import baaahs.app.ui.appContext
import baaahs.glshaders.CorePlugin
import baaahs.jsx.RangeSlider
import baaahs.show.Control
import baaahs.show.DataSource
import baaahs.show.SpecialControl
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import external.DraggableProvided
import external.copyFrom
import kotlinx.html.js.onClickFunction
import materialui.DragIndicator
import materialui.Edit
import materialui.icon
import react.*
import react.dom.div

val Control = xComponent<ControlProps>("Control") { props ->
    val control = props.control
    val specialControlProps = props.specialControlProps
    val editMode = specialControlProps.editMode

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
            is SpecialControl -> {
                val specialControlName = control.pluginRef.resourceName
                val component = when (specialControlName) {
                    "Scenes" -> SceneList
                    "Patches" -> PatchSetList
                    else -> error("unsupported special control $specialControlName")
                }
                child(component, specialControlProps)
            }

            is DataSource -> {
                val appContext = useContext(appContext)
                val dataFeed = appContext.showPlayer.useDataFeed(control)
                val title = (control as? CorePlugin.GadgetDataSource<*>)?.title ?: control.dataSourceName
                when (control.getRenderType()) {
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
        }
    }
}

external interface ControlProps : RProps {
    var control: Control
    var specialControlProps: SpecialControlProps
    var draggableProvided: DraggableProvided
}

fun RBuilder.control(handler: RHandler<ControlProps>): ReactElement =
    child(Control, handler = handler)