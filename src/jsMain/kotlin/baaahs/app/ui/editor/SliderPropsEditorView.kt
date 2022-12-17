package baaahs.app.ui.editor

import baaahs.app.ui.appContext
import baaahs.app.ui.gadgets.slider.slider
import baaahs.control.MutableSliderControl
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import kotlinx.css.*
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
    val styles = appContext.allStyles.modelEditor
    val controlsStyles = appContext.allStyles.controls
    val mutableSliderControl = props.mutableSliderControl

    val handlePositionChange by handler { value: Float -> }

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

        div {
            inlineStyles {
                position = Position.relative
                width = 10.em
            }

            slider {
                attrs.title = props.mutableSliderControl.title
                attrs.position = props.mutableSliderControl.initialValue
                attrs.minValue = props.mutableSliderControl.minValue
                attrs.maxValue = props.mutableSliderControl.maxValue
                attrs.stepValue = props.mutableSliderControl.stepValue
                attrs.reversed = true
                attrs.showTicks = true
                if (props.mutableSliderControl.maxValue <= 2) {
                    attrs.ticksScale = 100f
                }
                attrs.onPositionChange = handlePositionChange
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