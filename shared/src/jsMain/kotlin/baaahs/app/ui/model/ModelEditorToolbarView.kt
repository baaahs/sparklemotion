package baaahs.app.ui.model

import baaahs.app.ui.CommonIcons
import baaahs.app.ui.appContext
import baaahs.app.ui.editor.numberFieldEditor
import baaahs.model.EntityData
import baaahs.model.ModelUnit
import baaahs.ui.checked
import baaahs.ui.muiClasses
import baaahs.ui.typographyBody2
import baaahs.ui.unaryMinus
import baaahs.ui.xComponent
import baaahs.visualizer.ModelVisualEditor
import baaahs.visualizer.TransformMode
import js.objects.jso
import materialui.icon
import mui.material.*
import mui.system.*
import react.*
import react.dom.events.MouseEvent
import web.cssom.em
import web.dom.Element

private val ModelEditorToolbarView = xComponent<ModelEditorToolbarProps>("ModelEditorToolbar", true) { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.modelEditor
//    val visualizer = observe(props.visualizer)
    val visualizer = props.visualizer

    var newEntityMenuAnchor by state<Element?> { null }
    val handleNewEntityClick by mouseEventHandler { newEntityMenuAnchor = it.currentTarget as Element? }
    val hideNewEntityMenu by handler { newEntityMenuAnchor = null }

    var optionsMenuAnchor by state<Element?> { null }
    val handleOptionsMenuClick by mouseEventHandler { event -> optionsMenuAnchor = event.currentTarget }
    val handleOptionsMenuClose by handler { optionsMenuAnchor = null }

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

    val handleGetGridSize by handler(transformMode, visualizer) {
        transformMode.toDisplayValue(
            transformMode.getGridSize(visualizer)
                ?: transformMode.defaultGridSize
        )
    }
    val handleSetGridSize by handler(transformMode, visualizer) { value: Double ->
        val newSize = transformMode.fromDisplayValue(value)
        transformMode.setGridSize(visualizer, newSize)
        forceRender()
    }

    val handleNewEntitySelect by handler(props.onAddEntity) { entityType: EntityType ->
        props.onAddEntity(entityType.createNew())
        newEntityMenuAnchor = null
    }

    Slide {
        attrs.`in` = props.visible == true
        attrs.direction = SlideDirection.left

        Paper {
            attrs.className = -styles.visualizerToolbar
            attrs.elevation = 5

//        header("handle") { +"Tools" }

            ToggleButtonGroup {
                attrs.exclusive = true
                attrs.size = Size.small
                attrs.onChange = handleToolChange
                attrs.value = transformMode.modeName

                TransformMode.entries.forEach { theMode ->
                    ToggleButton {
                        attrs.title = theMode.name
                        attrs.value = theMode.modeName
                        icon(theMode.icon)
                    }
                }
            }

            Button {
                attrs.sx { minWidth = 2.em }
                icon(CommonIcons.MoreHoriz)
                attrs.onClick = handleOptionsMenuClick
            }

            Menu {
                attrs.open = optionsMenuAnchor != null
                attrs.autoFocus = true
                attrs.anchorEl = optionsMenuAnchor.asDynamic()
                attrs.onClose = handleOptionsMenuClose.asDynamic()
                attrs.anchorOrigin = jso {
                    horizontal = "right"
                    vertical = "bottom"
                }

                MenuItem {
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

                    numberFieldEditor<Double> {
                        attrs.classes = muiClasses {
                            root = -styles.visualizerNumberInput
                        }
                        attrs.adornment = buildElement {
                            +transformMode.getGridUnitAdornment(props.modelUnit)
                        }
                        attrs.disabled = !gridSnap
                        attrs.getValue = handleGetGridSize
                        attrs.setValue = handleSetGridSize
                    }
                }

                MenuItem {
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
}

external interface ModelEditorToolbarProps : Props {
    var visible: Boolean?
    var visualizer: ModelVisualEditor.Facade
    var modelUnit: ModelUnit
    var onAddEntity: (entityData: EntityData) -> Unit
}

fun RBuilder.modelEditorToolbar(handler: RHandler<ModelEditorToolbarProps>) =
    child(ModelEditorToolbarView, handler = handler)