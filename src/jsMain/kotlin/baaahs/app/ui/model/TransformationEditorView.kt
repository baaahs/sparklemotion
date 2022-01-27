package baaahs.app.ui.model

import baaahs.app.ui.appContext
import baaahs.geom.EulerAngle
import baaahs.geom.Vector3F
import baaahs.geom.toThreeEuler
import baaahs.scene.EditingEntity
import baaahs.ui.on
import baaahs.ui.xComponent
import baaahs.visualizer.toVector3
import kotlinx.css.em
import kotlinx.css.fontSize
import kotlinx.html.unsafe
import materialui.components.container.container
import materialui.components.container.enums.ContainerStyle
import react.*
import react.dom.header
import react.dom.span
import styled.inlineStyles

private val TransformationEditorView = xComponent<TransformationEditorProps>("TransformationEditor") { props ->
    val appContext = useContext(appContext)
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


    header { +"Transformation" }
    container(styles.transformEditSection on ContainerStyle.root) {
        header { +"Position:" }

        vectorEditor {
            attrs.vector3F = mutableEntity.position
            attrs.adornment = buildElement { +props.editingEntity.modelUnit.display }
            attrs.onChange = handlePositionChange
        }
    }

    container(styles.transformEditSection on ContainerStyle.root) {
        header { +"Rotation:" }

        rotationEditor {
            attrs.eulerAngle = mutableEntity.rotation
            attrs.adornment = buildElement { +"°" }
            attrs.onChange = handleRotationChange
        }
    }

    container(styles.transformEditSection on ContainerStyle.root) {
        header { +"Scale:" }

        vectorEditor {
            attrs.vector3F = mutableEntity.scale
            attrs.adornment = buildElement {
                span {
                    inlineStyles { fontSize = .7.em }
                    attrs.unsafe { +"&#x2715;" }
                }
            }
            attrs.onChange = handleScaleChange
        }
    }
}

external interface TransformationEditorProps : Props {
    var editingEntity: EditingEntity<*>
}

fun RBuilder.transformationEditor(handler: RHandler<TransformationEditorProps>) =
    child(TransformationEditorView, handler = handler)