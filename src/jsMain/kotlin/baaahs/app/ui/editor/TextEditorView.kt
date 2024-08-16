package baaahs.app.ui.editor

import ReactAce.Ace.IAceOptions
import ReactAce.Ace.IEditorProps
import ReactAce.Ace.reactAce
import acex.AceEditor
import acex.Mode
import acex.Theme
import acex.Themes
import baaahs.app.ui.appContext
import baaahs.ui.Styles
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import baaahs.util.useResizeListener
import js.objects.jso
import kotlinx.datetime.Instant
import mui.material.PaletteMode
import mui.material.styles.useTheme
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.dom.events.MouseEventHandler
import react.dom.onContextMenu
import react.useContext
import web.dom.Element
import web.timers.clearInterval
import web.timers.setInterval
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

private val TextEditorView = xComponent<TextEditorProps>("TextEditor", isPure = true) { props ->
    val appContext = useContext(appContext)

    val rootEl = ref<Element>()
    val aceEditor = ref<AceEditor>()
    val src = ref("")
    val srcLastChangedAt = ref<Instant>()

    val defaultDebouncePeriod = 0.seconds

    onMount {
        aceEditor.current?.let { props.onAceEditor(it) }
    }
    aceEditor.current?.let { props.onAceEditor(it) }

    useResizeListener(rootEl) { _, _ ->
        aceEditor.current?.editor?.resize()
    }

    val handleChangeDebounced = callback(
        props.document, props.debouncePeriod, props.onChange
    ) { value: String, _: Any ->
        props.document.content = value

        val debounceSeconds = props.debouncePeriod ?: defaultDebouncePeriod
        if (debounceSeconds <= 0.seconds) {
            props.onChange?.invoke(value)
        } else {
            // Change will get picked up soon by [applySrcChangesDebounced].
            src.current = value
            srcLastChangedAt.current = appContext.clock.now()
        }

        Unit
    }

    val setOptions = memo { jso<IAceOptions> { autoScrollEditorIntoView = true } }
    val editorProps = memo { jso<IEditorProps> { `$blockScrolling` = true } }

    onChange("debouncer", props.onChange, props.debouncePeriod) {
        val interval = setInterval({
            val debounceSeconds = props.debouncePeriod ?: defaultDebouncePeriod

            // Changed since we last passed on updates?
            srcLastChangedAt.current?.let { lastChange ->
                // Changed within .25 seconds?
                if (lastChange < appContext.clock.now() - debounceSeconds) {
                    srcLastChangedAt.current = null
                    props.onChange?.invoke(src.current!!)
                }
            }
        }, 100)
        withCleanup { clearInterval(interval) }
    }


    val theme = props.theme ?: when (val mode = useTheme<mui.material.styles.Theme>().palette.mode) {
        PaletteMode.light -> Themes.github
        PaletteMode.dark -> Themes.tomorrowNightBright
        else -> error("Huh? Unknown palette mode $mode.")
    }

    div(+Styles.textEditor) {
        ref = rootEl

        attrs.onContextMenu = props.onContextClick

        reactAce {
            ref = aceEditor
            key = props.document.key

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

            acex.Extensions.searchBox.install()
        }
    }
}

class Document(val key: String, var content: String)


external interface TextEditorProps : Props {
    var document: Document
    var mode: Mode?
    var theme: Theme?
    var onAceEditor: (AceEditor) -> Unit
    var debouncePeriod: Duration?
    var onChange: ((value: String) -> Unit)?
    var onCursorChange: ((value: Any, event: Any) -> Unit)?
    var onContextClick: MouseEventHandler<*>
}

fun RBuilder.textEditor(handler: RHandler<TextEditorProps>) =
    child(TextEditorView, handler = handler)
