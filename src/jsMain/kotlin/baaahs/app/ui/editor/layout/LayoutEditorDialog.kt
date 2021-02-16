package baaahs.app.ui.editor.layout

import ReactAce.Ace.reactAce
import acex.AceEditor
import baaahs.app.ui.appContext
import baaahs.show.Layout
import baaahs.show.Layouts
import baaahs.show.mutable.MutableLayouts
import baaahs.ui.*
import baaahs.ui.Styles.previewBar
import baaahs.window
import kotlinext.js.jsObject
import kotlinx.css.Display
import kotlinx.css.GridTemplateColumns
import kotlinx.css.display
import kotlinx.css.gridTemplateColumns
import kotlinx.html.js.onClickFunction
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import materialui.components.button.button
import materialui.components.button.enums.ButtonColor
import materialui.components.dialog.dialog
import materialui.components.dialog.enums.DialogStyle
import materialui.components.dialogactions.dialogActions
import materialui.components.dialogcontent.dialogContent
import materialui.components.dialogcontenttext.dialogContentText
import materialui.components.dialogtitle.dialogTitle
import materialui.components.tab.tab
import materialui.components.tabs.tabs
import materialui.icon
import materialui.icons.Icons
import materialui.toggleButton
import org.w3c.dom.events.Event
import react.*
import react.dom.div
import styled.css
import styled.inlineStyles
import styled.styledDiv

val LayoutEditorDialog = xComponent<LayoutEditorDialogProps>("LayoutEditorWindow") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.layoutEditor

    val aceEditor = ref<AceEditor>()
    val json = memo {
        Json {
            isLenient = true
            prettyPrint = true
        }
    }
    val mutableLayouts by state { MutableLayouts(props.layouts) }
    var panelNames by state { props.layouts.panelNames }
    var showCode by state { false }
    var errorMessage by state<String?> { null }
    val changed = ref { false }

    val currentFormat = "default"
    val currentTabIndex = 0
    val currentTab = mutableLayouts.formats[currentFormat]!!.tabs[currentTabIndex]

    val formatsSerializer = MapSerializer(String.serializer(), Layout.serializer())
    fun getLayoutsFromJson(): Map<String, Layout> {
        val layoutJson = aceEditor.current.editor.session.getDocument()
        return json.decodeFromString(formatsSerializer, layoutJson.getValue())
    }

    val checkLayout = useCallback {
        if (showCode && changed.current) {
            errorMessage = try {
                panelNames = getLayoutsFromJson().getPanelNames().toList().sorted()
                null
            } catch (e: Exception) {
                e.message
            }
            changed.current = false
        }
    }

    onMount {
        val interval = window.setInterval(checkLayout, 100)
        withCleanup { window.clearInterval(interval) }
    }

    val handleApply = useCallback(props.onApply) { _: Event ->
        if (showCode) {
            val layoutsMap = getLayoutsFromJson()
            val layouts = Layouts(
                layoutsMap.getPanelNames().toList().sorted(),
                layoutsMap
            )
            props.onApply(layouts)
        } else {
            props.onApply(mutableLayouts.build())
        }
    }

    val handleGridChange = handler("handleGridChange", handleApply) {
        changed.current = true
        props.onApply(mutableLayouts.build())
    }

    val handleShowCodeButton =
        useCallback { _: Event -> showCode = !showCode }

    val handleClose =
        useCallback(props.onClose) { _: Event, _: String -> props.onClose() }

    val handleCancel =
        useCallback(props.onClose) { _: Event -> props.onClose() }

    dialog(DraggablePaper.handleClassName on DialogStyle.paper) {
        attrs.open = props.open
        attrs.onClose = handleClose
        attrs.paperComponent(DraggablePaper::class)

        dialogTitle {
            +"Edit Layout…"

            toggleButton {
                attrs["selected"] = showCode
                attrs.onClickFunction = handleShowCodeButton
                icon(Icons.Code)
            }
        }

        dialogContent {
            dialogContentText(null) {
                +"Eventually we'll support tabs, and different layouts for phones/tablets/etc."
            }

            styledDiv {
                css { +previewBar }

                +(errorMessage ?: "Panels: ${panelNames.joinToString(", ")}")
            }

            if (showCode) {
                val jsonStr = json.encodeToString(formatsSerializer, props.layouts.formats)

                reactAce {
                    ref = aceEditor
                    attrs {
                        mode = "glsl"
                        theme = "tomorrow_night_bright"
                        width = "100%"
                        height = "30vh"
                        showGutter = true
                        this.onChange = { _, _ -> changed.current = true }
                        defaultValue = jsonStr
                        name = "LayoutEditorWindow"
                        setOptions = jsObject {
                            autoScrollEditorIntoView = true
                        }
                        editorProps = jsObject {
                            `$blockScrolling` = true
                        }
                    }
                }
            } else {
                tabs {
                    attrs.value = currentFormat

                    tab {
                        attrs.value = "default"
                        attrs.label { +"Default" }
                    }
                }

                div(+styles.editorGrid) {
                    // grid holder
                    inlineStyles {
                        display = Display.grid
                        gridTemplateColumns = GridTemplateColumns(
                            MutableList(currentTab.columns.size + 1) { "1fr" }.joinToString(" ")
                        )
//                        gridTemplateAreas = GridTemplateAreas(areas.joinToString(" ") { "\"$it\"" })
//                        gridTemplateColumns = GridTemplateColumns(currentTab.columns.joinToString(" "))
//                        gridTemplateRows = GridTemplateRows(currentTab.rows.joinToString(" "))
                    }

                    (-1 until currentTab.rows.size).forEach { rowIndex ->
                        (-1 until currentTab.columns.size).forEach { columnIndex ->
                            if (rowIndex == -1 && columnIndex == -1) {
                                // Empty cell at top-left.
                                div {}
                            } else if (rowIndex == -1) {
                                // Cell along the top.
                                layoutSizeCell {
                                    attrs.key = "col$columnIndex"
                                    attrs.dimen = currentTab.columns[columnIndex]
                                    attrs.onChange = handleGridChange
                                }
                            } else if (columnIndex == -1) {
                                // Cell along the left.
                                layoutSizeCell {
                                    attrs.key = "row$rowIndex"
                                    attrs.dimen = currentTab.rows[rowIndex]
                                    attrs.onChange = handleGridChange
                                }
                            } else {
                                layoutAreaCell {
                                    attrs.key = "area$rowIndex,$columnIndex"
                                    attrs.layouts = mutableLayouts
                                    attrs.tab = currentTab
                                    attrs.columnIndex = columnIndex
                                    attrs.rowIndex = rowIndex
                                    attrs.onChange = handleGridChange
                                }
                            }
                        }
                    }
                }
            }
        }

        dialogActions {
            button {
                +"Cancel"
                attrs.color = ButtonColor.secondary
                attrs.onClickFunction = handleCancel
            }
            button {
                +"Apply"
                attrs.disabled = errorMessage != null
                attrs.color = ButtonColor.primary
                attrs.onClickFunction = handleApply
            }
        }
    }
}

private fun Map<String, Layout>.getPanelNames(): Set<String> {
    return mutableSetOf<String>().apply {
        values.forEach { layout ->
            layout.tabs.forEach { tab ->
                addAll(tab.areas)
            }
        }
    }
}

external interface LayoutEditorDialogProps : RProps {
    var open: Boolean
    var layouts: Layouts
    var onApply: (Layouts) -> Unit
    var onClose: () -> Unit
}

fun RBuilder.layoutEditorDialog(handler: RHandler<LayoutEditorDialogProps>): ReactElement =
    child(LayoutEditorDialog, handler = handler)
