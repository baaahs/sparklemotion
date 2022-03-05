package baaahs.dmx

import baaahs.app.ui.appContext
import baaahs.scene.EditingController
import baaahs.scene.MutableFixtureMapping
import baaahs.ui.checked
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import kotlinx.html.js.onChangeFunction
import materialui.components.formcontrollabel.formControlLabel
import materialui.components.switches.switch
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.useContext

private val DmxTransportConfigEditorView =
    xComponent<DmxTransportConfigEditorProps>("DmxTransportConfigEditor") { props ->
        val appContext = useContext(appContext)
        val styles = appContext.allStyles.controllerEditor

        val mutableConfig = props.mutableFixtureMapping.transportConfig as MutableDmxTransportConfig?
        mutableConfig!!

        with (appContext.allStyles.modelEditor) {
            div(+styles.dmxTransportConfigEditorRow) {
                numberTextField("Start Channel", mutableConfig.startChannel, onChange = {
                    mutableConfig.startChannel = it
                    props.editingController.onChange()
                })

                numberTextField("End Channel", mutableConfig.endChannel, onChange = {
                    mutableConfig.endChannel = it
                    props.editingController.onChange()
                })
            }

            div(+styles.dmxTransportConfigEditorRow) {
                formControlLabel {
                    attrs.label { +"Components start at universe boundaries" }
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

            div(+styles.dmxTransportConfigEditorRow) {
                formControlLabel {
                    attrs.label { +"Start in a fresh universe" }
                    attrs.control {
                        switch {
                            attrs.checked = false // TODO mutableConfig.fixtureStartsInFreshUniverse
                            attrs.onChangeFunction = {
//                                val value = it.target.checked
//                                mutableConfig.componentsStartAtUniverseBoundaries = value
//                                props.editingController.onChange()
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