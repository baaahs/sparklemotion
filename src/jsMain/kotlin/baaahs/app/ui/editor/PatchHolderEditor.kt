package baaahs.app.ui.editor

import baaahs.show.mutable.MutablePatchHolder
import baaahs.ui.Renderer
import baaahs.ui.xComponent
import org.w3c.dom.events.Event
import react.*

@Suppress("UNCHECKED_CAST")
fun <T> Event.targetEl(): T = target as T

val PatchHolderEditor = xComponent<PatchHolderEditorProps>("PatchHolderEditor") { props ->
//    val showBuilder by state { ShowBuilder() }
//
//    val changed = props.mutablePatchHolder.isChanged()
//
//    val handleChange = baaahs.ui.useCallback { forceRender() }
//
//
//    var selectedPatch by state { props.mutablePatchHolder.patches.firstOrNull() }
//    var selectedShaderInstance by state<MutableShaderInstance?> { null }
//    val handleSelectShader = baaahs.ui.useCallback { selected: MutableShaderInstance ->
//        selectedShaderInstance = selected
////        visiblePanel = 1
//    }
//
//    val x = this
//    val styles = PatchHolderEditorStyles

//    div(+PatchHolderEditorStyles.panel and PatchHolderEditorStyles.columns) {
//        div(+PatchHolderEditorStyles.fixturesListCol) {
//            header {
//                +"Fixtures"
//                help {
//                    attrs.divClass = Styles.helpInline.name
//                    attrs.inject(PatchHolderEditorHelpText.fixtures)
//                }
//            }
//
//            list(PatchHolderEditorStyles.fixturesList on ListStyle.root) {
//                props.mutablePatchHolder.patches.forEachIndexed { index, mutablePatch ->
//                    listItem {
//                        attrs.button = true
//                        attrs.selected = mutablePatch == selectedPatch
//                        attrs.onClickFunction = x.eventHandler("handlePatchNavClick-$index") {
//                            selectedPatch = props.mutablePatchHolder.patches[index]
//                        }
//
//                        listItemIcon { icon(FilterList) }
//                        listItemText { +mutablePatch.surfaces.name }
//                    }
//                }
//
//                listItem {
//                    listItemIcon { icon(AddCircleOutline) }
//                    listItemText { +"New…" }
//
//                    attrs.onClickFunction = x.eventHandler("handleNewPatchClick") {
//                        props.mutablePatchHolder.patches.add(MutablePatch())
//                        this@xComponent.forceRender()
//                    }
//                }
//            }
//        }
//
//        div(+PatchHolderEditorStyles.patchOverviewCol) {
//            header {
//                +"Patch Overview"
//                help {
//                    attrs.divClass = Styles.helpInline.name
//                    attrs.inject(PatchHolderEditorHelpText.patchOverview)
//                }
//            }
//
//            div(+PatchHolderEditorStyles.patchOverview) {
//                if (selectedPatch == null) {
//                    typographyH6 { +"No patch selected." }
//                } else {
//                    patchOverview {
//                        attrs.mutablePatch = selectedPatch!!
//                        attrs.onSelectShaderInstance = handleSelectShader
//                    }
//                }
//            }
//        }
//    }


//    val patchNavPanel = Panel(selectedPatch?.surfaces?.name ?: "Surfaces", Apps) {
//    }

//    val panels = arrayListOf(patchNavPanel)


//    val shaderPanel = selectedShaderInstance?.let { shaderInstance ->
//        val shader = shaderInstance.mutableShader
//        val shaderChannels = props.mutablePatchHolder.findShaderChannels() + ShaderChannel.Main
//        Panel(shader.title, Icons.forShader(shader.type)) {
//            shaderInstanceEditor {
//                attrs.mutablePatch = selectedPatch!!
//                attrs.mutableShaderInstance = shaderInstance
//                attrs.shaderChannels = shaderChannels
//                attrs.showBuilder = showBuilder
//                attrs.onChange = handleChange
//            }
//        }
//    }
//    shaderPanel?.let { panels.add(it) }

//    portal {
//        form {
//            drawer(PatchHolderEditorStyles.drawer on DrawerStyle.paper) {
//                attrs.anchor = DrawerAnchor.bottom
//                attrs.variant = DrawerVariant.temporary
//                attrs.elevation
//                attrs.open = true
//                attrs.onClose = handleDrawerClose
//
//                attrs.onSubmitFunction = x.handler("onSubmit", changed, props.onApply) { event: Event ->
//                    if (changed) props.onApply()
//                    event.preventDefault()
//                }

//                dialogTitle {
//                    textField {
//                        attrs.autoFocus = true
//                        attrs.variant = FormControlVariant.outlined
//                        attrs.label { +"Title" }
//                        attrs.value = props.mutablePatchHolder.title
//                        attrs.onChangeFunction = handleTitleChange
//                    }
//                }

//                breadcrumbs {
//                    panels.forEachIndexed { index, panel ->
//                        link {
//                            attrs.onClickFunction = { visiblePanel = index }
//                            panel.icon?.let { icon(it) }
//                            +panel.title
//                        }
//                    }
//                    button { attrs.onClickFunction = { visiblePanel = 1 }; +"2" }
//                    button { attrs.onClickFunction = { visiblePanel = 2 }; +"3" }
//                }

//                dialogContent(+PatchHolderEditorStyles.dialogContent) {
//                    slidePanel {
//                        attrs.panels = panels.map { it.content }
//                        attrs.index = visiblePanel
//                        attrs.margins = 24.px
//                    }
//                }

//                dialogActions {
//                    button {
//                        +"Revert"
//                        attrs.color = ButtonColor.secondary
//                        attrs.onClickFunction = x.eventHandler(props.onCancel)
//                    }
//
//                    button {
//                        +"Apply"
//                        attrs.disabled = !changed
//                        attrs.color = ButtonColor.primary
//                        attrs.onClickFunction = x.eventHandler(props.onApply)
//                    }
//                }
            }
//        }
//    }
//}

fun renderWrapper(block: RBuilder.() -> Unit): Renderer {
    return object : Renderer {
        override fun RBuilder.render() {
            block()
        }
    }
}

//private class Panel(
//    val title: String,
//    val icon: Icon? = null,
//    val content: RBuilder.() -> Unit
//)

external interface PatchHolderEditorProps : RProps {
    var mutablePatchHolder: MutablePatchHolder
    var onApply: () -> Unit
    var onCancel: () -> Unit
}

fun RBuilder.patchHolderEditor(handler: RHandler<PatchHolderEditorProps>): ReactElement =
    child(PatchHolderEditor, handler = handler)
