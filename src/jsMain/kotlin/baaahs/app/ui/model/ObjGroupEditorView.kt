package baaahs.app.ui.model

import baaahs.app.ui.appContext
import baaahs.model.ObjGroup
import baaahs.scene.EditingEntity
import baaahs.scene.MutableObjModel
import baaahs.ui.on
import baaahs.ui.xComponent
import materialui.components.container.container
import materialui.components.container.enums.ContainerStyle
import materialui.components.formcontrollabel.formControlLabel
import materialui.components.switches.switch
import materialui.components.textfield.textField
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.header
import react.useContext

private val ObjGroupEditorView = xComponent<ObjGroupEditorProps>("ObjGroupEditor") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.modelEditor

    observe(props.editingEntity)
    val mutableEntity = props.editingEntity.mutableEntity as MutableObjModel
    val entityVisualizer = props.editingEntity.entityVisualizer

    header { +"OBJ Import" }

    container(styles.propertiesEditSection on ContainerStyle.root) {
        formControlLabel {
            attrs.control {
                switch {
                    attrs.checked = mutableEntity.objDataIsFileRef
                }
            }
            attrs.label { +"Is File" }
        }

        textField {
            attrs.label { +"File" }
            attrs.fullWidth = true
            attrs.value(mutableEntity.objData)
        }
    }
}

external interface ObjGroupEditorProps : Props {
    var editingEntity: EditingEntity<ObjGroup>
}

fun RBuilder.objGroupEditor(handler: RHandler<ObjGroupEditorProps>) =
    child(ObjGroupEditorView, handler = handler)