package baaahs.show.live

import baaahs.app.ui.controls.*
import baaahs.app.ui.layout.dragNDropContext
import baaahs.control.*
import baaahs.plugin.core.OpenTransitionControl
import baaahs.ui.View
import baaahs.ui.renderWrapper
import react.useContext

actual fun getControlViews(): ControlViews = object : ControlViews {
    override fun forButton(openButtonControl: OpenButtonControl, controlProps: ControlProps): View = renderWrapper {
        button {
            attrs.controlProps = controlProps
            attrs.buttonControl = openButtonControl
        }
    }

    override fun forButtonGroup(openButtonGroupControl: OpenButtonGroupControl, controlProps: ControlProps) = renderWrapper {
        val isLegacyLayout = useContext(dragNDropContext).isLegacy
        if (isLegacyLayout) {
            legacyButtonGroup {
                attrs.controlProps = controlProps
                attrs.buttonGroupControl = openButtonGroupControl
            }
        } else {
            gridButtonGroup {
                attrs.controlProps = controlProps
                attrs.buttonGroupControl = openButtonGroupControl
            }
        }
    }

    override fun forColorPicker(openColorPickerControl: OpenColorPickerControl, controlProps: ControlProps) = renderWrapper {
        colorPickerControl {
            attrs.controlProps = controlProps
            attrs.colorPickerControl = openColorPickerControl
        }
    }

    override fun forSlider(openSlider: OpenSliderControl, controlProps: ControlProps) = renderWrapper {
        sliderControl {
            attrs.controlProps = controlProps
            attrs.sliderControl = openSlider
        }
    }

    override fun forTransition(openTransitionControl: OpenTransitionControl, controlProps: ControlProps) = renderWrapper {
        transition {
            attrs.controlProps = controlProps
            attrs.transitionControl = openTransitionControl
        }
    }

    override fun forVacuity(openVacuityControl: OpenVacuityControl, controlProps: ControlProps) = renderWrapper {
        vacuity {
            attrs.controlProps = controlProps
            attrs.vacuityControl = openVacuityControl
        }
    }

    override fun forVisualizer(openVisualizerControl: OpenVisualizerControl, controlProps: ControlProps) = renderWrapper {
        visualizer {
            attrs.controlProps = controlProps
            attrs.visualizerControl = openVisualizerControl
        }
    }

    override fun forXyPad(openXyPadControl: OpenXyPadControl, controlProps: ControlProps)  = renderWrapper {
        xyPad {
            attrs.controlProps = controlProps
            attrs.xyPadControl = openXyPadControl
        }
    }
}