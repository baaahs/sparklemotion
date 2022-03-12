package baaahs.app.ui.model

import baaahs.app.ui.appContext
import baaahs.model.GridData
import baaahs.scene.EditingEntity
import baaahs.scene.MutableGridData
import baaahs.ui.checked
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
import materialui.components.switches.switch
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.br
import react.dom.header
import react.useContext

private val GridEditorView = xComponent<GridEditorProps>("GridEditor") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.modelEditor

    observe(props.editingEntity)
    val mutableEntity = props.editingEntity.mutableEntity

    val handleDirectionChange by eventHandler(mutableEntity) {
        mutableEntity.direction = GridData.Direction.valueOf(it.target.value)
        props.editingEntity.onChange()
    }

    val handleZigZagChange by eventHandler(mutableEntity) {
        mutableEntity.zigZag = it.target.checked
        props.editingEntity.onChange()
    }

    header { +"Grid" }
    container(styles.transformEditSection on ContainerStyle.root) {
        with(styles) {
            formControlLabel {
                attrs.label { +"Direction" }
                attrs.control {
                    select {
                        attrs.value(mutableEntity.direction.name)
                        attrs.onChangeFunction = handleDirectionChange

                        GridData.Direction.values().forEach { direction ->
                            menuItem {
                                attrs.value = direction.name
                                listItemText { +direction.title }
                            }
                        }
                    }
                }
            }

            br {}

            formControlLabel {
                attrs.control {
                    switch {
                        attrs.checked = mutableEntity.zigZag
                        attrs.onChangeFunction = handleZigZagChange
                    }
                }
                attrs.label { +"Zig Zag" }
            }

            br {}

            numberTextField(
                "Columns", mutableEntity.columns,
                onChange = this@xComponent.namedHandler("columns", mutableEntity) { v: Int ->
                    mutableEntity.columns = v
                    props.editingEntity.onChange()
                })

            numberTextField(
                "Rows", mutableEntity.rows,
                onChange = this@xComponent.namedHandler("rows", mutableEntity) { v: Int ->
                    mutableEntity.rows = v
                    props.editingEntity.onChange()
                })

            br {}

            numberTextField(
                "Column Gap", mutableEntity.columnGap,
                adornment = { +props.editingEntity.modelUnit.display },
                onChange = this@xComponent.namedHandler("columnGap", mutableEntity) { v: Float ->
                    mutableEntity.columnGap = v
                    props.editingEntity.onChange()
                })

            numberTextField(
                "Row Gap", mutableEntity.rowGap,
                adornment = { +props.editingEntity.modelUnit.display },
                onChange = this@xComponent.namedHandler("rowGap", mutableEntity) { v: Float ->
                    mutableEntity.rowGap = v
                    props.editingEntity.onChange()
                })
        }
    }
}

external interface GridEditorProps : Props {
    var editingEntity: EditingEntity<out MutableGridData>
}

fun RBuilder.gridEditor(handler: RHandler<GridEditorProps>) =
    child(GridEditorView, handler = handler)