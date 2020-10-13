package baaahs.app.ui.editor

import acex.*
import baaahs.boundedBy
import baaahs.gl.glsl.GlslType
import baaahs.jsx.ShowControls
import baaahs.jsx.ShowControlsProps
import baaahs.show.mutable.EditingShader
import baaahs.ui.*
import kotlinext.js.jsObject
import kotlinx.html.js.onClickFunction
import materialui.components.button.button
import org.w3c.dom.events.Event
import react.*
import react.dom.div

val ShaderEditor = xComponent<ShaderEditorProps>("ShaderEditor") { props ->
    var aceEditor by state<AceEditor?> { null }

    var extractionCandidate by state<ExtractionCandidate?> { null }
    val glslNumberMarker = ref<Number?> { null }
    val glslDoc = memo(props.editingShader) {
        Document(props.editingShader.id, props.editingShader.mutableShader.src)
    }

    onChange("AceEditor", props.editingShader, aceEditor) {
        val editor = aceEditor?.editor ?: return@onChange

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
                    setAnnotations(editingShader.shaderBuilder.glslErrors.map { error ->
                        jsObject {
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

    val handleSrcChange = memo(props.editingShader) {
        { incoming: String ->
            // Update [EditingShader].
            props.editingShader.updateSrc(incoming)
        }
    }

    val glslNumberRegex = Regex("[0-9.]")
    val glslIllegalRegex = Regex("[A-Za-z_]")
    val glslFloatOrIntRegex = Regex("^([0-9]+\\.[0-9]*|[0-9]*\\.[0-9]+|[0-9]+)$")
    val handleCursorChange = useCallback { value: Any, _: Any ->
        @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
        val selection = value as Selection
        val session = selection.session

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
            glslNumberMarker.current?.let { session.removeMarker(it) }
        } else {
            val prevRange = extractionCandidate?.range
            if (prevRange == null ||
                cursor.row != prevRange.start.row ||
                start != prevRange.start.column ||
                cursor.row != prevRange.end.row ||
                end != prevRange.end.column ||
                extractionCandidate?.text != candidate
            ) {
                val range = Range(cursor.row, start, cursor.row, end)
                glslNumberMarker.current = session.addMarker(range, glslNumberClassName, "text", false)

                extractionCandidate = ExtractionCandidate(range, candidate)
            }
        }
        Unit
    }

    val extractUniform = useCallback { _: Event ->
        val extraction = extractionCandidate ?: return@useCallback

        val editor = aceEditor?.editor ?: return@useCallback
        val session = editor.getSession()

        val originalText = extraction.text
        val type = if (originalText.indexOf('.') > -1) GlslType.Float else GlslType.Int
        val prefix = "${type.glslLiteral}Uniform"
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

    val x = this

    div(+Styles.shaderEditor) {
        textEditor {

            attrs.document = glslDoc
            attrs.mode = Modes.glsl
            attrs.onAceEditor = x.handler("onAceEditor") { incoming: AceEditor -> aceEditor = incoming }
            attrs.debounceSeconds = 0.25f
            attrs.onChange = handleSrcChange
            attrs.onCursorChange = handleCursorChange
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

external interface ShaderEditorProps : RProps {
    var editingShader: EditingShader
}

fun RBuilder.shaderEditor(handler: RHandler<ShaderEditorProps>) =
    child(ShaderEditor, handler = handler)

fun RBuilder.showControls(handler: RHandler<ShowControlsProps>): ReactElement =
    ShowControls { attrs { handler() } }
