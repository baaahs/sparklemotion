@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.Object3D

open external class MMDExporter {
    open fun parseVpd(skin: Object3D, outputShiftJis: Boolean, useOriginalBones: Boolean): dynamic /* JsTuple<> | Uint8Array */
}