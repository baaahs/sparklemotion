package baaahs.ui

import baaahs.app.ui.PatchHolderEditorHelpText
import baaahs.app.ui.appContext
import baaahs.show.ShaderChannel
import baaahs.show.mutable.MutablePatch
import baaahs.show.mutable.MutablePatchHolder
import baaahs.show.mutable.MutableShaderInstance
import baaahs.show.mutable.ShowBuilder
import baaahs.ui.misc.slidePanel
import kotlinx.css.*
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onSubmitFunction
import materialui.*
import materialui.components.breadcrumbs.breadcrumbs
import materialui.components.button.button
import materialui.components.button.enums.ButtonColor
import materialui.components.container.container
import materialui.components.dialogactions.dialogActions
import materialui.components.dialogcontent.dialogContent
import materialui.components.dialogtitle.dialogTitle
import materialui.components.drawer.drawer
import materialui.components.drawer.enums.DrawerAnchor
import materialui.components.drawer.enums.DrawerStyle
import materialui.components.drawer.enums.DrawerVariant
import materialui.components.formcontrol.enums.FormControlVariant
import materialui.components.link.link
import materialui.components.list.enums.ListStyle
import materialui.components.list.list
import materialui.components.listitem.listItem
import materialui.components.listitemicon.listItemIcon
import materialui.components.listitemtext.listItemText
import materialui.components.portal.portal
import materialui.components.textfield.textField
import materialui.components.typography.typographyH1
import materialui.components.typography.typographyH6
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event
import react.*
import react.dom.div
import react.dom.form
import react.dom.header
import styled.StyleSheet

@Suppress("UNCHECKED_CAST")
fun <T> Event.targetEl(): T = target as T

val PatchHolderEditor = xComponent<PatchHolderEditorProps>("PatchHolderEditor") { props ->
    val showBuilder by state { ShowBuilder() }

    val changed = props.mutablePatchHolder.isChanged()

    val handleTitleChange = useCallback(props.mutablePatchHolder) { event: Event ->
        props.mutablePatchHolder.title = event.targetEl<HTMLInputElement>().value
        forceRender()
    }

    val handleDrawerClose = eventHandler("handleDrawerClose", props.onCancel) {
        props.onCancel()
    }
    val handleChange = useCallback { forceRender() }

    var visiblePanel by state { 0 }

    var selectedPatch by state { props.mutablePatchHolder.patches.firstOrNull() }
    var selectedShaderInstance by state<MutableShaderInstance?> { null }
    val handleSelectShader = useCallback { selected: MutableShaderInstance ->
        selectedShaderInstance = selected
        visiblePanel = 1
    }

    val x = this
    val styles = PatchHolderEditorStyles
    val patchNavPanel = Panel(selectedPatch?.surfaces?.name ?: "Surfaces", Apps) {
        div(+styles.panel and styles.columns) {
            div(+styles.fixturesListCol) {
                header {
                    +"Fixtures"
                    help {
                        attrs.divClass = Styles.helpInline.name
                        attrs.inject(PatchHolderEditorHelpText.fixtures)
                    }
                }

                list(styles.fixturesList on ListStyle.root) {
                    props.mutablePatchHolder.patches.forEachIndexed { index, mutablePatch ->
                        listItem {
                            attrs.button = true
                            attrs.selected = mutablePatch == selectedPatch
                            attrs.onClickFunction = x.eventHandler("handlePatchNavClick-$index") {
                                selectedPatch = props.mutablePatchHolder.patches[index]
                            }

                            listItemIcon { icon(FilterList) }
                            listItemText { +mutablePatch.surfaces.name }
                        }
                    }

                    listItem {
                        listItemIcon { icon(AddCircleOutline) }
                        listItemText { +"New…" }

                        attrs.onClickFunction = x.eventHandler("handleNewPatchClick") {
                            props.mutablePatchHolder.patches.add(MutablePatch())
                            this@xComponent.forceRender()
                        }
                    }
                }
            }

            div(+styles.patchOverviewCol) {
                header {
                    +"Patch Overview"
                    help {
                        attrs.divClass = Styles.helpInline.name
                        attrs.inject(PatchHolderEditorHelpText.patchOverview)
                    }
                }

                div(+styles.patchOverview) {
                    if (selectedPatch == null) {
                        typographyH6 { +"No patch selected." }
                    } else {
                        patchOverview {
                            attrs.mutablePatch = selectedPatch!!
                            attrs.onSelectShaderInstance = handleSelectShader
                        }
                    }
                }
            }
        }
    }

    val panels = arrayListOf(patchNavPanel)

    val shaderPanel = selectedShaderInstance?.let { shaderInstance ->
        val shader = shaderInstance.mutableShader
        val shaderChannels = props.mutablePatchHolder.findShaderChannels() + ShaderChannel.Main
        Panel(shader.title, Icons.forShader(shader.type)) {
            shaderInstanceEditor {
                attrs.mutablePatch = selectedPatch!!
                attrs.mutableShaderInstance = shaderInstance
                attrs.shaderChannels = shaderChannels
                attrs.showBuilder = showBuilder
                attrs.onChange = handleChange
            }
        }
    }
    shaderPanel?.let { panels.add(it) }

    portal {
        form {
            drawer(styles.drawer on DrawerStyle.paper) {
                attrs.anchor = DrawerAnchor.bottom
                attrs.variant = DrawerVariant.temporary
                attrs.elevation
                attrs.open = true
                attrs.onClose = handleDrawerClose

                attrs.onSubmitFunction = x.handler("onSubmit", changed, props.onApply) { event: Event ->
                    if (changed) props.onApply()
                    event.preventDefault()
                }

                dialogTitle {
                    textField {
                        attrs.autoFocus = true
                        attrs.variant = FormControlVariant.outlined
                        attrs.label { +"Title" }
                        attrs.value = props.mutablePatchHolder.title
                        attrs.onChangeFunction = handleTitleChange
                    }
                }

                breadcrumbs {
                    panels.forEachIndexed { index, panel ->
                        link {
                            attrs.onClickFunction = { visiblePanel = index }
                            panel.icon?.let { icon(it) }
                            +panel.title
                        }
                    }
                    button { attrs.onClickFunction = { visiblePanel = 1 }; +"2" }
                    button { attrs.onClickFunction = { visiblePanel = 2 }; +"3" }
                }

                dialogContent(+styles.dialogContent) {
                    slidePanel {
                        attrs.panels = panels.map { it.content }
                        attrs.index = visiblePanel
                        attrs.margins = 24.px
                    }
                }

                dialogActions {
                    button {
                        +"Revert"
                        attrs.color = ButtonColor.secondary
                        attrs.onClickFunction = x.eventHandler(props.onCancel)
                    }

                    button {
                        +"Apply"
                        attrs.disabled = !changed
                        attrs.color = ButtonColor.primary
                        attrs.onClickFunction = x.eventHandler(props.onApply)
                    }
                }
            }
        }
    }
}

private class Panel(
    val title: String,
    val icon: Icon? = null,
    val content: RBuilder.() -> Unit
)

object PatchHolderEditorStyles : StyleSheet("ui-PatchHolderEditor", isStatic = true) {
    val cardWidth = 175.px

    val drawer by css {
        margin(horizontal = 5.em)
        minHeight = 85.vh
    }

    val dialogContent by css {
        display = Display.flex
        alignItems = Align.stretch
        child("*") {
            flex(1.0)
        }
    }

    val panel by css {
        display = Display.flex
        flexDirection = FlexDirection.column
        alignContent = Align.stretch
        alignItems = Align.stretch
        justifyContent = JustifyContent.stretch
    }

    val columns by css {
        flexDirection = FlexDirection.row
    }

    val fixturesListCol by css {
        flex(1.0, flexBasis = FlexBasis.zero)
    }
    val fixturesList by css {
    }

    val patchOverviewCol by css {
        flex(2.0, flexBasis = FlexBasis.zero)
        marginLeft = 2.em
    }
    val patchOverview by css {
        backgroundColor = StuffThatShouldComeFromTheTheme.lightBackgroundColor
        display = Display.grid
        gridTemplateColumns = GridTemplateColumns.repeat("auto-fit, minmax(175px, 1fr)")
        gap = Gap(1.em.toString())
        padding(1.em)
    }

    val shaderCard by css {
        maxWidth = cardWidth
    }

    val shaderCardContent by css {
    }
    val shaderCardActions by css {
        child(shaderCardContent) {
            flex(1.0)
        }

        child("button") {
            flex(0.0)
            padding(0.px)
        }
    }
}

external interface PatchHolderEditorProps : RProps {
    var mutablePatchHolder: MutablePatchHolder
    var onApply: () -> Unit
    var onCancel: () -> Unit
}

fun RBuilder.patchHolderEditor(handler: RHandler<PatchHolderEditorProps>): ReactElement =
    child(PatchHolderEditor, handler = handler)
