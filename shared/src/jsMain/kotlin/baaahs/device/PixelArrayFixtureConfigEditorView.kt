package baaahs.device

import baaahs.app.ui.appContext
import baaahs.app.ui.editor.betterSelect
import baaahs.app.ui.model.numberTextField
import baaahs.scene.EditingController
import baaahs.ui.asTextNode
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.useContext

private val PixelArrayFixtureConfigEditorView =
    xComponent<PixelArrayFixtureConfigEditorProps>("PixelArrayFixtureConfigEditor") { props ->
        val appContext = useContext(appContext)
        val styles = appContext.allStyles.controllerEditor

        val mutableConfig = props.mutableFixtureOptions

        val handlePixelFormatChange by handler(
            props.editingController, mutableConfig
        ) { value: PixelFormat? ->
            mutableConfig.pixelFormat = value
            props.editingController.onChange()
        }

        div(+styles.pixelArrayConfigEditorRow) {
            numberTextField<Int?> {
                attrs.label = "Pixel Count"
                attrs.value = mutableConfig.componentCount
                attrs.onChange = { v: Int? ->
                    mutableConfig.componentCount = if (v == 0) null else v
                    props.editingController.onChange()
                }
                attrs.placeholder = "default"
            }

            betterSelect<PixelFormat?> {
                attrs.label = "Pixel Format"
                attrs.values = listOf(null) + PixelFormat.values().toList()
                attrs.renderValueOption = { it, _ -> (it?.name ?: "Default").asTextNode() }
                attrs.value = mutableConfig.pixelFormat
                attrs.onChange = handlePixelFormatChange
            }

            numberTextField<Float?> {
                attrs.label = "Gamma Correction"
                attrs.value = mutableConfig.gammaCorrection
                attrs.onChange = { v: Float? ->
                    mutableConfig.gammaCorrection = v
                    props.editingController.onChange()
                }
                attrs.placeholder = "default"
            }
        }
    }

external interface PixelArrayFixtureConfigEditorProps : Props {
    var editingController: EditingController<*>
    var mutableFixtureOptions: PixelArrayDevice.MutableOptions
}

fun RBuilder.pixelArrayFixtureConfigEditor(handler: RHandler<PixelArrayFixtureConfigEditorProps>) =
    child(PixelArrayFixtureConfigEditorView, handler = handler)