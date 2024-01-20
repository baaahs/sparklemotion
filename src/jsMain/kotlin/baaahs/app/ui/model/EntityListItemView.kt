package baaahs.app.ui.model

import baaahs.app.ui.appContext
import baaahs.scene.MutableEntity
import baaahs.scene.MutableEntityGroup
import baaahs.ui.unaryMinus
import baaahs.ui.xComponent
import js.objects.jso
import mui.material.List
import mui.material.ListItemButton
import mui.material.ListItemText
import react.*

private val EntityListItemView: ComponentType<EntityListItemProps> = xComponent("EntityListItem") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.modelEditor

    val mutableEntity = props.mutableEntity
    val handleClick by mouseEventHandler(props.onSelect, mutableEntity) {
        props.onSelect(mutableEntity)
        it.stopPropagation()
    }

    ListItemButton {
        attrs.selected = mutableEntity == props.selectedMutableEntity
//        attrs.onClickFunction = handleClick
        attrs.onMouseDown = handleClick

        ListItemText { +mutableEntity.title }
    }

    if (mutableEntity is MutableEntityGroup) {
        List {
            attrs.classes = jso { this.root = -styles.entityList }
            mutableEntity.children.forEach { child ->
                entityListItem {
                    attrs.mutableEntity = child
                    attrs.selectedMutableEntity = props.selectedMutableEntity
                    attrs.onSelect = props.onSelect
                }
            }
        }
    }
}

external interface EntityListItemProps : Props {
    var mutableEntity: MutableEntity
    var selectedMutableEntity: MutableEntity?
    var onSelect: (MutableEntity) -> Unit
}

fun RBuilder.entityListItem(handler: RHandler<EntityListItemProps>) =
    child(EntityListItemView, handler = handler)