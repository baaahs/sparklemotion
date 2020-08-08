package baaahs.ui

import ReactAce.Ace.reactAce
import acex.*
import baaahs.JsClock
import baaahs.Time
import baaahs.boundedBy
import baaahs.jsx.ShowControls
import baaahs.jsx.ShowControlsProps
import baaahs.jsx.useResizeListener
import baaahs.show.ShaderChannel
import kotlinext.js.jsObject
import kotlinx.html.js.onClickFunction
import materialui.components.button.button
import materialui.styles.palette.PaletteType
import materialui.styles.palette.type
import materialui.useTheme
import org.w3c.dom.Element
import org.w3c.dom.events.Event
import react.*
import react.dom.div
import kotlin.browser.window

val ShaderEditor = xComponent<ShaderEditorProps>("ShaderEditor") { props ->
    val rootEl = useRef<Element>()
    val aceEditor = useRef<AceEditor?>()

    val src = ref { "" }
    val srcLastChangedAt = ref<Time?> { null }

    var extractionCandidate by state<ExtractionCandidate?> { null }
    var glslNumberMarker by state<Number?> { null }

    useResizeListener(rootEl) {
        aceEditor.current?.editor?.resize()
    }

    onMount(props.editingShader, aceEditor.current) {
        val editor = aceEditor.current?.editor ?: return@onMount
        val editingShader = props.editingShader

        val compilationObserver = editingShader.addObserver {
            fun setAnnotations(list: List<Annotation>) {
                editor.getSession().setAnnotations(list.toTypedArray())
            }
            when (editingShader.state) {
                EditingShader.State.Changed,
                EditingShader.State.Building,
                EditingShader.State.Success -> setAnnotations(emptyList())

                EditingShader.State.Errors -> {
                    val lineCount = editor.getSession().getLength().toInt()
                    setAnnotations(editingShader.previewShaderBuilder.glslErrors.map { error ->
                        jsObject<Annotation> {
                            row = (error.row).boundedBy(0 until lineCount)
                            column = 0
                            text = error.message
                            type = "error"
                        }
                    })
                }
            }
        }
        withCleanup { compilationObserver.remove() }
    }

    val applySrcChangesDebounced = useCallback(props.editingShader) {
        // Changed since we last passed on updates?
        srcLastChangedAt.current?.let { lastChange ->
            // Changed within .25 seconds?
            if (lastChange < clock.now() - .25) {
                srcLastChangedAt.current = null

                // Update [EditingShader].
                props.editingShader.updateSrc(src.current)
            }
        }
    }

    onMount {
        val interval = window.setInterval(applySrcChangesDebounced, 100)
        withCleanup { window.clearInterval(interval) }
    }

    val handleCodeChange: (String, Any) -> Unit = useCallback { newSrc: String, _: Any ->
        // Change will get picked up soon by [applySrcChangesDebounced].
        src.current = newSrc
        srcLastChangedAt.current = clock.now()
    }

    val glslNumberRegex = Regex("[0-9.]")
    val glslIllegalRegex = Regex("[A-Za-z_]")
    val glslFloatOrIntRegex = Regex("^([0-9]+\\.[0-9]*|[0-9]*\\.[0-9]+|[0-9]+)$")
    val onCursorChange = useCallback { value: Any, _: Any ->
        @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
        val selection = value as acex.Selection
        val session = selection.session
        glslNumberMarker?.let { session.removeMarker(it) }

        val cursor = selection.getCursor()
        val line = session.getDocument().getLine(cursor.row)
        var start = cursor.column as Int
        var end = cursor.column as Int
        while (glslNumberRegex.matches(line[start - 1].toString())) start--
        while (glslNumberRegex.matches(line[end].toString())) end++
        val badCharBefore = start > 0 && glslIllegalRegex.matches(line[start - 1].toString())
        val badCharAfter = end < line.length - 1 && glslIllegalRegex.matches(line[end].toString())
        val candidate = line.substring(start, end)
        val looksLikeFloatOrInt = glslFloatOrIntRegex.matches(candidate)
        if (badCharBefore || badCharAfter || !looksLikeFloatOrInt) {
            extractionCandidate = null
        } else {
            val range = Range(cursor.row, start, cursor.row, end)
            glslNumberMarker = session.addMarker(range, glslNumberClassName, "text", false)
            extractionCandidate = ExtractionCandidate(range, candidate)
        }
    }

    val extractUniform = useCallback { _: Event ->
        val extraction = extractionCandidate ?: return@useCallback

        val editor = aceEditor.current?.editor ?: return@useCallback
        val session = editor.getSession()

        val originalText = extraction.text
        val type = if (originalText.indexOf('.') > -1) "float" else "int"
        val prefix = "${type}Uniform"
        var num = 0
        while (session.getDocument().getValue().indexOf("${prefix}${num}") > -1) num++
        val uniformName = "${prefix}${num}"

        session.markUndoGroup()
        val lastUniform = editor.find("uniform", jsObject {
            needle = "uniform"
            backwards = true
            caseSensitive = true
            wholeWord = "true"
        })

        val max = originalText.toFloat() * 2f

        session.replace(extraction.range, uniformName)
        val insertionRow = lastUniform?.let { it.start.row.toInt() + 1 } ?: 0
        session.insert(
            point(insertionRow, 0),
            "uniform $type $uniformName; // @@Slider default=${originalText} max=${max}\n"
        )
        session.markUndoGroup()
    }

    val editorMode = Modes.glsl
    val editorTheme = when (useTheme().palette.type) {
        PaletteType.light -> Themes.github
        PaletteType.dark -> Themes.tomorrowNightBright
    }

    div(+Styles.shaderEditor) {
        ref = rootEl

        reactAce {
            ref = aceEditor
            attrs {
                mode = editorMode.id
                theme = editorTheme.id
                width = "100%"
                height = "100%"
                showGutter = true
                this.onChange = handleCodeChange
                this.onCursorChange = onCursorChange
                value = props.editingShader.mutableShader.src
                name = "ShaderEditor"
                focus = true
                setOptions = jsObject {
                    autoScrollEditorIntoView = true
                }
                editorProps = jsObject {
                    `$blockScrolling` = true
                }
            }
        }

        extractionCandidate?.let { extraction ->
            div {
                +"Extract ${extraction.text}?"
                button {
                    attrs.onClickFunction = extractUniform
                    +"Sure!"
                }
            }
        }
    }
}


data class ExtractionCandidate(
    val range: Range,
    val text: String
)


private fun point(row: Number, column: Number): Point =
    jsObject { this.row = row; this.column = column }

private val glslNumberClassName = Styles.glslNumber.name
private val clock = JsClock()

external interface ShaderEditorProps : RProps {
    var editingShader: EditingShader
    var shaderChannels: Set<ShaderChannel>
}

fun RBuilder.shaderEditor(handler: RHandler<ShaderEditorProps>) =
    child(ShaderEditor, handler = handler)

fun RBuilder.showControls(handler: RHandler<ShowControlsProps>): ReactElement =
    ShowControls { attrs { handler() } }
