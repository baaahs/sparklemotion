package baaahs.ui

import ReactAce.Ace.reactAce
import acex.AceEditor
import acex.Annotation
import acex.Point
import acex.Range
import baaahs.GadgetData
import baaahs.JsClock
import baaahs.Time
import baaahs.app.ui.appContext
import baaahs.boundedBy
import baaahs.glshaders.LinkedPatch
import baaahs.glsl.GlslError
import baaahs.glsl.GlslException
import baaahs.io.Fs
import baaahs.jsx.useResizeListener
import baaahs.show.Shader
import baaahs.show.mutable.MutablePatch
import baaahs.show.mutable.MutableShader
import kotlinext.js.jsObject
import kotlinx.css.px
import kotlinx.html.js.onClickFunction
import materialui.components.button.button
import org.w3c.dom.Element
import org.w3c.dom.events.Event
import react.*
import react.dom.div
import styled.css
import styled.styledDiv
import kotlin.browser.window

val ShaderEditor = xComponent<ShaderEditorProps>("ShaderEditor") { props ->
    val appContext = useContext(appContext)

    val rootEl = useRef<Element>()
    val aceEditor = useRef<AceEditor?>()
    val statusContainerEl = useRef<Element>()
    val activeShader = useRef<EditingShader?>(null)

    var curPatch by state<LinkedPatch?> { null }
    var extractionCandidate by state<ExtractionCandidate?> { null }
    var glslNumberMarker by state<Number?> { null }

    useResizeListener(rootEl) {
        aceEditor.current?.editor?.resize()
    }

    val showGlslErrors = useCallback(aceEditor.current) { glslErrors: Array<GlslError> ->
        val editor = aceEditor.current?.editor ?: return@useCallback
        val lineCount = editor.getSession().getLength().toInt()
        editor.getSession().setAnnotations(
            glslErrors.map { error ->
                jsObject<Annotation> {
                    row = (error.row).boundedBy(0 until lineCount)
                    column = 0
                    text = error.message
                    type = "error"
                }
            }.toTypedArray()
        )
    }

    val maybeUpdatePatch = useCallback(showGlslErrors) {
        val selectedShader = activeShader.current
            ?: return@useCallback
        val needsUpdate = selectedShader.mutablePatch == null
        val notActivelyEditing = selectedShader.lastModified < clock.now() - .25
        val alreadyFoundErrors = selectedShader.glslErrors.isNotEmpty()
        if (needsUpdate && notActivelyEditing && !alreadyFoundErrors) {
            try {
                selectedShader.mutablePatch =
                    appContext.autoWirer
                        .autoWire(Shader(selectedShader.src))
                        .resolve()
            } catch (e: GlslException) {
                selectedShader.glslErrors = e.errors.toTypedArray()
                showGlslErrors(selectedShader.glslErrors)
            } catch (e: Exception) {
                logger.warn(e) { "Failed to analyze shader." }
                selectedShader.glslErrors = arrayOf(GlslError(e.message ?: e.toString()))
                showGlslErrors(selectedShader.glslErrors)
            }
            curPatch = selectedShader.mutablePatch?.openForPreview()
        }
    }

    onMount {
        val interval = window.setInterval(maybeUpdatePatch, 100)
        withCleanup { window.clearInterval(interval) }
    }

    onChange("different shader being edited", props.mutableShader, aceEditor.current) {
        activeShader.current?.lastCursorPosition = aceEditor.current?.editor?.getCursorPosition()

        val selectedShader = props.mutableShader?.let { EditingShader(it) }
        activeShader.current = selectedShader

        if (selectedShader == null) {
            curPatch = null
            return@onChange
        }
        val editor = aceEditor.current?.editor ?: return@onChange

        selectedShader.lastCursorPosition?.let { editor.moveCursorToPosition(it) }
        selectedShader.lastCursorPosition = null
        showGlslErrors(selectedShader.glslErrors)
    }

    val handleChange: (String, Any) -> Unit = useCallback(props.onChange) { newSrc: String, event: Any ->
        activeShader.current?.apply {
            src = newSrc
            isModified = true
            lastModified = clock.now()
            mutablePatch = null
            gadgets = null
            glslErrors = emptyArray()
        }
        props.onChange()
    }

    val glslNumberRegex = Regex("[0-9.]")
    val glslIllegalRegex = Regex("[A-Za-z_]")
    val glslFloatOrIntRegex = Regex("^([0-9]+\\.[0-9]*|[0-9]*\\.[0-9]+|[0-9]+)$")
    val onCursorChange = useCallback { value: Any, event: Any ->
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

    val extractUniform = useCallback { event: Event ->
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

    val handleGlslErrors = useCallback { glslErrors: Array<GlslError> ->
        activeShader.current?.glslErrors = glslErrors
        showGlslErrors(glslErrors)
    }

    val handlePatchPreviewSuccess = useCallback {
        handleGlslErrors(emptyArray())
    }

    val handleGadgetsChange = useCallback { newGadgets: Array<GadgetData> ->
        activeShader.current?.gadgets = newGadgets
        forceRender()
    }




    div(+Styles.shaderEditor) {
        ref = rootEl

        styledDiv {
            css { +Styles.previewBar }

            patchPreview {
                attrs.patch = curPatch
                attrs.onSuccess = handlePatchPreviewSuccess
                attrs.onGadgetsChange = handleGadgetsChange
                attrs.width = 300.px
                attrs.height = 180.px
                attrs.onError = handleGlslErrors
            }
            styledDiv { css { +Styles.status }; ref = statusContainerEl }
            styledDiv {
                css { +Styles.controls }
                showControls { attrs.gadgets = activeShader.current?.gadgets ?: emptyArray() }
            }
        }

        reactAce {
            ref = aceEditor
            attrs {
                mode = "glsl"
                theme = "tomorrow_night_bright"
                width = "100%"
                height = "60vh"
                showGutter = true
                this.onChange = handleChange
                this.onCursorChange = onCursorChange
                value = activeShader.current?.src ?: ""
                name = "ShaderEditor"
                focus = true
                setOptions = jsObject {
                    autoScrollEditorIntoView = true
                }
                editorProps = jsObject {
                    `$blockScrolling` = true
                    readOnly = activeShader.current == null
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

data class EditingShader(
    val mutableShader: MutableShader,
    var isModified: Boolean = false,
    val file: Fs.File? = null
) {
    var title: String = mutableShader.title
    var src: String
        get() = mutableShader.shader.src
        set(value) {
            mutableShader.shader = Shader(value)
        }

    var lastCursorPosition: Point? = null
    var lastModified: Time = clock.now()
    var mutablePatch: MutablePatch? = null
    var gadgets: Array<GadgetData>? = null
    var glslErrors: Array<GlslError> = emptyArray()
}


private fun point(row: Number, column: Number): Point =
    jsObject { this.row = row; this.column = column }

private val glslNumberClassName = Styles.glslNumber.name
private val clock = JsClock()

external interface ShaderEditorProps : RProps {
    var mutableShader: MutableShader?
    var onChange: () -> Unit
}

fun RBuilder.shaderEditor(handler: RHandler<ShaderEditorProps>) =
    child(ShaderEditor, handler = handler)