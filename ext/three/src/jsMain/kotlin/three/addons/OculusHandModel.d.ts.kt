@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import kotlin.js.*
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

external open class OculusHandModel(controller: Object3D__0, loader: Loader__1<GLTF>? = definedExternally, onLoad: ((obj: Object3D__0) -> Unit)? = definedExternally) : Object3D__0 {
    open var controller: Object3D__0
    open var motionController: XRHandMeshModel?
    open var envMap: Texture?
    open var loader: Loader__1<GLTF>?
    open var onLoad: ((obj: Object3D__0) -> Unit)?
    open var mesh: Mesh__0?
    override fun updateMatrixWorld(force: Boolean)
    open fun getPointerPosition(): Vector3?
    open fun intersectBoxObject(boxObject: Object3D__0): Boolean
    open fun checkButton(button: Object3D__0)
}