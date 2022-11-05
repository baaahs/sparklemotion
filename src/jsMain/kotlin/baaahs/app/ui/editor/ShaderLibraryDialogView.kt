package baaahs.app.ui.editor

import baaahs.app.ui.appContext
import baaahs.app.ui.shaderCard
import baaahs.app.ui.toolchainContext
import baaahs.gl.withCache
import baaahs.libraries.ShaderLibrary
import baaahs.show.Shader
import baaahs.show.mutable.MutablePatch
import baaahs.show.mutable.MutableShader
import baaahs.ui.*
import baaahs.util.CacheBuilder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.css.*
import kotlinx.html.org.w3c.dom.events.Event
import kotlinx.js.jso
import materialui.icon
import mui.icons.material.Search
import mui.material.*
import react.*
import react.dom.div
import react.dom.events.FocusEvent
import react.dom.events.FormEvent
import react.dom.events.SyntheticEvent
import styled.inlineStyles

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

    val previewSizeRange = 1 .. 5
    var previewSize by state { 3 }
    val handleSmallerPreviewClick by mouseEventHandler { previewSize-- }
    val handleBiggerPreviewClick by mouseEventHandler { previewSize++ }
    val previewSizePx = (previewSize * 75).px

    var adjustInputs by state { true }
    val handleAdjustInputsChange by switchEventHandler { _, checked ->
        adjustInputs = checked
    }

    val searchJob = ref<Job>()
    val entryDisplayCache = memo(onSelect) {
        CacheBuilder<ShaderLibrary.Entry, EntryDisplay> {
            EntryDisplay(it, onSelect)
        }
    }
    var matches by state { emptyList<EntryDisplay>() }
    val runSearch by handler { terms: String ->
        searchJob.current?.cancel()
        searchJob.current = GlobalScope.launch {
            matches = shaderLibraries.searchFor(terms)
                .map { entryDisplayCache[it] }
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
                        attrs.control =  buildElement {
                            Switch {
                                attrs.checked = adjustInputs
                                attrs.onChange = handleAdjustInputsChange.withTChangeEvent()
                            }
                        }
                        attrs.label = "Adjust Inputs".asTextNode()
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
                                        attrs.onSelect = match.onSelect
                                        attrs.onDelete = null
                                        attrs.toolchain = toolchain
                                        attrs.cardSize = previewSizePx
                                        attrs.adjustGadgets = adjustInputs
                                    }
                                }
                            }
                        }
                    }
                }

                if (devWarningIsOpen) {
                    Snackbar {
                        attrs.open = devWarningIsOpen
                        attrs.message = "NOTE: Shader Library for dev purposes only, nothing useful happens when you select one."
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
    onSelect: (Shader?) -> Unit
) {
    val mutablePatch = MutablePatch(MutableShader(entry.shader))
    val onSelect = { onSelect(entry.shader) }
}

external interface ShaderLibraryDialogProps : Props {
    var onSelect: ((Shader?) -> Unit)?
    var devWarning: Boolean?
}

fun RBuilder.shaderLibraryDialog(handler: RHandler<ShaderLibraryDialogProps>) =
    child(ShaderLibraryDialogView, handler = handler)