package baaahs.ui

import baaahs.glshaders.LinkedPatch
import baaahs.glshaders.OpenShader
import baaahs.io.Fs
import baaahs.jsx.ShowControls
import baaahs.jsx.ShowControlsProps
import baaahs.show.Shader
import baaahs.show.mutable.MutableShader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.html.js.onClickFunction
import materialui.components.button.button
import materialui.components.tab.tab
import materialui.components.tabs.enums.TabsVariant
import materialui.components.tabs.tabs
import materialui.components.toolbar.toolbar
import org.w3c.dom.events.Event
import react.*
import react.dom.div
import kotlin.browser.window
import kotlin.math.min

val ShaderEditorWindow = xComponent<ShaderEditorWindowProps>("ShaderEditorWindow") { props ->
    val scope = memo { CoroutineScope(Dispatchers.Main) }

    var openShaders by state {
        props.shaders?.map { shader ->
            EditingShader(MutableShader(shader), isModified = false)
        } ?: arrayListOf()
    }
    var selectedShaderIndex by state { -1 }
    var curPatch by state<LinkedPatch?> { null }
    var fileDialogOpen by state { false }
    var fileDialogIsSaveAs by state { false }
    fun selectedShader() = if (selectedShaderIndex == -1) null else openShaders[selectedShaderIndex]

    val handleChange: () -> Unit = useCallback {
        forceRender()
    }

    val handleTabChange = useCallback() { event: Event, tabIndex: Int ->
        selectedShaderIndex = tabIndex
    }

    val handleNewShader = useCallback() { type: OpenShader.Type ->
        val newShader = EditingShader(
            MutableShader(Shader("// ${type.name} Shader\n\n${type.template}")),
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
                    EditingShader(it.mutableShader, isModified = false, file = saveToFile)
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
                file.fs.saveFile(file.withExtension(".glsl"), selectedShader.src, true)
                openShaders = openShaders.replace(selectedShaderIndex) {
                    EditingShader(it.mutableShader, false, file)
                }
            } else {
                val src = file.fs.loadFile(file)!!
                val newShader = EditingShader(MutableShader(Shader(src)), false, file)
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
                if (!window.confirm("Discard changes to ${selectedShader.title}?")) {
                    return@useCallback
                }
            }
            openShaders = openShaders.filterIndexed { index, openShader -> index != selectedShaderIndex }
            selectedShaderIndex = min(openShaders.size - 1, selectedShaderIndex)
        }
    }

    val selectedShader = selectedShader()

    div {
        toolbar {
//            iconButton {
//                attrs.edge = IconButtonEdge.start
//                +"Menu"
//            }

            menuButton {
                attrs.label = "New…"

                attrs.items = OpenShader.Type.values().map { type ->
                    MenuItem("New ${type.name} Shader…") {
                        handleNewShader(type)
                    }
                } + MenuItem("Import…") { }
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
                    var name = openShader.title
                    if (openShader.isModified) name += " *"
                    attrs.label = name.asTextNode()
                }
            }
        }

        shaderEditor {
            attrs.mutableShader = selectedShader?.mutableShader
            attrs.onChange = handleChange
        }

        if (fileDialogOpen) {
            fileDialog {
                attrs.isOpen = fileDialogOpen
                attrs.title = if (fileDialogIsSaveAs) "Save Shader As…" else "Open Shader…"
                attrs.isSaveAs = fileDialogIsSaveAs
                attrs.fileDisplayCallback = { file, fileDisplay ->
                    if (file.isDirectory == false) {
                        fileDisplay.isSelectable = file.name.endsWith(".glsl")
                    }
                }
                attrs.onSelect = handleFileSelected
                attrs.onCancel = handleSaveAsCancel
                attrs.defaultTarget = selectedShader?.file
            }
        }
    }
}

external interface ShaderEditorWindowProps : RProps {
    var shaders: List<Shader>?
    var onAddToPatch: (EditingShader) -> Unit
}

fun RBuilder.showControls(handler: RHandler<ShowControlsProps>): ReactElement =
    ShowControls { attrs { handler() } }

fun RBuilder.shaderEditorWindow(handler: RHandler<ShaderEditorWindowProps>): ReactElement =
    child(ShaderEditorWindow, handler = handler)
