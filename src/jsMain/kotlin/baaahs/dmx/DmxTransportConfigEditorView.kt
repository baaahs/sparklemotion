package baaahs.dmx

import baaahs.app.ui.appContext
import baaahs.scene.EditingController
import baaahs.scene.MutableFixtureMapping
import baaahs.ui.checked
import baaahs.ui.xComponent
import kotlinx.css.Display
import kotlinx.css.FlexDirection
import kotlinx.css.display
import kotlinx.css.flexDirection
import kotlinx.html.js.onChangeFunction
import materialui.components.container.container
import materialui.components.formcontrollabel.formControlLabel
import materialui.components.switches.switch
import react.Props
import react.RBuilder
import react.RHandler
import react.useContext
import styled.inlineStyles

private val DmxTransportConfigEditorView =
    xComponent<DmxTransportConfigEditorProps>("DmxTransportConfigEditor") { props ->
        val appContext = useContext(appContext)

        val mutableConfig = props.mutableFixtureMapping.transportConfig as MutableDmxTransportConfig?
        mutableConfig!!

        container {
            inlineStyles {
                display = Display.flex
                flexDirection = FlexDirection.column
            }

            with (appContext.allStyles.modelEditor) {
                numberTextField("Start Channel", mutableConfig.startChannel, onChange = {
                    mutableConfig.startChannel = it
                    props.editingController.onChange()
                })

                numberTextField("End Channel", mutableConfig.endChannel, onChange = {
                    mutableConfig.endChannel = it
                    props.editingController.onChange()
                })

                formControlLabel {
                    attrs.control {
                        switch {
                            attrs.checked = mutableConfig.componentsStartAtUniverseBoundaries
                            attrs.onChangeFunction = {
                                val value = it.target.checked
                                mutableConfig.componentsStartAtUniverseBoundaries = value
                                props.editingController.onChange()
                            }
                        }
                    }
                }
            }
        }
    }

external interface DmxTransportConfigEditorProps : Props {
    var editingController: EditingController<*>
    var mutableFixtureMapping: MutableFixtureMapping
}

fun RBuilder.dmxTransportConfigEditor(handler: RHandler<DmxTransportConfigEditorProps>) =
    child(DmxTransportConfigEditorView, handler = handler)