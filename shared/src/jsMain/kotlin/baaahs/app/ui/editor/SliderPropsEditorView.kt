package baaahs.app.ui.editor

import baaahs.app.ui.appContext
import baaahs.app.ui.gadgets.slider.slider
import baaahs.app.ui.model.numberTextField
import baaahs.control.MutableSliderControl
import baaahs.gadgets.Slider
import baaahs.show.MidiChannelEventBinding
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import kotlinx.css.*
import mui.material.Divider
import mui.material.FormControl
import mui.material.FormLabel
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.dom.html.ReactHTML
import react.useContext
import styled.inlineStyles
import kotlin.Float

private val SliderPropsEditorView = xComponent<SliderPropsEditorProps>("SliderPropsEditor") { props ->
    val appContext = useContext(appContext)
    val controlsStyles = appContext.allStyles.controls
    val mutableSliderControl = props.mutableSliderControl

    div(+EditableStyles.propertiesSection) {
        inlineStyles {
            display = Display.flex
            flexDirection = FlexDirection.row
        }

        div {
            FormControl {
                FormLabel {
                    attrs.component = ReactHTML.legend
                    +"Slider Properties"
                }

                numberTextField<Float> {
                    attrs.label = "Initial Value"
                    attrs.value = mutableSliderControl.initialValue
                    attrs.onChange = { v: Float ->
                        mutableSliderControl.initialValue = v
                        props.editableManager.onChange()
                    }
                }

                numberTextField<Float> {
                    attrs.label = "Min Value"
                    attrs.value = mutableSliderControl.minValue
                    attrs.onChange = { v: Float ->
                        mutableSliderControl.minValue = v
                        props.editableManager.onChange()
                    }
                }

                numberTextField<Float> {
                    attrs.label = "Max Value"
                    attrs.value = mutableSliderControl.maxValue
                    attrs.onChange = { v: Float ->
                        mutableSliderControl.maxValue = v
                        props.editableManager.onChange()
                    }
                }

                numberTextField<Float?> {
                    attrs.label = "Step Value"
                    attrs.value = mutableSliderControl.stepValue
                    attrs.onChange = { v: Float? ->
                        mutableSliderControl.stepValue = v
                        props.editableManager.onChange()
                    }
                }

                Divider {}

                FormLabel {
                    attrs.component = ReactHTML.legend
                    +"Event Bindings"
                }

                numberTextField<Int?> {
                    attrs.label = "MIDI channel"
                    val eventBinding = mutableSliderControl.eventBindings.firstOrNull() as? MidiChannelEventBinding
                    attrs.value = eventBinding?.channel
                    attrs.onChange = { v: Int? ->
                        mutableSliderControl.eventBindings =
                            v?.let { listOf(MidiChannelEventBinding(v)) } ?: emptyList()
                        props.editableManager.onChange()
                    }
                }
            }
        }

        div {
            inlineStyles {
                position = Position.relative
                width = 10.em
            }

            slider {
                attrs.slider = with (props.mutableSliderControl) {
                    Slider(title, initialValue, minValue, maxValue, stepValue)
                }
            }

            div(+controlsStyles.feedTitle) { +props.mutableSliderControl.title }
        }
    }
}

external interface SliderPropsEditorProps : Props {
    var editableManager: EditableManager<*>
    var mutableSliderControl: MutableSliderControl
}

fun RBuilder.sliderPropsEditor(handler: RHandler<SliderPropsEditorProps>) =
    child(SliderPropsEditorView, handler = handler)