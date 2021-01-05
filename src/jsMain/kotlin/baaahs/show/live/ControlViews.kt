package baaahs.show.live

import baaahs.app.ui.controls.Styles
import baaahs.app.ui.controls.button
import baaahs.app.ui.controls.buttonGroup
import baaahs.app.ui.controls.visualizer
import baaahs.gadgets.Slider
import baaahs.jsx.RangeSlider
import baaahs.ui.Renderer
import baaahs.ui.renderWrapper
import baaahs.ui.unaryPlus
import react.dom.div

actual fun getControlViews(): ControlViews = object : ControlViews {
    override fun forGadget(openGadgetControl: OpenGadgetControl, controlProps: ControlProps): Renderer = renderWrapper {
        val gadget = openGadgetControl.gadget
        val title = gadget.title

        when (gadget) {
            is Slider -> {
                RangeSlider {
                    attrs.gadget = gadget
                }
                div(+Styles.dataSourceTitle) { +title }
            }

            else -> {
                div(+Styles.dataSourceLonelyTitle) { +title }
            }
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

    override fun forVisualizer(openVisualizerControl: OpenVisualizerControl, controlProps: ControlProps) = renderWrapper {
        visualizer {
            attrs.controlProps = controlProps
            attrs.visualizerControl = openVisualizerControl
        }
    }
}