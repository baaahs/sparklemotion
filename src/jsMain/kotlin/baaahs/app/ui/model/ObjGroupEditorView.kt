package baaahs.app.ui.model

import baaahs.app.ui.CommonIcons
import baaahs.app.ui.appContext
import baaahs.scene.EditingEntity
import baaahs.scene.MutableImportedEntityGroup
import baaahs.ui.*
import kotlinx.js.jso
import materialui.icon
import mui.material.*
import react.*
import react.dom.*

private val ObjGroupEditorView = xComponent<ObjGroupEditorProps>("ObjGroupEditor") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.modelEditor

    observe(props.editingEntity)
    val mutableEntity = props.editingEntity.mutableEntity

    val handleIsFileClick by changeEventHandler(mutableEntity, props.editingEntity) {
        mutableEntity.objDataIsFileRef = it.target.checked
        props.editingEntity.onChange()
    }

    val handleObjDataChange by formEventHandler(mutableEntity, props.editingEntity) {
        mutableEntity.objData = it.target.value
        props.editingEntity.onChange()
    }

    val handleReloadClick by mouseEventHandler(mutableEntity) {
        mutableEntity.reloadFile()
        forceRender()
    }

    header { +"OBJ Import" }

    Container {
        attrs.classes = jso { this.root = -styles.propertiesEditSection }
        FormControlLabel {
            attrs.control = buildElement {
                Switch {
                    attrs.checked = mutableEntity.objDataIsFileRef
                    attrs.onChange = handleIsFileClick.withTChangeEvent()
                }
            }
            attrs.label = buildElement { +"From File" }
        }

        if (mutableEntity.objDataIsFileRef) {
            IconButton {
                attrs.onClick = handleReloadClick
                attrs.title = "Reload"
                icon(CommonIcons.Reload)
            }
        }

        br {}
        if (mutableEntity.objDataIsFileRef) {
            TextField {
                attrs.fullWidth = true
                attrs.onChange = handleObjDataChange
                attrs.value(mutableEntity.objData)
                attrs.label = buildElement { +"File" }
            }
        } else {
            TextField {
                attrs.fullWidth = true
                attrs.multiline = true
                attrs.rows = 6
                attrs.onChange = handleObjDataChange
                attrs.value(mutableEntity.objData)
                attrs.label = buildElement { +"OBJ Data" }
            }
        }

        Container {
            if (mutableEntity.problems.isEmpty()) {
                +"Imported ${mutableEntity.children.size} surfaces."
            } else {
                header { +"Problems Importing…" }
                ul {
                    mutableEntity.problems.forEach {
                        li { +(it.message ?: "Unknown problem.") }
                    }
                }
            }
        }
    }
}

external interface ObjGroupEditorProps : Props {
    var editingEntity: EditingEntity<out MutableImportedEntityGroup>
}

fun RBuilder.objGroupEditor(handler: RHandler<ObjGroupEditorProps>) =
    child(ObjGroupEditorView, handler = handler)