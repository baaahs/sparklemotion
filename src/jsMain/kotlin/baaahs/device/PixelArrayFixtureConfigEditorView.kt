package baaahs.device

import baaahs.app.ui.appContext
import baaahs.app.ui.editor.betterSelect
import baaahs.scene.EditingController
import baaahs.scene.MutableFixtureMapping
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

        val mutableConfig = props.mutableFixtureMapping.deviceConfig as PixelArrayDevice.MutableConfig?
        mutableConfig!!

        val handlePixelFormatChange by handler(
            props.editingController, mutableConfig
        ) { value: PixelArrayDevice.PixelFormat? ->
            mutableConfig.pixelFormat = value
            props.editingController.onChange()
        }

        div(+styles.pixelArrayConfigEditorRow) {
            with (appContext.allStyles.modelEditor) {
                numberTextField("Pixel Count", mutableConfig.componentCount, onChange = {
                    mutableConfig.componentCount = if (it == 0) null else it
                }, placeholder = "default")

                betterSelect<PixelArrayDevice.PixelFormat?> {
                    attrs.label = "Pixel Format"
                    attrs.values = listOf(null) + PixelArrayDevice.PixelFormat.values().toList()
                    attrs.renderValueOption = { (it?.name ?: "Default").asTextNode() }
                    attrs.value = mutableConfig.pixelFormat
                    attrs.onChange = handlePixelFormatChange
                }

                numberTextField("Gamma Correction", mutableConfig.gammaCorrection, onChange = {
                    mutableConfig.gammaCorrection = it
                }, placeholder = "default")
            }
        }
//        }
    }

external interface PixelArrayFixtureConfigEditorProps : Props {
    var editingController: EditingController<*>
    var mutableFixtureMapping: MutableFixtureMapping
}

fun RBuilder.pixelArrayFixtureConfigEditor(handler: RHandler<PixelArrayFixtureConfigEditorProps>) =
    child(PixelArrayFixtureConfigEditorView, handler = handler)