package baaahs.app.ui.patchmod

import baaahs.app.ui.appContext
import baaahs.app.ui.controls.Styles
import baaahs.app.ui.shaderPreview
import baaahs.show.live.ControlProps
import baaahs.show.live.OpenPatchHolder
import baaahs.ui.asTextNode
import baaahs.ui.unaryMinus
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import js.core.jso
import mui.material.*
import react.*
import react.dom.div
import react.dom.events.SyntheticEvent
import react.dom.header

private val PatchModView = xComponent<PatchModProps>("PatchMod") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.patchModStyles

    val patches = props.patchHolder.patches

    var selectedPatch by state { patches.first() }
    val handlePatchSelect by handler { _: SyntheticEvent<*, *>, value: dynamic ->
        selectedPatch = value
    }

    val patchMods = selectedPatch.patchMods
    var selectedPatchMod by state { patchMods.firstOrNull() }
    val handlePatchModSelect by handler { _: SyntheticEvent<*, *>, value: dynamic ->
        selectedPatchMod = value
    }

    val handleActiveSwitchChange by switchEventHandler(props.onToggle) { _, checked ->
        props.onToggle()
    }

    Dialog {
        attrs.open = true
        attrs.onClose = { _, _ -> props.onClose() }

        header {
            FormControlLabel {
                attrs.control = buildElement {
                    Switch {
                        attrs.checked = props.isActive
                        attrs.onChange = handleActiveSwitchChange
                    }
                }
                attrs.label = buildElement { +props.title }
            }
        }

        if (patches.size > 1) {
            Tabs {
                attrs.scrollButtons = TabsScrollButtons.auto
                attrs.value = selectedPatch
                attrs.onChange = handlePatchSelect

                patches.forEach { patch ->
                    Tab {
                        attrs.label = patch.title.asTextNode()
                        attrs.value = patch
                    }
                }
            }
        }

        div(+styles.container) {
            div(+styles.lightboxShaderPreviewContainer) {
                shaderPreview {
                    attrs.shader = selectedPatch.shader.shader
                    attrs.noSharedGlContext = true
                }

                selectedPatchMod?.let {
                    with(it.getView(selectedPatch)) { render() }
                }
            }

            div(+styles.controls) {
                val incomingFeeds = selectedPatch.feeds

                appContext.showManager.openShow?.allControls?.forEach { control ->
                    if (incomingFeeds.intersect(control.controlledFeeds()).isNotEmpty()) {
                        Card {
                            attrs.classes = jso { root = -Styles.controlBox }
                            with(control.getView(ControlProps(null, null, null))) {
                                render()
                            }
                        }
                    }
                }
            }
        }

        Tabs {
            attrs.value = selectedPatchMod
            attrs.onChange = handlePatchModSelect

            patchMods.forEach { patchMod ->
                Tab {
                    attrs.disabled = false
                    attrs.label = patchMod.title.asTextNode()
                    attrs.value = patchMod
                }
            }
        }
    }
}

external interface PatchModProps : Props {
    var title: String
    var patchHolder: OpenPatchHolder
    var isActive: Boolean
    var onToggle: () -> Unit
    var onClose: () -> Unit
}

fun RBuilder.patchMod(handler: RHandler<PatchModProps>) =
    child(PatchModView, handler = handler)