package baaahs.app.ui.model

import baaahs.app.ui.appContext
import baaahs.app.ui.editor.betterSelect
import baaahs.model.MovingHeadAdapter
import baaahs.scene.EditingEntity
import baaahs.scene.MutableMovingHeadData
import baaahs.ui.unaryMinus
import baaahs.ui.xComponent
import js.objects.jso
import mui.material.Container
import react.*
import react.dom.header

private val MovingHeadEditorView = xComponent<MovingHeadEditorProps>("MovingHeadEditor") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.modelEditor

    observe(props.editingEntity)
    val mutableEntity = props.editingEntity.mutableEntity

    val handleAdapterChange by handler(mutableEntity) { movingHeadAdapter: MovingHeadAdapter ->
        mutableEntity.adapter = movingHeadAdapter
        props.editingEntity.onChange()
    }

    header { +"Moving Head" }

    Container {
        attrs.classes = jso { this.root = -styles.transformEditSection }
        betterSelect<MovingHeadAdapter> {
            attrs.label = "Adapter"
            attrs.values = MovingHeadAdapter.all
            attrs.renderValueOption = { adapter -> buildElement { +adapter.id } }
            attrs.value = mutableEntity.adapter
            attrs.onChange = handleAdapterChange
        }
    }
}

external interface MovingHeadEditorProps : Props {
    var editingEntity: EditingEntity<out MutableMovingHeadData>
}

fun RBuilder.movingHeadEditor(handler: RHandler<MovingHeadEditorProps>) =
    child(MovingHeadEditorView, handler = handler)