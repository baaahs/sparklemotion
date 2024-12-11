package baaahs.app.ui.model

import baaahs.app.ui.appContext
import baaahs.app.ui.editor.betterSelect
import baaahs.app.ui.editor.numberFieldEditor
import baaahs.model.GridData
import baaahs.scene.EditingEntity
import baaahs.scene.MutableGridData
import baaahs.ui.and
import baaahs.ui.asTextNode
import baaahs.ui.checked
import baaahs.ui.unaryMinus
import baaahs.ui.withTChangeEvent
import baaahs.ui.xComponent
import mui.material.Box
import mui.material.Container
import mui.material.FormControlLabel
import mui.material.Switch
import mui.system.sx
import react.Props
import react.RBuilder
import react.RHandler
import react.buildElement
import react.useContext
import web.cssom.Display
import web.cssom.GridColumn
import web.cssom.em

private val GridEditorView = xComponent<GridEditorProps>("GridEditor") { props ->
    val appContext = useContext(appContext)
    val editMode = observe(appContext.sceneManager.editMode)
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

    fun <T : Function<*>> namedHandler(name: String, vararg watch: Any?, block: T) =
        this.namedHandler<T>(name, watch = watch, block)

    Container {
        attrs.className = -styles.propertiesEditSection and styles.twoColumns

        Box {
            attrs.sx {
                gridColumn = "span 2".unsafeCast<GridColumn>()
                display = Display.flex
                gap = 1.em
            }

            betterSelect<GridData.Direction> {
                attrs.label = "Direction"
                attrs.disabled = editMode.isOff
                attrs.value = mutableEntity.direction
                attrs.values = GridData.Direction.entries
                attrs.renderValueOption = { it, _ -> it.title.asTextNode() }
                attrs.onChange = handleDirectionChange
            }

            FormControlLabel {
                attrs.control = buildElement {
                    Switch {
                        attrs.disabled = editMode.isOff
                        attrs.checked = mutableEntity.zigZag
                        attrs.onChange = handleZigZagChange.withTChangeEvent()
                    }
                }
                attrs.label = buildElement { +"Zig Zag" }
            }
        }
        
        numberFieldEditor<Int> {
            attrs.label = "Columns"
            attrs.disabled = editMode.isOff
            attrs.isInteger = true
            attrs.isNullable = false
            attrs.getValue = { mutableEntity.columns }
            attrs.setValue = namedHandler("setColumns", mutableEntity) { v: Int ->
                mutableEntity.columns = v
                props.editingEntity.onChange()
            }
        }

        numberFieldEditor<Int> {
            attrs.label = "Rows"
            attrs.disabled = editMode.isOff
            attrs.getValue = namedHandler("getRows", mutableEntity) { mutableEntity.rows }
            attrs.setValue = namedHandler("setRows", mutableEntity) { v: Int ->
                mutableEntity.rows = v
                props.editingEntity.onChange()
            }
        }

        numberFieldEditor<Float> {
            attrs.label = "Column Gap"
            attrs.disabled = editMode.isOff
            attrs.adornment = props.editingEntity.modelUnit.display.asTextNode()
            attrs.getValue = namedHandler("getColumnGap", mutableEntity) { mutableEntity.columnGap }
            attrs.setValue = namedHandler("setColumnGap", mutableEntity) { v: Float ->
                mutableEntity.columnGap = v
                props.editingEntity.onChange()
            }
        }

        numberFieldEditor<Float> {
            attrs.label = "Row Gap"
            attrs.disabled = editMode.isOff
            attrs.adornment = props.editingEntity.modelUnit.display.asTextNode()
            attrs.getValue = namedHandler("getRowGap", mutableEntity) { mutableEntity.rowGap }
            attrs.setValue = namedHandler("setRowGap", mutableEntity) { v: Float ->
                mutableEntity.rowGap = v
                props.editingEntity.onChange()
            }
        }

        /**
         * TODO: Implement this?
         * The idea is that pixels could be shifted like so:
         *     stagger period = 2
         *       *   *   *   *
         *         *   *   *   *
         *     stagger period = 3
         *       *  *  *  *
         *        *  *  *  *
         *         *  *  *  *
         */
//        numberTextField<Int> {
//            attrs.label = "Stagger Period"
//            attrs.disabled = editMode.isOff
//            attrs.value = mutableEntity.stagger
//            attrs.onChange = namedHandler("stagger", mutableEntity) { v: Int ->
//                if (v != v.toInt()) error("Must be an integer.")
//                if (v < 1) error("Must be a positive integer.")
//                mutableEntity.stagger = v
//                props.editingEntity.onChange()
//            }
//        }
    }
}

external interface GridEditorProps : Props {
    var editingEntity: EditingEntity<out MutableGridData>
}

fun RBuilder.gridEditor(handler: RHandler<GridEditorProps>) =
    child(GridEditorView, handler = handler)