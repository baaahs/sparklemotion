package baaahs.device

import baaahs.app.ui.appContext
import baaahs.app.ui.editor.betterSelect
import baaahs.scene.EditingController
import baaahs.scene.MutableFixtureMapping
import baaahs.ui.xComponent
import kotlinx.css.Display
import kotlinx.css.FlexDirection
import kotlinx.css.display
import kotlinx.css.flexDirection
import materialui.components.container.container
import react.Props
import react.RBuilder
import react.RHandler
import react.useContext
import styled.inlineStyles

private val PixelArrayFixtureConfigEditorView =
    xComponent<PixelArrayFixtureConfigEditorProps>("PixelArrayFixtureConfigEditor") { props ->
        val appContext = useContext(appContext)

        val mutableConfig = props.mutableFixtureMapping.deviceConfig as PixelArrayDevice.MutableConfig?
        mutableConfig!!

        val handlePixelFormatChange by handler(
            props.editingController, mutableConfig
        ) { value: PixelArrayDevice.PixelFormat? ->
            mutableConfig.pixelFormat = value
            props.editingController.onChange()
        }

        container {
            inlineStyles {
                display = Display.flex
                flexDirection = FlexDirection.row
            }

            with (appContext.allStyles.modelEditor) {
                numberTextField("Pixel Count", mutableConfig.componentCount ?: 0, onChange = {
                    mutableConfig.componentCount = if (it == 0) null else it
                })

                betterSelect<PixelArrayDevice.PixelFormat?> {
                    attrs.label = "Pixel Format"
                    attrs.values = PixelArrayDevice.PixelFormat.values().toList()
                    attrs.value = mutableConfig.pixelFormat
                    attrs.onChange = handlePixelFormatChange
                }

                numberTextField("Gamma Correction", mutableConfig.gammaCorrection, onChange = {
                    mutableConfig.gammaCorrection = it
                })
            }
        }
    }

external interface PixelArrayFixtureConfigEditorProps : Props {
    var editingController: EditingController<*>
    var mutableFixtureMapping: MutableFixtureMapping
}

fun RBuilder.pixelArrayFixtureConfigEditor(handler: RHandler<PixelArrayFixtureConfigEditorProps>) =
    child(PixelArrayFixtureConfigEditorView, handler = handler)