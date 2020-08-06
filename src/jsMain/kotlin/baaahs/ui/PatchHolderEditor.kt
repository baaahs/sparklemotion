package baaahs.ui

import baaahs.app.ui.appContext
import baaahs.show.ShaderChannel
import baaahs.show.ShaderType
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
import materialui.components.avatar.avatar
import materialui.components.breadcrumbs.breadcrumbs
import materialui.components.button.button
import materialui.components.button.enums.ButtonColor
import materialui.components.card.card
import materialui.components.cardheader.cardHeader
import materialui.components.container.container
import materialui.components.dialogactions.dialogActions
import materialui.components.dialogcontent.dialogContent
import materialui.components.dialogtitle.dialogTitle
import materialui.components.divider.divider
import materialui.components.drawer.drawer
import materialui.components.drawer.enums.DrawerAnchor
import materialui.components.drawer.enums.DrawerStyle
import materialui.components.drawer.enums.DrawerVariant
import materialui.components.formcontrol.enums.FormControlVariant
import materialui.components.formcontrol.formControl
import materialui.components.formhelpertext.formHelperText
import materialui.components.link.link
import materialui.components.list.enums.ListStyle
import materialui.components.list.list
import materialui.components.listitem.listItem
import materialui.components.listitemicon.listItemIcon
import materialui.components.listitemtext.listItemText
import materialui.components.listsubheader.listSubheader
import materialui.components.menuitem.menuItem
import materialui.components.paper.enums.PaperStyle
import materialui.components.portal.portal
import materialui.components.select.select
import materialui.components.textfield.textField
import materialui.components.typography.enums.TypographyColor
import materialui.components.typography.enums.TypographyDisplay
import materialui.components.typography.enums.TypographyVariant
import materialui.components.typography.typography
import materialui.components.typography.typographyH1
import materialui.components.typography.typographyH6
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event
import react.*
import react.dom.div
import react.dom.form
import styled.StyleSheet
import styled.styledDiv

@Suppress("UNCHECKED_CAST")
fun <T> Event.targetEl(): T = target as T

val PatchHolderEditor = xComponent<PatchHolderEditorProps>("PatchHolderEditor") { props ->
    val appContext = useContext(appContext)
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

    var selectedPatch by state { props.mutablePatchHolder.patches.firstOrNull() }
    var selectedShaderInstance by state<MutableShaderInstance?> { null }
    var visiblePanel by state { 0 }

    val x = this
    val styles = PatchHolderEditorStyles
    val patchNavPanel = Panel(selectedPatch?.surfaces?.name ?: "Surfaces", Apps) {
        div(+styles.panel and styles.columns) {
            list(styles.patchList on ListStyle.root) {
                listSubheader { +"Fixtures" }

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

            div(+styles.patchOverview) {
                val mutablePatch = selectedPatch
                if (mutablePatch == null) {
                    typographyH6 { +"No patch selected." }
                } else {
                    mutablePatch.mutableShaderInstances.forEachIndexed { index, mutableShaderInstance ->
                        val shader = mutableShaderInstance.mutableShader

                        card(+styles.shaderCard on PaperStyle.root) {
                            attrs.onClickFunction = x.eventHandler("handleShaderInstanceClick-$index") {
                                selectedShaderInstance = selectedPatch!!.mutableShaderInstances[index]
                                visiblePanel = 1
                            }

                            cardHeader {
                                attrs.avatar {
                                    avatar { icon(Icons.forShader(shader.type)) }
                                }
                                attrs.title { +shader.title }
//                                attrs.subheader { +"${shader.type.name} Shader" }
                            }

                            shaderPreview {
                                attrs.mutableShaderInstance = mutableShaderInstance
                                attrs.width = styles.cardWidth
                                attrs.height = styles.cardWidth
                            }

                            div(+styles.shaderCardContent) {
                                typography {
                                    attrs.display = TypographyDisplay.block
                                    attrs.variant = TypographyVariant.body2
                                    attrs.color = TypographyColor.textSecondary
                                    +"${shader.type.name} Shader"
                                }
                            }
//                            cardActions {
//                                button { +"Edit" }
//                            }
                        }
                    }

                    card {
                        menuButton {
                            attrs.icon = AddCircleOutline
                            attrs.label = "New Shader…"

                            attrs.items = ShaderType.values().map { type ->
                                MenuItem("New ${type.name} Shader…") {
                                    val newShader = type.shaderFromTemplate().build()
                                    val contextShaders =
                                        mutablePatch.mutableShaderInstances.map { it.mutableShader.build() } + newShader
                                    val unresolvedPatch = appContext.autoWirer.autoWire(
                                        *contextShaders.toTypedArray(),
                                        focus = newShader
                                    )
                                    mutablePatch.addShaderInstance(newShader) {
                                        // TODO: Something better than this.
                                        val resolved = unresolvedPatch
                                            .acceptSymbolicChannelLinks()
                                            .resolve()
                                            .mutableShaderInstances[0]
                                        incomingLinks.putAll(resolved.incomingLinks)
                                        shaderChannel = resolved.shaderChannel
                                    }
                                    this@xComponent.forceRender()
                                }
                            } + MenuItem("Import…") { }
                        }

                        typography {
                            attrs.display = TypographyDisplay.block
                            attrs.variant = TypographyVariant.subtitle1
                            +"New Shader…"
                        }
                    }
                }
            }
        }
    }

    val panels = arrayListOf(patchNavPanel)

    val shaderPanel = selectedShaderInstance?.let { shaderInstance ->
        val shader = shaderInstance.mutableShader.build()
        val shaderChannels = props.mutablePatchHolder.findShaderChannels() + ShaderChannel.Main
        val handleUpdate =
            handler("handleShaderUpdate", shaderInstance) { block: MutableShaderInstance.() -> Unit ->
                selectedShaderInstance!!.block()
                forceRender()
            }

        Panel(shader.title, Icons.forShader(shader.type)) {
            styledDiv {
                css.display = Display.grid
                css.gridTemplateColumns = GridTemplateColumns("auto min-content auto")
                css.gap = Gap(1.em.toString())

                div {
                    linksEditor {
                        attrs.mutablePatch = selectedPatch!!
                        attrs.showBuilder = showBuilder
                        attrs.mutableShaderInstance = shaderInstance
                        attrs.shaderChannels = shaderChannels
                        attrs.onChange = handleChange
                    }
                }

                div {
                    shaderPreview {
                        attrs.mutableShaderInstance = shaderInstance
                        attrs.width = 250.px
                        attrs.height = 250.px
                    }
                }

                div {
                    typographyH6 { +"Meta and gadgets and stuff!" }

                    div(+Styles.shaderMeta) {
                        formControl {
                            textField {
                                attrs.autoFocus = false
                                attrs.fullWidth = true
                                attrs.value = shaderInstance.mutableShader.title
                                attrs.onChangeFunction = { event: Event ->
                                    val str = event.target!!.asDynamic().value as String
                                    handleUpdate { mutableShader.title = str }
                                }
                            }
                            formHelperText { +"Shader Name" }
                        }

                        formControl {
                            select {
                                attrs.value(shaderInstance.shaderChannel?.id ?: "")
                                attrs.onChangeFunction = { event: Event ->
                                    val channelId = event.target!!.asDynamic().value as String
                                    handleUpdate { shaderChannel = if (channelId.isNotBlank()) ShaderChannel(channelId) else null }
                                }
                                shaderChannels.sortedBy { it.id }.forEach { shaderChannel ->
                                    menuItem {
                                        attrs["value"] = shaderChannel.id
                                        +shaderChannel.id
                                    }
                                }

                                divider {}
                                menuItem {
                                    attrs["value"] = ""
                                    +"None"
                                }
                            }
                            formHelperText { +"Channel" }
                        }

                        formControl {
                            textField {
                                attrs.value = shaderInstance.priority
                                attrs.onChangeFunction = { event: Event ->
                                    val priorityStr = event.target!!.asDynamic().value as String
                                    handleUpdate { priority = priorityStr.toFloat() }
                                }
                            }
                            formHelperText { +"Priority" }
                        }
                    }

                }
            }

            shaderEditor {
                attrs.mutableShaderInstance = shaderInstance
                attrs.shaderChannels = shaderChannels
                attrs.onChange = handleChange
            }
        }
    }
    shaderPanel?.let { panels.add(it) }

    val secondPanel = Panel("All Surfaces", AccountTree) { container { typographyH1 { +"Page 2" } } }
    val thirdPanel = Panel("Shader", SettingsInputComponent) { container { typographyH1 { +"Page 3" } } }

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
                    +"${props.mutablePatchHolder.displayType}:"

                    textField {
                        attrs.autoFocus = true
                        attrs.variant = FormControlVariant.outlined
                        attrs.label = "${props.mutablePatchHolder.displayType} Title".asTextNode()
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
    val patchList by css {
        flex(1.0, flexBasis = FlexBasis.zero)
    }
    val patchOverview by css {
        flex(2.0, flexBasis = FlexBasis.zero)
        backgroundColor = StuffThatShouldComeFromTheTheme.lightBackgroundColor
        display = Display.grid
        gridTemplateColumns = GridTemplateColumns.repeat("auto-fit, minmax(175px, 1fr)")
        gap = Gap(1.em.toString())
        padding(1.em)
        marginLeft = 2.em
    }

    val shaderCard by css {
        maxWidth = cardWidth
    }

    val shaderCardContent by css {
        padding(8.px, 16.px)
    }
}

external interface PatchHolderEditorProps : RProps {
    var mutablePatchHolder: MutablePatchHolder
    var onApply: () -> Unit
    var onCancel: () -> Unit
}

fun RBuilder.patchHolderEditor(handler: RHandler<PatchHolderEditorProps>): ReactElement =
    child(PatchHolderEditor, handler = handler)
