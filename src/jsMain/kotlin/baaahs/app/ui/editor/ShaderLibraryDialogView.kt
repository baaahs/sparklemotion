package baaahs.app.ui.editor

import baaahs.app.ui.appContext
import baaahs.app.ui.shaderCard
import baaahs.gl.withCache
import baaahs.libraries.ShaderLibrary
import baaahs.show.Shader
import baaahs.show.mutable.MutablePatch
import baaahs.show.mutable.MutableShader
import baaahs.ui.value
import baaahs.ui.xComponent
import baaahs.util.CacheBuilder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.css.*
import mui.material.*
import org.w3c.dom.events.Event
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.dom.events.FocusEvent
import react.dom.events.FormEvent
import react.dom.onChange
import react.useContext
import styled.inlineStyles

private val ShaderLibraryDialogView = xComponent<ShaderLibraryDialogProps>("ShaderLibraryDialog") { props ->
    val appContext = useContext(appContext)
    val shaderLibraries = appContext.webClient.shaderLibraries
    val toolchain = appContext.toolchain.withCache("Shader Library")

    val handleClose = callback(props.onSelect) { _: Event, _: String -> props.onSelect(null) }

    val searchJob = ref<Job>()
    var matches by state { emptyList<ShaderLibrary.Entry>() }
    val runSearch by handler { terms: String ->
        searchJob.current?.cancel()
        searchJob.current = GlobalScope.launch {
            matches = shaderLibraries.searchFor(terms)
        }
    }

    val handleSearchChange by formEventHandler { event: FormEvent<*> ->
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

    val handleShaderSelect = CacheBuilder<ShaderLibrary.Entry, () -> Unit> { entry ->
        { props.onSelect(entry.shader) }
    }


    Dialog {
        attrs.open = true
        attrs.fullWidth = true
//        attrs.fullScreen = true
        attrs.maxWidth = "xl"
        attrs.scroll = DialogScroll.body
        attrs.onClose = handleClose

        DialogTitle { +"Shader Library" }

        DialogContent {
            FormControl {
                TextField {
                    attrs.autoFocus = true
                    attrs.fullWidth = true
//                attrs.label { +props.label }
                    attrs.defaultValue = ""

                    attrs.onChange = handleSearchChange
                    attrs.onBlur = handleSearchBlur
//                attrs.onKeyDownFunction = handleSearchKeyDown
                    attrs.onInput = handleSearchInput
                }

                FormHelperText { +"Enter stuff to search for!" }
            }

            Divider {}

            div {
                inlineStyles {
                    display = Display.grid
                    gridTemplateColumns = GridTemplateColumns("1fr 1fr 1fr")
                    gap = 1.em
                }

                matches.forEach { match ->
                    shaderCard {
                        key = match.id
                        attrs.mutablePatch = MutablePatch(MutableShader(match.shader))
                        attrs.onSelect = handleShaderSelect[match]
                        attrs.onDelete = null
                        attrs.toolchain = toolchain
                    }
                }
            }
        }
    }
}

external interface ShaderLibraryDialogProps : Props {
    var onSelect: (Shader?) -> Unit
}

fun RBuilder.shaderLibraryDialog(handler: RHandler<ShaderLibraryDialogProps>) =
    child(ShaderLibraryDialogView, handler = handler)