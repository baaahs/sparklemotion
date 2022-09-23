package baaahs.app.ui.editor

import acex.*
import acex.Editor
import baaahs.app.ui.appContext
import baaahs.boundedBy
import baaahs.plugin.PluginRef
import baaahs.plugin.Plugins
import baaahs.show.mutable.EditingShader
import baaahs.ui.addObserver
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import kotlinx.css.left
import kotlinx.css.px
import kotlinx.css.top
import kotlinx.html.js.onClickFunction
import kotlinx.html.org.w3c.dom.events.Event
import kotlinx.js.jso
import materialui.icon
import mui.material.Divider
import mui.material.ListItemText
import mui.material.Menu
import mui.material.MenuItem
import org.w3c.dom.Element
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.useContext
import styled.inlineStyles

private val ShaderEditorView = xComponent<ShaderEditorProps>("ShaderEditor") { props ->
    val appContext = useContext(appContext)
    var aceEditor by state<AceEditor?> { null }
    val styles = appContext.allStyles.shaderEditor

    val glslDoc = memo(props.editingShader) {
        Document(props.editingShader.id, props.editingShader.mutableShader.src)
    }

    val pluginCompleter = memo { PluginCompleter(appContext.plugins) }

    val lastSelection = ref<dynamic>()

    onChange("AceEditor", props.editingShader, aceEditor) {
        val editor = aceEditor?.editor ?: return@onChange

        val editingShader = props.editingShader

        // Restore selection if we have a new editor for the same shader.
        if (lastSelection.current != null) {
            editor.selection.fromJSON(lastSelection.current.unsafeCast<SavedSelection>())
        }

        fun setAnnotations() {
            val lineCount = editor.getSession().getLength().toInt()
            val annotations = editingShader.shaderBuilder.glslErrors.map { error ->
                jso<Annotation> {
                    row = (error.row).boundedBy(1 until lineCount) - 1
                    column = 0
                    text = error.message
                    type = "error"
                }
            }.toTypedArray()
            editor.getSession().setAnnotations(annotations)
        }
        setAnnotations()

        editor.completers.asDynamic().push(pluginCompleter)

        val compilationObserver = editingShader.addObserver {
            when (editingShader.state) {
                EditingShader.State.Building,
                EditingShader.State.Success,
                EditingShader.State.Errors -> setAnnotations()
            }
        }
        withCleanup { compilationObserver.remove() }
    }

    val shaderRefactor = memo(props.editingShader, aceEditor?.editor) {
        aceEditor?.editor?.let {
            ShaderRefactor(props.editingShader, it, appContext) { forceRender() }
        }
    }

    val handleSrcChange = memo(props.editingShader) {
        { incoming: String ->
            // Update [EditingShader].
            props.editingShader.updateSrc(incoming)
        }
    }

    val handleCursorChange = callback(shaderRefactor) { value: Any, _: Any ->
        @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
        val selection = value as Selection
        shaderRefactor?.onCursorChange(selection)
        lastSelection.current = selection.toJSON()
    }

    var refactorMenuAnchor by state<Element?> { null }
    val showRefactorMenu = callback { event: Event -> refactorMenuAnchor = event.target as Element? }
    val hideRefactorMenu = callback { _: Event?, _: String? -> refactorMenuAnchor = null}

    val handleExtractUniform by mouseEventHandler(shaderRefactor) {
        hideRefactorMenu(null, null)
        shaderRefactor?.onExtract()
        Unit
    }

    val handleAceEditor by handler { incoming: AceEditor ->
        later { aceEditor = incoming }
    }

    textEditor {
        attrs.document = glslDoc
        attrs.mode = Modes.glsl
        attrs.onAceEditor = handleAceEditor
        attrs.debounceSeconds = 0.25f
        attrs.onChange = handleSrcChange
        attrs.onCursorChange = handleCursorChange
    }

    shaderRefactor?.selectionEndScreenPosition?.let { (x, y) ->
        div(+styles.editorActionMenuAffordance) {
            inlineStyles { top = y.px; left = x.px }
            attrs.onClickFunction = showRefactorMenu

            icon(mui.icons.material.MoreHoriz)
        }
    }

    if (refactorMenuAnchor != null) {
        Menu {
            attrs.anchorEl = refactorMenuAnchor.asDynamic()
            attrs.anchorOrigin = jso {
                horizontal = "left"
                vertical = "top"
            }
            attrs.open = true
            attrs.onClose = hideRefactorMenu

            shaderRefactor?.let {
                it.extractionCandidate?.let { extraction ->
                    MenuItem {
                        attrs.onClick = handleExtractUniform

                        ListItemText { +"Extract ${extraction.text}…" }
                    }
                }
            }

            Divider {}

            MenuItem {
                attrs.disabled = true

                ListItemText { +"Rename…" }
            }
        }
    }
}

class PluginCompleter(private val plugins: Plugins) : Completer {
    val pluginRefs = plugins.dataSourceBuilders.withPlugin
        .filterNot { (_, v) -> v.internalOnly }
//        .sortedBy { (_, v) -> v.title }
//        .map { (plugin, dataSourceBuilder) ->
//            val pluginRef = PluginRef(plugin.packageName, dataSourceBuilder.resourceName)
//            pluginRef.shortRef()
//        }
        .toTypedArray()

    override fun getCompletions(
        editor: Editor,
        session: EditSession,
        position: Point,
        prefix: String,
        callback: CompleterCallback
    ) {
        if (true || prefix.startsWith("@")) {
            callback(
                null.unsafeCast<Any>(),
                plugins.dataSourceBuilders.withPlugin
                    .filterNot { (_, v) -> v.internalOnly }
                    .map { (plugin, dataSourceBuilder) ->
                        val pluginRef = PluginRef(plugin.packageName, dataSourceBuilder.resourceName)
                        val fullRef = pluginRef.toRef()
                        val shortRef = pluginRef.shortRef()
                        val trimmedPrefix = prefix.trimStart('@')

                        jso<Completion> {
                            value = "@@$shortRef"
                            score = if (shortRef.startsWith(trimmedPrefix) || fullRef.startsWith(trimmedPrefix)) 1
                            else if (shortRef.contains(trimmedPrefix) || fullRef.contains(trimmedPrefix)) .5
                            else 0

                            name = dataSourceBuilder.title
                            caption = dataSourceBuilder.description
                        }
                    }
                    .toTypedArray()
            )
        }
    }
}


external interface ShaderEditorProps : Props {
    var editingShader: EditingShader
}

fun RBuilder.shaderEditor(handler: RHandler<ShaderEditorProps>) =
    child(ShaderEditorView, handler = handler)
