package three.addons

import org.khronos.webgl.*
import org.w3c.dom.*
import org.w3c.dom.events.*
import org.w3c.dom.parsing.*
import org.w3c.dom.svg.*
import org.w3c.dom.url.*
import org.w3c.fetch.*
import org.w3c.files.*
import org.w3c.notifications.*
import org.w3c.performance.*
import org.w3c.workers.*
import org.w3c.xhr.*
import three.*
import kotlin.js.*

open external class XRControllerModel : Object3D {
    open var motionController: Any
    open var envMap: Texture?
    open fun setEnvironmentMap(envMap: Texture): XRControllerModel
}

open external class XRControllerModelFactory(gltfLoader: Loader__1<GLTF>? = definedExternally, onLoad: ((scene: Group) -> Unit)? = definedExternally) {
    open var gltfLoader: Loader__1<GLTF>?
    open var path: String
    open var onLoad: ((scene: Group) -> Unit)?
    open fun setPath(path: String): XRControllerModelFactory /* this */
    open fun createControllerModel(controller: Group): XRControllerModel
}