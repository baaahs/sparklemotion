package baaahs.app.ui.model

import baaahs.app.ui.appContext
import baaahs.geom.Vector3F
import baaahs.scene.EditingEntity
import baaahs.scene.MutableHengeData
import baaahs.ui.asTextNode
import baaahs.ui.unaryMinus
import baaahs.ui.xComponent
import kotlinx.js.jso
import mui.material.Container
import react.*
import react.dom.header

private val HengeEditorView = xComponent<HengeEditorProps>("HengeEditor") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.modelEditor

    observe(props.editingEntity)
    val mutableEntity = props.editingEntity.mutableEntity

    // startVertex and endVertex came from light bar and I don't thing we care
//    val handleStartVertexChange by handler(mutableEntity) { position: Vector3F ->
//        mutableEntity.startVertex = position
//        props.editingEntity.onChange()
//    }
//
//    val handleEndVertexChange by handler(mutableEntity) { position: Vector3F ->
//        mutableEntity.endVertex = position
//        props.editingEntity.onChange()
//    }

    // handleLengthChange

    header { +"Henge" }
//
//    Container {
//        attrs.classes = jso { this.root = -styles.transformEditSection }
//        header { +"Start:" }
//
//        vectorEditor {
//            attrs.vector3F = mutableEntity.startVertex
//            attrs.adornment = buildElement { +props.editingEntity.modelUnit.display }
//            attrs.onChange = handleStartVertexChange
//        }
//    }
//
//    Container {
//        attrs.classes = jso { this.root = -styles.transformEditSection }
//        header { +"End:" }
//
//        vectorEditor {
//            attrs.vector3F = mutableEntity.endVertex
//            attrs.adornment = buildElement { +props.editingEntity.modelUnit.display }
//            attrs.onChange = handleEndVertexChange
//        }
//    }
//
//    Container {
//        attrs.classes = jso { this.root = -styles.transformEditSection }
//        header { +"Length:" }
//
//        with(styles) {
//            val length = with (mutableEntity) { (endVertex - startVertex).length() }
//            numberTextField<Float> {
//                this.attrs.label = ""
//                this.attrs.value = length
//                this.attrs.adornment = props.editingEntity.modelUnit.display.asTextNode()
//                this.attrs.onChange = handleLengthChange
//            }
//        }
//    }
}

external interface HengeEditorProps : Props {
    var editingEntity: EditingEntity<out MutableHengeData>
}

fun RBuilder.hengeEditor(handler: RHandler<HengeEditorProps>) =
    child(HengeEditorView, handler = handler)