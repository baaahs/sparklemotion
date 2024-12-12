package baaahs.dmx

import baaahs.app.ui.appContext
import baaahs.app.ui.model.numberTextField
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

        div(+styles.dmxTransportConfigEditorRow) {
            numberTextField<Int?> {
                attrs.label = "Start Channel"
                attrs.value = mutableConfig.startChannel
                attrs.onChange = { v: Int? ->
                    mutableConfig.startChannel = v
                    props.editingController.onChange()
                }
            }
        }

        div(+styles.dmxTransportConfigEditorRow) {
            FormControl {
                FormControlLabel {
                    attrs.label = buildElement { +"Start each fixture in a fresh universe" }
                    attrs.control = buildElement {
                        Checkbox {
                            attrs.checked = mutableConfig.fixtureStartsInFreshUniverse
                            attrs.onChange = { e, _ ->
                                val value = e.target.checked
                                mutableConfig.fixtureStartsInFreshUniverse = value
                                props.editingController.onChange()
                            }
                        }
                    }
                }

                FormHelperText {
                    +"""
                        For DMX controllers that support multiple universes, Sparkle Motion will
                        start every fixture at the first channel of a fresh universe unless this
                        option is checked.
                    """.trimIndent()
                }
            }
        }

        div(+styles.dmxTransportConfigEditorRow) {
            FormControl {
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

                FormHelperText {
                    +"""
                        If a fixture needs more channels than the universe can accommodate,
                        Sparkle Motion will prevent components (e.g. a pixel's RGB value) from
                        spanning universes. This may cause some channels to be left unused.
                    """.trimIndent()
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