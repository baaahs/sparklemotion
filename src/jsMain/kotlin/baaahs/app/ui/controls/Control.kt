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
import kotlinx.css.*
import kotlinx.css.properties.Timing
import kotlinx.css.properties.s
import kotlinx.css.properties.transition
import materialui.DragHandle
import materialui.Edit
import materialui.icon
import react.*
import react.dom.b
import react.dom.div
import styled.css
import styled.styledDiv

val Control = xComponent<ControlProps>("Control") { props ->
    val control = props.control
    val specialControlProps = props.specialControlProps
    val editMode = specialControlProps.editMode

    div(+Styles.controlBox) {
        ref = props.draggableProvided.innerRef
        copyFrom(props.draggableProvided.draggableProps)
        copyFrom(props.draggableProvided.dragHandleProps)

        styledDiv {
            css {
                visibility = if (editMode) Visibility.visible else Visibility.hidden
                transition(StyledElement::visibility, duration = 0.25.s, timing = Timing.linear)
                position = Position.absolute
                right = 2.px
                bottom = (-2).px
                zIndex = 1
            }

            icon(DragHandle)
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
                val dataFeed = appContext.showResources.useDataFeed(control)
                when (control.getRenderType()) {
                    "Slider" -> {
                        RangeSlider {
                            attrs.gadget = (dataFeed as CorePlugin.GadgetDataFeed).gadget
                        }
                        b { +control.dataSourceName }
                    }
                }
            }
        }

        if (specialControlProps.editMode) {
            icon(Edit)
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