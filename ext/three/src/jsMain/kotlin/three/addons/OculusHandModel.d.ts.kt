@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.*

open external class OculusHandModel(controller: Object3D, loader: Loader__1<GLTF>? = definedExternally, onLoad: ((obj: Object3D) -> Unit)? = definedExternally) : Object3D {
    open var controller: Object3D
    open var motionController: XRHandMeshModel?
    open var envMap: Texture?
    open var loader: Loader__1<GLTF>?
    open var onLoad: ((obj: Object3D) -> Unit)?
    open var mesh: Mesh<*, *>?
    override fun updateMatrixWorld(force: Boolean)
    open fun getPointerPosition(): Vector3?
    open fun intersectBoxObject(boxObject: Object3D): Boolean
    open fun checkButton(button: Object3D)
}