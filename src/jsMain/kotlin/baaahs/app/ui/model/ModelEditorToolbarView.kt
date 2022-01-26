package baaahs.app.ui.model

import baaahs.app.ui.CommonIcons
import baaahs.app.ui.appContext
import baaahs.model.EntityData
import baaahs.model.ModelUnit
import baaahs.ui.*
import baaahs.util.CacheBuilder
import baaahs.visualizer.ModelVisualizer
import baaahs.visualizer.TransformMode
import external.react_draggable.Draggable
import kotlinx.html.InputType
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import kotlinx.html.title
import materialui.components.container.container
import materialui.components.formcontrollabel.formControlLabel
import materialui.components.iconbutton.iconButton
import materialui.components.input.enums.InputStyle
import materialui.components.input.input
import materialui.components.inputadornment.enums.InputAdornmentPosition
import materialui.components.listitemtext.listItemText
import materialui.components.menu.menu
import materialui.components.menuitem.menuItem
import materialui.components.popover.enums.PopoverOriginHorizontal
import materialui.components.popover.enums.PopoverOriginVertical
import materialui.components.popover.horizontal
import materialui.components.popover.vertical
import materialui.components.switches.switch
import materialui.components.textfield.textField
import materialui.icon
import materialui.lab.components.togglebutton.toggleButton
import materialui.lab.components.togglebuttongroup.toggleButtonGroup
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventTarget
import react.*
import react.dom.div
import react.dom.header
import kotlin.collections.set

private val ModelEditorToolbarView = xComponent<ModelEditorToolbarProps>("ModelEditorToolbar", true) { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.modelEditor
    val visualizer = props.visualizer

    var newEntityMenuAnchor by state<EventTarget?> { null }
    val handleNewEntityClick by eventHandler { newEntityMenuAnchor = it.currentTarget }
    val hideNewEntityMenu by handler { _: Event, _: String -> newEntityMenuAnchor = null }

    val handleToolChange by handler(visualizer) { _: Event, value: Any? ->
        val modeEnum = TransformMode.find(value as String)
        visualizer.transformMode = modeEnum
        forceRender()
    }

    val handleLocalCoordinatesChange by eventHandler(visualizer) {
        visualizer.transformInLocalSpace = (it.target as HTMLInputElement).checked
        forceRender()
    }

    val transformMode = visualizer.transformMode
    val gridSize = transformMode.getGridSize(visualizer)
    val gridSnap = gridSize != null
    val gridSizeMemo = memo { mutableMapOf<TransformMode, Double?>() }

    val handleGridSnapChange by eventHandler(visualizer, transformMode) {
        val gridEnabled = (it.target as HTMLInputElement).checked
        val priorGridSize = transformMode.getGridSize(visualizer)
        val newSize = if (gridEnabled) {
            priorGridSize
                ?: gridSizeMemo[transformMode]
                ?: transformMode.defaultGridSize
        } else null
        gridSizeMemo[transformMode] = priorGridSize
        transformMode.setGridSize(visualizer, newSize)
        forceRender()
    }

    val handleGridSizeChange by eventHandler(visualizer, transformMode) {
        val newSize = transformMode.fromDisplayValue(it.target.value.toDouble())
        transformMode.setGridSize(visualizer, newSize)
        forceRender()
    }

    val addNewEntityTypeHandlers = memo(EntityTypes) {
        CacheBuilder<EntityType, (Event) -> Unit> {
            { _: Event ->
                props.onAddEntity(it.createNew())
                newEntityMenuAnchor = null
            }
        }
    }

    Draggable {
        attrs.handle = ".handle"

        div(+styles.visualizerToolbar) {
            header("handle") { +"Tools" }

            container {
                iconButton {
                    icon(CommonIcons.Add)
                    attrs.title = "New Entity…"
                    attrs.onClickFunction = handleNewEntityClick
                }

                menu {
                    attrs.getContentAnchorEl = null
                    attrs.anchorEl(newEntityMenuAnchor)
                    attrs.anchorOrigin {
                        horizontal(PopoverOriginHorizontal.left)
                        vertical(PopoverOriginVertical.bottom)
                    }
                    attrs.open = newEntityMenuAnchor != null
                    attrs.onClose = hideNewEntityMenu

                    EntityTypes.forEach { entityType ->
                        menuItem {
                            attrs.onClickFunction = addNewEntityTypeHandlers[entityType]
                            listItemText { +entityType.addNewTitle }
                        }
                    }
                }

                toggleButtonGroup {
                    attrs.exclusive = true
                    attrs.onChange = handleToolChange
                    attrs.value = transformMode.modeName

                    TransformMode.values().forEach { theMode ->
                        toggleButton {
                            attrs.title = theMode.name
                            attrs.value = theMode.modeName
                            icon(theMode.icon)
                        }
                    }
                }
            }

            container {
                formControlLabel {
                    attrs.control {
                        switch {
                            attrs.checked = gridSnap
                            attrs.onClickFunction = handleGridSnapChange
                        }
                    }
                    attrs.label { typographyBody2 { +"Snap to Grid" } }
                }

                textField {
                    attrs.type = InputType.number
                    attrs.InputProps = buildElement {
                        input(+styles.gridSizeInput on InputStyle.input, +styles.partialUnderline on InputStyle.underline) {
                            attrs.endAdornment {
                                attrs.position = InputAdornmentPosition.end
                                +transformMode.getGridUnitAdornment(props.modelUnit)
                            }
                        }
                    }.props.unsafeCast<PropsWithChildren>()
                    attrs.disabled = !gridSnap
                    attrs.onChangeFunction = handleGridSizeChange
                    attrs.value(transformMode.toDisplayValue(gridSize ?: transformMode.defaultGridSize))
                }
            }

            container {
                formControlLabel {
                    attrs.control {
                        switch {
                            attrs.checked = visualizer.transformInLocalSpace
                            attrs.onClickFunction = handleLocalCoordinatesChange
                        }
                    }
                    attrs.label { typographyBody2 { +"Local Coordinates" } }
                }
            }

        }
    }
}

external interface ModelEditorToolbarProps : Props {
    var visualizer: ModelVisualizer.Facade
    var modelUnit: ModelUnit
    var onAddEntity: (entityData: EntityData) -> Unit
}

fun RBuilder.modelEditorToolbar(handler: RHandler<ModelEditorToolbarProps>) =
    child(ModelEditorToolbarView, handler = handler)