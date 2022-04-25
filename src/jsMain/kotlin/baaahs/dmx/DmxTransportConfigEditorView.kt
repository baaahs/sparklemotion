package baaahs.dmx

import baaahs.app.ui.appContext
import baaahs.scene.EditingController
import baaahs.scene.MutableTransportConfig
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import mui.material.Checkbox
import mui.material.FormControl
import mui.material.FormControlLabel
import mui.material.FormHelperText
import react.*
import react.dom.div

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
                FormControl {
                    FormControlLabel {
                        attrs.label = buildElement { +"Start in a fresh universe" }
                        attrs.control = buildElement {
                            Checkbox {
                                attrs.checked = mutableConfig.fixtureStartsInFreshUniverse
                                attrs.onChange = { _, _ ->
//                                val value = it.target.checked
//                                mutableConfig.componentsStartAtUniverseBoundaries = value
//                                props.editingController.onChange()
                                }
                            }
                        }
                    }

                    FormHelperText {
                        +"texty text text"
                    }
                }
            }

            div(+styles.dmxTransportConfigEditorRow) {
                FormControlLabel {
                    attrs.label = buildElement { +"Components may span universes" }
                    attrs.control = buildElement {
                        Checkbox {
                            attrs.checked = mutableConfig.componentMaySpanUniverses
                            attrs.onChange = { _, checked ->
                                mutableConfig.componentMaySpanUniverses = checked
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