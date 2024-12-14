package baaahs.app.ui.model

import baaahs.app.ui.appContext
import baaahs.geom.EulerAngle
import baaahs.geom.Vector3F
import baaahs.geom.toThreeEuler
import baaahs.scene.EditingEntity
import baaahs.ui.unaryMinus
import baaahs.ui.xComponent
import baaahs.visualizer.toVector3
import mui.material.Container
import mui.material.FormControl
import mui.material.InputLabel
import react.Props
import react.RBuilder
import react.RHandler
import react.buildElement
import react.dom.span
import react.useContext

private val TransformationEditorView = xComponent<TransformationEditorProps>("TransformationEditor") { props ->
    val appContext = useContext(appContext)
    val editMode = observe(appContext.sceneManager.editMode)
    val styles = appContext.allStyles.modelEditor

    observe(props.editingEntity)
    observe(props.editingEntity.affineTransforms)
    val mutableEntity = props.editingEntity.mutableEntity
    val entityVisualizer = props.editingEntity.itemVisualizer

    val handlePositionChange by handler(entityVisualizer, mutableEntity) { value: Vector3F ->
        entityVisualizer?.obj?.position?.copy(value.toVector3())
        mutableEntity.position = value
        props.editingEntity.onChange()
    }

    val handleRotationChange by handler(entityVisualizer, mutableEntity) { value: EulerAngle ->
        entityVisualizer?.obj?.rotation?.copy(value.toThreeEuler())
        mutableEntity.rotation = value
        props.editingEntity.onChange()
    }

    val handleScaleChange by handler(entityVisualizer, mutableEntity) { value: Vector3F ->
        entityVisualizer?.obj?.scale?.copy(value.toVector3())
        mutableEntity.scale = value
        props.editingEntity.onChange()
    }


    Container {
        attrs.className = -styles.transformEditSection

        FormControl {
            attrs.className = -styles.threeColumns
            InputLabel {
                attrs.shrink = true
                +"Position"
            }

            vectorEditor {
                attrs.vector3F = mutableEntity.position
                attrs.disabled = editMode.isOff
                attrs.adornment = buildElement { +props.editingEntity.modelUnit.display }
                attrs.onChange = handlePositionChange
            }
        }
    }

    Container {
        attrs.className = -styles.transformEditSection

        FormControl {
            attrs.className = -styles.threeColumns
            InputLabel {
                attrs.shrink = true
                +"Rotation"
            }
            rotationEditor {
                attrs.eulerAngle = mutableEntity.rotation
                attrs.disabled = editMode.isOff
                attrs.onChange = handleRotationChange
            }
        }
    }

    Container {
        attrs.className = -styles.transformEditSection

        FormControl {
            attrs.className = -styles.threeColumns
            InputLabel {
                attrs.shrink = true
                +"Scale"
            }

            vectorEditor {
                attrs.vector3F = mutableEntity.scale
                attrs.disabled = editMode.isOff
                attrs.adornment = buildElement {
                    span { +Typography.times.toString() }
                }
                attrs.onChange = handleScaleChange
            }
        }
    }
}

external interface TransformationEditorProps : Props {
    var editingEntity: EditingEntity<*>
}

fun RBuilder.transformationEditor(handler: RHandler<TransformationEditorProps>) =
    child(TransformationEditorView, handler = handler)