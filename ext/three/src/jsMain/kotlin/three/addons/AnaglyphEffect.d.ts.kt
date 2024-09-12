@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.Camera
import three.Matrix3
import three.Scene
import three.WebGLRenderer

open external class AnaglyphEffect(renderer: WebGLRenderer, width: Number = definedExternally, height: Number = definedExternally) {
    open var colorMatrixLeft: Matrix3
    open var colorMatrixRight: Matrix3
    open var setSize: (width: Number, height: Number) -> Unit
    open var render: (scene: Scene, camera: Camera) -> Unit
    open var dispose: () -> Unit
}