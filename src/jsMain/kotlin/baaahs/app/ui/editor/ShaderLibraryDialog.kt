package baaahs.app.ui.editor

import baaahs.app.ui.appContext
import baaahs.app.ui.shaderCard
import baaahs.gl.withCache
import baaahs.libraries.ShaderLibrary
import baaahs.show.Shader
import baaahs.show.mutable.MutableShader
import baaahs.show.mutable.MutableShaderInstance
import baaahs.ui.value
import baaahs.ui.xComponent
import baaahs.util.CacheBuilder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.css.*
import kotlinx.html.js.onBlurFunction
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onInputFunction
import materialui.components.dialog.dialog
import materialui.components.dialog.enums.DialogMaxWidth
import materialui.components.dialog.enums.DialogScroll
import materialui.components.dialogcontent.dialogContent
import materialui.components.dialogtitle.dialogTitle
import materialui.components.divider.divider
import materialui.components.formcontrol.formControl
import materialui.components.formhelpertext.formHelperText
import materialui.components.textfield.textField
import org.w3c.dom.events.Event
import react.*
import react.dom.div
import styled.inlineStyles

val ShaderLibraryDialog = xComponent<ShaderLibraryDialogProps>("ShaderLibraryDialog") { props ->
    val appContext = useContext(appContext)
    val shaderLibraries = appContext.webClient.shaderLibraries
    val toolchain = appContext.toolchain.withCache("Shader Library")

    val handleClose = callback(props.onSelect) { _: Event, _: String -> props.onSelect(null) }

    val searchJob = ref<Job?> { null }
    var matches by state { emptyList<ShaderLibrary.Entry>() }
    val runSearch by handler { terms: String ->
        searchJob.current?.cancel()
        searchJob.current = GlobalScope.launch {
            matches = shaderLibraries.searchFor(terms)
        }
    }

    val handleSearchChange by eventHandler { event: Event ->
        println("onChange $event — ${event.target.value}")
//        props.setValue(event.target.value)
//        props.editableManager.onChange(pushToUndoStack = false)
    }

    val handleSearchBlur by eventHandler { event: Event ->
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

    val handleSearchInput by eventHandler(runSearch) { event: Event ->
        println("onInput $event — ${event.target.value}")
        runSearch(event.target.value)
//        if (event.asDynamic().keyCode == 13) {
//            handleBlur(event)
//        }
    }

    val handleShaderSelect = CacheBuilder<ShaderLibrary.Entry, () -> Unit> { entry ->
        { props.onSelect(entry.shader) }
    }


    dialog {
        attrs.open = true
        attrs.fullWidth = true
//        attrs.fullScreen = true
        attrs.maxWidth = DialogMaxWidth.xl
        attrs.scroll = DialogScroll.body
        attrs.onClose = handleClose

        dialogTitle { +"Shader Library" }

        dialogContent {
            formControl {
                textField {
                    attrs.autoFocus = true
                    attrs.fullWidth = true
//                attrs.label { +props.label }
                    attrs.defaultValue = ""

                    attrs.onChangeFunction = handleSearchChange
                    attrs.onBlurFunction = handleSearchBlur
//                attrs.onKeyDownFunction = handleSearchKeyDown
                    attrs.onInputFunction = handleSearchInput
                }

                formHelperText { +"Enter stuff to search for!" }
            }

            divider {}

            div {
                inlineStyles {
                    display = Display.grid
                    gridTemplateColumns = GridTemplateColumns("1fr 1fr 1fr")
                    gap = Gap(1.em.value)
                }

                matches.forEach { match ->
                    shaderCard {
                        key = match.id
                        attrs.mutableShaderInstance = MutableShaderInstance(MutableShader(match.shader))
                        attrs.onSelect = handleShaderSelect[match]
                        attrs.onDelete = null
                        attrs.toolchain = toolchain
                    }
                }
            }
        }
    }
}

external interface ShaderLibraryDialogProps : RProps {
    var onSelect: (Shader?) -> Unit
}

fun RBuilder.shaderLibraryDialog(handler: RHandler<ShaderLibraryDialogProps>) =
    child(ShaderLibraryDialog, handler = handler)