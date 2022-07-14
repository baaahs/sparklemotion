package baaahs.app.ui.patchmod

import baaahs.app.ui.appContext
import baaahs.app.ui.controls.xyPadControl
import baaahs.app.ui.gadgets.xypad.xyPad
import baaahs.app.ui.shaderPreview
import baaahs.control.OpenXyPadControl
import baaahs.gadgets.XyPad
import baaahs.plugin.core.datasource.XyPadDataSource
import baaahs.show.live.ControlProps
import baaahs.show.live.OpenPatchHolder
import baaahs.ui.asTextNode
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import mui.material.Dialog
import mui.material.Tab
import mui.material.Tabs
import mui.material.TabsScrollButtons
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.dom.events.SyntheticEvent
import react.dom.header
import react.useContext

private val PatchModView = xComponent<PatchModProps>("PatchMod") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.patchModStyles

    val patches = props.patchHolder.patches

    var selectedPatch by state { patches.first() }
    val handlePatchSelect by handler { _: SyntheticEvent<*, *>, value: dynamic ->
        selectedPatch = value
    }

    val patchMods = selectedPatch.patchMods
    var selectedPatchMod by state { patchMods.first() }
    val handlePatchModSelect by handler { _: SyntheticEvent<*, *>, value: dynamic ->
        selectedPatchMod = value
    }


    Dialog {
        attrs.open = true
        attrs.onClose = { _, _ -> props.onClose() }

        header { +props.title }

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

        div(+styles.lightboxShaderPreviewContainer) {
            shaderPreview {
                attrs.shader = selectedPatch.shader.shader
                attrs.noSharedGlContext = true
            }

            with(selectedPatchMod.getView(selectedPatch)) { render() }
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
    var onClose: () -> Unit
}

fun RBuilder.patchMod(handler: RHandler<PatchModProps>) =
    child(PatchModView, handler = handler)