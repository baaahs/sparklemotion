package baaahs.app.ui.model

import baaahs.app.ui.appContext
import baaahs.scene.EditingEntity
import baaahs.ui.on
import baaahs.ui.value
import baaahs.ui.xComponent
import kotlinx.css.Display
import kotlinx.css.FlexDirection
import kotlinx.css.display
import kotlinx.css.flexDirection
import kotlinx.html.js.onChangeFunction
import materialui.components.container.container
import materialui.components.container.enums.ContainerStyle
import materialui.components.textfield.textField
import react.Props
import react.RBuilder
import react.RHandler
import react.useContext
import styled.inlineStyles

private val TitleAndDescriptionEditorView =
    xComponent<TitleAndDescriptionEditorProps>("TitleAndDescriptionEditor") { props ->
        val appContext = useContext(appContext)
        val styles = appContext.allStyles.modelEditor

        observe(props.editingEntity)
        val mutableEntity = props.editingEntity.mutableEntity

        val handleTitleChange by eventHandler(mutableEntity, props.editingEntity) {
            mutableEntity.title = it.target.value
            props.editingEntity.onChange()
        }

        val handleDescriptionChange by eventHandler(mutableEntity, props.editingEntity) {
            mutableEntity.description = it.target.value
            props.editingEntity.onChange()
        }

        container(styles.propertiesEditSection on ContainerStyle.root) {
            inlineStyles {
                display = Display.flex
                flexDirection = FlexDirection.column
            }

            textField {
                attrs.label { +"Title" }
                attrs.fullWidth = true
                attrs.value(mutableEntity.title)
                attrs.onChangeFunction = handleTitleChange
            }

            textField {
                attrs.label { +"Description" }
                attrs.fullWidth = true
                attrs.value(mutableEntity.description ?: "")
                attrs.onChangeFunction = handleDescriptionChange
            }
        }
    }

external interface TitleAndDescriptionEditorProps : Props {
    var editingEntity: EditingEntity<*>
}

fun RBuilder.titleAndDescriptionEditor(handler: RHandler<TitleAndDescriptionEditorProps>) =
    child(TitleAndDescriptionEditorView, handler = handler)