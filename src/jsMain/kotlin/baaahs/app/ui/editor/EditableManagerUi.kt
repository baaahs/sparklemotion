package baaahs.app.ui.editor

import baaahs.app.ui.EditorPanel
import baaahs.app.ui.PatchHolderEditorHelpText
import baaahs.app.ui.controls.problemBadge
import baaahs.ui.*
import external.ErrorBoundary
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
import materialui.components.list.enums.ListStyle
import materialui.components.list.list
import materialui.components.listitem.listItem
import materialui.components.listitemicon.listItemIcon
import materialui.components.listitemtext.listItemText
import materialui.components.listsubheader.enums.ListSubheaderStyle
import materialui.components.listsubheader.listSubheader
import materialui.components.portal.portal
import materialui.components.snackbar.snackbar
import materialui.components.typography.typographyH6
import materialui.icon
import materialui.icons.Icons
import materialui.lab.components.alert.alert
import materialui.lab.components.alert.enums.AlertSeverity
import materialui.lab.components.alerttitle.alertTitle
import org.w3c.dom.events.Event
import react.RBuilder
import react.RHandler
import react.RProps
import react.child
import react.dom.div
import react.dom.form
import react.dom.header

val EditableManagerUi = xComponent<EditableManagerUiProps>("EditableManagerUi") { props ->
    observe(props.editableManager)

//    val handleTitleChange = baaahs.ui.useCallback(props.mutablePatchHolder) { event: Event ->
//        props.mutablePatchHolder.title = event.targetEl<HTMLInputElement>().value
//        forceRender()
//    }

    var showModifiedWarning by state { false }

    val handleFormSubmit = useCallback { _: Event ->
        if (props.editableManager.isModified()) {
            props.editableManager.applyChanges()
        }
    }
    val handleDrawerClose = useCallback { _: Event ->
        if (props.editableManager.isModified()) {
            showModifiedWarning = true
        } else {
            props.editableManager.close()
        }
    }

    val handleUndo = useCallback { _: Event -> props.editableManager.undo() }
    val handleRedo = useCallback { _: Event -> props.editableManager.redo() }

    val handleClose = useCallback { _: Event -> props.editableManager.close() }
    val handleApply = useCallback { _: Event -> props.editableManager.applyChanges() }

    val editorPanels = props.editableManager.editorPanels
    val selectedPanel = props.editableManager.selectedPanel

    fun RBuilder.recursingList(editorPanels: List<EditorPanel>) {
        list(EditableStyles.tabsList on ListStyle.root) {
            var previousListSubhead: String? = null
            editorPanels.forEach { editorPanel ->
                editorPanel.listSubhead?.let { subhead ->
                    if (previousListSubhead != subhead) {
                        listSubheader(EditableStyles.tabsSubheader on ListSubheaderStyle.root) { +subhead }
                        previousListSubhead = subhead
                    }
                }

                listItem {
                    attrs.button = true
                    attrs.selected = selectedPanel == editorPanel
                    attrs.onClickFunction = { props.editableManager.openPanel(editorPanel) }

                    editorPanel.icon?.let {
                        listItemIcon(+EditableStyles.tabsListItemIcon) {
                            icon(it)
                        }
                    }

                    listItemText {
                        +editorPanel.title
                    }

                    editorPanel.problemLevel?.let {
                        problemBadge(it)
                    }
                }

                val nestedPanels = editorPanel.getNestedEditorPanels()
                if (nestedPanels.isNotEmpty()) {
                    recursingList(nestedPanels)
                }
            }
        }
    }

    portal {
        form {
            attrs.onSubmitFunction = handleFormSubmit

            drawer(EditableStyles.drawer on DrawerStyle.paper) {
                attrs.anchor = DrawerAnchor.bottom
                attrs.variant = DrawerVariant.temporary
                attrs.elevation
                attrs.open = props.editableManager.isEditing()
                attrs.onClose = handleDrawerClose

                dialogTitle(+EditableStyles.dialogTitle) {
                    attrs.disableTypography = true
                    typographyH6 { +props.editableManager.uiTitle }

                    div(+EditableStyles.dialogTitleButtons) {
                        iconButton(Styles.buttons on IconButtonStyle.root) {
                            icon(Icons.Undo)
                            attrs["disabled"] = !props.editableManager.canUndo()
                            attrs.onClickFunction = handleUndo

                            typographyH6 { +"Undo" }
                        }

                        iconButton(Styles.buttons on IconButtonStyle.root) {
                            icon(Icons.Redo)
                            attrs["disabled"] = !props.editableManager.canRedo()
                            attrs.onClickFunction = handleRedo

                            typographyH6 { +"Redo" }
                        }
                    }
                }

                div(+EditableStyles.panel and EditableStyles.columns) {
                    div(+EditableStyles.tabsListCol) {
                        header {
                            +"Tabs"
                            help {
                                attrs.divClass = Styles.helpInline.name
                                attrs.inject(PatchHolderEditorHelpText.fixtures)
                            }
                        }

                        recursingList(editorPanels)
                    }

                    div(+EditableStyles.editorCol) {
                        selectedPanel?.let { editorPanel ->
                            header {
                                +editorPanel.title

                                help {
                                    attrs.divClass = Styles.helpInline.name
                                    // TODO: this should come from editorPanel:
                                    attrs.inject(PatchHolderEditorHelpText.patchOverview)
                                }
                            }

                            val panelView = editorPanel.getView()
                            ErrorBoundary {
                                attrs.FallbackComponent = ErrorDisplay

                                with (panelView) { render() }
                            }
                        }
                    }
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
                        attrs["disabled"] = !props.editableManager.isModified()
                        attrs.color = ButtonColor.primary
                        attrs.onClickFunction = handleApply
                    }
                }
            }
        }
    }
}

external interface EditableManagerUiProps : RProps {
    var editMode: Boolean
    var editableManager: EditableManager
}

fun RBuilder.editableManagerUi(handler: RHandler<EditableManagerUiProps>) =
    child(EditableManagerUi, handler = handler)