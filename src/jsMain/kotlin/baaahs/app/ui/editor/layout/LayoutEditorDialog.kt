package baaahs.app.ui.editor.layout

import ReactAce.Ace.reactAce
import acex.AceEditor
import baaahs.app.ui.CommonIcons
import baaahs.app.ui.appContext
import baaahs.show.Layout
import baaahs.show.Panel
import baaahs.show.Show
import baaahs.show.mutable.MutableLayout
import baaahs.show.mutable.MutablePanel
import baaahs.show.mutable.MutableShow
import baaahs.ui.*
import kotlinext.js.jsObject
import kotlinx.css.Float
import kotlinx.css.float
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import materialui.components.button.button
import materialui.components.button.enums.ButtonColor
import materialui.components.button.enums.ButtonSize
import materialui.components.dialog.dialog
import materialui.components.dialog.enums.DialogMaxWidth
import materialui.components.dialog.enums.DialogStyle
import materialui.components.dialogactions.dialogActions
import materialui.components.dialogcontent.dialogContent
import materialui.components.dialogcontenttext.dialogContentText
import materialui.components.dialogtitle.dialogTitle
import materialui.components.iconbutton.iconButton
import materialui.components.list.list
import materialui.components.listitem.listItem
import materialui.components.listitemicon.listItemIcon
import materialui.components.listitemtext.listItemText
import materialui.components.listsubheader.enums.ListSubheaderStyle
import materialui.components.listsubheader.listSubheader
import materialui.components.textfield.textField
import materialui.icon
import materialui.lab.components.togglebutton.toggleButton
import org.w3c.dom.events.Event
import react.*
import react.dom.div
import react.dom.i
import styled.inlineStyles
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

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
    val mutableShow by state { MutableShow(props.show) }
    val mutableLayouts = mutableShow.layouts
    var panelIds = mutableLayouts.panels.keys

    var showCode by state { false }
    var errorMessage by state<String?> { null }
    val changed = ref(false)

    var currentFormat by state<String?> { "default" }

    val formatsSerializer = MapSerializer(String.serializer(), Layout.serializer())
    fun getLayoutsFromJson(): Map<String, Layout> {
        val layoutJson = aceEditor.current!!.editor.session.getDocument()
        return json.decodeFromString(formatsSerializer, layoutJson.getValue())
    }

    val checkLayout = callback {
        if (showCode && changed.current == true) {
            errorMessage = try {
                panelIds = getLayoutsFromJson().getPanelIds().toMutableSet()
                null
            } catch (e: Exception) {
                e.message
            }
            changed.current = false
        }
    }

    onMount {
        val interval = baaahs.window.setInterval(checkLayout, 100)
        withCleanup { baaahs.window.clearInterval(interval) }
    }

    val handleApply = callback(props.onApply) { _: Event ->
        if (showCode) {
            val layoutsMap = getLayoutsFromJson()
            mutableLayouts.formats.clear()
            layoutsMap.forEach { (formatId, layout) ->
                mutableLayouts.formats[formatId] = MutableLayout(layout, mutableShow.layouts.panels)
            }
        }

        props.onApply(mutableShow)
    }

    val handlePanelsClick by eventHandler { _: Event -> currentFormat = null }
    val handleLayoutClicks = memo(mutableLayouts.formats.keys) {
        mutableLayouts.formats.keys.associateWith { { _: Event -> currentFormat = it } }
    }

    val handleGridChange by handler(handleApply) {
        changed.current = true
        props.onApply(mutableShow)
    }

    val handleShowCodeButton =
        callback { _: Event -> showCode = !showCode }

    val handleClose =
        callback(props.onClose) { _: Event, _: String -> props.onClose() }

    val handleCancel =
        callback(props.onClose) { _: Event -> props.onClose() }

    dialog(DraggablePaper.handleClassName on DialogStyle.paper) {
        attrs.open = props.open
        attrs.maxWidth = DialogMaxWidth.lg
        attrs.onClose = handleClose
        attrs.paperComponent(DraggablePaper::class)

        dialogTitle {
            +"Edit Layout…"

            toggleButton {
                inlineStyles {
                    float = Float.right
                }
                attrs["selected"] = showCode
                attrs["size"] = ButtonSize.small.name
                attrs.onClickFunction = handleShowCodeButton
                icon(materialui.icons.Code)
            }
        }

        dialogContent {
            dialogContentText(null) {
                +"Eventually we'll support tabs, and different layouts for phones/tablets/etc."
            }

            div(+styles.outerContainer) {
                list {
                    listItem {
                        attrs.button = true
                        attrs.selected = currentFormat == null
                        attrs.onClickFunction = handlePanelsClick

                        listItemText { +"Panels" }
                    }

                    listSubheader(styles.listSubheader on ListSubheaderStyle.root) { +"Formats:" }
                    mutableLayouts.formats.forEach { (id, layout) ->
                        listItem {
                            attrs.button = true
                            attrs.selected = currentFormat == id
                            attrs.onClickFunction = handleLayoutClicks[id]!!

                            listItemText {
                                val mediaQuery = layout.mediaQuery
                                if (mediaQuery == null) {
                                    i { +"Default" }
                                } else +mediaQuery
                            }
                        }
                    }

                    listItem {
                        attrs.button = true
                        attrs.disabled = true
                        +"New Format…"
                    }
                }

                div {
                    if (showCode) {
                        val jsonStr = json.encodeToString(formatsSerializer, props.show.layouts.formats)

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
                        val showFormat = currentFormat
                        if (showFormat == null) {
                            list {
                                mutableLayouts.panels.forEach { (panelId, panel) ->
                                    listItem {
                                        listItemIcon { icon(CommonIcons.Layout) }
                                        textField {
                                            attrs.autoFocus = false
                                            attrs.fullWidth = true
//                                        attrs.label { +panel.title }
                                            attrs.value = panel.title

                                            // Notify EditableManager of changes as we type, but don't push them to the undo stack...
                                            attrs.onChangeFunction = { event: Event ->
                                                panel.title = event.target.value
                                                props.onApply(mutableShow)
                                            }

                                            // ... until we lose focus or hit return; then, push to the undo stack only if the value would change.
//                                        attrs.onBlurFunction = handleBlur
//                                        attrs.onKeyDownFunction = handleKeyDown
                                        }
                                        iconButton {
                                            attrs.onClickFunction = { _: Event ->
                                                if (baaahs.window.confirm("Delete panel \"${panel.title}\"? This might be bad news if anything is still placed in it.")) {
                                                    mutableLayouts.panels.remove(panelId)
                                                    props.onApply(mutableShow)
                                                }
                                            }
                                            icon(materialui.icons.Delete)
                                        }
                                    }
                                }

                                listItem {
                                    attrs.button = true
                                    attrs.onClickFunction = { _: Event ->
                                        appContext.prompt(
                                            Prompt(
                                                "Create New Panel",
                                                "Enter a name for your new panel.",
                                                "",
                                                fieldLabel = "Panel Name",
                                                cancelButtonLabel = "Cancel",
                                                submitButtonLabel = "Create",
                                                isValid = { name ->
                                                    if (name.isBlank()) return@Prompt "No name given."

                                                    val newPanel = Panel(name)
                                                    if (mutableLayouts.panels.containsKey(newPanel.suggestId())) {
                                                        "Looks like there's already a panel named named \"$name\"."
                                                    } else null
                                                },
                                                onSubmit = { name ->
                                                    val newPanel = Panel(name)
                                                    mutableLayouts.panels[newPanel.suggestId()] = MutablePanel(newPanel)
                                                    props.onApply(mutableShow)
                                                }
                                            )
                                        )
                                    }
                                    listItemIcon { icon(CommonIcons.Add) }
                                    listItemText { +"New Panel…" }
                                }
                            }
                        } else {
                            layoutEditor {
                                attrs.mutableShow = mutableShow
                                attrs.format = showFormat
                                attrs.onGridChange = handleGridChange
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

private fun Map<String, Layout>.getPanelIds(): Set<String> {
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
    var show: Show
    var onApply: (MutableShow) -> Unit
    var onClose: () -> Unit
}

fun RBuilder.layoutEditorDialog(handler: RHandler<LayoutEditorDialogProps>): ReactElement =
    child(LayoutEditorDialog, handler = handler)
