package baaahs.show.live

import baaahs.app.ui.controls.*
import baaahs.control.OpenButtonControl
import baaahs.control.OpenButtonGroupControl
import baaahs.control.OpenGadgetControl
import baaahs.control.OpenVisualizerControl
import baaahs.plugin.core.OpenTransitionControl
import baaahs.ui.Renderer
import baaahs.ui.renderWrapper

actual fun getControlViews(): ControlViews = object : ControlViews {
    override fun forGadget(openGadgetControl: OpenGadgetControl, controlProps: ControlProps): Renderer = renderWrapper {
        gadget {
            attrs.controlProps = controlProps
            attrs.gadgetControl = openGadgetControl
        }
    }

    override fun forButton(openButtonControl: OpenButtonControl, controlProps: ControlProps): Renderer = renderWrapper {
        button {
            attrs.controlProps = controlProps
            attrs.buttonControl = openButtonControl
        }
    }

    override fun forButtonGroup(openButtonGroupControl: OpenButtonGroupControl, controlProps: ControlProps) = renderWrapper {
        buttonGroup {
            attrs.controlProps = controlProps
            attrs.buttonGroupControl = openButtonGroupControl
        }
    }

    override fun forTransition(openTransitionControl: OpenTransitionControl, controlProps: ControlProps) = renderWrapper {
        transition {
            attrs.controlProps = controlProps
            attrs.transitionControl = openTransitionControl
        }
    }

    override fun forVisualizer(openVisualizerControl: OpenVisualizerControl, controlProps: ControlProps) = renderWrapper {
        visualizer {
            attrs.controlProps = controlProps
            attrs.visualizerControl = openVisualizerControl
        }
    }
}