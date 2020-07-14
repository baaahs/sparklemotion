package baaahs.app.ui.controls

import baaahs.app.ui.appContext
import baaahs.glshaders.CorePlugin
import baaahs.jsx.RangeSlider
import baaahs.show.Control
import baaahs.show.DataSource
import baaahs.show.SpecialControl
import baaahs.ui.xComponent
import materialui.Edit
import materialui.icon
import react.*
import react.dom.b
import react.dom.div

val Control = xComponent<ControlProps>("Control") { props ->
    val control = props.control
    val specialControlProps = props.specialControlProps

    div {
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
}

fun RBuilder.control(handler: RHandler<ControlProps>): ReactElement =
    child(Control, handler = handler)