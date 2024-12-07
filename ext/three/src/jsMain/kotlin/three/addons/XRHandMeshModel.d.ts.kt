package three.addons

import three.Loader__1
import three.Object3D

open external class XRHandMeshModel(handModel: Object3D, controller: Object3D, path: String, handedness: String, loader: Loader__1<GLTF>? = definedExternally, onLoad: ((obj: Object3D) -> Unit)? = definedExternally) {
    open var controller: Object3D
    open var handModel: Object3D
    open var bones: Array<Object3D>
    open fun updateMesh()
}