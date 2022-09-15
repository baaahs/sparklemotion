package baaahs.app.ui.patchmod

import baaahs.gl.Toolchain
import baaahs.gl.patch.ProgramNode
import baaahs.gl.shader.OpenShader
import baaahs.show.DataSource
import baaahs.show.live.GadgetProvider
import baaahs.show.live.LinkedPatch
import baaahs.show.live.OpenPatch
import baaahs.ui.View

// TODO: This should come from plugins.
val patchModBuilders = listOf(
    PositionAndScalePatchMod,
    RotationPatchMod,
    ColorPatchMod
)

interface PatchMod {
    val title: String
    val dataSources: List<DataSource>

    fun getView(openPatch: OpenPatch): View
    fun maybeWrapLink(moddedNode: LinkedPatch, inputPortId: String, link: ProgramNode, forShader: OpenShader): ProgramNode?
    fun registerGadgets(gadgetProvider: GadgetProvider)
}

interface PatchModBuilder {
    fun buildIfRelevant(
        openShader: OpenShader,
        patchId: String,
        toolchain: Toolchain,
        gadgetProvider: GadgetProvider
    ): PatchMod? = null
}

interface PatchModViews {
    fun forPositionAndScale(patchMod: PositionAndScalePatchMod, openPatch: OpenPatch): View
    fun forRotation(patchMod: RotationPatchMod, openPatch: OpenPatch): View
    fun forColor(patchMod: ColorPatchMod, openPatch: OpenPatch): View
}

val patchModViews by lazy { getPatchModViews() }
expect fun getPatchModViews(): PatchModViews