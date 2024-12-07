package three.addons

import three.Object3D

open external class MMDExporter {
    open fun parseVpd(skin: Object3D, outputShiftJis: Boolean, useOriginalBones: Boolean): dynamic /* JsTuple<> | Uint8Array */
}