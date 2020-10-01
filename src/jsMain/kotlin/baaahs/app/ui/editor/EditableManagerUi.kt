package baaahs.app.ui.editor

import baaahs.app.ui.EditorPanel
import baaahs.app.ui.PatchHolderEditorHelpText
import baaahs.ui.*
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
import materialui.components.list.enums.ListStyle
import materialui.components.list.list
import materialui.components.listitem.listItem
import materialui.components.listitemicon.listItemIcon
import materialui.components.listitemtext.listItemText
import materialui.components.listsubheader.enums.ListSubheaderStyle
import materialui.components.listsubheader.listSubheader
import materialui.components.portal.portal
import materialui.components.typography.typographyH6
import materialui.icon
import materialui.icons.Icons
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

    val handleFormSubmit = useCallback { event: Event -> event.preventDefault() }
    val handleDrawerClose = useCallback { event: Event -> event.preventDefault() }

    val handleUndo = useCallback { event: Event -> props.editableManager.undo() }
    val handleRedo = useCallback { event: Event -> props.editableManager.redo() }

    val handleClose = useCallback { event: Event -> props.editableManager.close() }
    val handleApply = useCallback { event: Event -> props.editableManager.applyChanges() }

    val handleTabClick = useCallback { event: Event -> event.preventDefault() }

    val editorPanels = props.editableManager.editorPanels

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
                    attrs.selected = props.editableManager.selectedPanel == editorPanel
                    attrs.onClickFunction = { props.editableManager.openPanel(editorPanel) }

                    editorPanel.icon?.let {
                        listItemIcon(+EditableStyles.tabsListItemIcon) {
                            icon(it)
                        }
                    }

                    listItemText {
                        +editorPanel.title
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

                dialogTitle {
                    +props.editableManager.uiTitle
//                    textField {
//                        attrs.autoFocus = true
//                        attrs.variant = FormControlVariant.outlined
//                        attrs.label { +"Title" }
//                        attrs.value = props.mutablePatchHolder.title
//                        attrs.onChangeFunction = handleTitleChange
//                    }

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
                        props.editableManager.selectedPanel?.let { editorPanel ->
                            header {
                                +editorPanel.title

                                help {
                                    attrs.divClass = Styles.helpInline.name
                                    attrs.inject(PatchHolderEditorHelpText.patchOverview)
                                }
                            }

                            val panelRenderer = editorPanel.getRenderer(props.editableManager)
                            with (panelRenderer) { render() }
                        }
                    }
                }

                dialogActions {
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

external interface EditableManagerUiProps : RProps {
    var editMode: Boolean
    var editableManager: EditableManager
}

fun RBuilder.editableManagerUi(handler: RHandler<EditableManagerUiProps>) =
    child(EditableManagerUi, handler = handler)