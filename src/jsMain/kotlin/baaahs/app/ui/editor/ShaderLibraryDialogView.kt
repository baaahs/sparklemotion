package baaahs.app.ui.editor

import baaahs.app.ui.appContext
import baaahs.app.ui.library.resultsSummary
import baaahs.app.ui.shaderCard
import baaahs.app.ui.toolchainContext
import baaahs.gl.preview.ShaderBuilder
import baaahs.gl.withCache
import baaahs.libraries.ShaderLibrary
import baaahs.show.Shader
import baaahs.show.mutable.MutablePatch
import baaahs.show.mutable.MutableShader
import baaahs.ui.*
import baaahs.util.CacheBuilder
import js.core.jso
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.css.*
import materialui.icon
import mui.icons.material.Search
import mui.material.*
import mui.system.sx
import react.*
import react.dom.div
import react.dom.events.FocusEvent
import react.dom.events.FormEvent
import react.dom.events.SyntheticEvent
import styled.inlineStyles
import web.cssom.AlignItems
import web.cssom.Display
import web.events.Event

private val ShaderLibraryDialogView = xComponent<ShaderLibraryDialogProps>("ShaderLibraryDialog") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.shaderLibrary
    val shaderLibraries = appContext.webClient.shaderLibraries
    val baseToolchain = useContext(toolchainContext)
    val toolchain = memo(baseToolchain) { baseToolchain.withCache("Shader Library") }

    var devWarningIsOpen by state { props.devWarning ?: false }
    val handleHideDevWarning by handler { _: SyntheticEvent<*, *>, _: SnackbarCloseReason -> devWarningIsOpen = false }
    val handleShowDevWarning by handler { _: Shader? -> devWarningIsOpen = true }
    val onSelect = props.onSelect ?: handleShowDevWarning

    val handleClose = callback(onSelect) { _: Event, _: String -> onSelect(null) }

    val previewSizeRange = 1..5
    var previewSize by state { 3 }
    val handleSmallerPreviewClick by mouseEventHandler { previewSize-- }
    val handleBiggerPreviewClick by mouseEventHandler { previewSize++ }
    val previewSizePx = (previewSize * 75).px

    var adjustInputs by state { true }
    val handleAdjustInputsChange by switchEventHandler { _, checked ->
        adjustInputs = checked
    }

    val shaderStates = memo { ShaderStates() }
    val searchJob = ref<Job>()
    val entryDisplayCache = memo(onSelect) {
        CacheBuilder<ShaderLibrary.Entry, EntryDisplay> {
            EntryDisplay(it, onSelect) { entry, state ->
                shaderStates.onShaderStateChange(entry, state)
            }
        }
    }
    var matches by state { emptyList<EntryDisplay>() }
    val runSearch by handler { terms: String ->
        searchJob.current?.cancel()
        searchJob.current = GlobalScope.launch {
            matches = shaderLibraries.searchFor(terms)
                .map { entryDisplayCache[it] }
                .also {
                    shaderStates.onNewResults(it.size)
                }
        }
    }

    @Suppress("UNUSED_VARIABLE")
    val justOnce = memo { runSearch("") }

    val handleSearchChange by changeEventHandler { event ->
        println("onChange $event — ${event.target.value}")
//        props.setValue(event.target.value)
//        props.editableManager.onChange(pushToUndoStack = false)
    }

    val handleSearchBlur by focusEventHandler { event: FocusEvent<*> ->
        println("onBlur $event — ${event.target.value}")
//        val newValue = event.target.value
//        if (newValue != valueOnUndoStack.current) {
//            valueOnUndoStack.current = newValue
//            props.editableManager.onChange(pushToUndoStack = true)
//        }
    }

    val handleSearchKeyDown by eventHandler(runSearch) { event: Event ->
        println("onKeydown $event — ${event.target.value}")
        runSearch(event.target.value)
//        if (event.asDynamic().keyCode == 13) {
//            handleBlur(event)
//        }
    }

    val handleSearchInput by formEventHandler(runSearch) { event: FormEvent<*> ->
        println("onInput $event — ${event.target.value}")
        runSearch(event.target.value)
//        if (event.asDynamic().keyCode == 13) {
//            handleBlur(event)
//        }
    }


    toolchainContext.Provider {
        attrs.value = toolchain

        Dialog {
            attrs.open = true
            attrs.fullWidth = true
//            attrs.fullScreen = true
            attrs.maxWidth = "xl"
            attrs.scroll = DialogScroll.paper
            attrs.onClose = handleClose

            DialogTitle { +"Shader Library" }

            DialogContent {
                attrs.classes = jso { this.root = -styles.dialogContent }

                Box {
                    attrs.sx {
                        display = Display.flex
                        alignItems = AlignItems.center
                    }

                    FormControl {
                        TextField<StandardTextFieldProps> {
                            attrs.autoFocus = true
                            attrs.fullWidth = true
//                attrs.label { +props.label }
                            attrs.InputProps = jso {
                                endAdornment = buildElement { icon(Search) }
                            }
                            attrs.defaultValue = ""

                            attrs.onChange = handleSearchChange
                            attrs.onBlur = handleSearchBlur
//                attrs.onKeyDownFunction = handleSearchKeyDown
                            attrs.onInput = handleSearchInput
                        }

                        FormHelperText { +"Enter stuff to search for!" }
                    }

                    IconButton {
                        attrs.title = "Smaller"
                        attrs.disabled = !previewSizeRange.contains(previewSize - 1)
                        attrs.onClick = handleSmallerPreviewClick
                        icon(mui.icons.material.ZoomOut)
                    }

                    IconButton {
                        attrs.title = "Bigger"
                        attrs.disabled = !previewSizeRange.contains(previewSize + 1)
                        attrs.onClick = handleBiggerPreviewClick
                        icon(mui.icons.material.ZoomIn)
                    }

                    FormControlLabel {
                        attrs.control = buildElement {
                            Switch {
                                attrs.checked = adjustInputs
                                attrs.onChange = handleAdjustInputsChange.withTChangeEvent()
                            }
                        }
                        attrs.label = "Adjust Inputs".asTextNode()
                    }

                    div {
                        inlineStyles { flex = Flex.GROW }
                    }

                    resultsSummary {
                        attrs.shaderStates = shaderStates
                    }
                }

                Divider {}

                div(+styles.results) {
                    sharedGlContext {
                        div(+styles.shaderGridScrollContainer) {
                            div(+styles.shaderGrid) {
                                inlineStyles {
                                    gridTemplateColumns =
                                        GridTemplateColumns("repeat(auto-fit, minmax($previewSizePx, 1fr))")
                                }
                                matches.forEach { match ->
                                    shaderCard {
                                        key = match.entry.id
                                        attrs.mutablePatch = match.mutablePatch
                                        attrs.subtitle = match.entry.id.split(":").first()
                                        attrs.toolchain = toolchain
                                        attrs.cardSize = previewSizePx
                                        attrs.adjustGadgets = adjustInputs
                                        attrs.onSelect = match.onSelect
                                        attrs.onDelete = null
                                        attrs.onShaderStateChange = match.onShaderStateChange
                                    }
                                }
                            }
                        }
                    }
                }

                if (devWarningIsOpen) {
                    Snackbar {
                        attrs.open = devWarningIsOpen
                        attrs.message =
                            "NOTE: Shader Library for dev purposes only, nothing useful happens when you select one."
                                .asTextNode()
                        attrs.autoHideDuration = 5000
                        attrs.onClose = handleHideDevWarning
                    }
                }
            }
        }
    }
}

class EntryDisplay(
    val entry: ShaderLibrary.Entry,
    onSelect: (Shader?) -> Unit,
    onShaderStateChange: (ShaderLibrary.Entry, ShaderBuilder.State) -> Unit
) {
    val mutablePatch = MutablePatch(MutableShader(entry.shader))
    val onSelect = { onSelect(entry.shader) }
    val onShaderStateChange = { state: ShaderBuilder.State -> onShaderStateChange(entry, state) }
}

class ShaderStates(
    shaderCount: Int? = null
) : Observable() {
    var shaderCount = shaderCount
        private set

    private val states: MutableMap<String, ShaderBuilder.State> = mutableMapOf()

    fun onShaderStateChange(entry: ShaderLibrary.Entry, state: ShaderBuilder.State) {
        states[entry.id] = state
        notifyChanged()
    }

    fun onNewResults(shaderCount: Int) {
        this.shaderCount = shaderCount
        notifyChanged()
    }

    val stateCount: Map<ShaderBuilder.State, Int>
        get() = states.values.groupingBy { it }.eachCount()
}

external interface ShaderLibraryDialogProps : Props {
    var onSelect: ((Shader?) -> Unit)?
    var devWarning: Boolean?
}

fun RBuilder.shaderLibraryDialog(handler: RHandler<ShaderLibraryDialogProps>) =
    child(ShaderLibraryDialogView, handler = handler)