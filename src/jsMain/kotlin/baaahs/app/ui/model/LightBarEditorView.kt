package baaahs.app.ui.model

import baaahs.app.ui.appContext
import baaahs.geom.Vector3F
import baaahs.scene.EditingEntity
import baaahs.scene.MutableLightBarData
import baaahs.ui.on
import baaahs.ui.xComponent
import materialui.components.container.container
import materialui.components.container.enums.ContainerStyle
import react.*
import react.dom.header

private val LightBarEditorView = xComponent<LightBarEditorProps>("LightBarEditor") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.modelEditor

    observe(props.editingEntity)
    val mutableEntity = props.editingEntity.mutableEntity

    val handleStartVertexChange by handler(mutableEntity) { position: Vector3F ->
        mutableEntity.startVertex = position
        props.editingEntity.onChange()
    }

    val handleEndVertexChange by handler(mutableEntity) { position: Vector3F ->
        mutableEntity.endVertex = position
        props.editingEntity.onChange()
    }

    val handleLengthChange by handler(mutableEntity) { length: Float ->
        with (mutableEntity) {
            val normal = (endVertex - startVertex).normalize()
            mutableEntity.endVertex = startVertex + normal * length
        }
        props.editingEntity.onChange()
    }

    header { +"Light Bar" }

    container(styles.transformEditSection on ContainerStyle.root) {
        header { +"Start:" }

        vectorEditor {
            attrs.vector3F = mutableEntity.startVertex
            attrs.adornment = buildElement { +props.editingEntity.modelUnit.display }
            attrs.onChange = handleStartVertexChange
        }
    }

    container(styles.transformEditSection on ContainerStyle.root) {
        header { +"End:" }

        vectorEditor {
            attrs.vector3F = mutableEntity.endVertex
            attrs.adornment = buildElement { +props.editingEntity.modelUnit.display }
            attrs.onChange = handleEndVertexChange
        }
    }

    container(styles.transformEditSection on ContainerStyle.root) {
        header { +"Length:" }

        with(styles) {
            val length = with (mutableEntity) { (endVertex - startVertex).length() }
            numberTextField("", length, { +props.editingEntity.modelUnit.display }, onChange = handleLengthChange)
        }
    }
}

external interface LightBarEditorProps : Props {
    var editingEntity: EditingEntity<out MutableLightBarData>
}

fun RBuilder.lightBarEditor(handler: RHandler<LightBarEditorProps>) =
    child(LightBarEditorView, handler = handler)