package baaahs.ui

import ReactAce.Ace.reactAce
import acex.AceEditor
import acex.Annotation
import acex.Point
import acex.Range
import baaahs.GadgetData
import baaahs.glshaders.AutoWirer
import baaahs.glshaders.GlslAnalyzer
import baaahs.glshaders.Patch
import baaahs.glshaders.Plugins
import baaahs.glsl.CompiledShader
import baaahs.io.Fs
import baaahs.jsx.ShowControls
import baaahs.jsx.ShowControlsProps
import baaahs.jsx.useResizeListener
import baaahs.ui.Styles.controls
import baaahs.ui.Styles.glslNumber
import baaahs.ui.Styles.previewBar
import baaahs.ui.Styles.status
import kotlinext.js.jsObject
import kotlinx.html.js.onClickFunction
import materialui.components.iconbutton.enums.IconButtonEdge
import materialui.components.iconbutton.iconButton
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

private val glslNumberClassName = glslNumber.getName()

val ShaderEditorWindow = functionalComponent<ShaderEditorWindowProps> { props ->
    val windowRootEl = useRef<Element>()

    val preact = Preact()

    val aceEditor = useRef<AceEditor>()
    val statusContainerEl = useRef<Element>()
    var gadgets by preact.state<Array<GadgetData>> { arrayOf() }
    var openShaders by preact.state<List<OpenShader>> { arrayListOf() }
    var selectedShaderIndex by preact.state { -1 }
    var patch by preact.state<Patch?> { null }
    var extractionCandidate by preact.state<ExtractionCandidate?> { null }
    var glslNumberMarker by preact.state<Number?> { null }
    var fileDialogOpen by preact.state { false }
    var fileDialogIsSaveAs by preact.state { false }
    val selectedShader = if (selectedShaderIndex == -1) null else openShaders[selectedShaderIndex]

    useResizeListener(windowRootEl) {
        aceEditor.current.editor.resize()
    }

    fun previewShaderOnSimulator(shader: OpenShader) {
//     TODO   simulator.switchToShow(
//            GlslShow(selectedShow, shader.src, GlslShader.globalRenderContext, true)
//        )
    }

    val showGlslErrors = useCallback(aceEditor) { glslErrors: Array<CompiledShader.GlslError> ->
        val editor = aceEditor.current.editor
        editor.getSession().setAnnotations(
            glslErrors.map { error ->
                jsObject<Annotation> {
                    row = error.row
                    column = error.column
                    text = error.message
                    type = "error"
                }
            }.toTypedArray()
        )
    }

//    preact.sideEffect("show change", selectedShow) {
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

    preact.sideEffect("shaders change", selectedShader, showGlslErrors) {
        if (selectedShader == null) {
            patch = null
            return@sideEffect
        }
        val editor = aceEditor.current.editor

        selectedShader.lastCursorPosition?.let { editor.moveCursorToPosition(it) }
        selectedShader.lastCursorPosition = null
        showGlslErrors(selectedShader.glslErrors)

        if (selectedShader.patch == null) {
            try {
                selectedShader.patch = AutoWirer(Plugins.findAll()).autoWire(
                    mapOf("color" to GlslAnalyzer().asShader(selectedShader.src))
                ).resolve()
            } catch (e: Exception) {
                selectedShader.glslErrors = arrayOf(
                    CompiledShader.GlslError(e.message ?: e.toString())
                )
            }
        }
        patch = selectedShader.patch
    }

    val onChange: (String, Any) -> Unit = useCallback(
        openShaders, selectedShaderIndex
    ) { newValue: String, event: Any ->
        openShaders = openShaders.replace(selectedShaderIndex) {
            it.copy(src = newValue, isModified = true)
        }
    }

    val glslNumberRegex = Regex("[0-9.]")
    val glslIllegalRegex = Regex("[A-Za-z_]")
    val glslFloatOrIntRegex = Regex("^([0-9]+\\.[0-9]*|[0-9]*\\.[0-9]+|[0-9]+)$")
    val onCursorChange = useCallback({ value: Any, event: Any ->
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
    }, arrayOf(glslNumberMarker))

    val extractUniform = useCallback({ event: Event ->
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
        session.insert(
            Point(lastUniform.start.row.toInt() + 1, 0),
            "uniform $type $uniformName; // @@Slider default=${originalText} max=${max}\n"
        )
        session.markUndoGroup()
    }, arrayOf(aceEditor, extractionCandidate))

    val handleGlslErrors = useCallback(
        aceEditor, selectedShader, showGlslErrors
    ) { glslErrors: Array<CompiledShader.GlslError> ->
        selectedShader?.glslErrors = glslErrors
        showGlslErrors(glslErrors)
    }

    val handlePatchPreviewSuccess = useCallback(selectedShader) {
        selectedShader?.let { shader ->
            shader.glslErrors = emptyArray()
            previewShaderOnSimulator(shader)
        }
        Unit
    }

    val handleGadgetsChange = useCallback() { newGadgets: Array<GadgetData> ->
        gadgets = newGadgets
    }

    val handleTabChange = useCallback(selectedShader) { event: Event, tabIndex: Int ->
        selectedShader?.let { shader ->
            shader.lastCursorPosition = aceEditor.current.editor.getCursorPosition()
        }

        selectedShaderIndex = tabIndex
    }

    val handleNewShader = useCallback(openShaders) { type: NewShaderType ->
        val newShader = OpenShader(
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

    val handleSave = useCallback(selectedShader, openShaders, selectedShaderIndex) { event: Event ->
        selectedShader?.file?.let { saveToFile ->
            saveToFile.fs.saveFile(saveToFile, selectedShader.src, true)
            openShaders = openShaders.replace(selectedShaderIndex) {
                OpenShader(it.name, selectedShader.src, false, saveToFile)
            }
        }
        Unit
    }

    val handleSaveAs = useCallback(selectedShader) { event: Event ->
        if (selectedShader != null) {
            fileDialogOpen = true
            fileDialogIsSaveAs = true
        }
    }

    val handleFileSelected = useCallback(
        fileDialogIsSaveAs, selectedShader, openShaders, selectedShaderIndex
    ) { file: Fs.File ->
        fileDialogOpen = false

        if (fileDialogIsSaveAs) {
            selectedShader!!
            file.fs.saveFile(file, selectedShader.src, true)
            openShaders = openShaders.replace(selectedShaderIndex) {
                OpenShader(file.name, selectedShader.src, false, file)
            }
        } else {
            val src = file.fs.loadFile(file)!!
            val newShader = OpenShader(file.name, src, false, file)
            openShaders += newShader
            selectedShaderIndex = openShaders.size - 1
        }
    }

    val handleSaveAsCancel = useCallback { fileDialogOpen = false }

    val handleClose = useCallback(selectedShader, openShaders, selectedShaderIndex) { event: Event ->
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

    div {
        ref = windowRootEl

        toolbar {
            iconButton {
                attrs.edge = IconButtonEdge.start
                +"Menu"
            }

            menuButton {
                name = "New…"

                items = listOf(
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
                this.patch = patch
                onSuccess = handlePatchPreviewSuccess
                onGadgetsChange = handleGadgetsChange
                onError = handleGlslErrors
            }
            styledDiv { css { +status }; ref = statusContainerEl }
            styledDiv {
                css { +controls }
                showControls { this.gadgets = gadgets }
            }
        }

        reactAce {
            ref = aceEditor
            attrs {
                mode = "glsl"
                theme = "tomorrow_night_bright"
                width = "100%"
                height = "100%"
                showGutter = true
                this.onChange = onChange
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

        fileDialog {
            isOpen = fileDialogOpen
            title = if (fileDialogIsSaveAs) "Save Shader" else "Open Shader"
            isSaveAs = fileDialogIsSaveAs
            onSelect = handleFileSelected
            onCancel = handleSaveAsCancel
            filesystems = props.filesystems
            defaultTarget = selectedShader?.file?.let { file ->
                SaveAsTarget(props.filesystems.find { it.fs == file.fs }, file.name)
            }
        }
    }
}

data class ExtractionCandidate(
    val range: Range,
    val text: String
)

data class OpenShader(
    val name: String,
    val src: String,
    val isModified: Boolean = false,
    val file: Fs.File? = null
) {
    var patch: Patch? = null
    var glslErrors: Array<CompiledShader.GlslError> = emptyArray()
    var lastCursorPosition: Point? = null
}


external interface ShaderEditorWindowProps : RProps {
    var filesystems: List<SaveAsFs>
}

fun Point(row: Number, column: Number): Point =
    jsObject { this.row = row; this.column = column }

fun RBuilder.showControls(handler: ShowControlsProps.() -> Unit): ReactElement =
    ShowControls { attrs { handler() } }

fun RBuilder.shaderEditorWindow(handler: ShaderEditorWindowProps.() -> Unit): ReactElement =
    child(ShaderEditorWindow) { attrs { handler() } }

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