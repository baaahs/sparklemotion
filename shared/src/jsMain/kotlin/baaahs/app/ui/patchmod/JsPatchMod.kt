package baaahs.app.ui.patchmod

import baaahs.show.live.OpenPatch
import baaahs.ui.View
import baaahs.ui.renderWrapper

actual fun getPatchModViews(): PatchModViews = object : PatchModViews {
    override fun forPositionAndScale(patchMod: PositionAndScalePatchMod, openPatch: OpenPatch): View = renderWrapper {
        positionAndScale {
            attrs.patchMod = patchMod
            attrs.patch = openPatch
        }
    }

    override fun forRotation(patchMod: RotationPatchMod, openPatch: OpenPatch): View = renderWrapper {
    }

    override fun forColor(patchMod: ColorPatchMod, openPatch: OpenPatch): View = renderWrapper {
    }
}