@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.Group
import three.Texture

external interface XRHandPrimitiveModelOptions {
    var primitive: String? /* "sphere" | "box" */
        get() = definedExternally
        set(value) = definedExternally
}

open external class XRHandPrimitiveModel(handModel: XRHandModel, controller: Group, path: String, handedness: String /* "left" | "right" */, options: XRHandPrimitiveModelOptions) {
    open var controller: Group
    open var handModel: XRHandModel
    open var envMap: Texture?
    open var handMesh: Group
    open var updateMesh: () -> Unit
}