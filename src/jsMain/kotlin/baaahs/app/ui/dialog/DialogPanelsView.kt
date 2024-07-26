package baaahs.app.ui.dialog

import baaahs.app.ui.PatchHolderEditorHelpText
import baaahs.app.ui.controls.problemBadge
import baaahs.ui.*
import external.ErrorBoundary
import js.objects.jso
import materialui.icon
import mui.material.*
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.dom.header

private val DialogPanelsView = xComponent<DialogPanelsProps>("DialogPanels") { props ->
    var selectedPanel by state { props.selectedPanel ?: props.panels.firstOrNull() }
    val showSelectedPanel = if (props.onSelectPanel == null) selectedPanel else props.selectedPanel

    fun RBuilder.recursingList(dialogPanels: List<DialogPanel>) {
        List {
            attrs.classes = jso { this.root = -DialogStyles.tabsList }
            var previousListSubhead: String? = null
            dialogPanels.forEach { dialogPanel ->
                dialogPanel.listSubhead?.let { subhead ->
                    if (previousListSubhead != subhead) {
                        ListSubheader {
                            attrs.classes = jso { this.root = -DialogStyles.tabsSubheader }
                            +subhead
                        }
                        previousListSubhead = subhead
                    }
                }

                ListItemButton {
                    attrs.selected = showSelectedPanel == dialogPanel
                    attrs.onClick = {
                        props.onSelectPanel?.invoke(dialogPanel)
                        selectedPanel = dialogPanel
                    }

                    dialogPanel.icon?.let {
                        ListItemIcon {
                            attrs.classes = jso { this.root = -DialogStyles.tabsListItemIcon }
                            icon(it)
                        }
                    }

                    ListItemText {
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

                    panelView.render(this)
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