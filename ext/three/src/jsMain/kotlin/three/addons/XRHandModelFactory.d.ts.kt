package three.addons

import three.Group
import three.Loader__1
import three.Object3D

open external class XRHandModel : Object3D {
    open var motionController: dynamic /* XRHandPrimitiveModel | XRHandMeshModel */
}

open external class XRHandModelFactory(gltfLoader: Loader__1<GLTF>? = definedExternally, onLoad: ((obj: Object3D) -> Unit)? = definedExternally) {
    open var gltfLoader: Loader__1<GLTF>?
    open var path: String?
    open var onLoad: ((obj: Object3D) -> Unit)?
    open fun setPath(path: String?): XRHandModelFactory /* this */
    open fun createHandModel(controller: Group, profile: String /* "spheres" | "boxes" | "mesh" */ = definedExternally, options: XRHandPrimitiveModelOptions = definedExternally): XRHandModel
}