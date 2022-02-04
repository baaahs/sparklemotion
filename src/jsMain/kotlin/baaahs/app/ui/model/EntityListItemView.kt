package baaahs.app.ui.model

import baaahs.app.ui.appContext
import baaahs.scene.MutableEntity
import baaahs.scene.MutableGroupEntity
import baaahs.ui.on
import baaahs.ui.xComponent
import kotlinx.html.js.onMouseDownFunction
import materialui.components.list.enums.ListStyle
import materialui.components.list.list
import materialui.components.listitem.listItem
import materialui.components.listitemtext.listItemText
import react.*

private val EntityListItemView: FunctionComponent<EntityListItemProps> = xComponent("EntityListItem") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.modelEditor

    val mutableEntity = props.mutableEntity
    val handleClick by eventHandler(props.onSelect, mutableEntity) {
        props.onSelect(mutableEntity)
        it.stopPropagation()
    }

    listItem {
        attrs.button = true
        attrs.selected = mutableEntity == props.selectedMutableEntity
//        attrs.onClickFunction = handleClick
        attrs.onMouseDownFunction = handleClick

        listItemText { +mutableEntity.title }

        if (mutableEntity is MutableGroupEntity) {
            list(styles.entityList on ListStyle.root) {
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
}

external interface EntityListItemProps : Props {
    var mutableEntity: MutableEntity
    var selectedMutableEntity: MutableEntity?
    var onSelect: (MutableEntity) -> Unit
}

fun RBuilder.entityListItem(handler: RHandler<EntityListItemProps>) =
    child(EntityListItemView, handler = handler)