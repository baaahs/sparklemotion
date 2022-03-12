package baaahs.app.ui.editor

import baaahs.app.ui.appContext
import baaahs.control.MutableSliderControl
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import materialui.components.formcontrol.formControl
import materialui.components.formlabel.formLabel
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.useContext

private val SliderPropsEditorView = xComponent<SliderPropsEditorProps>("SliderPropsEditor") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.modelEditor
    val mutableSliderControl = props.mutableSliderControl

    div(+EditableStyles.propertiesSection) {
        formControl {
            formLabel {
                attrs.component = "legend"
                +"Surface Display Mode"
            }

            with(styles) {
                numberTextField("Initial Value", mutableSliderControl.initialValue) {
                    mutableSliderControl.initialValue = it
                    props.editableManager.onChange()
                }

                numberTextField("Min Value", mutableSliderControl.minValue) {
                    mutableSliderControl.minValue = it
                    props.editableManager.onChange()
                }

                numberTextField("Max Value", mutableSliderControl.maxValue) {
                    mutableSliderControl.maxValue = it
                    props.editableManager.onChange()
                }

                numberTextField("Step Value", mutableSliderControl.stepValue) {
                    mutableSliderControl.stepValue = it
                    props.editableManager.onChange()
                }
            }
        }
    }
}

external interface SliderPropsEditorProps : Props {
    var editableManager: EditableManager<*>
    var mutableSliderControl: MutableSliderControl
}

fun RBuilder.sliderPropsEditor(handler: RHandler<SliderPropsEditorProps>) =
    child(SliderPropsEditorView, handler = handler)