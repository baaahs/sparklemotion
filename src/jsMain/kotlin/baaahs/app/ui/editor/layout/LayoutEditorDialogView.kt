package baaahs.app.ui.editor.layout

import ReactAce.Ace.reactAce
import acex.AceEditor
import baaahs.app.ui.appContext
import baaahs.show.Layout
import baaahs.show.LegacyTab
import baaahs.show.Show
import baaahs.show.mutable.MutableLayout
import baaahs.show.mutable.MutableShow
import baaahs.ui.*
import js.core.jso
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
import react.dom.events.MouseEvent
import react.dom.i
import react.useContext
import web.cssom.ClassName
import web.cssom.Float
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
                getLayoutsFromJson()
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
                        if (showFormat != null) {
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
