package baaahs.app.ui.editor

import baaahs.app.ui.dialog.DialogStyles
import baaahs.app.ui.dialog.dialogPanels
import baaahs.ui.Styles
import baaahs.ui.on
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onSubmitFunction
import materialui.components.button.button
import materialui.components.button.enums.ButtonColor
import materialui.components.dialogactions.dialogActions
import materialui.components.dialogtitle.dialogTitle
import materialui.components.drawer.drawer
import materialui.components.drawer.enums.DrawerAnchor
import materialui.components.drawer.enums.DrawerStyle
import materialui.components.drawer.enums.DrawerVariant
import materialui.components.iconbutton.enums.IconButtonStyle
import materialui.components.iconbutton.iconButton
import materialui.components.link.link
import materialui.components.portal.portal
import materialui.components.snackbar.snackbar
import materialui.components.typography.typographyH6
import materialui.icon
import materialui.lab.components.alert.alert
import materialui.lab.components.alert.enums.AlertSeverity
import materialui.lab.components.alerttitle.alertTitle
import org.w3c.dom.events.Event
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.dom.form

val EditableManagerUi = xComponent<EditableManagerUiProps>("EditableManagerUi") { props ->
    observe(props.editableManager)

//    val handleTitleChange = baaahs.ui.callback(props.mutablePatchHolder) { event: Event ->
//        props.mutablePatchHolder.title = event.targetEl<HTMLInputElement>().value
//        forceRender()
//    }

    var showModifiedWarning by state { false }

    val handleFormSubmit = callback { _: Event ->
        if (props.editableManager.isModified()) {
            props.editableManager.applyChanges()
        }
    }
    val handleDrawerClose = callback { _: Event ->
        if (props.editableManager.isModified()) {
            showModifiedWarning = true
        } else {
            props.editableManager.close()
        }
    }

    val handleUndo = callback { _: Event -> props.editableManager.undo() }
    val handleRedo = callback { _: Event -> props.editableManager.redo() }

    val handleClose = callback { _: Event -> props.editableManager.close() }
    val handleApply = callback { _: Event -> props.editableManager.applyChanges() }

    val editorPanels = props.editableManager.dialogPanels
    val selectedPanel = props.editableManager.selectedPanel

    portal {
        form {
            attrs.onSubmitFunction = handleFormSubmit

            drawer(EditableStyles.drawer on DrawerStyle.paper) {
                attrs.anchor = DrawerAnchor.bottom
                attrs.variant = DrawerVariant.temporary
                attrs.elevation
                attrs.open = props.editableManager.isEditing()
                attrs.onClose = handleDrawerClose

                dialogTitle(+DialogStyles.dialogTitle) {
                    attrs.disableTypography = true
                    typographyH6 { +"Editing ${props.editableManager.uiTitle}" }

                    div(+DialogStyles.dialogTitleButtons) {
                        iconButton(Styles.buttons on IconButtonStyle.root) {
                            icon(materialui.icons.Undo)
                            attrs.disabled = !props.editableManager.canUndo()
                            attrs.onClickFunction = handleUndo

                            typographyH6 { +"Undo" }
                        }

                        iconButton(Styles.buttons on IconButtonStyle.root) {
                            icon(materialui.icons.Redo)
                            attrs.disabled = !props.editableManager.canRedo()
                            attrs.onClickFunction = handleRedo

                            typographyH6 { +"Redo" }
                        }
                    }
                }

                dialogPanels {
                    attrs.panels = editorPanels
                    attrs.selectedPanel = selectedPanel
                    attrs.onSelectPanel = props.editableManager::openPanel
                }

                dialogActions {
                    if (showModifiedWarning) {
                        snackbar {
                            attrs.open = true
                            attrs.onClose = { _, _ -> showModifiedWarning = false }

                            alert {
                                attrs.severity = AlertSeverity.error
                                attrs.onClose = { showModifiedWarning = false }

                                alertTitle {
                                    link {
                                        attrs.onClickFunction = {
                                            props.editableManager.applyChanges()
                                            props.editableManager.close()
                                        }
                                        +"Apply changes and close"
                                    }
                                    +" or "
                                    link {
                                        attrs.onClickFunction = {
                                            props.editableManager.close()
                                        }
                                        +"abandon changes and close"
                                    }
                                    +"."
                                }
                            }
                        }
                    }

                    button {
                        if (props.editableManager.isModified()) {
                            +"Abandon Changes"
                        } else {
                            +"Close"
                        }
                        attrs.color = ButtonColor.secondary
                        attrs.onClickFunction = handleClose
                    }

                    button {
                        +"Apply"
                        attrs.disabled = !props.editableManager.isModified()
                        attrs.color = ButtonColor.primary
                        attrs.onClickFunction = handleApply
                    }
                }
            }
        }
    }
}

external interface EditableManagerUiProps : Props {
    var editableManager: ShowEditableManager
}

fun RBuilder.editableManagerUi(handler: RHandler<EditableManagerUiProps>) =
    child(EditableManagerUi, handler = handler)