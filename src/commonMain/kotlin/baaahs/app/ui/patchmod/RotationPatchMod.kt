package baaahs.app.ui.patchmod

import baaahs.gl.patch.ProgramNode
import baaahs.gl.shader.OpenShader
import baaahs.show.DataSource
import baaahs.show.live.GadgetProvider
import baaahs.show.live.LinkedPatch
import baaahs.show.live.OpenPatch
import baaahs.ui.View

class RotationPatchMod : PatchMod {
    override val title: String
        get() = "Rotation"

    override val dataSources: List<DataSource>
        get() = emptyList()

    override fun getView(openPatch: OpenPatch): View =
        patchModViews.forRotation(this, openPatch)

    override fun maybeWrapLink(moddedNode: LinkedPatch, inputPortId: String, link: ProgramNode, forShader: OpenShader): ProgramNode? {
        return null
    }

    override fun registerGadgets(gadgetProvider: GadgetProvider) {}

    companion object : PatchModBuilder {}
}