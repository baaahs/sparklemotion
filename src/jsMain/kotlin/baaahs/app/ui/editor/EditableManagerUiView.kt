package baaahs.app.ui.editor

import baaahs.app.ui.appContext
import baaahs.app.ui.dialog.DialogStyles
import baaahs.app.ui.dialog.dialogPanels
import baaahs.ui.*
import external.ErrorBoundary
import js.objects.jso
import mui.base.Portal
import mui.icons.material.Redo
import mui.icons.material.Undo
import mui.material.*
import mui.system.Union
import react.*
import react.dom.div
import react.dom.form
import react.dom.onSubmit
import web.events.Event

private val EditableManagerUi = xComponent<EditableManagerUiProps>("EditableManagerUi") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.editableManager

    observe(props.editableManager)

//    val handleTitleChange = baaahs.ui.callback(props.mutablePatchHolder) { event: Event ->
//        props.mutablePatchHolder.title = event.targetEl<HTMLInputElement>().value
//        forceRender()
//    }

    var showModifiedWarning by state { false }

    val handleFormSubmit by formEventHandler(props.editableManager) { _ ->
        if (props.editableManager.isModified()) {
            props.editableManager.applyChanges()
        }
    }
    val handleDrawerClose = callback(props.editableManager) { _: Event, reason: String ->
        if (props.editableManager.isModified()) {
            showModifiedWarning = true
        } else {
            props.editableManager.close()
        }
    }

    val handleUndo by mouseEventHandler(props.editableManager) { props.editableManager.undo() }
    val handleRedo by mouseEventHandler(props.editableManager) { props.editableManager.redo() }

    val handleClose by mouseEventHandler(props.editableManager) { props.editableManager.close() }
    val handleApply by mouseEventHandler(props.editableManager) { props.editableManager.applyChanges() }

    val editorPanels = props.editableManager.dialogPanels
    val selectedPanel = props.editableManager.selectedPanel

    Portal {
        form {
            attrs.onSubmit = handleFormSubmit

            Drawer {
                attrs.classes = jso { this.paper = -styles.drawer }
                attrs.anchor = DrawerAnchor.bottom
                attrs.variant = DrawerVariant.temporary
                attrs.elevation
                attrs.open = props.editableManager.isEditing()
                attrs.onClose = handleDrawerClose

                DialogTitle {
                    +"Editing ${props.editableManager.uiTitle}"

                    div(+DialogStyles.dialogTitleButtons) {
                        Button {
                            attrs.classes = jso { this.root = -Styles.buttons }
                            attrs.startIcon = +Undo
                            attrs.disabled = !props.editableManager.canUndo()
                            attrs.onClick = handleUndo

                            +"Undo"
                        }

                        Button {
                            attrs.classes = jso { this.root = -Styles.buttons }
                            attrs.startIcon = +Redo
                            attrs.disabled = !props.editableManager.canRedo()
                            attrs.onClick = handleRedo

                            +"Redo"
                        }
                    }
                }

                DialogContent {
                    attrs.classes = jso { this.root = -styles.dialogContent }
                    if (editorPanels.size == 1) {
                        ErrorBoundary {
                            attrs.FallbackComponent = ErrorDisplay

                            val dialogPanel = editorPanels[0]
                            val classes = +styles.singlePanel and
                                    if (dialogPanel.noMargin) styles.singlePanelNoMargin else null
                            div(classes) {
                                dialogPanel.getView().render(this)
                            }
                        }
                    } else {
                        dialogPanels {
                            attrs.panels = editorPanels
                            attrs.selectedPanel = selectedPanel
                            attrs.onSelectPanel = props.editableManager::openPanel
                        }
                    }
                }

                DialogActions {
                    if (editorPanels.size == 1) {
                        FormControlLabel {
                            attrs.classes = jso { this.root = -styles.expandSwitchLabel }

                            attrs.control = buildElement {
                                Switch {
                                    attrs.checked = props.editableManager.isForceExpanded
                                    attrs.onChange = { _, _ ->
                                        props.editableManager.isForceExpanded = !props.editableManager.isForceExpanded
                                        this@xComponent.forceRender()
                                    }
                                }
                            }
                            attrs.label = buildElement { +"Expand" }
                        }
                    }
                    if (showModifiedWarning) {
                        Snackbar {
                            attrs.open = true
                            attrs.onClose = { _, _ -> showModifiedWarning = false }

                            Alert {
                                attrs.severity = AlertColor.error.unsafeCast<Union>()
                                attrs.onClose = { showModifiedWarning = false }

                                AlertTitle {
                                    Link {
                                        attrs.onClick = {
                                            props.editableManager.applyChanges()
                                            props.editableManager.close()
                                        }
                                        +"Apply changes and close"
                                    }
                                    +" or "
                                    Link {
                                        attrs.onClick = {
                                            props.editableManager.close()
                                        }
                                        +"abandon changes and close"
                                    }
                                    +"."
                                }
                            }
                        }
                    }

                    Button {
                        if (props.editableManager.isModified()) {
                            +"Abandon Changes"
                        } else {
                            +"Close"
                        }
                        attrs.color = ButtonColor.secondary
                        attrs.onClick = handleClose
                    }

                    Button {
                        +"Apply"
                        attrs.disabled = !props.editableManager.isModified()
                        attrs.color = ButtonColor.primary
                        attrs.onClick = handleApply
                    }
                }
            }
        }
    }
}

external interface EditableManagerUiProps : Props {
    var editableManager: EditableManager<*>
}

fun RBuilder.editableManagerUi(handler: RHandler<EditableManagerUiProps>) =
    child(EditableManagerUi, handler = handler)