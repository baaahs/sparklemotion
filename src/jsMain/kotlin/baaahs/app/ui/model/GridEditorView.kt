package baaahs.app.ui.model

import baaahs.app.ui.appContext
import baaahs.app.ui.editor.betterSelect
import baaahs.model.GridData
import baaahs.scene.EditingEntity
import baaahs.scene.MutableGridData
import baaahs.ui.*
import js.objects.jso
import mui.material.*
import react.*
import react.dom.br
import react.dom.header

private val GridEditorView = xComponent<GridEditorProps>("GridEditor") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.modelEditor

    observe(props.editingEntity)
    val mutableEntity = props.editingEntity.mutableEntity

    val handleDirectionChange by handler(mutableEntity) { value: GridData.Direction ->
        mutableEntity.direction = value
        props.editingEntity.onChange()
    }

    val handleZigZagChange by changeEventHandler(mutableEntity) {
        mutableEntity.zigZag = it.target.checked
        props.editingEntity.onChange()
    }

    header { +"Grid" }
    Container {
        attrs.classes = jso { this.root = -styles.transformEditSection }
        with(styles) {
            betterSelect<GridData.Direction> {
                attrs.label = "Direction"
                attrs.value = mutableEntity.direction
                attrs.values = GridData.Direction.values().toList()
                attrs.renderValueOption = { it.title.asTextNode() }
                attrs.onChange = handleDirectionChange

                GridData.Direction.values().forEach { direction ->
                    MenuItem {
                        attrs.value = direction.name
                        ListItemText { +direction.title }
                    }
                }
            }

            br {}

            FormControlLabel {
                attrs.control = buildElement {
                    Switch {
                        attrs.checked = mutableEntity.zigZag
                        attrs.onChange = handleZigZagChange.withTChangeEvent()
                    }
                }
                attrs.label = buildElement { +"Zig Zag" }
            }

            br {}

            numberTextField {
                this.attrs.label = "Columns"
                this.attrs.value = mutableEntity.columns
                this.attrs.onChange = this@xComponent.namedHandler("columns", mutableEntity) { v: Int ->
                    mutableEntity.columns = v
                    props.editingEntity.onChange()
                }
            }

            numberTextField {
                this.attrs.label = "Rows"
                this.attrs.value = mutableEntity.rows
                this.attrs.onChange = this@xComponent.namedHandler("rows", mutableEntity) { v: Int ->
                    mutableEntity.rows = v
                    props.editingEntity.onChange()
                }
            }

            br {}

            numberTextField {
                this.attrs.label = "Column Gap"
                this.attrs.value = mutableEntity.columnGap
                this.attrs.adornment = props.editingEntity.modelUnit.display.asTextNode()
                this.attrs.onChange = this@xComponent.namedHandler("columnGap", mutableEntity) { v: Float ->
                    mutableEntity.columnGap = v
                    props.editingEntity.onChange()
                }
            }

            numberTextField {
                this.attrs.label = "Row Gap"
                this.attrs.value = mutableEntity.rowGap
                this.attrs.adornment = props.editingEntity.modelUnit.display.asTextNode()
                this.attrs.onChange = this@xComponent.namedHandler("rowGap", mutableEntity) { v: Float ->
                    mutableEntity.rowGap = v
                    props.editingEntity.onChange()
                }
            }

            br {}

            numberTextField {
                this.attrs.label = "Stagger"
                this.attrs.value = mutableEntity.stagger
                this.attrs.onChange = this@xComponent.namedHandler("stagger", mutableEntity) { v: Int ->
                    if (v != v.toInt()) error("Must be an integer.")
                    if (v < 1) error("Must be a positive integer.")
                    mutableEntity.stagger = v
                    props.editingEntity.onChange()
                }
            }
        }
    }
}

external interface GridEditorProps : Props {
    var editingEntity: EditingEntity<out MutableGridData>
}

fun RBuilder.gridEditor(handler: RHandler<GridEditorProps>) =
    child(GridEditorView, handler = handler)