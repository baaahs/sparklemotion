package baaahs.app.ui.model

import baaahs.app.ui.CommonIcons
import baaahs.app.ui.appContext
import baaahs.scene.EditingEntity
import baaahs.scene.MutableObjModel
import baaahs.ui.on
import baaahs.ui.value
import baaahs.ui.xComponent
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import kotlinx.html.title
import materialui.components.container.container
import materialui.components.container.enums.ContainerStyle
import materialui.components.formcontrollabel.formControlLabel
import materialui.components.iconbutton.iconButton
import materialui.components.switches.switch
import materialui.components.textfield.textField
import materialui.icon
import org.w3c.dom.HTMLInputElement
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.br
import react.dom.header
import react.dom.li
import react.dom.ul
import react.useContext

private val ObjGroupEditorView = xComponent<ObjGroupEditorProps>("ObjGroupEditor") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.modelEditor

    observe(props.editingEntity)
    val mutableEntity = props.editingEntity.mutableEntity

    val handleIsFileClick by eventHandler(mutableEntity, props.editingEntity) {
        mutableEntity.objDataIsFileRef = (it.target as HTMLInputElement).checked
        props.editingEntity.onChange()
    }

    val handleObjDataChange by eventHandler(mutableEntity, props.editingEntity) {
        mutableEntity.objData = it.target.value
        props.editingEntity.onChange()
    }

    val handleReloadClick by eventHandler(mutableEntity) {
        mutableEntity.reloadFile()
        forceRender()
    }

    header { +"OBJ Import" }

    container(styles.propertiesEditSection on ContainerStyle.root) {
        formControlLabel {
            attrs.control {
                switch {
                    attrs.checked = mutableEntity.objDataIsFileRef
                    attrs.onChangeFunction = handleIsFileClick
                }
            }
            attrs.label { +"From File" }
        }

        if (mutableEntity.objDataIsFileRef) {
            iconButton {
                attrs.onClickFunction = handleReloadClick
                attrs.title = "Reload"
                icon(CommonIcons.Reload)
            }
        }

        br {}
        if (mutableEntity.objDataIsFileRef) {
            textField {
                attrs.fullWidth = true
                attrs.onChangeFunction = handleObjDataChange
                attrs.value(mutableEntity.objData)
                attrs.label { +"File" }
            }
        } else {
            textField {
                attrs.fullWidth = true
                attrs.multiline = true
                attrs.rows = 6
                attrs.onChangeFunction = handleObjDataChange
                attrs.value(mutableEntity.objData)
                attrs.label { +"OBJ Data" }
            }
        }

        container {
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
    var editingEntity: EditingEntity<out MutableObjModel>
}

fun RBuilder.objGroupEditor(handler: RHandler<ObjGroupEditorProps>) =
    child(ObjGroupEditorView, handler = handler)