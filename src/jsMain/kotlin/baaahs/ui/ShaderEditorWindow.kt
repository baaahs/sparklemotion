package baaahs.ui

import Ace.AceEditor
import Ace.Annotation
import Ace.Point
import Ace.Range
import ReactAce.Ace.reactAce
import baaahs.GadgetData
import baaahs.glshaders.AutoWirer
import baaahs.glshaders.GlslAnalyzer
import baaahs.glshaders.Patch
import baaahs.glsl.CompiledShader
import baaahs.jsx.ShowControls
import baaahs.jsx.ShowControlsProps
import baaahs.jsx.store
import baaahs.jsx.useResizeListener
import baaahs.shaders.GlslShader
import baaahs.shows.GlslShow
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
import react.dom.*
import styled.css
import styled.styledDiv

val ShaderEditorWindow = functionalComponent<ShaderEditorWindowProps> {
    val windowRootEl = useRef<Element>()
    val contextState = useContext(store).state
    val sheepSimulator = contextState.sheepSimulator
    val selectedShow = contextState.selectedShow
    
    val preact = Preact()

    val aceEditor = useRef<AceEditor>()
    val statusContainerEl = useRef<Element>()
    var gadgets by preact.state<Array<GadgetData>> { arrayOf() }
    var openShaders by preact.state<List<OpenShader>> { arrayListOf() }
    var selectedShaderIndex by preact.state { -1 }
    var patch by preact.state<Patch?> { null }
    var extractionCandidate by preact.state { ExtractionCandidate() }
    var glslNumberMarker by preact.state<Number?> { null }

    useResizeListener(windowRootEl) {
        aceEditor.current.editor.resize()
    }

    fun previewShaderOnSimulator(shader: OpenShader) {
        sheepSimulator?.switchToShow(
            GlslShow(selectedShow, shader.src, GlslShader.globalRenderContext, true)
        )
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

    preact.sideEffect("show change", selectedShow) {
        // Look up the text for the show
        val allShows = sheepSimulator?.shows?.toTypedArray() ?: emptyArray()
        val currentShow = allShows.find { it.name == contextState.selectedShow }

        if (currentShow != null && currentShow is GlslShow && !currentShow.isPreview) {
            val existingShaderIdx = openShaders.indexOfFirst { it.name == selectedShow }
            val shader = if (existingShaderIdx == -1) {
                val newShader = OpenShader(currentShow.name, currentShow.src, currentShow)
                openShaders += newShader
                selectedShaderIndex = openShaders.size - 1
                newShader
            } else {
                selectedShaderIndex = existingShaderIdx
                openShaders[existingShaderIdx]
            }
        }
    }

    preact.sideEffect("shaders change", openShaders, selectedShaderIndex, showGlslErrors) {
        if (selectedShaderIndex == -1) return@sideEffect
        val selectedShader = openShaders[selectedShaderIndex]
        val editor = aceEditor.current.editor

        selectedShader.lastCursorPosition?.let { editor.moveCursorToPosition(it) }
        selectedShader.lastCursorPosition = null
        showGlslErrors(selectedShader.glslErrors)

        if (selectedShader.patch == null) {
            selectedShader.patch = AutoWirer().autoWire(
                mapOf("color" to GlslAnalyzer().asShader(selectedShader.src)))
        }
        patch = selectedShader.patch
    }

    val onChange: (String, Any) -> Unit = useCallback(
        openShaders, selectedShaderIndex
    ) { newValue: String, event: Any ->
        val origShaders = openShaders
        openShaders = openShaders.replace(selectedShaderIndex) {
            it.copy(src = newValue)
        }
        println("Changed? ${origShaders === openShaders} with == ${origShaders == openShaders}")
    }

    val glslNumberRegex = Regex("[0-9.]")
    val glslIllegalRegex = Regex("[A-Za-z_]")
    val glslFloatOrIntRegex = Regex("^([0-9]+\\.[0-9]*|[0-9]*\\.[0-9]+|[0-9]+)$")
    val onCursorChange = useCallback({ value: Any, event: Any ->
        @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
        val selection = value as Ace.Selection
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
            if (extractionCandidate.text != null) extractionCandidate = ExtractionCandidate()
        } else {
            val range = Range(cursor.row, start, cursor.row, end)
            glslNumberMarker = session.addMarker(range, glslNumber.toString(), "text", false)

            extractionCandidate = ExtractionCandidate(range, candidate)
        }
    }, arrayOf(extractionCandidate, glslNumberMarker))

    val extractUniform = useCallback({ event: Event ->
        val editor = aceEditor.current.editor
        val session = editor.getSession()

        val originalText = extractionCandidate.text ?: error("no text")
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

        session.replace(extractionCandidate.range ?: error("no range"), uniformName)
        session.insert(
            Point(lastUniform.start.row.toInt() + 1, 0),
            "uniform $type $uniformName; // @@Slider default=${originalText} max=${max}\n"
        )
        session.markUndoGroup()
    }, arrayOf(aceEditor, extractionCandidate))

    val handleGlslErrors = useCallback(
        aceEditor, openShaders, selectedShaderIndex, showGlslErrors
    ) { glslErrors: Array<CompiledShader.GlslError> ->
        val selectedShader = openShaders[selectedShaderIndex]
        selectedShader.glslErrors = glslErrors
        showGlslErrors(glslErrors)
    }

    val handlePatchPreviewSuccess = useCallback(openShaders, selectedShaderIndex) {
        val selectedShader = openShaders[selectedShaderIndex]
        selectedShader.glslErrors = emptyArray()
        previewShaderOnSimulator(selectedShader)
    }

    val handleGadgetsChange = useCallback() { newGadgets: Array<GadgetData> ->
        gadgets = newGadgets
    }

    val handleTabChange = useCallback({ event: Event, tabIndex: Int ->
        if (selectedShaderIndex != -1) {
            val selectedShader = openShaders[selectedShaderIndex]
            selectedShader.lastCursorPosition = aceEditor.current.editor.getCursorPosition()
        }

        selectedShaderIndex = tabIndex
    }, arrayOf())

    val handleMenuButton = useCallback() { event: Event ->
//        setMenuEl(event.currentTarget)
    }

    val handleMenuClose = useCallback() { event: Event ->
//        setMenuEl(null)
    }

    val selectedShader = if (selectedShaderIndex == -1) null else openShaders[selectedShaderIndex]

    println("ShaderEditorWindow: render patch from ${selectedShader?.name}")
    div {
        ref = windowRootEl

        toolbar {
            iconButton {
                attrs.edge = IconButtonEdge.start
                +"Menu"
            }

            button { +"New…" }
            button { +"Open…" }
            button { +"Save" }
            button { +"Save As…" }
            button { +"Close" }
        }

        tabs {
            attrs.variant = TabsVariant.scrollable
            attrs.value = if (selectedShaderIndex == -1) 0 else selectedShaderIndex
            attrs.onChange = handleTabChange

            openShaders.forEach { openShader ->
                tab {
                    attrs.label = openShader.name.asTextNode()
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

        if (extractionCandidate.text != null) {
            div {
                +"Extract ${extractionCandidate.text}?"
                button {
                    attrs.onClickFunction = extractUniform
                    +"Sure!"
                }
            }
        }
    }
}

data class ExtractionCandidate(
    val range: Range? = null,
    val text: String? = null
)

data class OpenShader(
    val name: String,
    val src: String,
    val show: GlslShow
) {
    var patch: Patch? = null
    var glslErrors: Array<CompiledShader.GlslError> = emptyArray()
    var lastCursorPosition: Point? = null
}


external interface ShaderEditorWindowProps : RProps

fun Point(row: Number, column: Number): Point =
    jsObject { this.row = row; this.column = column }

fun Range(startRow: Number, startCol: Number, endRow: Number, endCol: Number): Range =
    jsObject {
        start = Point(startRow, startCol)
        end = Point(endRow, endCol)
    }


fun RBuilder.showControls(handler: ShowControlsProps.() -> Unit): ReactElement =
    ShowControls { attrs { handler() } }

fun RBuilder.shaderEditorWindow(handler: ShaderEditorWindowProps.() -> Unit): ReactElement =
    child(ShaderEditorWindow) { attrs { handler() } }