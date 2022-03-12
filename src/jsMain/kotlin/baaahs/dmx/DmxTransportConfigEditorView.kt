package baaahs.dmx

import baaahs.app.ui.appContext
import baaahs.scene.EditingController
import baaahs.scene.MutableTransportConfig
import baaahs.ui.checked
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import kotlinx.html.js.onChangeFunction
import materialui.components.checkbox.checkbox
import materialui.components.formcontrol.formControl
import materialui.components.formcontrollabel.formControlLabel
import materialui.components.formhelpertext.formHelperText
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.useContext

private val DmxTransportConfigEditorView =
    xComponent<DmxTransportConfigEditorProps>("DmxTransportConfigEditor") { props ->
        val appContext = useContext(appContext)
        val styles = appContext.allStyles.controllerEditor

        val mutableConfig = props.mutableTransportConfig as MutableDmxTransportConfig?
        mutableConfig!!

        with (appContext.allStyles.modelEditor) {
            div(+styles.dmxTransportConfigEditorRow) {
                numberTextField("Start Channel", mutableConfig.startChannel, onChange = {
                    mutableConfig.startChannel = it
                    props.editingController.onChange()
                })
            }

            div(+styles.dmxTransportConfigEditorRow) {
                formControl {
                    formControlLabel {
                        attrs.label { +"Start in a fresh universe" }
                        attrs.control {
                            checkbox {
                                attrs.checked = mutableConfig.fixtureStartsInFreshUniverse
                                attrs.onChangeFunction = {
//                                val value = it.target.checked
//                                mutableConfig.componentsStartAtUniverseBoundaries = value
//                                props.editingController.onChange()
                                }
                            }
                        }
                    }

                    formHelperText {
                        +"texty text text"
                    }
                }
            }

            div(+styles.dmxTransportConfigEditorRow) {
                formControlLabel {
                    attrs.label { +"Components may span universes" }
                    attrs.control {
                        checkbox {
                            attrs.checked = mutableConfig.componentMaySpanUniverses
                            attrs.onChangeFunction = {
                                val value = it.target.checked
                                mutableConfig.componentMaySpanUniverses = value
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
    var mutableTransportConfig: MutableTransportConfig
}

fun RBuilder.dmxTransportConfigEditor(handler: RHandler<DmxTransportConfigEditorProps>) =
    child(DmxTransportConfigEditorView, handler = handler)