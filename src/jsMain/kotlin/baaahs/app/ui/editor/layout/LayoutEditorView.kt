package baaahs.app.ui.editor.layout

import baaahs.app.ui.CommonIcons
import baaahs.app.ui.appContext
import baaahs.show.mutable.MutableGridTab
import baaahs.show.mutable.MutableLegacyTab
import baaahs.show.mutable.MutableShow
import baaahs.ui.*
import js.core.jso
import materialui.icon
import mui.icons.material.Delete
import mui.material.Tab
import mui.material.Tabs
import org.w3c.dom.events.Event
import react.*
import react.dom.div

private val LayoutEditorView = xComponent<LayoutEditorProps>("LayoutEditor") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.layoutEditor

    var currentTabIndex by state { 0 }
    val mutableLayouts = props.mutableShow.layouts
    val mutableLayout = mutableLayouts.formats[props.format]!!
    val currentTab = mutableLayout.tabs[currentTabIndex]

    val handleTabChange by handler { e: Event, value: String ->
        currentTabIndex = value.toInt()
    }

    val handleNewTabClick by mouseEventHandler(mutableLayout, props.onLayoutChange) {
        appContext.prompt(
            Prompt(
                "Create New Tab",
                "Enter a name for your new tab.",
                "",
                fieldLabel = "Tab Name",
                cancelButtonLabel = "Cancel",
                submitButtonLabel = "Create",
                isValid = { name ->
                    if (name.isBlank()) return@Prompt "No name given."

                    if (mutableLayout.tabs.any { it.title == name }) {
                        "Looks like there's already a tab named named \"$name\"."
                    } else null
                },
                onSubmit = { name ->
                    val newTab = MutableGridTab(name)
                    mutableLayout.tabs.add(newTab)
                    props.onLayoutChange(true)
                }
            )
        )
    }

    val handleTabDoubleClick by mouseEventHandler(currentTab, mutableLayout, props.onLayoutChange) {
        appContext.prompt(Prompt(
            "Rename Tab",
            "Enter the name of the tab.",
            defaultValue = currentTab.title,
            fieldLabel = "Tab Name",
            cancelButtonLabel = "Cancel",
            submitButtonLabel = "Rename",
            onSubmit = { name ->
                currentTab.title = name
                props.onLayoutChange(true)
            }
        ))
    }

    val handleTabDeleteClick = mutableLayout.tabs.mapIndexed { index, tab ->
        namedHandler("Delete ${tab.title}", mutableLayout, props.onLayoutChange) { event: Event ->
            if (confirm("Delete tab \"${tab.title}\"?\n\n(You can undo.)")) {
                mutableLayout.tabs.removeAt(index)
                props.onLayoutChange(true)
                event.stopPropagation()
                event.preventDefault()
            }
        }
    }

    div {
        Tabs {
            attrs.value = currentTabIndex.toString()
            attrs.onChange = handleTabChange.asDynamic()

            mutableLayout.tabs.forEachIndexed { index, tab ->
                Tab {
                    attrs.value = index.toString()
                    attrs.label = buildElements {
                        +tab.title

                        child(Delete) {
                            attrs.classes = jso { root = -styles.deleteTabIcon }
                            attrs.onClick = handleTabDeleteClick[index].withMouseEvent()
                        }
                    }
                    attrs.onDoubleClick = handleTabDoubleClick
                }
            }

            Tab {
                attrs.value = currentTabIndex.toString()
                attrs.icon = buildElement { icon(CommonIcons.Add) }
                attrs.onClick = handleNewTabClick
            }
        }

        when (currentTab) {
            is MutableLegacyTab ->
                legacyLayoutEditor {
                    attrs.layouts = mutableLayouts
                    attrs.tab = currentTab
                    attrs.onLayoutChange = props.onLayoutChange
                }

            is MutableGridTab ->
                gridLayoutEditor {
                    attrs.layouts = mutableLayouts
                    attrs.tab = currentTab
                    attrs.onLayoutChange = props.onLayoutChange
                }

        }
    }
}

external interface LayoutEditorProps : Props {
    var mutableShow: MutableShow
    var format: String
    var onLayoutChange: (pushToUndoStack: Boolean) -> Unit
}

fun RBuilder.layoutEditor(handler: RHandler<LayoutEditorProps>) =
    child(LayoutEditorView, handler = handler)