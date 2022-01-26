package baaahs.app.ui.model

import baaahs.app.ui.appContext
import baaahs.getBang
import baaahs.model.MovingHead
import baaahs.model.MovingHeadAdapter
import baaahs.scene.EditingEntity
import baaahs.scene.MutableMovingHeadData
import baaahs.ui.on
import baaahs.ui.value
import baaahs.ui.xComponent
import kotlinx.html.js.onChangeFunction
import materialui.components.container.container
import materialui.components.container.enums.ContainerStyle
import materialui.components.formcontrollabel.formControlLabel
import materialui.components.listitemtext.listItemText
import materialui.components.menuitem.menuItem
import materialui.components.select.select
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.header
import react.useContext

private val MovingHeadEditorView = xComponent<MovingHeadEditorProps>("MovingHeadEditor") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.modelEditor

    observe(props.editingEntity)
    val mutableEntity = props.editingEntity.mutableEntity as MutableMovingHeadData

    val handleAdapterChange by eventHandler(mutableEntity) {
        mutableEntity.adapter = MovingHeadAdapter.all.getBang(it.target.value, "adapter") as MovingHeadAdapter
        props.editingEntity.onChange()
    }

    header { +"Moving Head" }

    container(styles.transformEditSection on ContainerStyle.root) {
        formControlLabel {
            attrs.label { +"Adapter" }
            attrs.control {
                select {
                    attrs.value(MovingHeadAdapter.all.firstNotNullOf { (name, adapter) ->
                        if (mutableEntity.adapter == adapter) name else null
                    })
                    attrs.onChangeFunction = handleAdapterChange

                    MovingHeadAdapter.all.forEach { (name, adapter) ->
                        menuItem {
                            attrs.value = name
                            listItemText { +name }
                        }
                    }
                }
            }
        }
    }
}

external interface MovingHeadEditorProps : Props {
    var editingEntity: EditingEntity<out MovingHead>
}

fun RBuilder.movingHeadEditor(handler: RHandler<MovingHeadEditorProps>) =
    child(MovingHeadEditorView, handler = handler)