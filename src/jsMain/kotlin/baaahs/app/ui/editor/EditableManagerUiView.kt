package baaahs.app.ui.editor

import baaahs.app.ui.appContext
import baaahs.app.ui.dialog.DialogStyles
import baaahs.app.ui.dialog.dialogPanels
import baaahs.ui.*
import kotlinx.html.js.onSubmitFunction
import kotlinx.js.jso
import materialui.icon
import mui.base.Portal
import mui.material.*
import org.w3c.dom.events.Event
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.dom.form
import react.useContext
import styled.inlineStyles

private val EditableManagerUi = xComponent<EditableManagerUiProps>("EditableManagerUi") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.editableManager

    observe(props.editableManager)

//    val handleTitleChange = baaahs.ui.callback(props.mutablePatchHolder) { event: Event ->
//        props.mutablePatchHolder.title = event.targetEl<HTMLInputElement>().value
//        forceRender()
//    }

    var showModifiedWarning by state { false }

    val handleFormSubmit = callback(props.editableManager) { _: Event ->
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
            attrs.onSubmitFunction = handleFormSubmit

            Drawer {
                attrs.classes = jso { this.paper = -styles.drawer }
                attrs.anchor = DrawerAnchor.bottom
                attrs.variant = DrawerVariant.temporary
                attrs.elevation
                attrs.open = props.editableManager.isEditing()
                attrs.onClose = handleDrawerClose

                DialogTitle {
                    attrs.classes = jso { this.root = -DialogStyles.dialogTitle }
                    typographyH6 { +"Editing ${props.editableManager.uiTitle}" }

                    div(+DialogStyles.dialogTitleButtons) {
                        IconButton {
                            attrs.classes = jso { this.root = -Styles.buttons }
                            icon(mui.icons.material.Undo)
                            attrs.disabled = !props.editableManager.canUndo()
                            attrs.onClick = handleUndo

                            typographyH6 { +"Undo" }
                        }

                        IconButton {
                            attrs.classes = jso { this.root = -Styles.buttons }
                            icon(mui.icons.material.Redo)
                            attrs.disabled = !props.editableManager.canRedo()
                            attrs.onClick = handleRedo

                            typographyH6 { +"Redo" }
                        }
                    }
                }

                dialogContent(+styles.dialogContent) {
                    if (editorPanels.size == 1) {
                        ErrorBoundary {
                            attrs.FallbackComponent = ErrorDisplay

                            val dialogPanel = editorPanels[0]
                            div(+styles.singlePanel) {
                                with (dialogPanel.getView()) { render() }
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
                        formControlLabel {
                            inlineStyles {
                                grow(Grow.GROW)
                                paddingLeft = 1.em
                            }

                            attrs.control {
                                switch {
                                    attrs.checked = props.editableManager.isForceExpanded
                                    attrs.onChangeFunction = {
                                        props.editableManager.isForceExpanded = !props.editableManager.isForceExpanded
                                        this@xComponent.forceRender()
                                    }
                                }
                            }
                            attrs.label { +"Expand" }
                        }
                    }
                    if (showModifiedWarning) {
                        Snackbar {
                            attrs.open = true
                            attrs.onClose = { _, _ -> showModifiedWarning = false }

                            Alert {
                                attrs.severity = AlertColor.error
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