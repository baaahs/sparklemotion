package baaahs.app.ui.model

import baaahs.app.ui.appContext
import baaahs.geom.Vector3F
import baaahs.scene.EditingEntity
import baaahs.scene.MutableLightBarData
import baaahs.ui.asTextNode
import baaahs.ui.unaryMinus
import baaahs.ui.xComponent
import mui.material.Container
import react.*
import react.dom.header

private val LightBarEditorView = xComponent<LightBarEditorProps>("LightBarEditor") { props ->
    val appContext = useContext(appContext)
    val editMode = observe(appContext.sceneManager.editMode)
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

    if (mutableEntity.startVertex != Vector3F.origin) {
        Container {
            attrs.className = -styles.transformEditSection
            header { +"Start:" }

            vectorEditor {
                attrs.vector3F = mutableEntity.startVertex
                attrs.adornment = buildElement { +props.editingEntity.modelUnit.display }
                attrs.disabled = editMode.isOff
                attrs.onChange = handleStartVertexChange
            }
        }
    }

    Container {
        attrs.className = -styles.transformEditSection
        header { +"End:" }

        vectorEditor {
            attrs.vector3F = mutableEntity.endVertex
            attrs.adornment = buildElement { +props.editingEntity.modelUnit.display }
            attrs.disabled = editMode.isOff
            attrs.onChange = handleEndVertexChange
        }
    }

    Container {
        attrs.className = -styles.transformEditSection
        header { +"Length:" }

        with(styles) {
            val length = with (mutableEntity) { (endVertex - startVertex).length() }
            numberTextField<Float> {
                attrs.label = ""
                attrs.value = length
                attrs.adornment = props.editingEntity.modelUnit.display.asTextNode()
                attrs.onChange = handleLengthChange
                attrs.disabled = editMode.isOff
            }
        }
    }
}

external interface LightBarEditorProps : Props {
    var editingEntity: EditingEntity<out MutableLightBarData>
}

fun RBuilder.lightBarEditor(handler: RHandler<LightBarEditorProps>) =
    child(LightBarEditorView, handler = handler)