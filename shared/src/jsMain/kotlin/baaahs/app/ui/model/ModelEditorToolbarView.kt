package baaahs.app.ui.model

import baaahs.app.ui.CommonIcons
import baaahs.app.ui.appContext
import baaahs.model.EntityData
import baaahs.model.ModelUnit
import baaahs.ui.checked
import baaahs.ui.typographyBody2
import baaahs.ui.unaryMinus
import baaahs.ui.xComponent
import baaahs.util.CacheBuilder
import baaahs.visualizer.ModelVisualEditor
import baaahs.visualizer.TransformMode
import external.react_draggable.Draggable
import js.objects.jso
import materialui.icon
import mui.material.*
import org.w3c.dom.events.Event
import react.*
import react.dom.events.MouseEvent
import react.dom.header
import web.dom.Element

private val ModelEditorToolbarView = xComponent<ModelEditorToolbarProps>("ModelEditorToolbar", true) { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.modelEditor
//    val visualizer = observe(props.visualizer)
    val visualizer = props.visualizer

    var newEntityMenuAnchor by state<Element?> { null }
    val handleNewEntityClick by mouseEventHandler { newEntityMenuAnchor = it.currentTarget as Element? }
    val hideNewEntityMenu by handler { newEntityMenuAnchor = null }

    val handleToolChange by handler(visualizer) { _: MouseEvent<*, *>, value: Any? ->
        val modeEnum = TransformMode.find(value as String)
        visualizer.transformMode = modeEnum
        forceRender()
    }

    val handleLocalCoordinatesChange by mouseEventHandler(visualizer) {
        visualizer.transformInLocalSpace = it.target.checked
        forceRender()
    }

    val transformMode = visualizer.transformMode
    val gridSize = transformMode.getGridSize(visualizer)
    val gridSnap = gridSize != null
    val gridSizeMemo = memo { mutableMapOf<TransformMode, Double?>() }

    val handleGridSnapChange by mouseEventHandler(visualizer, transformMode) {
        val gridEnabled = it.target.checked
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

    val handleGridSizeChange by handler(visualizer, transformMode) { value: Double ->
        val newSize = transformMode.fromDisplayValue(value)
        transformMode.setGridSize(visualizer, newSize)
        forceRender()
    }

    val handleNewEntitySelect by handler(props.onAddEntity) { entityType: EntityType ->
        props.onAddEntity(entityType.createNew())
        newEntityMenuAnchor = null
    }

    Draggable {
        attrs.handle = ".handle"

        Paper {
            attrs.className = -styles.visualizerToolbar
            attrs.elevation = 5

            header("handle") { +"Tools" }

            Container {
                IconButton {
                    icon(CommonIcons.Add)
                    attrs.title = "New Entity…"
                    attrs.onClick = handleNewEntityClick
                }

                newEntityMenu {
                    attrs.menuAnchor = newEntityMenuAnchor
                    attrs.onSelect = handleNewEntitySelect
                    attrs.onClose = hideNewEntityMenu
                }

                ToggleButtonGroup {
                    attrs.exclusive = true
                    attrs.onChange = handleToolChange
                    attrs.value = transformMode.modeName

                    TransformMode.values().forEach { theMode ->
                        ToggleButton {
                            attrs.title = theMode.name
                            attrs.value = theMode.modeName
                            icon(theMode.icon)
                        }
                    }
                }
            }

            Container {
                attrs.className = -styles.visualizerSnapToGrid
                FormControlLabel {
                    attrs.control = buildElement {
                        Switch {
                            attrs.checked = gridSnap
                            attrs.onClick = handleGridSnapChange
                        }
                    }
                    attrs.label = buildElement { typographyBody2 { +"Snap to Grid" } }
                }

                numberTextField<Double> {
                    attrs.adornment = buildElement {
                        +transformMode.getGridUnitAdornment(props.modelUnit)
                    }
                    attrs.disabled = !gridSnap
                    attrs.onChange = handleGridSizeChange
                    attrs.value = transformMode.toDisplayValue(gridSize ?: transformMode.defaultGridSize)
                }
            }

            Container {
                FormControlLabel {
                    attrs.control = buildElement {
                        Switch {
                            attrs.checked = visualizer.transformInLocalSpace
                            attrs.onClick = handleLocalCoordinatesChange
                        }
                    }
                    attrs.label = buildElement { typographyBody2 { +"Local Coordinates" } }
                }
            }

        }
    }
}

external interface ModelEditorToolbarProps : Props {
    var visualizer: ModelVisualEditor.Facade
    var modelUnit: ModelUnit
    var onAddEntity: (entityData: EntityData) -> Unit
}

fun RBuilder.modelEditorToolbar(handler: RHandler<ModelEditorToolbarProps>) =
    child(ModelEditorToolbarView, handler = handler)