package baaahs.ui

import ReactAce.Ace.reactAce
import acex.AceEditor
import acex.Annotation
import acex.Point
import acex.Range
import baaahs.GadgetData
import baaahs.JsClock
import baaahs.Time
import baaahs.boundedBy
import baaahs.glshaders.AutoWirer
import baaahs.glshaders.GlslAnalyzer
import baaahs.glshaders.OpenPatch
import baaahs.glshaders.Plugins
import baaahs.glsl.GlslError
import baaahs.glsl.GlslException
import baaahs.io.Fs
import baaahs.jsx.ShowControls
import baaahs.jsx.ShowControlsProps
import baaahs.jsx.useResizeListener
import baaahs.show.PatchEditor
import baaahs.ui.Styles.controls
import baaahs.ui.Styles.glslNumber
import baaahs.ui.Styles.previewBar
import baaahs.ui.Styles.status
import kotlinext.js.jsObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.html.js.onClickFunction
import materialui.components.tab.tab
import materialui.components.tabs.enums.TabsVariant
import materialui.components.tabs.tabs
import materialui.components.toolbar.toolbar
import org.w3c.dom.Element
import org.w3c.dom.events.Event
import react.*
import react.dom.button
import react.dom.div
import styled.css
import styled.styledDiv
import kotlin.browser.window
import kotlin.math.min

private val glslNumberClassName = glslNumber.name

val ShaderEditorWindow = xComponent<ShaderEditorWindowProps>("ShaderEditorWindow") { props ->
    val scope = memo { CoroutineScope(Dispatchers.Main) }
    val windowRootEl = useRef<Element>()

    val aceEditor = useRef<AceEditor>()
    val statusContainerEl = useRef<Element>()
    var gadgets by state<Array<GadgetData>> { arrayOf() }
    var openShaders by state<List<EditingShader>> { arrayListOf() }
    var selectedShaderIndex by state { -1 }
    var curPatch by state<OpenPatch?> { null }
    var extractionCandidate by state<ExtractionCandidate?> { null }
    var glslNumberMarker by state<Number?> { null }
    var fileDialogOpen by state { false }
    var fileDialogIsSaveAs by state { false }
    fun selectedShader() = if (selectedShaderIndex == -1) null else openShaders[selectedShaderIndex]

    useResizeListener(windowRootEl) {
        aceEditor.current.editor.resize()
    }

    fun previewShaderOnSimulator(shaderEditor: EditingShader) {
//     TODO   simulator.switchToShow(
//            GlslShow(selectedShow, shader.src, GlslShader.globalRenderContext, true)
//        )
    }

    val showGlslErrors = useCallback(aceEditor) { glslErrors: Array<GlslError> ->
        val editor = aceEditor.current.editor
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

//    sideEffect("show change", selectedShow) {
//        // Look up the text for the show
//        val allShows = BakedInShaders.all.toTypedArray()
//        val currentShow = allShows.find { it.name == contextState.selectedShow }
//
//        if (currentShow != null && currentShow is BakedInShaders.BakedInShader/* && !currentShow.isPreview*/) {
//            val existingShaderIdx = openShaders.indexOfFirst { it.name == selectedShow }
//            val shader = if (existingShaderIdx == -1) {
//                val newShader = OpenShader(currentShow.name, currentShow.src)
//                openShaders += newShader
//                selectedShaderIndex = openShaders.size - 1
//                newShader
//            } else {
//                selectedShaderIndex = existingShaderIdx
//                openShaders[existingShaderIdx]
//            }
//        }
//    }

    val maybeUpdatePatch = useCallback(showGlslErrors) {
        val selectedShader = selectedShader() ?: return@useCallback
        val needsUpdate = selectedShader.patch == null
        val notActivelyEditing = selectedShader.lastModified < clock.now() - .25
        val alreadyFoundErrors = selectedShader.glslErrors.isNotEmpty()
        if (needsUpdate && notActivelyEditing && !alreadyFoundErrors) {
            try {
                selectedShader.patch = AutoWirer(Plugins.findAll()).autoWire(
                    GlslAnalyzer().asShader(selectedShader.src)
                ).resolve()
            } catch (e: GlslException) {
                selectedShader.glslErrors = e.errors.toTypedArray()
                showGlslErrors(selectedShader.glslErrors)
            } catch (e: Exception) {
                logger.warn(e) { "Failed to analyze shader." }
                selectedShader.glslErrors = arrayOf(GlslError(e.message ?: e.toString()))
                showGlslErrors(selectedShader.glslErrors)
            }
            curPatch = selectedShader.patch?.open()
        }
    }

    onMount {
        val interval = window.setInterval(maybeUpdatePatch, 100)
        withCleanup { window.clearInterval(interval) }
    }

    onChange("shaders change", openShaders, selectedShaderIndex) {
        val selectedShader = selectedShader()
        if (selectedShader == null) {
            curPatch = null
            return@onChange
        }
        val editor = aceEditor.current.editor

        selectedShader.lastCursorPosition?.let { editor.moveCursorToPosition(it) }
        selectedShader.lastCursorPosition = null
        showGlslErrors(selectedShader.glslErrors)
        editor.focus()
    }

    val handleChange: (String, Any) -> Unit = useCallback() { newValue: String, event: Any ->
        openShaders = openShaders.replace(selectedShaderIndex) {
            it.copy(src = newValue, isModified = true)
        }
    }

    val glslNumberRegex = Regex("[0-9.]")
    val glslIllegalRegex = Regex("[A-Za-z_]")
    val glslFloatOrIntRegex = Regex("^([0-9]+\\.[0-9]*|[0-9]*\\.[0-9]+|[0-9]+)$")
    val onCursorChange = useCallback() { value: Any, event: Any ->
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

    val extractUniform = useCallback() { event: Event ->
        val extraction = extractionCandidate ?: return@useCallback

        val editor = aceEditor.current.editor
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
            Point(insertionRow, 0),
            "uniform $type $uniformName; // @@Slider default=${originalText} max=${max}\n"
        )
        session.markUndoGroup()
    }

    val handleGlslErrors = useCallback() { glslErrors: Array<GlslError> ->
        val selectedShader = selectedShader()
        selectedShader?.glslErrors = glslErrors
        showGlslErrors(glslErrors)
    }

    val handlePatchPreviewSuccess = useCallback() {
        val selectedShader = selectedShader()
        selectedShader?.let { shader ->
            shader.glslErrors = emptyArray()
            previewShaderOnSimulator(shader)
        }
        Unit
    }

    val handleGadgetsChange = useCallback() { newGadgets: Array<GadgetData> ->
        gadgets = newGadgets
    }

    val handleTabChange = useCallback() { event: Event, tabIndex: Int ->
        val selectedShader = selectedShader()
        selectedShader?.let { shader ->
            shader.lastCursorPosition = aceEditor.current.editor.getCursorPosition()
        }

        selectedShaderIndex = tabIndex
    }

    val handleNewShader = useCallback() { type: NewShaderType ->
        val newShader = EditingShader(
            type.newName,
            "// ${type.newName}\n\n${type.template}",
            isModified = true
        )
        openShaders += newShader
        selectedShaderIndex = openShaders.size - 1
    }

    val handleOpen = useCallback { event: Event ->
        fileDialogOpen = true
        fileDialogIsSaveAs = false
    }

    val handleSave = useCallback() { event: Event ->
        val selectedShader = selectedShader()
        selectedShader?.file?.let { saveToFile ->
            scope.launch {
                saveToFile.fs.saveFile(saveToFile, selectedShader.src, true)
                openShaders = openShaders.replace(selectedShaderIndex) {
                    EditingShader(it.name, selectedShader.src, false, saveToFile)
                }
            }
        }
        Unit
    }

    val handleSaveAs = useCallback() { event: Event ->
        val selectedShader = selectedShader()
        if (selectedShader != null) {
            fileDialogOpen = true
            fileDialogIsSaveAs = true
        }
    }

    val handleSaveToPatch = useCallback(props.onAddToPatch) { event: Event ->
        val selectedShader = selectedShader()
        if (selectedShader != null) {
            props.onAddToPatch(selectedShader)
        }
    }

    val handleFileSelected = useCallback() { file: Fs.File ->
        fileDialogOpen = false

        scope.launch {
            if (fileDialogIsSaveAs) {
                val selectedShader = selectedShader()
                selectedShader!!
                file.fs.saveFile(file, selectedShader.src, true)
                openShaders = openShaders.replace(selectedShaderIndex) {
                    EditingShader(file.name, selectedShader.src, false, file)
                }
            } else {
                val src = file.fs.loadFile(file)!!
                val newShader = EditingShader(file.name, src, false, file)
                openShaders += newShader
                selectedShaderIndex = openShaders.size - 1
            }
        }
        Unit
    }

    val handleSaveAsCancel = useCallback { fileDialogOpen = false }

    val handleClose = useCallback() { event: Event ->
        val selectedShader = selectedShader()
        if (selectedShader != null) {
            if (selectedShader.isModified) {
                if (!window.confirm("Discard changes to ${selectedShader.name}?")) {
                    return@useCallback
                }
            }
            openShaders = openShaders.filterIndexed { index, openShader -> index != selectedShaderIndex }
            selectedShaderIndex = min(openShaders.size - 1, selectedShaderIndex)
        }
    }

    val selectedShader = selectedShader()

    div {
        ref = windowRootEl

        toolbar {
//            iconButton {
//                attrs.edge = IconButtonEdge.start
//                +"Menu"
//            }

            menuButton {
                attrs.name = "New…"

                attrs.items = listOf(
                    MenuItem("New Color Shader…") {
                        handleNewShader(NewShaderType.COLOR)
                    },
                    MenuItem("New Transform Shader…") {
                        handleNewShader(NewShaderType.TRANSFORM)
                    },
                    MenuItem("New Filter Shader…") {
                        handleNewShader(NewShaderType.FILTER)
                    },
                    MenuItem("Import…") { }
                )
            }

            button {
                +"Open…"
                attrs.onClickFunction = handleOpen
            }
            button {
                +"Save"
                attrs.disabled = selectedShader == null || !selectedShader.isModified
                attrs.onClickFunction = handleSave
            }
            button {
                +"Save As…"
                attrs.disabled = selectedShader == null
                attrs.onClickFunction = handleSaveAs
            }
            button {
                +"Save to Patch"
                attrs.disabled = selectedShader == null
                attrs.onClickFunction = handleSaveToPatch
            }
            button {
                +"Close"
                attrs.disabled = openShaders.isEmpty()
                attrs.onClickFunction = handleClose
            }
        }

        tabs {
            attrs.variant = TabsVariant.scrollable
            attrs.value = if (selectedShaderIndex == -1) 0 else selectedShaderIndex
            attrs.onChange = handleTabChange

            openShaders.forEach { openShader ->
                tab {
                    var name = openShader.name
                    if (openShader.isModified) name += " *"
                    attrs.label = name.asTextNode()
                }
            }
        }

        styledDiv {
            css { +previewBar }

            patchPreview {
                attrs.patch = curPatch
                attrs.onSuccess = handlePatchPreviewSuccess
                attrs.onGadgetsChange = handleGadgetsChange
                attrs.onError = handleGlslErrors
            }
            styledDiv { css { +status }; ref = statusContainerEl }
            styledDiv {
                css { +controls }
                showControls { attrs.gadgets = gadgets }
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
                value = selectedShader?.src ?: ""
                name = "ShaderEditorWindow"
                setOptions = jsObject {
                    autoScrollEditorIntoView = true
                }
                editorProps = jsObject {
                    `$blockScrolling` = true
                    readOnly = selectedShader == null
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

        if (fileDialogOpen) {
            fileDialog {
                attrs.isOpen = fileDialogOpen
                attrs.title = if (fileDialogIsSaveAs) "Save Shader As…" else "Open Shader…"
                attrs.isSaveAs = fileDialogIsSaveAs
                attrs.onSelect = handleFileSelected
                attrs.onCancel = handleSaveAsCancel
                attrs.defaultTarget = selectedShader?.file
            }
        }
    }
}

data class ExtractionCandidate(
    val range: Range,
    val text: String
)

data class EditingShader(
    val name: String,
    val src: String,
    val isModified: Boolean = false,
    val file: Fs.File? = null
) {
    var lastCursorPosition: Point? = null
    val lastModified: Time = clock.now()
    var patch: PatchEditor? = null
    var glslErrors: Array<GlslError> = emptyArray()
}

private val clock = JsClock()

external interface ShaderEditorWindowProps : RProps {
    var onAddToPatch: (EditingShader) -> Unit
}

fun Point(row: Number, column: Number): Point =
    jsObject { this.row = row; this.column = column }

fun RBuilder.showControls(handler: RHandler<ShowControlsProps>): ReactElement =
    ShowControls { attrs { handler() } }

fun RBuilder.shaderEditorWindow(handler: RHandler<ShaderEditorWindowProps>): ReactElement =
    child(ShaderEditorWindow, handler = handler)

enum class NewShaderType(val newName: String, val template: String) {
    COLOR("New Color Shader", """
        uniform float time;

        void main(void) {
            gl_FragColor = vec4(1., 1., 1., 1.);
        }
    """.trimIndent()),

    TRANSFORM("New Translate Shader", """
        
    """.trimIndent()),

    FILTER("New Fader Shader", """
        
    """.trimIndent())
}