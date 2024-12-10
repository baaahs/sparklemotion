package baaahs.app.ui.model

import baaahs.app.ui.appContext
import baaahs.app.ui.editor.numberFieldEditor
import baaahs.geom.Vector3F
import baaahs.scene.EditingEntity
import baaahs.scene.MutableLightBarData
import baaahs.ui.asTextNode
import baaahs.ui.unaryMinus
import baaahs.ui.xComponent
import mui.material.Container
import mui.material.FormControl
import mui.material.InputLabel
import react.Props
import react.RBuilder
import react.RHandler
import react.buildElement
import react.useContext

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

    val handleGetLength by handler(mutableEntity) {
        with(mutableEntity) { (endVertex - startVertex).length() }
    }
    val handleLengthChange by handler(mutableEntity) { length: Float ->
        with (mutableEntity) {
            val normal = (endVertex - startVertex).normalize()
            val newEndVertex = startVertex + normal * length
            if (!newEndVertex.isNan())
                mutableEntity.endVertex = newEndVertex
        }
        props.editingEntity.onChange()
    }

    // Length and entity transformation seems like a simpler way to describe geometry
    //    than startVertex and endVertex.
    if (mutableEntity.startVertex != Vector3F.origin) {
        Container {
            attrs.className = -styles.propertiesEditSection

            FormControl {
                attrs.className = -styles.threeColumns
                InputLabel {
                    attrs.shrink = true
                    +"Start"
                }

                vectorEditor {
                    attrs.vector3F = mutableEntity.startVertex
                    attrs.adornment = buildElement { +props.editingEntity.modelUnit.display }
                    attrs.disabled = editMode.isOff
                    attrs.onChange = handleStartVertexChange
                }
            }
        }
    }

    if (mutableEntity.endVertex.y != 0f || mutableEntity.endVertex.z != 0f) {
        Container {
            attrs.className = -styles.propertiesEditSection
            FormControl {
                attrs.className = -styles.threeColumns
                InputLabel {
                    attrs.shrink = true
                    +"End"
                }

                vectorEditor {
                    attrs.vector3F = mutableEntity.endVertex
                    attrs.adornment = buildElement { +props.editingEntity.modelUnit.display }
                    attrs.disabled = editMode.isOff
                    attrs.onChange = handleEndVertexChange
                }
            }
        }
    }

    Container {
        attrs.className = -styles.propertiesEditSection

        numberFieldEditor {
            attrs.isInteger = false
            attrs.isNullable = false
            attrs.label = "Length"
            attrs.getValue = handleGetLength
            attrs.setValue = handleLengthChange
            attrs.adornment = props.editingEntity.modelUnit.display.asTextNode()
            attrs.onChange = {}
            attrs.disabled = editMode.isOff
        }
    }
}

external interface LightBarEditorProps : Props {
    var editingEntity: EditingEntity<out MutableLightBarData>
}

fun RBuilder.lightBarEditor(handler: RHandler<LightBarEditorProps>) =
    child(LightBarEditorView, handler = handler)