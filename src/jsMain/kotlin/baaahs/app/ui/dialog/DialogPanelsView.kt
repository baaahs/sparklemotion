package baaahs.app.ui.dialog

import baaahs.app.ui.PatchHolderEditorHelpText
import baaahs.app.ui.controls.problemBadge
import baaahs.ui.*
import external.ErrorBoundary
import kotlinx.html.js.onClickFunction
import materialui.components.list.enums.ListStyle
import materialui.components.list.list
import materialui.components.listitem.listItem
import materialui.components.listitemicon.listItemIcon
import materialui.components.listitemtext.listItemText
import materialui.components.listsubheader.enums.ListSubheaderStyle
import materialui.components.listsubheader.listSubheader
import materialui.icon
import react.FunctionComponent
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.dom.header

private val DialogPanelsView: FunctionComponent<DialogPanelsProps> = xComponent("DialogPanels") { props ->
    var selectedPanel by state { props.selectedPanel ?: props.panels.firstOrNull() }
    val showSelectedPanel = if (props.onSelectPanel == null) selectedPanel else props.selectedPanel

    fun RBuilder.recursingList(dialogPanels: List<DialogPanel>) {
        list(DialogStyles.tabsList on ListStyle.root) {
            var previousListSubhead: String? = null
            dialogPanels.forEach { dialogPanel ->
                dialogPanel.listSubhead?.let { subhead ->
                    if (previousListSubhead != subhead) {
                        listSubheader(DialogStyles.tabsSubheader on ListSubheaderStyle.root) { +subhead }
                        previousListSubhead = subhead
                    }
                }

                listItem {
                    attrs.button = true
                    attrs.selected = showSelectedPanel == dialogPanel
                    attrs.onClickFunction = {
                        props.onSelectPanel?.invoke(dialogPanel)
                        selectedPanel = dialogPanel
                    }

                    dialogPanel.icon?.let {
                        listItemIcon(+DialogStyles.tabsListItemIcon) {
                            icon(it)
                        }
                    }

                    listItemText {
                        +dialogPanel.title
                    }

                    dialogPanel.problemLevel?.let {
                        problemBadge(it)
                    }
                }

                val nestedPanels = dialogPanel.getNestedDialogPanels()
                if (nestedPanels.isNotEmpty()) {
                    recursingList(nestedPanels)
                }
            }
        }
    }

    div(+DialogStyles.panel and DialogStyles.columns) {
        div(+DialogStyles.tabsListCol) {
            header {
                +"Tabs"
                help {
                    attrs.divClass = Styles.helpInline.name
                    attrs.inject(PatchHolderEditorHelpText.fixtures)
                }
            }

            recursingList(props.panels)
        }

        div(+DialogStyles.panelCol) {
            showSelectedPanel?.let { dialogPanel ->
                header {
                    +dialogPanel.title

                    help {
                        attrs.divClass = Styles.helpInline.name
                        // TODO: this should come from dialogPanel:
                        attrs.inject(PatchHolderEditorHelpText.patchOverview)
                    }
                }

                val panelView = dialogPanel.getView()
                ErrorBoundary {
                    attrs.FallbackComponent = ErrorDisplay

                    with (panelView) { render() }
                }
            }
        }
    }
}

external interface DialogPanelsProps : Props {
    var panels: List<DialogPanel>
    var selectedPanel: DialogPanel?
    var onSelectPanel: ((DialogPanel) -> Unit)?
}

fun RBuilder.dialogPanels(handler: RHandler<DialogPanelsProps>) =
    child(DialogPanelsView, handler = handler)