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
import baaahs.ui.Styles.buttons
import baaahs.ui.Styles.controls
import baaahs.ui.Styles.glslNumber
import baaahs.ui.Styles.iconButton
import baaahs.ui.Styles.previewBar
import baaahs.ui.Styles.showName
import baaahs.ui.Styles.showNameInput
import baaahs.ui.Styles.status
import baaahs.ui.Styles.toolbar
import kotlinext.js.jsObject
import kotlinx.html.js.onClickFunction
import org.w3c.dom.Element
import org.w3c.dom.events.Event
import react.*
import react.dom.button
import react.dom.defaultValue
import react.dom.div
import react.dom.i
import styled.css
import styled.styledDiv
import styled.styledI
import styled.styledInput

val ShaderEditorWindow = functionalComponent<ShaderEditorWindowProps> {
    val windowRootEl = useRef<Element>()
    val contextState = useContext(store).state
    val sheepSimulator = contextState.sheepSimulator
    val selectedShow = contextState.selectedShow

    val aceEditor = useRef<AceEditor>()
    val statusContainerEl = useRef<Element>()
    val (gadgets, setGadgets) = useState<Array<GadgetData>>(arrayOf())
    val (showStr, setShowStr) = useState("")
    val (patch, setPatch) = useState<Patch?>(null)
    val (openShaders, setOpenShaders) = useState<List<OpenShader>>(arrayListOf())
    val (extractionCandidate, setExtractionCandidate) = useState(ExtractionCandidate())
    var glslNumberMarker: Number? = null

    useResizeListener(windowRootEl) {
        aceEditor.current.editor.resize()
    }

    useEffectWithCleanup {
        println("shaderEditorWindow hihi!")

        return@useEffectWithCleanup {
            println("shaderEditorWindow byebye!")
        }
    }

    fun updateSimulatorWithAlteredShader(src: String) {
        sheepSimulator?.switchToShow(
            GlslShow(selectedShow, src, GlslShader.globalRenderContext, true)
        )
    }

    fun updatePreview(src: String) {
        aceEditor.current.editor.getSession().setAnnotations(emptyArray())
        setPatch(
            AutoWirer().autoWire(
                mapOf("color" to GlslAnalyzer().asShader(src)))
        )

        updateSimulatorWithAlteredShader(src)
    }

    useEffect(listOf(selectedShow)) {
        // Look up the text for the show
        val allShows = sheepSimulator?.shows?.toTypedArray() ?: emptyArray()
        val currentShow = allShows.find { it.name == selectedShow }

        if (currentShow != null && currentShow is GlslShow && !currentShow.isPreview) {
            val shaderSource = currentShow.src
            setShowStr(shaderSource)
            updatePreview(shaderSource)

            setOpenShaders(
                openShaders
                        + OpenShader(currentShow.name, currentShow.src, currentShow)
            )
        }
    }

    val onChange: (String, Any) -> Unit = useCallback(
        fun(newValue: String, event: Any) {
            setShowStr(newValue)
            try {
                updatePreview(newValue)
            } catch (e: Exception) {
                console.error("Uncaught exception in editor onChange", e)
            }
        },
        arrayOf(showStr)
    )

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
            if (extractionCandidate.text != null) setExtractionCandidate(ExtractionCandidate())
        } else {
            val range = Range(cursor.row, start, cursor.row, end)
            glslNumberMarker = session.addMarker(range, glslNumber.toString(), "text", false)

            setExtractionCandidate(ExtractionCandidate(range, candidate))
        }
    }, arrayOf())

    val extractUniform = useCallback({ event: Event ->
        val editor = aceEditor.current.editor
        val session = editor.getSession()

        val originalText = extractionCandidate.text ?: error("no text")
        val type = if (originalText.indexOf('.') > -1) "float" else "int"
        val prefix = "${type}Uniform"
        var num = 0
        while (showStr.indexOf("${prefix}${num}") > -1) num++
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

    val handleGlslErrors = useCallback({ glslErrors: Array<CompiledShader.GlslError> ->
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
    }, arrayOf(aceEditor))

    val handleGadgetsChange = useCallback({ newGadgets: Array<GadgetData> ->
        setGadgets(newGadgets)
    }, arrayOf())

    div {
        ref = windowRootEl
        styledDiv {
            css { +toolbar }

            styledDiv { css { +showName } }

            i("fas fa-chevron-right") {}
            styledInput {
                css { +showNameInput }
                attrs.defaultValue = selectedShow
            }

            styledDiv {
                css { +buttons }
                styledI {
                    css { +"fas"; +"fa-play"; +iconButton }
                }
            }
        }

        styledDiv {
            css { +previewBar }

            patchPreview {
                this.patch = patch
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
                value = showStr
                name = "ShaderEditorWindow"
                setOptions = jsObject {
                    autoScrollEditorIntoView = true
                }
                editorProps = jsObject {
                    `$blockScrolling` = true
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
)


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