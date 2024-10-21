package baaahs.show.live

import baaahs.app.ui.controls.*
import baaahs.control.*
import baaahs.plugin.core.OpenTransitionControl
import baaahs.ui.View
import baaahs.ui.renderWrapper

actual fun getControlViews(): ControlViews = object : ControlViews {
    override fun forButton(openButtonControl: OpenButtonControl, controlProps: ControlProps): View = renderWrapper {
        buttonControl {
            attrs.controlProps = controlProps
            attrs.buttonControl = openButtonControl
        }
    }

    override fun forButtonGroup(openButtonGroupControl: OpenButtonGroupControl, controlProps: ControlProps) = renderWrapper {
        gridButtonGroupControl {
            attrs.controlProps = controlProps
            attrs.buttonGroupControl = openButtonGroupControl
        }
    }

    override fun forColorPicker(openColorPickerControl: OpenColorPickerControl, controlProps: ControlProps) = renderWrapper {
        colorPickerControl {
            attrs.controlProps = controlProps
            attrs.colorPickerControl = openColorPickerControl
        }
    }

    override fun forImagePicker(openImagePickerControl: OpenImagePickerControl, controlProps: ControlProps) = renderWrapper {
        imagePickerControl {
            attrs.controlProps = controlProps
            attrs.imagePickerControl = openImagePickerControl
        }
    }

    override fun forSlider(openSlider: OpenSliderControl, controlProps: ControlProps) = renderWrapper {
        sliderControl {
            attrs.controlProps = controlProps
            attrs.slider = openSlider.slider
            attrs.sliderControl = openSlider
        }
    }

    override fun forTransition(openTransitionControl: OpenTransitionControl, controlProps: ControlProps) = renderWrapper {
        transitionControl {
            attrs.controlProps = controlProps
            attrs.transitionControl = openTransitionControl
        }
    }

    override fun forVacuity(openVacuityControl: OpenVacuityControl, controlProps: ControlProps) = renderWrapper {
        vacuityControl {
            attrs.controlProps = controlProps
            attrs.vacuityControl = openVacuityControl
        }
    }

    override fun forVisualizer(openVisualizerControl: OpenVisualizerControl, controlProps: ControlProps) = renderWrapper {
        visualizerControl {
            attrs.controlProps = controlProps
            attrs.visualizerControl = openVisualizerControl
        }
    }

    override fun forXyPad(openXyPadControl: OpenXyPadControl, controlProps: ControlProps)  = renderWrapper {
        xyPadControl {
            attrs.controlProps = controlProps
            attrs.xyPadControl = openXyPadControl
        }
    }
}