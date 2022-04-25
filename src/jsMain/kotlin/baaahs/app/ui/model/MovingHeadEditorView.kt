package baaahs.app.ui.model

import baaahs.app.ui.appContext
import baaahs.app.ui.editor.betterSelect
import baaahs.model.MovingHeadAdapter
import baaahs.scene.EditingEntity
import baaahs.scene.MutableMovingHeadData
import baaahs.ui.unaryMinus
import baaahs.ui.xComponent
import kotlinx.js.jso
import mui.material.Container
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.header
import react.useContext

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