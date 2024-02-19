package baaahs.app.ui.model

import baaahs.app.ui.appContext
import baaahs.scene.EditingEntity
import baaahs.ui.unaryMinus
import baaahs.ui.value
import baaahs.ui.xComponent
import js.objects.jso
import mui.material.Container
import mui.material.TextField
import mui.system.sx
import react.*
import react.dom.onChange
import web.cssom.FlexDirection

private val TitleAndDescriptionEditorView =
    xComponent<TitleAndDescriptionEditorProps>("TitleAndDescriptionEditor") { props ->
        val appContext = useContext(appContext)
        val styles = appContext.allStyles.modelEditor

        observe(props.editingEntity)
        val mutableEntity = props.editingEntity.mutableEntity

        val handleTitleChange by formEventHandler(mutableEntity, props.editingEntity) {
            mutableEntity.title = it.target.value
            props.editingEntity.onChange()
        }

        val handleDescriptionChange by formEventHandler(mutableEntity, props.editingEntity) {
            mutableEntity.description = it.target.value
            props.editingEntity.onChange()
        }

        Container {
            attrs.classes = jso { this.root = -styles.propertiesEditSection }
            attrs.sx {
                display = web.cssom.Display.flex
                flexDirection = FlexDirection.column
            }

            TextField {
                attrs.label = buildElement { +"Title" }
                attrs.fullWidth = true
                attrs.value = mutableEntity.title
                attrs.onChange = handleTitleChange
            }

            TextField {
                attrs.label = buildElement { +"Description" }
                attrs.fullWidth = true
                attrs.value = mutableEntity.description ?: ""
                attrs.onChange = handleDescriptionChange
            }
        }
    }

external interface TitleAndDescriptionEditorProps : Props {
    var editingEntity: EditingEntity<*>
}

fun RBuilder.titleAndDescriptionEditor(handler: RHandler<TitleAndDescriptionEditorProps>) =
    child(TitleAndDescriptionEditorView, handler = handler)