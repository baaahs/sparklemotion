package baaahs.ui

import ReactAce.Ace.IAceOptions
import ReactAce.Ace.IEditorProps
import ReactAce.Ace.reactAce
import acex.AceEditor
import acex.Mode
import acex.Theme
import acex.Themes
import baaahs.JsClock
import baaahs.Time
import baaahs.jsx.useResizeListener
import kotlinext.js.jsObject
import materialui.styles.palette.PaletteType
import materialui.styles.palette.type
import materialui.useTheme
import org.w3c.dom.Element
import react.RBuilder
import react.RHandler
import react.RProps
import react.child
import react.dom.div
import kotlin.browser.window

val TextEditor = xComponent<TextEditorProps>("TextEditor", isPure = true) { props ->
    val rootEl = useRef<Element>()
    val aceEditor = useRef<AceEditor?>()

    val src = ref { "" }
    val srcLastChangedAt = ref<Time?> { null }

    val defaultDebounceSeconds = 0f

    useResizeListener(rootEl) {
        aceEditor.current?.editor?.resize()
    }

    val handleChangeDebounced = useCallback { value: String, event: Any ->
        props.document.content = value

        val debounceSeconds = props.debounceSeconds ?: defaultDebounceSeconds
        if (debounceSeconds <= 0) {
            props.onChange?.invoke(value)
        } else {
            // Change will get picked up soon by [applySrcChangesDebounced].
            src.current = value
            srcLastChangedAt.current = clock.now()
        }

        Unit
    }

    val setOptions = memo { jsObject<IAceOptions> { autoScrollEditorIntoView = true } }
    val editorProps = memo { jsObject<IEditorProps> { `$blockScrolling` = true } }

    onMount {
        val interval = window.setInterval({
            val debounceSeconds = props.debounceSeconds ?: defaultDebounceSeconds

            // Changed since we last passed on updates?
            srcLastChangedAt.current?.let { lastChange ->
                // Changed within .25 seconds?
                if (lastChange < clock.now() - debounceSeconds) {
                    srcLastChangedAt.current = null
                    props.onChange?.invoke(src.current)
                }
            }
        }, 100)
        withCleanup { window.clearInterval(interval) }
    }


    val theme = props.theme ?: when (useTheme().palette.type) {
        PaletteType.light -> Themes.github
        PaletteType.dark -> Themes.tomorrowNightBright
    }

    div(+Styles.textEditor) {
        ref = rootEl

        reactAce {
            ref = aceEditor

            attrs.mode = props.mode?.id ?: error("no mode specified")
            attrs.theme = theme.id
            attrs.width = "100%"
            attrs.height = "100%"
            attrs.showGutter = true
            attrs.onChange = handleChangeDebounced
            props.onCursorChange?.let { attrs.onCursorChange = it }
            attrs.defaultValue = props.document.content
            attrs.name = "ShaderEditor"
            attrs.focus = true
            attrs.setOptions = setOptions
            attrs.editorProps = editorProps
        }
    }
}

class Document(var content: String)

private val clock = JsClock()


external interface TextEditorProps : RProps {
    var document: Document
    var mode: Mode?
    var theme: Theme?
    var onAceEditor: (AceEditor) -> Unit
    var debounceSeconds: Float?
    var onChange: ((value: String) -> Unit)?
    var onCursorChange: ((value: Any, event: Any) -> Unit)?
}

fun RBuilder.textEditor(handler: RHandler<TextEditorProps>) =
    child(TextEditor, handler = handler)
