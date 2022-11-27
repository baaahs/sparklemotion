package baaahs.app.ui.editor.layout

import ReactAce.Ace.reactAce
import acex.AceEditor
import baaahs.app.ui.CommonIcons
import baaahs.app.ui.appContext
import baaahs.show.Layout
import baaahs.show.LegacyTab
import baaahs.show.Panel
import baaahs.show.Show
import baaahs.show.mutable.MutableLayout
import baaahs.show.mutable.MutablePanel
import baaahs.show.mutable.MutableShow
import baaahs.ui.*
import csstype.ClassName
import csstype.Float
import dom.html.HTMLDivElement
import kotlinx.js.jso
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import materialui.icon
import mui.material.*
import mui.system.Breakpoint
import mui.system.sx
import org.w3c.dom.events.Event
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.dom.events.FormEvent
import react.dom.events.MouseEvent
import react.dom.i
import react.dom.onChange
import react.useContext
import web.timers.clearInterval
import web.timers.setInterval
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

private val LayoutEditorDialogView = xComponent<LayoutEditorDialogProps>("LayoutEditorWindow") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.layoutEditor

    val aceEditor = ref<AceEditor>()
    val json = memo {
        Json {
            isLenient = true
            prettyPrint = true
            serializersModule = appContext.plugins.serialModule
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
        val interval = setInterval(checkLayout, 100)
        withCleanup { clearInterval(interval) }
    }

    val handleApplyCode by mouseEventHandler(props.onApply) {
        if (showCode) {
            val layoutsMap = getLayoutsFromJson()
            mutableLayouts.formats.clear()
            layoutsMap.forEach { (formatId, layout) ->
                mutableLayouts.formats[formatId] = MutableLayout(layout, mutableShow.layouts.panels, mutableShow)
            }
        }

        props.onApply(mutableShow, true)
    }

    val handlePanelsClick by mouseEventHandler { currentFormat = null }
    val handleLayoutClicks = memo(mutableLayouts.formats.keys) {
        mutableLayouts.formats.keys.associateWith { { _: MouseEvent<*, *> -> currentFormat = it } }
    }

    val handleLayoutChange by handler(props.onApply) { pushToUndoStack: Boolean ->
        changed.current = true
        props.onApply(mutableShow, pushToUndoStack)
    }

    val handleShowCodeButton by mouseEventHandler { showCode = !showCode }
    val handleDialogClose = callback(props.onClose) { _: Event, _: String -> props.onClose() }
    val handleCloseButton by mouseEventHandler(props.onClose) { props.onClose() }

    Dialog {
        attrs.classes = jso {
            paper = ClassName(+styles.dialog and DraggablePaperHandleClassName)
        }
        attrs.open = props.open
        attrs.maxWidth = Breakpoint.lg
        attrs.onClose = handleDialogClose
        attrs.PaperComponent = DraggablePaper

        DialogTitle {
            +"Edit Layout…"

            ToggleButton {
                attrs.sx {
                    float = Float.right
                }
                attrs.selected = showCode
                attrs.size = Size.small
                attrs.onClick = handleShowCodeButton.withTMouseEvent()
                icon(mui.icons.material.Code)
            }
        }

        DialogContent {
            DialogContentText {
                +"Eventually we'll support tabs, and different layouts for phones/tablets/etc."
            }

            div(+styles.outerContainer) {
                List {
                    ListItemButton {
                        attrs.selected = currentFormat == null
                        attrs.onClick = handlePanelsClick

                        ListItemText { +"Panels" }
                    }

                    ListSubheader {
                        attrs.classes = jso { this.root = -styles.listSubheader }
                        +"Formats:"
                    }
                    mutableLayouts.formats.forEach { (id, layout) ->
                        ListItemButton {
                            attrs.selected = currentFormat == id
                            attrs.onClick = handleLayoutClicks[id]!!

                            ListItemText {
                                val mediaQuery = layout.mediaQuery
                                if (mediaQuery == null) {
                                    i { +"Default" }
                                } else +mediaQuery
                            }
                        }
                    }

                    ListItemButton {
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
                                width = "calc(min(60em, 80vw))"
                                height = "calc(min(30em, 80vh))"
                                showGutter = true
                                this.onChange = { _, _ -> changed.current = true }
                                defaultValue = jsonStr
                                name = "LayoutEditorWindow"
                                setOptions = jso {
                                    autoScrollEditorIntoView = true
                                }
                                editorProps = jso {
                                    `$blockScrolling` = true
                                }
                            }
                        }
                    } else {
                        val showFormat = currentFormat
                        if (showFormat == null) {
                            List {
                                mutableLayouts.panels.forEach { (panelId, panel) ->
                                    ListItem {
                                        ListItemIcon { icon(CommonIcons.Layout) }
                                        TextField {
                                            attrs.autoFocus = false
                                            attrs.fullWidth = true
//                                        attrs.label { +panel.title }
                                            attrs.value = panel.title

                                            // Notify EditableManager of changes as we type, but don't push them to the undo stack...
                                            attrs.onChange = { event: FormEvent<HTMLDivElement> ->
                                                panel.title = event.target.value
                                                props.onApply(mutableShow, false)
                                            }

                                            // ... until we lose focus or hit return; then, push to the undo stack only if the value would change.
//                                        attrs.onBlurFunction = handleBlur
//                                        attrs.onKeyDownFunction = handleKeyDown
                                        }
                                        IconButton {
                                            attrs.onClick = {
                                                if (confirm("Delete panel \"${panel.title}\"? This might be bad news if anything is still placed in it.")) {
                                                    mutableLayouts.panels.remove(panelId)
                                                    props.onApply(mutableShow, true)
                                                }
                                            }
                                            icon(mui.icons.material.Delete)
                                        }
                                    }
                                }

                                ListItemButton {
                                    attrs.onClick = {
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
                                                    props.onApply(mutableShow, true)
                                                }
                                            )
                                        )
                                    }
                                    ListItemIcon { icon(CommonIcons.Add) }
                                    ListItemText { +"New Panel…" }
                                }
                            }
                        } else {
                            layoutEditor {
                                attrs.mutableShow = mutableShow
                                attrs.format = showFormat
                                attrs.onLayoutChange = handleLayoutChange
                            }
                        }
                    }
                }
            }
        }

        DialogActions {
            if (showCode) {
                Button {
                    +"Cancel"
                    attrs.color = ButtonColor.secondary
                    attrs.onClick = handleCloseButton
                }
                Button {
                    +"Apply"
                    attrs.disabled = errorMessage != null
                    attrs.color = ButtonColor.primary
                    attrs.onClick = handleApplyCode
                }
            } else {
                Button {
                    +"Close"
                    attrs.color = ButtonColor.primary
                    attrs.onClick = handleCloseButton
                }
            }
        }
    }
}

private fun Map<String, Layout>.getPanelIds(): Set<String> {
    return mutableSetOf<String>().apply {
        values.forEach { layout ->
            layout.tabs.forEach { tab ->
                tab as LegacyTab
                addAll(tab.areas)
            }
        }
    }
}

external interface LayoutEditorDialogProps : Props {
    var open: Boolean
    var show: Show
    var onApply: (show: MutableShow, pushToUndoStack: Boolean) -> Unit
    var onClose: () -> Unit
}

fun RBuilder.layoutEditorDialog(handler: RHandler<LayoutEditorDialogProps>) =
    child(LayoutEditorDialogView, handler = handler)
